package no.nav.foreldrepenger.graphql.codegen.mapper;

import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.ANNOTATIONS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.JAVA_DOC;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.PACKAGE;

import java.util.HashMap;
import java.util.Map;

import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.builders.JavaDocBuilder;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;

/**
 * Map union definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class UnionDefinitionToDataModelMapper {

    private final AnnotationsMapper annotationsMapper;
    private final DataModelMapper dataModelMapper;

    public UnionDefinitionToDataModelMapper(MapperFactory mapperFactory) {
        this.annotationsMapper = mapperFactory.getAnnotationsMapper();
        this.dataModelMapper = mapperFactory.getDataModelMapper();
    }

    /**
     * Map union definition to a Freemarker data model
     *
     * @param mappingContext Global mapping context
     * @param definition     Definition of union type including base definition and its extensions
     * @return Freemarker data model of the GraphQL union
     */
    public Map<String, Object> map(MappingContext mappingContext, ExtendedUnionTypeDefinition definition) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, dataModelMapper.getModelClassNameWithPrefixAndSuffix(definition));
        dataModel.put(ANNOTATIONS, annotationsMapper.getAnnotations(mappingContext, definition));
        dataModel.put(JAVA_DOC, JavaDocBuilder.build(definition));
        return dataModel;
    }

}
