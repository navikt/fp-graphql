package no.nav.foreldrepenger.graphql.codegen.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Mapping config.
 *
 * @author kobylynskyi
 * @author valinha
 */
public class MappingConfig implements GraphQLCodegenConfiguration {

    // package name configs:
    private String packageName;
    private String modelPackageName;

    // validation
    private String modelValidationAnnotation;

    // various toggles:
    private Boolean generateBuilder;
    private Boolean generateJacksonTypeIdResolver;
    private Boolean generateJackson3;

    // schema scalar to type mapping:
    private Map<String, String> customTypesMapping = new HashMap<>();

    /**
     * Put custom type mapping if absent.
     *
     * @param from the from
     * @param to   the to
     */
    public void putCustomTypeMappingIfAbsent(String from, String to) {
        if (customTypesMapping == null) {
            customTypesMapping = new HashMap<>();
        }
        customTypesMapping.computeIfAbsent(from, _ -> to);
    }

    @Override
    public Map<String, String> getCustomTypesMapping() {
        return customTypesMapping;
    }

    public void setCustomTypesMapping(Map<String, String> customTypesMapping) {
        this.customTypesMapping = customTypesMapping;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getModelPackageName() {
        return modelPackageName;
    }

    public void setModelPackageName(String modelPackageName) {
        this.modelPackageName = modelPackageName;
    }

    @Override
    public String getModelValidationAnnotation() {
        return modelValidationAnnotation;
    }

    public void setModelValidationAnnotation(String modelValidationAnnotation) {
        this.modelValidationAnnotation = modelValidationAnnotation;
    }

    @Override
    public Boolean getGenerateBuilder() {
        return generateBuilder;
    }

    public void setGenerateBuilder(Boolean generateBuilder) {
        this.generateBuilder = generateBuilder;
    }

    @Override
    public Boolean getGenerateJacksonTypeIdResolver() {
        return generateJacksonTypeIdResolver;
    }

    public void setGenerateJacksonTypeIdResolver(Boolean generateJacksonTypeIdResolver) {
        this.generateJacksonTypeIdResolver = generateJacksonTypeIdResolver;
    }

    @Override
    public Boolean getGenerateJackson3() {
        return generateJackson3;
    }

    public void setGenerateJackson3(Boolean generateJackson3) {
        this.generateJackson3 = generateJackson3;
    }

    /**
     * Initializes this config with default values for any unset fields
     */
    public void initDefaultValues() {
        if (modelValidationAnnotation == null) {
            modelValidationAnnotation = MappingConfigConstants.DEFAULT_VALIDATION_ANNOTATION;
        }
        if (generateBuilder == null) {
            generateBuilder = MappingConfigConstants.DEFAULT_BUILDER;
        }
        if (generateJacksonTypeIdResolver == null) {
            generateJacksonTypeIdResolver = MappingConfigConstants.DEFAULT_GENERATE_JACKSON_TYPE_ID_RESOLVER;
        }
        if (generateJackson3 == null) {
            generateJackson3 = MappingConfigConstants.DEFAULT_GENERATE_JACKSON3;
        }
    }

}
