package no.nav.foreldrepenger.graphql.codegen.mapper;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.Type;
import graphql.language.TypeName;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.NamedDefinition;

/**
 * Map GraphQL type to language-specific type (java)
 *
 * @author kobylynskyi
 */
public class GraphQLTypeMapper {

    private static final String JAVA_UTIL_LIST = "java.util.List";
    private static final Set<String> JAVA_PRIMITIVE_TYPES = new HashSet<>(asList(
        "byte", "short", "int", "long", "float", "double", "char", "boolean"));

    public static boolean isJavaPrimitive(String possiblyPrimitiveType) {
        return JAVA_PRIMITIVE_TYPES.contains(possiblyPrimitiveType);
    }

    /**
     * Get nested type of GraphQL Type. Example:
     * {@code Event -> Event}
     * {@code Event! -> Event}
     * {@code [Event!]! -> Event}
     * {@code [[Event]] -> Event}
     *
     * @param graphqlType GraphQL type
     * @return GraphQL type without List/NonNull wrapping
     */
    public static String getNestedTypeName(Type<?> graphqlType) {
        return switch (graphqlType) {
            case TypeName typeName -> typeName.getName();
            case ListType listType -> getNestedTypeName(listType.getType());
            case NonNullType nonNullType -> getNestedTypeName(nonNullType.getType());
            case null, default -> null;
        };
    }

    public static String getMandatoryType(String typeName) {
        return typeName + "!";
    }

    public String wrapIntoList(String type) {
        return getGenericsString(JAVA_UTIL_LIST, type);
    }

    public String wrapSuperTypeIntoList(String type) {
        return getGenericsString(JAVA_UTIL_LIST, "? extends " + type);
    }

    public String wrapApiInputTypeIfRequired(MappingContext mappingContext, NamedDefinition namedDefinition) {
        return getTypeConsideringPrimitive(mappingContext, namedDefinition, namedDefinition.getJavaName());
    }

    public String wrapApiDefaultValueIfRequired(String defaultValue) {
        return defaultValue;
    }

    public boolean isPrimitive(String possiblyPrimitiveType) {
        return isJavaPrimitive(possiblyPrimitiveType);
    }

    /**
     * Wrap string into generics type
     *
     * @param genericType   Generics type
     * @param typeParameter Parameter of generics type
     * @return type wrapped into generics
     */
    public String getGenericsString(String genericType, String typeParameter) {
        if (genericType.contains("%s")) {
            return String.format(genericType, typeParameter);
        } else {
            return String.format("%s<%s>", genericType, typeParameter);
        }
    }

    /**
     * Convert GraphQL type to a corresponding language-specific type
     *
     * @param mappingContext Global mapping context
     * @param type           GraphQL type
     * @return Corresponding language-specific type (java)
     */
    public String getLanguageType(MappingContext mappingContext, Type<?> type) {
        return getLanguageType(mappingContext, type, null, null).getJavaName();
    }

    /**
     * Convert GraphQL type to a corresponding language-specific type (java)
     *
     * @param mappingContext Global mapping context
     * @param graphqlType    GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @return Corresponding language-specific type (java)
     */
    public NamedDefinition getLanguageType(MappingContext mappingContext, Type<?> graphqlType, String name, String parentTypeName) {
        return getLanguageType(mappingContext, graphqlType, name, parentTypeName, false, false);
    }


    /**
     * Convert GraphQL type to a corresponding language-specific type (java)
     *
     * @param mappingContext Global mapping context
     * @param graphqlType    GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @param mandatory      GraphQL type is non-null
     * @param collection     GraphQL type is collection
     * @return Corresponding language-specific type (java)
     */
    public NamedDefinition getLanguageType(MappingContext mappingContext, Type<?> graphqlType,
                                           String name, String parentTypeName,
                                           boolean mandatory, boolean collection) {
        return switch (graphqlType) {
            case TypeName tn -> getLanguageType(mappingContext, tn.getName(), name, parentTypeName, mandatory, collection);
            case ListType listType -> {
                var mappedCollectionType = getLanguageType(mappingContext, listType.getType(),
                        name, parentTypeName, false, true);
                if (mappedCollectionType.isInterfaceOrUnion() && isInterfaceOrUnion(mappingContext, parentTypeName)) {
                    mappedCollectionType.setJavaName(wrapSuperTypeIntoList(mappedCollectionType.getJavaName()));
                } else {
                    mappedCollectionType.setJavaName(wrapIntoList(mappedCollectionType.getJavaName()));
                }
                yield mappedCollectionType;
            }
            case NonNullType nnt -> getLanguageType(mappingContext, nnt.getType(), name, parentTypeName, true, collection);
            case null, default -> throw new IllegalArgumentException("Unknown type: " + graphqlType);
        };
    }

    /**
     * Convert GraphQL type to a corresponding language-specific type (java)
     *
     * @param mappingContext Global mapping context
     * @param graphQLType    GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @param mandatory      GraphQL type is non-null
     * @param collection     GraphQL type is collection
     * @return Corresponding language-specific type (java)
     */
    public NamedDefinition getLanguageType(MappingContext mappingContext, String graphQLType, String name,
                                           String parentTypeName, boolean mandatory, boolean collection) {
        var customTypesMapping = mappingContext.getCustomTypesMapping();
        String langTypeName;
        var primitiveCanBeUsed = !collection;
        if (name != null && parentTypeName != null && customTypesMapping.containsKey(parentTypeName + "." + name)) {
            langTypeName = customTypesMapping.get(parentTypeName + "." + name);
            primitiveCanBeUsed = false;
        } else if (customTypesMapping.containsKey(graphQLType)) {
            langTypeName = customTypesMapping.get(graphQLType);
        } else {
            langTypeName = DataModelMapper.getModelClassNameWithPrefixAndSuffix(graphQLType);
        }

        return new NamedDefinition(langTypeName, graphQLType, isInterfaceOrUnion(mappingContext, graphQLType),
            mandatory, primitiveCanBeUsed);
    }

    public String getTypeConsideringPrimitive(MappingContext mappingContext,
                                              NamedDefinition namedDefinition,
                                              String computedTypeName) {
        var graphqlTypeName = namedDefinition.getGraphqlTypeName();
        if (namedDefinition.isMandatory() && namedDefinition.isPrimitiveCanBeUsed()) {
            var possiblyPrimitiveType = mappingContext.getCustomTypesMapping()
                    .get(getMandatoryType(graphqlTypeName));
            if (isPrimitive(possiblyPrimitiveType)) {
                return possiblyPrimitiveType;
            }
        }
        return computedTypeName;
    }

    public String getResponseReturnType(String computedTypeName) {
        return computedTypeName;
    }

    protected boolean isInterfaceOrUnion(MappingContext mappingContext, String graphQLType) {
        return mappingContext.getInterfacesName().contains(graphQLType) ||
                mappingContext.getUnionsNames().contains(graphQLType);
    }
}
