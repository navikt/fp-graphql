package no.nav.foreldrepenger.graphql.codegen.model.definitions;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import graphql.language.NamedNode;
import graphql.language.UnionTypeDefinition;
import graphql.language.UnionTypeExtensionDefinition;

/**
 * Extended definition of GraphQL union type: based definition + its extensions
 */
public final class ExtendedUnionTypeDefinition extends ExtendedDefinition<UnionTypeDefinition, UnionTypeExtensionDefinition> {

    private Set<String> memberTypeNames;

    /**
     * Find out if a given definition is part of a union.
     */
    public boolean isDefinitionPartOfUnion(ExtendedDefinition<?, ?> definition) {
        return getMemberTypeNames().contains(definition.getName());
    }

    public Set<String> getMemberTypeNames() {
        if (memberTypeNames == null) {
            var baseMemberNames = definition != null
                    ? definition.getMemberTypes().stream()
                    : Stream.empty();
            var extensionMemberNames = extensions.stream()
                    .map(UnionTypeDefinition::getMemberTypes)
                    .flatMap(Collection::stream);
            memberTypeNames = Stream.concat(baseMemberNames, extensionMemberNames)
                    .map(NamedNode.class::cast)
                    .map(NamedNode::getName)
                    .collect(Collectors.toUnmodifiableSet());
        }
        return memberTypeNames;
    }
}
