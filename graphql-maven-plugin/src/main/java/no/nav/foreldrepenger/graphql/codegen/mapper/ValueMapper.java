package no.nav.foreldrepenger.graphql.codegen.mapper;

import java.util.Collections;

import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.EnumValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.NullValue;
import graphql.language.ObjectValue;
import graphql.language.StringValue;
import graphql.language.Type;
import graphql.language.TypeName;
import graphql.language.Value;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;

/**
 * A class for mapping GraphQL value to a java value
 */
public class ValueMapper {

    private static final String NULL_STRING = "null";

    private final DataModelMapper dataModelMapper;

    public ValueMapper(DataModelMapper dataModelMapper) {
        this.dataModelMapper = dataModelMapper;
    }

    private static String mapBoolean(BooleanValue value) {
        return value.isValue() ? "true" : "false";
    }

    /**
     * Map value of GraphQL Int type to a value that will be present in a generated code.
     *
     * @param mappingContext Global mapping context
     * @param value          GraphQL Value
     * @param graphQLType    GraphQL Type
     * @return formatted value
     */
    private static String mapInt(MappingContext mappingContext, IntValue value, Type<?> graphQLType) {
        // default java basic type is `int`. so, default value like 123 that must wrap or append suffix `L` when it be
        // defined as `int` in graphql schema.
        // `int` cannot assign to `Long`, also `double` cannot assign to `Float`, but graphql Float default mapping is
        //  Double in java, so, not modify `mapFloat`.
        if (graphQLType instanceof TypeName tn) {
            var customType = mappingContext.getCustomTypesMapping().get("Long");
            var typeName = tn.getName();
            if ("Long".equals(typeName) && ("java.lang.Long".equals(customType) || "Long".equals(customType))) {
                return String.valueOf(value.getValue()).concat("L");
            }
        }

        if (graphQLType instanceof NonNullType nnt) {
            // unwrapping NonNullType
            return mapInt(mappingContext, value, nnt.getType());
        }
        return String.valueOf(value.getValue());
    }

    private static String mapFloat(FloatValue value) {
        return String.valueOf(value.getValue());
    }

    private static String mapString(StringValue value) {
        return "\"" + value.getValue() + "\"";
    }

    public String map(MappingContext mappingContext, Value<?> value, Type<?> graphQLType) {
        return map(mappingContext, value, graphQLType, null);
    }

    /**
     * Map GraphQL value of a given type according to a formatter
     *
     * @param mappingContext Global mapping context
     * @param value          GraphQL Value
     * @param graphQLType    GraphQL Type
     * @param formatter      value formatter
     * @return formatted value
     */
    public String map(MappingContext mappingContext, Value<?> value, Type<?> graphQLType,
                      String formatter) {
        return switch (value) {
            case null -> null;
            case NullValue _ -> ValueFormatter.format(NULL_STRING, formatter);
            case BooleanValue booleanValue -> ValueFormatter.format(mapBoolean(booleanValue), formatter);
            case IntValue intValue -> ValueFormatter.format(mapInt(mappingContext, intValue, graphQLType), formatter);
            case FloatValue floatValue -> ValueFormatter.format(mapFloat(floatValue), formatter);
            case StringValue stringValue -> ValueFormatter.format(mapString(stringValue), formatter);
            case EnumValue enumValue -> ValueFormatter.format(mapEnum(mappingContext, enumValue, graphQLType), formatter);
            case ObjectValue _ -> null; // default object values are not supported yet
            case ArrayValue arrayValue -> mapArray(mappingContext, arrayValue, graphQLType, formatter);
            default -> null;
        };
    }

    private String mapEnum(MappingContext mappingContext, EnumValue value, Type<?> graphQLType) {
        return switch (graphQLType) {
            case null -> {
                var typeName = value.getName();
                yield mappingContext.getCustomTypesMapping().getOrDefault(typeName, typeName);
            }
            case TypeName tn -> {
                var typeName = mappingContext.getCustomTypesMapping().getOrDefault(tn.getName(),
                        DataModelMapper.getModelClassNameWithPrefixAndSuffix(tn.getName()));
                yield typeName + "." + dataModelMapper.capitalizeIfRestricted(value.getName());
            }
            case NonNullType nnt -> mapEnum(mappingContext, value, nnt.getType());
            default -> throw new IllegalArgumentException("Unexpected Enum value for list type");
        };
    }

    @SuppressWarnings({"rawtypes", "java:S3740"})
    private String mapArray(MappingContext mappingContext, ArrayValue value, Type<?> graphQLType,
                            String formatter) {
        if (graphQLType == null || graphQLType instanceof ListType) {
            var values = value.getValues();
            if (values.isEmpty()) {
                return ValueFormatter.formatList(Collections.emptyList(), formatter);
            }
            Type<?> elementType = null;
            if (graphQLType != null) {
                elementType = ((ListType) graphQLType).getType();
            }
            var listElementType = elementType;
            return ValueFormatter.formatList(values.stream()
                    .map(v -> map(mappingContext, v, listElementType, formatter))
                    .toList(), formatter);
        }
        if (graphQLType instanceof NonNullType nnt) {
            return mapArray(mappingContext, value, nnt.getType(), formatter);
        }
        throw new IllegalArgumentException("Unexpected array default value for non-list type");
    }

}
