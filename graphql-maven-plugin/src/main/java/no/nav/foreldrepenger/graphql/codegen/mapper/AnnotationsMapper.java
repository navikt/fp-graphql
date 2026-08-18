package no.nav.foreldrepenger.graphql.codegen.mapper;

import static no.nav.foreldrepenger.graphql.codegen.mapper.GraphQLTypeMapper.getMandatoryType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import graphql.language.InputValueDefinition;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ListType;
import graphql.language.NamedNode;
import graphql.language.NonNullType;
import graphql.language.Type;
import graphql.language.TypeName;
import graphql.language.UnionTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

/**
 * Mapper for annotations
 */
public class AnnotationsMapper {

    /**
     * Get annotations for a given GraphQL type
     *
     * @param mappingContext Global mapping context
     * @param type           GraphQL type
     * @param def            GraphQL definition
     * @param mandatory      Type is mandatory
     * @return list of Java annotations for a given GraphQL type
     */
    public List<String> getAnnotations(MappingContext mappingContext, Type<?> type, NamedNode<?> def, boolean mandatory) {
        if (type instanceof ListType lt) {
            Type<?> subType = lt.getType();
            return getAnnotations(mappingContext, subType, def, mandatory);
        } else if (type instanceof NonNullType nnt) {
            Type<?> parentType = null;
            if (def instanceof ExtendedFieldDefinition efd) {
                parentType = efd.getType();
            } else if (def instanceof InputValueDefinition ivd) {
                parentType = ivd.getType();
            }
            // if parent is a list, then pass a mandatory flag as is (do not override it)
            if (!(parentType instanceof ListType)) {
                mandatory = true;
            }
            return getAnnotations(mappingContext, nnt.getType(), def, mandatory);
        } else if (type instanceof TypeName tn) {
            return getAnnotations(mappingContext, tn.getName(), mandatory, def);
        }
        return Collections.emptyList();
    }

    public List<String> getAnnotations(MappingContext mappingContext, ExtendedDefinition<?, ?> extendedDefinition) {
        if (extendedDefinition == null) {
            return Collections.emptyList();
        }

        var def = extendedDefinition.getDefinition();
        return getAnnotations(mappingContext, extendedDefinition.getName(), false, def);
    }

    public List<String> getAnnotations(MappingContext mappingContext, String name) {
        return getAnnotations(mappingContext, name, false, null);
    }

    /**
     * Get annotations for a given GraphQL type
     *
     * @param mappingContext  Global mapping context
     * @param graphQLTypeName GraphQL type
     * @param mandatory       Type is mandatory
     * @param def             GraphQL definition
     * @return list of Java annotations for a given GraphQL type
     */
    public List<String> getAnnotations(MappingContext mappingContext, String graphQLTypeName, boolean mandatory, NamedNode<?> def) {
        // 1. Add model validation annotation
        List<String> annotations = new ArrayList<>();
        if (mandatory) {
            var modelValidationAnnotation = getModelValidationAnnotation(mappingContext, graphQLTypeName);
            if (modelValidationAnnotation != null) {
                annotations.add(modelValidationAnnotation);
            }
        }

        // 2. Add Jackson-related annotations
        annotations.addAll(getJacksonTypeIdAnnotations(mappingContext, def));

        return annotations;
    }

    private String getModelValidationAnnotation(MappingContext mappingContext, String graphQLTypeName) {
        var possiblyPrimitiveType = mappingContext.getCustomTypesMapping()
                .get(getMandatoryType(graphQLTypeName));
        var modelValidationAnnotation = mappingContext.getModelValidationAnnotation();
        if (Utils.isNotBlank(modelValidationAnnotation) &&
                addModelValidationAnnotationForType(possiblyPrimitiveType)) {
            return modelValidationAnnotation;
        }
        return null;
    }

    /**
     * Get Jackson type id resolver annotations
     *
     * @param mappingContext Global mapping context
     * @param def            GraphQL definition
     * @return list of Jackson type id resolver annotations
     */
    public List<String> getJacksonTypeIdAnnotations(MappingContext mappingContext, NamedNode<?> def) {
        List<String> defaults = new ArrayList<>();
        if (Boolean.TRUE.equals(mappingContext.getGenerateJacksonTypeIdResolver())
                && (def instanceof UnionTypeDefinition || def instanceof InterfaceTypeDefinition)) {
            defaults.add("com.fasterxml.jackson.annotation.JsonTypeInfo(use = " +
                    "com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = \"__typename\")");
            var modelPackageName = DataModelMapper.getModelPackageName(mappingContext);
            if (modelPackageName == null) {
                modelPackageName = "";
            } else if (Utils.isNotBlank(modelPackageName)) {
                modelPackageName += ".";
            }
            defaults.add(getJacksonResolverTypeIdAnnotation(modelPackageName));
        }
        return defaults;
    }

    public boolean addModelValidationAnnotationForType(String type) {
        return !GraphQLTypeMapper.isJavaPrimitive(type);
    }

    public String getJacksonResolverTypeIdAnnotation(String modelPackageName) {
        return "tools.jackson.databind.annotation.JsonTypeIdResolver(" + modelPackageName + "GraphqlJacksonTypeIdResolver.class)";
    }

}
