package no.nav.foreldrepenger.graphql.codegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import no.nav.foreldrepenger.graphql.codegen.generators.FilesGeneratorsFactory;
import no.nav.foreldrepenger.graphql.codegen.mapper.DataModelMapperFactory;
import no.nav.foreldrepenger.graphql.codegen.mapper.JavaMapperFactoryImpl;
import no.nav.foreldrepenger.graphql.codegen.model.MappingConfig;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedDocument;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedScalarTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.parser.GraphQLDocumentParser;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

/**
 * Generates classes based on GraphQL schema.
 * Extendable for customizing code generation for other JVM languages
 *
 * @author kobylynskyi
 * @author valinhadev
 */
public class GraphQLCodegen {

    private static final String STRING = "String";

    protected final MappingConfig mappingConfig;

    private final List<String> schemas;
    private final File outputDir;
    private final DataModelMapperFactory dataModelMapperFactory;

    // used in plugins
    public GraphQLCodegen(List<String> schemas,
                          File outputDir,
                          MappingConfig mappingConfig) {
        this.schemas = schemas;
        this.outputDir = outputDir;
        this.dataModelMapperFactory = new DataModelMapperFactory(new JavaMapperFactoryImpl());
        this.mappingConfig = mappingConfig;

        initDefaultValues(mappingConfig);
        sanitize(mappingConfig);
    }

    private static void sanitize(MappingConfig mappingConfig) {
        mappingConfig.setModelValidationAnnotation(Utils.replaceLeadingAtSign(mappingConfig.getModelValidationAnnotation()));
        if (mappingConfig.getCustomTypesMapping() != null) {
            mappingConfig.setCustomTypesMapping(new HashMap<>(mappingConfig.getCustomTypesMapping()));
        }
    }

    protected void initDefaultValues(MappingConfig mappingConfig) {
        mappingConfig.initDefaultValues();
    }

    /**
     * Entry point.
     * Generates class files based on GraphQL schema
     *
     * @return a list of generated classes
     * @throws IOException in case some I/O error occurred, e.g.: file can't be created, directory access issues, etc.
     */
    public List<File> generate() throws IOException {
        var startTime = System.currentTimeMillis();

        // prepare output directory
        Utils.deleteDir(outputDir);
        Utils.createDirIfAbsent(outputDir);

        if (!Utils.isEmpty(schemas)) {
            var document = GraphQLDocumentParser.getDocumentFromSchemas(schemas);
            return processDefinitions(document, schemas.size() + " schema(s)", startTime);
        } else {
            // either schemas or introspection result should be provided
            throw new IllegalArgumentException(
                    "Either graphql schema path or introspection result path should be supplied");
        }
    }

    private List<File> processDefinitions(ExtendedDocument document, String source, long startTime) {
        initCustomTypeMappings(document.getScalarDefinitions());
        var context = new MappingContext(outputDir, mappingConfig, document);

        List<File> generatedFiles = new ArrayList<>();
        for (var generator : FilesGeneratorsFactory.getAll(context, dataModelMapperFactory)) {
            generatedFiles.addAll(generator.generate());
        }
        printOutputResult(source, generatedFiles.size(), System.currentTimeMillis() - startTime);
        return generatedFiles;
    }

    private void printOutputResult(String source, int classesGenerated, long duration) {
        System.out.printf("Generated %d classes from %s in folder %s, took %d ms%n",
                classesGenerated, source, outputDir.getAbsolutePath(), duration);  // NOSONAR - build-output
    }

    protected void initCustomTypeMappings(Collection<ExtendedScalarTypeDefinition> scalarTypeDefinitions) {
        for (var definition : scalarTypeDefinitions) {
            if (definition.getDefinition() != null) {
                mappingConfig.putCustomTypeMappingIfAbsent(definition.getDefinition().getName(), STRING);
            }
            for (var extension : definition.getExtensions()) {
                mappingConfig.putCustomTypeMappingIfAbsent(extension.getName(), STRING);
            }
        }
        mappingConfig.putCustomTypeMappingIfAbsent("ID", String.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent(STRING, String.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent("Int", Integer.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent("Int!", "int");
        mappingConfig.putCustomTypeMappingIfAbsent("Float", Double.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent("Float!", "double");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean", Boolean.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean!", "boolean");
    }

}
