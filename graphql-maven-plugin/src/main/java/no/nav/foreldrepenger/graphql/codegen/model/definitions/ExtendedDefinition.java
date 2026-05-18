package no.nav.foreldrepenger.graphql.codegen.model.definitions;

import java.util.ArrayList;
import java.util.List;

import graphql.language.NamedNode;

/**
 * Base class for all GraphQL definition types that contains base definition and its extensions
 *
 * @param <T> base type
 * @param <E> extension type
 */
public abstract sealed class ExtendedDefinition<T extends NamedNode<T>, E extends T>
        permits ExtendedImplementingTypeDefinition, ExtendedInputObjectTypeDefinition,
                ExtendedEnumTypeDefinition, ExtendedScalarTypeDefinition, ExtendedUnionTypeDefinition {

    /**
     * Nullable because some schemas can have just "extends"
     */
    protected T definition;
    protected List<E> extensions = new ArrayList<>();

    public String getName() {
        if (definition != null) {
            return definition.getName();
        } else {
            return extensions.stream().map(NamedNode::getName).findFirst().orElse(null);
        }
    }

    public T getDefinition() {
        return definition;
    }

    public void setDefinition(T definition) {
        this.definition = definition;
    }

    public List<E> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<E> extensions) {
        this.extensions = extensions;
    }
}
