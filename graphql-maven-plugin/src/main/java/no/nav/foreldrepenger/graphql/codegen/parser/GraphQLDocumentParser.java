package no.nav.foreldrepenger.graphql.codegen.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.EnumTypeDefinition;
import graphql.language.EnumTypeExtensionDefinition;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputObjectTypeExtensionDefinition;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.InterfaceTypeExtensionDefinition;
import graphql.language.NamedNode;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ObjectTypeExtensionDefinition;
import graphql.language.ScalarTypeDefinition;
import graphql.language.ScalarTypeExtensionDefinition;
import graphql.language.UnionTypeDefinition;
import graphql.language.UnionTypeExtensionDefinition;
import graphql.parser.MultiSourceReader;
import graphql.parser.Parser;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedDocument;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedEnumTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedInputObjectTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedScalarTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

/**
 * Provides methods for extended document generation
 */
public class GraphQLDocumentParser {

    private GraphQLDocumentParser() {
    }

    /**
     * Generate an extended document from schema files
     *
     * @param schemaPaths Paths to GraphQL schema files
     * @return extended document definition
     * @throws IOException in case unable to read the file content
     */
    public static ExtendedDocument getDocumentFromSchemas(List<String> schemaPaths)
            throws IOException {
        var document = readDocument(schemaPaths);

        var extendedDocumentBuilder = new ExtendedDocumentBuilder();

        for (Definition<?> definition : document.getDefinitions()) {
            processDefinition(extendedDocumentBuilder, definition);
        }
        return extendedDocumentBuilder.build();
    }

    private static void processDefinition(ExtendedDocumentBuilder extendedDocumentBuilder,
                                          Definition<?> definition) {
        if (!(definition instanceof NamedNode<?> namedNode)) {
            // the only definition that does not have a name is SchemaDefinition, so skipping it
            return;
        }
        var definitionName = namedNode.getName();

        switch (definition) {
            case ObjectTypeDefinition _ when Utils.isGraphqlOperation(definitionName) ->
                populateDefinition(extendedDocumentBuilder.operationDefinitions, definition, definitionName,
                        ObjectTypeExtensionDefinition.class, _ -> new ExtendedObjectTypeDefinition());
            case ObjectTypeDefinition _ ->
                populateDefinition(extendedDocumentBuilder.typeDefinitions, definition, definitionName,
                        ObjectTypeExtensionDefinition.class, _ -> new ExtendedObjectTypeDefinition());
            case EnumTypeDefinition _ ->
                populateDefinition(extendedDocumentBuilder.enumDefinitions, definition, definitionName,
                        EnumTypeExtensionDefinition.class, _ -> new ExtendedEnumTypeDefinition());
            case InputObjectTypeDefinition _ ->
                populateDefinition(extendedDocumentBuilder.inputDefinitions, definition, definitionName,
                        InputObjectTypeExtensionDefinition.class, _ -> new ExtendedInputObjectTypeDefinition());
            case UnionTypeDefinition _ ->
                populateDefinition(extendedDocumentBuilder.unionDefinitions, definition, definitionName,
                        UnionTypeExtensionDefinition.class, _ -> new ExtendedUnionTypeDefinition());
            case ScalarTypeDefinition _ ->
                populateDefinition(extendedDocumentBuilder.scalarDefinitions, definition, definitionName,
                        ScalarTypeExtensionDefinition.class, _ -> new ExtendedScalarTypeDefinition());
            case InterfaceTypeDefinition _ ->
                populateDefinition(extendedDocumentBuilder.interfaceDefinitions, definition, definitionName,
                        InterfaceTypeExtensionDefinition.class, _ -> new ExtendedInterfaceTypeDefinition());
            default -> { }
        }
    }

    @SuppressWarnings("unchecked")
    private static <
            D extends ExtendedDefinition<B, E>,
            B extends NamedNode<B>,
            E extends B> void populateDefinition(Map<String, D> definitionsMap,
                                                 Definition<?> definition,
                                                 String definitionName,
                                                 Class<E> extensionDefinitionClass,
                                                 Function<String, D> mappingFunction) {
        var extendedDefinition = definitionsMap.computeIfAbsent(definitionName, mappingFunction);
        if (extensionDefinitionClass.isAssignableFrom(definition.getClass())) {
            extendedDefinition.getExtensions().add((E) definition);
        } else {
            extendedDefinition.setDefinition((B) definition);
        }
    }

    private static Document readDocument(List<String> schemaPaths) throws IOException {
        try (var reader = createMultiSourceReader(schemaPaths)) {
            return new Parser().parseDocument(reader);
        }
    }

    public static MultiSourceReader createMultiSourceReader(List<String> schemaPaths) throws IOException {
        if (schemaPaths == null) {
            return MultiSourceReader.newMultiSourceReader().build();
        }
        var builder = MultiSourceReader.newMultiSourceReader();
        for (var path : schemaPaths) {
            // appending EOL to ensure that schema tokens are not mixed in case files are not properly ended with EOL
            var content = Utils.getFileContent(path) + System.lineSeparator();
            builder.string(content, path);
        }
        return builder.trackData(true).build();
    }

    private static class ExtendedDocumentBuilder {

        private final Map<String, ExtendedObjectTypeDefinition> operationDefinitions = new HashMap<>();
        private final Map<String, ExtendedObjectTypeDefinition> typeDefinitions = new HashMap<>();
        private final Map<String, ExtendedInputObjectTypeDefinition> inputDefinitions = new HashMap<>();
        private final Map<String, ExtendedEnumTypeDefinition> enumDefinitions = new HashMap<>();
        private final Map<String, ExtendedScalarTypeDefinition> scalarDefinitions = new HashMap<>();
        private final Map<String, ExtendedInterfaceTypeDefinition> interfaceDefinitions = new HashMap<>();
        private final Map<String, ExtendedUnionTypeDefinition> unionDefinitions = new HashMap<>();

        ExtendedDocument build() {
            return new ExtendedDocument(
                    operationDefinitions.values(),
                    typeDefinitions.values(),
                    inputDefinitions.values(),
                    enumDefinitions.values(),
                    scalarDefinitions.values(),
                    interfaceDefinitions.values(),
                    unionDefinitions.values());
        }

    }

}
