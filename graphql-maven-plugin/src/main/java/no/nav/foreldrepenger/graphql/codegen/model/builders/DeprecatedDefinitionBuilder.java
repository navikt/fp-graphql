package no.nav.foreldrepenger.graphql.codegen.model.builders;

import graphql.language.DirectivesContainer;
import no.nav.foreldrepenger.graphql.codegen.model.DeprecatedDefinition;

@SuppressWarnings({"java:S1133", "java:S1123"})
public class DeprecatedDefinitionBuilder {

    private static final String JAVA_ANNOTATION = "Deprecated";

    private DeprecatedDefinitionBuilder() {
    }

    public static DeprecatedDefinition build(DirectivesContainer<?> directivesContainer) {
        for (var d : directivesContainer.getDirectives()) {
            if (d.getName().equalsIgnoreCase(Deprecated.class.getSimpleName())) {
                return new DeprecatedDefinition(JAVA_ANNOTATION);
            }
        }
        return null;
    }

}
