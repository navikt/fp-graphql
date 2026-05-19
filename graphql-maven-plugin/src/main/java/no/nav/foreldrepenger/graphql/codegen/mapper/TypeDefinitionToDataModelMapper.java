package no.nav.foreldrepenger.graphql.codegen.mapper;

import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.ANNOTATIONS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.BUILDER;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.FIELDS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.IMPLEMENTS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.JAVA_DOC;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.PACKAGE;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.ParameterDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.builders.JavaDocBuilder;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedDocument;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

/**
 * Map type definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class TypeDefinitionToDataModelMapper {

    private final GraphQLTypeMapper graphQLTypeMapper;
    private final AnnotationsMapper annotationsMapper;
    private final DataModelMapper dataModelMapper;
    private final FieldDefinitionToParameterMapper fieldDefinitionToParameterMapper;

    public TypeDefinitionToDataModelMapper(MapperFactory mapperFactory,
                                           FieldDefinitionToParameterMapper fieldDefinitionToParameterMapper) {
        this.graphQLTypeMapper = mapperFactory.getGraphQLTypeMapper();
        this.annotationsMapper = mapperFactory.getAnnotationsMapper();
        this.dataModelMapper = mapperFactory.getDataModelMapper();
        this.fieldDefinitionToParameterMapper = fieldDefinitionToParameterMapper;
    }

    /**
     * Merge parameter definition data from the type and interface
     * Annotations from the type have higher precedence
     *
     * @param typeDef      Definition of the same parameter from the type
     * @param interfaceDef Definition of the same parameter from the interface
     * @return merged parameter definition
     */
    private static ParameterDefinition merge(ParameterDefinition typeDef, ParameterDefinition interfaceDef) {
        typeDef.setDefinitionInParentType(interfaceDef);
        if (Utils.isEmpty(typeDef.getAnnotations())) {
            typeDef.setAnnotations(interfaceDef.getAnnotations());
        }
        return typeDef;
    }

    /**
     * Map type definition to a Freemarker data model
     *
     * @param mappingContext Global mapping context
     * @param definition     Definition of object type including base definition and its extensions
     * @return Freemarker data model of the GraphQL type
     */
    public Map<String, Object> map(MappingContext mappingContext,
                                   ExtendedObjectTypeDefinition definition) {
        var document = mappingContext.getDocument();

        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, dataModelMapper.getModelClassNameWithPrefixAndSuffix(definition));
        dataModel.put(IMPLEMENTS, getInterfaces(mappingContext, definition));
        dataModel.put(ANNOTATIONS, annotationsMapper.getAnnotations(mappingContext, definition));
        dataModel.put(FIELDS, getFields(mappingContext, definition, document));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(JAVA_DOC, JavaDocBuilder.build(definition));
        return dataModel;
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingContext Global mapping context
     * @param typeDefinition GraphQL type definition
     * @param document       Parent GraphQL document
     * @return Freemarker data model of the GraphQL type
     */
    private Collection<ParameterDefinition> getFields(MappingContext mappingContext,
                                                      ExtendedObjectTypeDefinition typeDefinition,
                                                      ExtendedDocument document) {
        // using the map to exclude duplicate fields from the type and interfaces
        Map<String, ParameterDefinition> allParameters = new LinkedHashMap<>();

        // includes parameters from the base definition and extensions
        fieldDefinitionToParameterMapper.mapFields(mappingContext, typeDefinition.getFieldDefinitions(), typeDefinition)
                .forEach(p -> allParameters.put(p.getName(), p));
        // includes parameters from the interface
        DataModelMapper.getInterfacesOfType(typeDefinition, document).stream()
                .map(i -> fieldDefinitionToParameterMapper.mapFields(mappingContext, i.getFieldDefinitions(), i))
                .flatMap(Collection::stream)
                .forEach(paramDef -> allParameters
                        .merge(paramDef.getName(), paramDef, TypeDefinitionToDataModelMapper::merge));

        return allParameters.values();
    }

    private Set<String> getInterfaces(MappingContext mappingContext,
                                      ExtendedObjectTypeDefinition definition) {
        var unionsNames = mappingContext.getDocument().getUnionDefinitions()
                .stream()
                .filter(union -> union.isDefinitionPartOfUnion(definition))
                .map(ExtendedUnionTypeDefinition::getName)
                .map(DataModelMapper::getModelClassNameWithPrefixAndSuffix)
                .toList();
        var interfaceNames = definition.getImplements()
                .stream()
                .map(anImplement -> graphQLTypeMapper.getLanguageType(mappingContext, anImplement))
                .collect(Collectors.toSet());

        Set<String> allInterfaces = new LinkedHashSet<>();
        allInterfaces.addAll(unionsNames);
        allInterfaces.addAll(interfaceNames);
        return allInterfaces;
    }

}
