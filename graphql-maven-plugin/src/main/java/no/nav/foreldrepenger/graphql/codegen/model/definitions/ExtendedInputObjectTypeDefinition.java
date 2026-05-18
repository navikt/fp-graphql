package no.nav.foreldrepenger.graphql.codegen.model.definitions;

import java.util.List;
import java.util.stream.Stream;

import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputObjectTypeExtensionDefinition;
import graphql.language.InputValueDefinition;

/**
 * Extended definition of GraphQL input type: based definition + its extensions
 */
public final class ExtendedInputObjectTypeDefinition
        extends ExtendedDefinition<InputObjectTypeDefinition, InputObjectTypeExtensionDefinition> {

    public List<InputValueDefinition> getValueDefinitions() {
        var baseDefs = definition != null
                ? definition.getInputValueDefinitions().stream()
                : Stream.<InputValueDefinition>empty();
        var extensionDefs = extensions.stream()
                .map(InputObjectTypeDefinition::getInputValueDefinitions)
                .flatMap(List::stream);
        return Stream.concat(baseDefs, extensionDefs).toList();
    }
}
