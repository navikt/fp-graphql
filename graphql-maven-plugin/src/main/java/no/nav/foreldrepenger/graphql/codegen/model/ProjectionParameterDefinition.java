package no.nav.foreldrepenger.graphql.codegen.model;

/**
 * Freemarker-understandable format of parameter used in ResponseProjection
 */
public record ProjectionParameterDefinition(
        String type,
        String name,
        String methodName,
        DeprecatedDefinition deprecated,
        String parametrizedInputClassName) {
}
