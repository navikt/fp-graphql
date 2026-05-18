package no.nav.foreldrepenger.graphql.codegen.mapper;

import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.ANNOTATIONS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.BUILDER;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.DEPRECATED;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.FIELDS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.JAVA_DOC;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.METHOD_NAME;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.OPERATION_NAME;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.OPERATION_TYPE;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.PACKAGE;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.RETURN_TYPE_NAME;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import no.nav.foreldrepenger.graphql.codegen.model.MappingConfigConstants;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.ProjectionParameterDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.builders.DeprecatedDefinitionBuilder;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

/**
 * Map request and response definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class RequestResponseDefinitionToDataModelMapper {

    private final GraphQLTypeMapper graphQLTypeMapper;
    private final AnnotationsMapper annotationsMapper;
    private final DataModelMapper dataModelMapper;
    private final FieldDefinitionToParameterMapper fieldDefinitionToParameterMapper;
    private final InputValueDefinitionToParameterMapper inputValueDefinitionToParameterMapper;

    public RequestResponseDefinitionToDataModelMapper(MapperFactory mapperFactory,
                                                      FieldDefinitionToParameterMapper fieldDefinitionToParameterMapper,
                                                      InputValueDefinitionToParameterMapper inputValDefToParamMapper) {
        this.graphQLTypeMapper = mapperFactory.getGraphQLTypeMapper();
        this.annotationsMapper = mapperFactory.getAnnotationsMapper();
        this.dataModelMapper = mapperFactory.getDataModelMapper();
        this.fieldDefinitionToParameterMapper = fieldDefinitionToParameterMapper;
        this.inputValueDefinitionToParameterMapper = inputValDefToParamMapper;
    }

    /**
     * Examples:
     * - EventsByIdsQueryRequest
     * - EventsByCategoryQueryRequest
     * - CreateEventMutationResponse
     */
    private static String getClassName(ExtendedFieldDefinition operationDef,
                                       List<String> fieldNames,
                                       String objectType,
                                       String suffix) {
        var classNameBuilder = new StringBuilder();
        classNameBuilder.append(Utils.capitalize(operationDef.getName()));
        if (Collections.frequency(fieldNames, operationDef.getName()) > 1) {
            classNameBuilder.append(DataModelMapper.getClassNameSuffixWithInputValues(operationDef));
        }
        classNameBuilder.append(objectType);
        if (Utils.isNotBlank(suffix)) {
            classNameBuilder.append(suffix);
        }
        return classNameBuilder.toString();
    }

    private static ProjectionParameterDefinition getChildDefinition(String childName) {
        var name = "...on " + childName;
        var methodName = "on" + childName;
        var type = Utils.capitalize(childName + MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_SUFFIX);
        return new ProjectionParameterDefinition(type, name, methodName, null, null);
    }

    private static ProjectionParameterDefinition getTypeNameProjectionParameterDefinition() {
        return new ProjectionParameterDefinition(null, "__typename", "typename", null, null);
    }

    /**
     * Map type definition to a Freemarker data model of Response Projection.
     *
     * @param mappingContext Global mapping context
     * @param definition     GraphQL definition (type or union)
     * @return Freemarker data model of the GraphQL Response Projection
     */
    public Map<String, Object> mapResponseProjection(MappingContext mappingContext,
                                                     ExtendedDefinition<?, ?> definition) {
        var className = Utils.capitalize(definition.getName()) + MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_SUFFIX;
        Map<String, Object> dataModel = new HashMap<>();
        // ResponseProjection classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, className);
        dataModel.put(ANNOTATIONS, annotationsMapper.getAnnotations(mappingContext, className));
        dataModel.put(FIELDS, getProjectionFields(mappingContext, definition));
        dataModel.put(JAVA_DOC, Collections.singletonList("Response projection for " + definition.getName()));
        return dataModel;
    }

    /**
     * Map field definition to a Freemarker data model of Parametrized Input.
     *
     * @param mappingContext       Global mapping context
     * @param fieldDefinition      GraphQL field definition
     * @param parentTypeDefinition GraphQL parent type definition
     * @return Freemarker data model of the GraphQL Parametrized Input
     */
    public Map<String, Object> mapParametrizedInput(MappingContext mappingContext,
                                                    ExtendedFieldDefinition fieldDefinition,
                                                    ExtendedDefinition<?, ?> parentTypeDefinition) {
        var className = DataModelMapper
                .getParametrizedInputClassName(fieldDefinition, parentTypeDefinition);
        Map<String, Object> dataModel = new HashMap<>();
        // ParametrizedInput classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, className);
        dataModel.put(ANNOTATIONS, annotationsMapper.getAnnotations(mappingContext, className));
        dataModel.put(FIELDS, inputValueDefinitionToParameterMapper.map(
                mappingContext, fieldDefinition.getInputValueDefinitions(), parentTypeDefinition.getName()));
        dataModel.put(JAVA_DOC, Collections.singletonList(String.format("Parametrized input for field %s in type %s",
                fieldDefinition.getName(), parentTypeDefinition.getName())));
        return dataModel;
    }

    /**
     * Map field definition to a Response Freemarker data model.
     *
     * @param mappingContext Global mapping context
     * @param operationDef   GraphQL operation definition
     * @param objectTypeName Object type (e.g.: "Query", "Mutation" or "Subscription")
     * @param fieldNames     Names of all fields inside the rootType. Used to detect duplicate
     * @return Freemarker data model of the GraphQL response
     */
    public Map<String, Object> mapResponse(MappingContext mappingContext,
                                           ExtendedFieldDefinition operationDef,
                                           String objectTypeName,
                                           List<String> fieldNames) {
        var className = getClassName(operationDef, fieldNames, objectTypeName, MappingConfigConstants.DEFAULT_RESPONSE_SUFFIX);
        var namedDefinition = graphQLTypeMapper.getLanguageType(
                mappingContext, operationDef.getType(), operationDef.getName(), objectTypeName);
        var returnType = graphQLTypeMapper
                .getResponseReturnType(namedDefinition.getJavaName());
        Map<String, Object> dataModel = new HashMap<>();
        // Response classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(ANNOTATIONS, annotationsMapper.getAnnotations(mappingContext, className));
        dataModel.put(CLASS_NAME, className);
        dataModel.put(DEPRECATED, DeprecatedDefinitionBuilder.build(operationDef));
        dataModel.put(OPERATION_NAME, operationDef.getName());
        dataModel.put(METHOD_NAME, dataModelMapper.capitalizeMethodNameIfRestricted(
            operationDef.getName()));
        dataModel.put(RETURN_TYPE_NAME, returnType);
        dataModel.put(JAVA_DOC, operationDef.getJavaDoc());
        return dataModel;
    }

    /**
     * Map field definition to a Request Freemarker data model.
     *
     * @param mappingContext Global mapping context
     * @param operationDef   GraphQL operation definition
     * @param objectTypeName Object type (e.g.: "Query", "Mutation" or "Subscription")
     * @param fieldNames     Names of all fields inside the rootType. Used to detect duplicate
     * @return Freemarker data model of the GraphQL request
     */
    public Map<String, Object> mapRequest(MappingContext mappingContext,
                                          ExtendedFieldDefinition operationDef,
                                          String objectTypeName,
                                          List<String> fieldNames) {
        var className = getClassName(operationDef, fieldNames, objectTypeName, MappingConfigConstants.DEFAULT_REQUEST_SUFFIX);

        Map<String, Object> dataModel = new HashMap<>();
        // Request classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(ANNOTATIONS, annotationsMapper.getAnnotations(mappingContext, className));
        dataModel.put(CLASS_NAME, className);
        dataModel.put(OPERATION_NAME, operationDef.getName());
        dataModel.put(OPERATION_TYPE, objectTypeName.toUpperCase());
        dataModel.put(FIELDS, inputValueDefinitionToParameterMapper
                .map(mappingContext, operationDef.getInputValueDefinitions(), operationDef.getName()));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(JAVA_DOC, operationDef.getJavaDoc());
        return dataModel;
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingContext Global mapping context
     * @param definition     GraphQL definition (type / union / interface)
     * @return Freemarker data model of the GraphQL type
     */
    private Collection<ProjectionParameterDefinition> getProjectionFields(MappingContext mappingContext,
                                                                          ExtendedDefinition<?, ?> definition) {

        if (definition instanceof ExtendedObjectTypeDefinition extendedObjectTypeDefinition) {
            return getProjectionFields(mappingContext, extendedObjectTypeDefinition);
        } else if (definition instanceof ExtendedUnionTypeDefinition extendedUnionTypeDefinition) {
            return getProjectionFields(extendedUnionTypeDefinition);
        } else if (definition instanceof ExtendedInterfaceTypeDefinition extendedInterfaceTypeDefinition) {
            return getProjectionFields(mappingContext, extendedInterfaceTypeDefinition);
        }
        return Collections.emptyList();
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param unionDefinition GraphQL union definition
     * @return Freemarker data model for response projection of the GraphQL union
     */
    private static Collection<ProjectionParameterDefinition> getProjectionFields(ExtendedUnionTypeDefinition unionDefinition) {
        // using the map to exclude duplicate fields from the type and interfaces
        Map<String, ProjectionParameterDefinition> allParameters = new LinkedHashMap<>();
        for (var memberTypeName : unionDefinition.getMemberTypeNames()) {
            var memberDef = getChildDefinition(memberTypeName);
            allParameters.put(memberDef.methodName(), memberDef);
        }
        var typeNameProjParamDef = getTypeNameProjectionParameterDefinition();
        allParameters.put(typeNameProjParamDef.methodName(), typeNameProjParamDef);
        return allParameters.values();
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingContext Global mapping context
     * @param typeDefinition GraphQL type definition
     * @return Freemarker data model for response projection of the GraphQL type
     */
    private Collection<ProjectionParameterDefinition> getProjectionFields(
            MappingContext mappingContext, ExtendedObjectTypeDefinition typeDefinition) {
        // using the map to exclude duplicate fields from the type and interfaces
        Map<String, ProjectionParameterDefinition> allParameters = new LinkedHashMap<>();
        // includes parameters from the base definition and extensions
        fieldDefinitionToParameterMapper
                .mapProjectionFields(mappingContext, typeDefinition.getFieldDefinitions(), typeDefinition)
                .forEach(p -> allParameters.put(p.methodName(), p));
        // includes parameters from the interface
        var interfacesOfType = DataModelMapper
                .getInterfacesOfType(typeDefinition, mappingContext.getDocument());
        interfacesOfType.stream()
                .map(i -> fieldDefinitionToParameterMapper
                        .mapProjectionFields(mappingContext, i.getFieldDefinitions(), i))
                .flatMap(Collection::stream)
                .filter(paramDef -> !allParameters.containsKey(paramDef.methodName()))
                .forEach(paramDef -> allParameters.put(paramDef.methodName(), paramDef));
        var typeNameProjParamDef = getTypeNameProjectionParameterDefinition();
        allParameters.put(typeNameProjParamDef.methodName(), typeNameProjParamDef);
        return allParameters.values();
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingContext      Global mapping context
     * @param interfaceDefinition GraphQL interface definition
     * @return Freemarker data model for response projection of the GraphQL interface
     */
    private Collection<ProjectionParameterDefinition> getProjectionFields(
            MappingContext mappingContext, ExtendedInterfaceTypeDefinition interfaceDefinition) {
        // using the map to exclude duplicate fields from the type and interfaces
        Map<String, ProjectionParameterDefinition> allParameters = new LinkedHashMap<>();
        // includes parameters from the base definition and extensions
        fieldDefinitionToParameterMapper
                .mapProjectionFields(mappingContext, interfaceDefinition.getFieldDefinitions(), interfaceDefinition)
                .forEach(p -> allParameters.put(p.methodName(), p));
        // includes parameters from the interface
        DataModelMapper.getInterfacesOfType(interfaceDefinition, mappingContext.getDocument()).stream()
                .map(i -> fieldDefinitionToParameterMapper
                        .mapProjectionFields(mappingContext, i.getFieldDefinitions(), i))
                .flatMap(Collection::stream)
                .filter(paramDef -> !allParameters.containsKey(paramDef.methodName()))
                .forEach(paramDef -> allParameters.put(paramDef.methodName(), paramDef));

        var interfaceChildren = mappingContext.getInterfaceChildren()
                .getOrDefault(interfaceDefinition.getName(),
                        Collections.emptySet());
        for (var childName : interfaceChildren) {
            var childDef = getChildDefinition(childName);
            allParameters.put(childDef.methodName(), childDef);
        }
        var typeNameProjParamDef = getTypeNameProjectionParameterDefinition();
        allParameters.put(typeNameProjParamDef.methodName(), typeNameProjParamDef);
        return allParameters.values();
    }

}
