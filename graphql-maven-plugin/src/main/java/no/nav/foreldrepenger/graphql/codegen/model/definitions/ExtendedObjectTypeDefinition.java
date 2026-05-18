package no.nav.foreldrepenger.graphql.codegen.model.definitions;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import graphql.language.ObjectTypeDefinition;
import graphql.language.ObjectTypeExtensionDefinition;

/**
 * Extended definition of GraphQL object type: based definition + its extensions
 */
public final class ExtendedObjectTypeDefinition
        extends ExtendedImplementingTypeDefinition<ObjectTypeDefinition, ObjectTypeExtensionDefinition> {

    /**
     * Get fields with extended information of the given object
     *
     * @return List of field definitions
     */
    public List<ExtendedFieldDefinition> getFieldDefinitions() {
        var baseFields = definition != null
                ? definition.getFieldDefinitions().stream().map(ExtendedFieldDefinition::new)
                : Stream.<ExtendedFieldDefinition>empty();
        var extensionFields = extensions.stream()
                .map(ObjectTypeExtensionDefinition::getFieldDefinitions)
                .flatMap(Collection::stream)
                .map(ExtendedFieldDefinition::new);
        return Stream.concat(baseFields, extensionFields).toList();
    }

}
