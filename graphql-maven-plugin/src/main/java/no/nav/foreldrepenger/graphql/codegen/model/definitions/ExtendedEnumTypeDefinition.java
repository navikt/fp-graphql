package no.nav.foreldrepenger.graphql.codegen.model.definitions;

import java.util.List;
import java.util.stream.Stream;

import graphql.language.EnumTypeDefinition;
import graphql.language.EnumTypeExtensionDefinition;
import graphql.language.EnumValueDefinition;

/**
 * Extended definition of GraphQL enum type: based definition + its extensions
 */
public final class ExtendedEnumTypeDefinition extends ExtendedDefinition<EnumTypeDefinition, EnumTypeExtensionDefinition> {

    /**
     * Get enum value definitions from the definition and its extensions
     *
     * @return list of all enum value definitions
     */
    public List<EnumValueDefinition> getValueDefinitions() {
        var baseDefs = definition != null
                ? definition.getEnumValueDefinitions().stream()
                : Stream.<EnumValueDefinition>empty();
        var extensionDefs = extensions.stream()
                .map(EnumTypeExtensionDefinition::getEnumValueDefinitions)
                .flatMap(List::stream);
        return Stream.concat(baseDefs, extensionDefs).toList();
    }
}
