package no.nav.foreldrepenger.graphql.codegen.model;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.graphql.codegen.mapper.DataModelMapper;

/**
 * Freemarker-understandable format of method parameter and field definition
 *
 * @author kobylynskyi
 */
public class ParameterDefinition {

    private String type;
    /**
     * Normalized name using {@link DataModelMapper#capitalizeIfRestricted(String)} }
     */
    private String name;
    /**
     * Original name that appears in GraphQL schema
     */
    private String originalName;
    private String defaultValue;
    private boolean isMandatory;
    private List<String> annotations = new ArrayList<>();
    private DeprecatedDefinition deprecated;
    private List<String> javaDoc = new ArrayList<>();
    private String getterMethodName;
    /**
     * If the type is parametrized then input parameters will be defined here
     */
    private List<ParameterDefinition> inputParameters;
    /**
     * Definition of the same type, but defined in the parent
     */
    private ParameterDefinition definitionInParentType;

    public ParameterDefinition() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }

    public DeprecatedDefinition getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(DeprecatedDefinition deprecated) {
        this.deprecated = deprecated;
    }

    public String getGetterMethodName() {
        return getterMethodName;
    }

    public void setGetterMethodName(String getterMethodName) {
        this.getterMethodName = getterMethodName;
    }

    public List<String> getJavaDoc() {
        return javaDoc;
    }

    public void setJavaDoc(List<String> javaDoc) {
        this.javaDoc = javaDoc;
    }

    public ParameterDefinition getDefinitionInParentType() {
        return definitionInParentType;
    }

    public void setDefinitionInParentType(ParameterDefinition definitionInParentType) {
        this.definitionInParentType = definitionInParentType;
    }

    public List<ParameterDefinition> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(List<ParameterDefinition> inputParameters) {
        this.inputParameters = inputParameters;
    }
}
