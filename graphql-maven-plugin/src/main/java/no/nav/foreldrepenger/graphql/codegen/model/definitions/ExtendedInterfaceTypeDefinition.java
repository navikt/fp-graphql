package no.nav.foreldrepenger.graphql.codegen.model.definitions;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import graphql.language.InterfaceTypeDefinition;
import graphql.language.InterfaceTypeExtensionDefinition;

/**
 * Extended definition of GraphQL interface type: based definition + its extensions
 */
public final class ExtendedInterfaceTypeDefinition
        extends ExtendedImplementingTypeDefinition<InterfaceTypeDefinition, InterfaceTypeExtensionDefinition> {

    /**
     * Get fields with extended information of the given interface
     *
     * @return List of field definitions
     */
    public List<ExtendedFieldDefinition> getFieldDefinitions() {
        var baseFields = definition != null
                ? definition.getFieldDefinitions().stream().map(ExtendedFieldDefinition::new)
                : Stream.<ExtendedFieldDefinition>empty();
        var extensionFields = extensions.stream()
                .map(InterfaceTypeExtensionDefinition::getFieldDefinitions)
                .flatMap(Collection::stream)
                .map(ExtendedFieldDefinition::new);
        return Stream.concat(baseFields, extensionFields).toList();
    }

}
