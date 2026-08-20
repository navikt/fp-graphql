package no.nav.foreldrepenger.graphql.codegen.model;

import java.util.Map;

/**
 * Defines all properties that should be parsed in the plugin.
 */
public interface GraphQLCodegenConfiguration {

    /**
     * Can be used to supply custom mappings for scalars.
     *
     * <p>Supports:
     * <ul>
     *   <li>Map of (GraphqlObjectName.fieldName) to (JavaType)</li>
     *   <li>Map of (GraphqlType) to (JavaType)</li>
     * </ul>
     *
     * <p>E.g.:
     * <ul>
     *   <li>{@code DateTime --- String}</li>
     *   <li>{@code Price.amount --- java.math.BigDecimal}</li>
     * </ul>
     *
     * @return mappings from GraphqlType to JavaType
     */
    Map<String, String> getCustomTypesMapping();

    /**
     * Java package for generated classes.
     *
     * @return Java package for generated classes.
     */
    String getPackageName();

    /**
     * Java package for generated model classes (type, input, interface, enum, union).
     *
     * @return Java package for generated model classes.
     */
    String getModelPackageName();

    /**
     * Annotation for mandatory (NonNull) fields. Can be null/empty.
     *
     * @return Annotation for mandatory (NonNull) fields
     */
    String getModelValidationAnnotation();

    /**
     * Specifies whether generated model classes should have builder.
     *
     * @return <b>true</b> if generated model classes should have builder.
     */
    Boolean getGenerateBuilder();

    /**
     * Specifies whether generated union interfaces should be annotated with a Jackson type id resolver generated in
     * model package.
     *
     * @return <b>true</b> if union interfaces should be annotated with a Jackson type id resolver generated in model
     * package
     */
    Boolean getGenerateJacksonTypeIdResolver();

}
