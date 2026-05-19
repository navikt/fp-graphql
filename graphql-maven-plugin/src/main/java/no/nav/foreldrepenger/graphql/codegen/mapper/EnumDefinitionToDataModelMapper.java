package no.nav.foreldrepenger.graphql.codegen.mapper;

import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.ANNOTATIONS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.FIELDS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.IMPLEMENTS;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.JAVA_DOC;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.PACKAGE;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import graphql.language.Comment;
import no.nav.foreldrepenger.graphql.codegen.model.EnumValueDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.builders.DeprecatedDefinitionBuilder;
import no.nav.foreldrepenger.graphql.codegen.model.builders.JavaDocBuilder;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedEnumTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

/**
 * Map enum definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class EnumDefinitionToDataModelMapper {

    private final AnnotationsMapper annotationsMapper;
    private final DataModelMapper dataModelMapper;

    public EnumDefinitionToDataModelMapper(MapperFactory mapperFactory) {
        this.annotationsMapper = mapperFactory.getAnnotationsMapper();
        this.dataModelMapper = mapperFactory.getDataModelMapper();
    }

    private static Set<String> getUnionInterfaces(MappingContext mappingContext,
                                                  ExtendedEnumTypeDefinition definition) {
        return mappingContext.getDocument().getUnionDefinitions().stream()
                .filter(union -> union.isDefinitionPartOfUnion(definition))
                .map(ExtendedUnionTypeDefinition::getName)
                .map(DataModelMapper::getModelClassNameWithPrefixAndSuffix)
                .collect(Collectors.toSet());
    }

    /**
     * Map field definition to a Freemarker data model
     *
     * @param mappingContext Global mapping context
     * @param definition     Definition of enum type including base definition and its extensions
     * @return Freemarker data model of the GraphQL enum
     */
    public Map<String, Object> map(MappingContext mappingContext, ExtendedEnumTypeDefinition definition) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, dataModelMapper.getModelClassNameWithPrefixAndSuffix(definition));
        dataModel.put(IMPLEMENTS, getUnionInterfaces(mappingContext, definition));
        dataModel.put(ANNOTATIONS, annotationsMapper.getAnnotations(mappingContext, definition));
        dataModel.put(FIELDS, map(definition.getValueDefinitions()));
        dataModel.put(JAVA_DOC, JavaDocBuilder.build(definition));
        return dataModel;
    }

    /**
     * Mapper from GraphQL's EnumValueDefinition to a Freemarker-understandable format
     *
     * @param def list of GraphQL EnumValueDefinition types
     * @return list of strings
     */
    private static List<String> getJavaDoc(graphql.language.EnumValueDefinition def) {
        if (def.getDescription() != null) {
            return Collections.singletonList(def.getDescription().getContent());
        }
        return def.getComments().stream()
                .map(Comment::getContent).filter(Utils::isNotBlank)
                .map(String::trim)
                .toList();
    }

    private List<EnumValueDefinition> map(List<graphql.language.EnumValueDefinition> enumValueDefinitions) {
        return enumValueDefinitions.stream()
                .map(f -> new EnumValueDefinition(
                        dataModelMapper.capitalizeIfRestricted(f.getName()),
                        f.getName(),
                        getJavaDoc(f),
                        DeprecatedDefinitionBuilder.build(f)))
                .toList();
    }

}
