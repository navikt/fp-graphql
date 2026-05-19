package no.nav.foreldrepenger.graphql.plugin;

import static no.nav.foreldrepenger.graphql.codegen.model.MappingConfigConstants.DEFAULT_VALIDATION_ANNOTATION;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import no.nav.foreldrepenger.graphql.codegen.GraphQLCodegen;
import no.nav.foreldrepenger.graphql.codegen.model.MappingConfig;

/**
 * Simplified GraphQL code generation Mojo for Java client generation.
 * Extracted from io.github.kobylynskyi:graphql-codegen-maven-plugin (MIT License).
 *
 * <p>Defaults are tailored to Nav foreldrepengers configuration / needs
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class GraphQLCodegenMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    // --- Required parameters ---

    @Parameter
    private String[] graphqlSchemaPaths;

    @Parameter(required = true)
    private File outputDir;

    @Parameter
    private String packageName;

    @Parameter
    private String modelPackageName;

    // --- Optional parameters with NAV-standard defaults ---

    @Parameter
    private Properties customTypesMapping;

    @Parameter(defaultValue = "true")
    private boolean generateBuilder;

    @Parameter(defaultValue = DEFAULT_VALIDATION_ANNOTATION)
    private String modelValidationAnnotation;

    @Parameter(defaultValue = "false")
    private boolean generateJacksonTypeIdResolver;

    @Override
    public void execute() throws MojoExecutionException {
        addCompileSourceRoot();

        var mappingConfig = new MappingConfig();

        // Package
        mappingConfig.setPackageName(packageName);
        mappingConfig.setModelPackageName(modelPackageName);

        // Type mappings
        var typesMapping = convertToMap(customTypesMapping);
        mappingConfig.setCustomTypesMapping(typesMapping);

        // Generation flags
        mappingConfig.setGenerateBuilder(generateBuilder);
        mappingConfig.setGenerateJacksonTypeIdResolver(generateJacksonTypeIdResolver);

        // Annotations
        mappingConfig.setModelValidationAnnotation(modelValidationAnnotation);

        try {
            new GraphQLCodegen(getSchemas(), outputDir, mappingConfig).generate();
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("GraphQL code generation failed. See above for the full exception.");
        }
    }

    private List<String> getSchemas() {
        if (graphqlSchemaPaths != null) {
            return Arrays.asList(graphqlSchemaPaths);
        } else {
            throw new IllegalStateException("No GraphQL schema paths provided. Please provide <graphqlSchemaPaths>");
        }
    }

    private void addCompileSourceRoot() {
        var path = outputDir.getPath();
        getLog().info("Added the following path to the source root: " + path);
        project.addCompileSourceRoot(path);
    }

    private static Map<String, String> convertToMap(Properties properties) {
        if (properties == null) {
            return new HashMap<>();
        }
        Map<String, String> result = HashMap.newHashMap(properties.size());
        for (var name : properties.stringPropertyNames()) {
            result.put(name, properties.getProperty(name));
        }
        return result;
    }
}
