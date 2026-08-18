package no.nav.foreldrepenger.graphql.codegen.model;

import java.io.File;
import java.util.Map;
import java.util.Set;

import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedDocument;

/**
 * A context of a single mapping process.
 * Contains mapping configuration, parsed schema elements and other things used in code generation process
 */
public class MappingContext implements GraphQLCodegenConfiguration {

    private final File outputDirectory;
    private final MappingConfig config;
    private final ExtendedDocument document;
    private final Set<String> typesUnionsInterfacesNames;
    private final Set<String> interfacesName;
    private final Set<String> unionsName;
    private final Map<String, Set<String>> interfaceChildren;

    public MappingContext(File outputDirectory, MappingConfig config, ExtendedDocument document) {
        this.outputDirectory = outputDirectory;
        this.config = config;
        this.document = document;
        this.typesUnionsInterfacesNames = document.getTypesUnionsInterfacesNames();
        this.interfacesName = document.getInterfacesNames();
        this.unionsName = document.getUnionsNames();
        this.interfaceChildren = document.getInterfaceChildren();
    }

    @Override
    public Map<String, String> getCustomTypesMapping() {
        return config.getCustomTypesMapping();
    }

    @Override
    public String getPackageName() {
        return config.getPackageName();
    }

    @Override
    public String getModelPackageName() {
        return config.getModelPackageName();
    }

    @Override
    public String getModelValidationAnnotation() {
        return config.getModelValidationAnnotation();
    }

    @Override
    public Boolean getGenerateBuilder() {
        return config.getGenerateBuilder();
    }

    @Override
    public Boolean getGenerateJacksonTypeIdResolver() {
        return config.getGenerateJacksonTypeIdResolver();
    }

    public ExtendedDocument getDocument() {
        return document;
    }

    public Set<String> getTypesUnionsInterfacesNames() {
        return typesUnionsInterfacesNames;
    }

    public Set<String> getInterfacesName() {
        return interfacesName;
    }

    public Set<String> getUnionsNames() {
        return unionsName;
    }

    public Map<String, Set<String>> getInterfaceChildren() {
        return interfaceChildren;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

}
