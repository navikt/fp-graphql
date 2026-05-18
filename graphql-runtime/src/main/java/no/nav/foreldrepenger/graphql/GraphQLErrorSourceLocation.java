package no.nav.foreldrepenger.graphql;

/**
 * Source location of GraphQL error in the schema
 */
public record GraphQLErrorSourceLocation(int line, int column, String sourceName) {
}
