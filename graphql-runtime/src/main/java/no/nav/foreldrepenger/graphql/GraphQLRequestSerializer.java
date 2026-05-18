package no.nav.foreldrepenger.graphql;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Serializer of GraphQL request.
 * Provides ability to convert GraphQLRequest object to HTTP Json body, as well as to raw query string.
 */
public class GraphQLRequestSerializer {

    private GraphQLRequestSerializer() {
    }


    /**
     * Serializes GraphQL request as raw query string
     *
     * @param graphQLRequest the GraphQL request to serialize
     * @return the serialized request
     */
    public static String toQueryString(GraphQLRequest graphQLRequest) {
        if (graphQLRequest == null || graphQLRequest.getRequest() == null) {
            return null;
        }

        var operationName = graphQLRequest.getOperationName() == null ?
                graphQLRequest.getRequest().getOperationName() : graphQLRequest.getOperationName();

        return operationWrapper(
                graphQLRequest.getRequest().getOperationType(),
                operationName,
                buildQuery(graphQLRequest));
    }

    private static String operationWrapper(GraphQLOperation operationType, String operationName, String queryValue) {
        if (operationType == null) {
            throw new IllegalArgumentException("GraphQL operation type must not be null");
        }
        var operationTypeLowerCased = operationType.name().toLowerCase();
        if (operationName == null) {
            return "%s { %s }".formatted(operationTypeLowerCased, queryValue);
        } else {
            return "%s %s { %s }".formatted(operationTypeLowerCased, operationName, queryValue);
        }
    }

    private static String buildQuery(GraphQLRequest graphQLRequest) {
        var builder = new StringBuilder();
        var request = graphQLRequest.getRequest();
        if (request.getAlias() != null) {
            builder.append(request.getAlias()).append(": ");
        }
        builder.append(request.getOperationName());
        var input = request.getInput();
        if (requestHasInput(input)) {
            var args = input.entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .map(e -> e.getKey() + ": " + getEntry(e.getValue()))
                    .collect(Collectors.joining(", "));
            builder.append("(").append(args).append(")");
        }
        if (graphQLRequest.getResponseProjection() != null) {
            builder.append(graphQLRequest.getResponseProjection().toString());
        }
        return builder.toString();
    }

    private static boolean requestHasInput(Map<String, Object> input) {
        return input != null && !input.isEmpty() &&
                input.values().stream().anyMatch(Objects::nonNull);
    }

    /**
     * Serialize object to a string.
     *
     * @param input can be any object or collection/map of objects.
     * @return serialized object
     */
    @SuppressWarnings("java:S1872")
    public static String getEntry(Object input) {
        return switch (input) {
            case null -> null;
            case Collection<?> objects -> serializeCollection(objects);
            case Map<?, ?> map -> serializeMap(map);
            case Map.Entry<?, ?> entry -> serializeMapEntry(entry);
            case Enum<?> anEnum -> serializeEnum(anEnum);
            case String s -> escapeJsonString(s);
            case Temporal t -> escapeJsonString(t.toString());
            case TemporalAmount ta -> escapeJsonString(ta.toString());
            default -> input.toString();
        };
    }

    public static String serializeCollection(Collection<?> input) {
        var joiner = new StringJoiner(", ", "[ ", " ]");
        for (var entry : input) {
            joiner.add(getEntry(entry));
        }
        return joiner.toString();
    }

    public static String serializeMap(Map<?, ?> input) {
        var joiner = new StringJoiner(", ", "{ ", " }");
        for (var entry : input.entrySet()) {
            joiner.add(getEntry(entry));
        }
        return joiner.toString();
    }

    public static String serializeMapEntry(Map.Entry<?, ?> input) {
        return input.getKey() + ": " + getEntry(input.getValue());
    }

    public static String serializeEnum(Enum<?> input) {
        return input.toString();
    }

    /**
     * Encodes the value as a JSON string according to http://json.org/ rules
     *
     * @param stringValue the value to encode as a JSON string
     * @return the encoded string
     */
    public static String escapeJsonString(String stringValue) {
        var len = stringValue.length();
        var sb = new StringBuilder(len + 2);
        sb.append("\"");
        for (var i = 0; i < len; i++) {
            var ch = stringValue.charAt(i);
            switch (ch) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(ch);
            }
        }
        sb.append("\"");
        return sb.toString();
    }

}
