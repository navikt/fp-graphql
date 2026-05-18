package no.nav.foreldrepenger.graphql.codegen.mapper;

import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.ANNOTATIONS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.BUILDER;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.FIELDS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.JAVA_DOC;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.PACKAGE;

import java.util.HashMap;
import java.util.Map;

import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.builders.JavaDocBuilder;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedInputObjectTypeDefinition;

/**
 * Map input type definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class InputDefinitionToDataModelMapper {

    private final AnnotationsMapper annotationsMapper;
    private final DataModelMapper dataModelMapper;
    private final InputValueDefinitionToParameterMapper inputValueDefinitionToParameterMapper;

    public InputDefinitionToDataModelMapper(MapperFactory mapperFactory,
                                            InputValueDefinitionToParameterMapper inputValueDefToParamMapper) {
        this.annotationsMapper = mapperFactory.getAnnotationsMapper();
        this.dataModelMapper = mapperFactory.getDataModelMapper();
        this.inputValueDefinitionToParameterMapper = inputValueDefToParamMapper;
    }

    /**
     * Map input type definition to a Freemarker data model
     *
     * @param mappingContext Global mapping context
     * @param definition     Definition of input type including base definition and its extensions
     * @return Freemarker data model of the GraphQL type
     */
    public Map<String, Object> map(MappingContext mappingContext, ExtendedInputObjectTypeDefinition definition) {
        var fields = inputValueDefinitionToParameterMapper
                .map(mappingContext, definition.getValueDefinitions(), definition.getName());

        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, dataModelMapper.getModelClassNameWithPrefixAndSuffix(definition));
        dataModel.put(FIELDS, fields);
        dataModel.put(ANNOTATIONS, annotationsMapper.getAnnotations(mappingContext, definition));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(JAVA_DOC, JavaDocBuilder.build(definition));
        return dataModel;
    }

}
