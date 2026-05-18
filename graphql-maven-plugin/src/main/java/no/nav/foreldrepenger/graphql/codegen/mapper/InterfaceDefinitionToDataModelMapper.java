package no.nav.foreldrepenger.graphql.codegen.mapper;

import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.ANNOTATIONS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.FIELDS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.IMPLEMENTS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.JAVA_DOC;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.PACKAGE;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.builders.JavaDocBuilder;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;

/**
 * Map interface definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class InterfaceDefinitionToDataModelMapper {

    private final GraphQLTypeMapper graphQLTypeMapper;
    private final AnnotationsMapper annotationsMapper;
    private final DataModelMapper dataModelMapper;
    private final FieldDefinitionToParameterMapper fieldDefinitionToParameterMapper;

    public InterfaceDefinitionToDataModelMapper(MapperFactory mapperFactory,
                                                FieldDefinitionToParameterMapper fieldDefinitionToParameterMapper) {
        this.graphQLTypeMapper = mapperFactory.getGraphQLTypeMapper();
        this.annotationsMapper = mapperFactory.getAnnotationsMapper();
        this.dataModelMapper = mapperFactory.getDataModelMapper();
        this.fieldDefinitionToParameterMapper = fieldDefinitionToParameterMapper;
    }

    /**
     * Map interface definition to a Freemarker data model
     *
     * @param mappingContext Global mapping context
     * @param definition     Definition of interface type including base definition and its extensions
     * @return Freemarker data model of the GraphQL interface
     */
    public Map<String, Object> map(MappingContext mappingContext, ExtendedInterfaceTypeDefinition definition) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, dataModelMapper.getModelClassNameWithPrefixAndSuffix(definition));
        dataModel.put(IMPLEMENTS, getInterfaces(mappingContext, definition));
        dataModel.put(ANNOTATIONS, annotationsMapper.getAnnotations(mappingContext, definition));
        dataModel.put(FIELDS, fieldDefinitionToParameterMapper
                .mapFields(mappingContext, definition.getFieldDefinitions(), definition));
        dataModel.put(JAVA_DOC, JavaDocBuilder.build(definition));
        return dataModel;
    }

    private Set<String> getInterfaces(MappingContext mappingContext,
                                      ExtendedInterfaceTypeDefinition definition) {
        return definition.getImplements()
                .stream()
                .map(anImplement -> graphQLTypeMapper.getLanguageType(mappingContext, anImplement))
                .collect(Collectors.toSet());
    }

}
