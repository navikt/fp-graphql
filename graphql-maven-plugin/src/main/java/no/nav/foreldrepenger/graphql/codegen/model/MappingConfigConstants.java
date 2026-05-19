package no.nav.foreldrepenger.graphql.codegen.model;

/**
 * Various constants used in code generation process
 * Constants with "_STRING" suffix are used by plugins
 */
public class MappingConfigConstants {

    public static final String DEFAULT_VALIDATION_ANNOTATION = "jakarta.validation.constraints.NotNull";

    public static final boolean DEFAULT_BUILDER = true;

    public static final boolean DEFAULT_GENERATE_JACKSON_TYPE_ID_RESOLVER = false;

    public static final boolean DEFAULT_GENERATE_JACKSON3 = false;

    public static final String DEFAULT_REQUEST_SUFFIX = "Request";
    public static final String DEFAULT_RESPONSE_SUFFIX = "Response";
    public static final String DEFAULT_RESPONSE_PROJECTION_SUFFIX = "ResponseProjection";
    public static final String DEFAULT_PARAMETRIZED_INPUT_SUFFIX = "ParametrizedInput";

    private MappingConfigConstants() {
    }
}
