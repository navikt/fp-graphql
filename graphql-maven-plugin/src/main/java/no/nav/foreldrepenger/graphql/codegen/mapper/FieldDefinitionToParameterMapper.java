package no.nav.foreldrepenger.graphql.codegen.mapper;

import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.graphql.codegen.model.MappingConfigConstants;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.ParameterDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.ProjectionParameterDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.builders.DeprecatedDefinitionBuilder;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

/**
 * Mapper from GraphQL's FieldDefinition to a Freemarker-understandable format
 *
 * @author kobylynskyi
 */
public class FieldDefinitionToParameterMapper {

    private final GraphQLTypeMapper graphQLTypeMapper;
    private final DataModelMapper dataModelMapper;
    private final AnnotationsMapper annotationsMapper;

    public FieldDefinitionToParameterMapper(MapperFactory mapperFactory) {
        this.graphQLTypeMapper = mapperFactory.getGraphQLTypeMapper();
        this.dataModelMapper = mapperFactory.getDataModelMapper();
        this.annotationsMapper = mapperFactory.getAnnotationsMapper();
    }

    /**
     * Map field definition to a Freemarker-understandable data model type
     *
     * @param mappingContext   Global mapping context
     * @param fieldDefinitions List of GraphQL field definitions
     * @param parentDefinition Parent GraphQL definition
     * @return Freemarker data model of the GraphQL field definition
     */
    public List<ParameterDefinition> mapFields(MappingContext mappingContext,
                                               List<ExtendedFieldDefinition> fieldDefinitions,
                                               ExtendedDefinition<?, ?> parentDefinition) {
        return fieldDefinitions.stream()
                .map(fieldDef -> mapField(mappingContext, fieldDef, parentDefinition))
                .toList();
    }

    /**
     * Map field definition to a Freemarker-understandable data model type
     *
     * @param mappingContext       Global mapping context
     * @param fieldDefinitions     List of GraphQL field definitions
     * @param parentTypeDefinition Parent GraphQL type definition
     * @return Freemarker data model of the GraphQL field definition
     */
    public List<ProjectionParameterDefinition> mapProjectionFields(MappingContext mappingContext,
                                                                   List<ExtendedFieldDefinition> fieldDefinitions,
                                                                   ExtendedDefinition<?, ?> parentTypeDefinition) {
        return fieldDefinitions.stream()
                .map(fieldDef -> mapProjectionField(mappingContext, fieldDef, parentTypeDefinition))
                .toList();
    }

    /**
     * Map GraphQL's FieldDefinition to a Freemarker-understandable format of parameter
     *
     * @param mappingContext   Global mapping context
     * @param fieldDef         GraphQL field definition
     * @param parentDefinition Parent GraphQL definition
     * @return Freemarker-understandable format of parameter (field)
     */
    private ParameterDefinition mapField(MappingContext mappingContext,
                                         ExtendedFieldDefinition fieldDef,
                                         ExtendedDefinition<?, ?> parentDefinition) {
        var namedDefinition = graphQLTypeMapper
                .getLanguageType(mappingContext, fieldDef.getType(), fieldDef.getName(), parentDefinition.getName());

        var parameter = new ParameterDefinition();
        parameter.setName(dataModelMapper.capitalizeIfRestricted(fieldDef.getName()));
        parameter.setOriginalName(fieldDef.getName());
        parameter.setType(graphQLTypeMapper.getTypeConsideringPrimitive(mappingContext, namedDefinition,
                namedDefinition.getJavaName()));
        parameter.setAnnotations(annotationsMapper.getAnnotations(
                mappingContext, fieldDef.getType(), fieldDef, false));
        parameter.setJavaDoc(fieldDef.getJavaDoc());
        parameter.setDeprecated(DeprecatedDefinitionBuilder.build(fieldDef));
        parameter.setMandatory(namedDefinition.isMandatory());
        parameter.setGetterMethodName(dataModelMapper.capitalizeMethodNameIfRestricted(
            "get" + Utils.capitalize(fieldDef.getName())));

        parameter.setInputParameters(Collections.emptyList());
        return parameter;
    }

    /**
     * Map GraphQL's FieldDefinition to a Freemarker-understandable format of parameter
     *
     * @param mappingContext Global mapping context
     * @param fieldDef       GraphQL field definition
     * @param parentTypeDef  GraphQL definition which is a parent to provided field definition
     * @return Freemarker-understandable format of parameter (field)
     */
    private ProjectionParameterDefinition mapProjectionField(MappingContext mappingContext,
                                                             ExtendedFieldDefinition fieldDef,
                                                             ExtendedDefinition<?, ?> parentTypeDef) {
        var name = fieldDef.getName();
        var methodName = dataModelMapper.capitalizeMethodNameIfRestricted(name);
        var nestedType = GraphQLTypeMapper.getNestedTypeName(fieldDef.getType());
        String type = mappingContext.getTypesUnionsInterfacesNames().contains(nestedType)
                ? Utils.capitalize(nestedType + MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_SUFFIX)
                : null;
        String parametrizedInputClassName = !Utils.isEmpty(fieldDef.getInputValueDefinitions())
                ? DataModelMapper.getParametrizedInputClassName(fieldDef, parentTypeDef)
                : null;
        var deprecated = DeprecatedDefinitionBuilder.build(fieldDef);
        return new ProjectionParameterDefinition(type, name, methodName, deprecated, parametrizedInputClassName);
    }

}
