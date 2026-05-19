package no.nav.foreldrepenger.graphql.codegen.model.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import graphql.language.AbstractDescribedNode;
import graphql.language.Comment;
import graphql.language.Description;
import graphql.language.NamedNode;
import graphql.language.Node;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedDefinition;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

public class JavaDocBuilder {

    private JavaDocBuilder() {
    }

    public static <T extends NamedNode<T>, E extends T> List<String> build(
            ExtendedDefinition<T, E> extendedDefinition) {
        var javaDocFromDescription = buildFromDescription(extendedDefinition);
        if (javaDocFromDescription.isEmpty()) {
            return buildFromComments(extendedDefinition);
        }
        return javaDocFromDescription;
    }

    public static <T extends NamedNode<T>, E extends T> List<String> buildFromDescription(
            ExtendedDefinition<T, E> extendedDefinition) {
        var definition = extendedDefinition.getDefinition();
        var extensions = extendedDefinition.getExtensions();

        List<String> descriptions = new ArrayList<>();
        if (definition instanceof AbstractDescribedNode) {
            var description = ((AbstractDescribedNode<?>) definition).getDescription();
            if (description != null && Utils.isNotBlank(description.getContent())) {
                descriptions.add(description.getContent().trim());
            }
            extensions.stream()
                    .filter(Objects::nonNull)
                    .map(AbstractDescribedNode.class::cast)
                    .map(AbstractDescribedNode::getDescription).filter(Objects::nonNull)
                    .map(Description::getContent).filter(Utils::isNotBlank)
                    .map(String::trim).forEach(descriptions::add);
        }
        return descriptions;
    }

    public static <T extends NamedNode<T>, E extends T> List<String> buildFromComments(
            ExtendedDefinition<T, E> extendedDefinition) {
        var definition = extendedDefinition.getDefinition();
        var extensions = extendedDefinition.getExtensions();

        List<String> comments = new ArrayList<>();
        if (definition != null) {
            definition.getComments().stream()
                .map(Comment::getContent).filter(Utils::isNotBlank)
                .map(String::trim).forEach(comments::add);
        }
        extensions.stream()
                .map(Node::getComments)
                .flatMap(Collection::stream).filter(Objects::nonNull)
                .map(Comment::getContent).filter(Utils::isNotBlank)
                .map(String::trim).forEach(comments::add);
        return comments;
    }
}
