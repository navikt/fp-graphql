package no.nav.foreldrepenger.graphql;

import java.util.List;
import java.util.Map;

/**
 * A record describing a GraphQL error
 */
public record GraphQLError(
        String message,
        List<GraphQLErrorSourceLocation> locations,
        GraphQLErrorType errorType,
        List<Object> path,
        Map<String, Object> extensions) {
}
