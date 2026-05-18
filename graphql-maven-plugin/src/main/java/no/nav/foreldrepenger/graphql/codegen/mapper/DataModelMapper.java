package no.nav.foreldrepenger.graphql.codegen.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import graphql.language.InputValueDefinition;
import graphql.language.TypeName;
import no.nav.foreldrepenger.graphql.codegen.model.MappingConfigConstants;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedDocument;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedImplementingTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

/**
 * Generic mapper for all languages
 */
public class DataModelMapper {

    /**
     * Generates a model class name including prefix and suffix (if any)
     *
     * @param extendedDefinition GraphQL extended definition
     * @return Class name of GraphQL model node
     */
    public String getModelClassNameWithPrefixAndSuffix(ExtendedDefinition<?, ?> extendedDefinition) {
        return getModelClassNameWithPrefixAndSuffix(extendedDefinition.getName());
    }

    /**
     * Generates a model class name including prefix and suffix (if any)
     *
     * @param definitionName GraphQL node name
     * @return Class name of GraphQL model node
     */
    public static String getModelClassNameWithPrefixAndSuffix(String definitionName) {
        return Utils.capitalize(definitionName);
    }

    /**
     * Generates a class name for ParametrizedInput
     *
     * @param fieldDefinition      GraphQL field definition for a field that has parametrized input
     * @param parentTypeDefinition GraphQL definition which is a parent for fieldDefinition
     * @return Class name of parametrized input
     */
    static String getParametrizedInputClassName(ExtendedFieldDefinition fieldDefinition,
                                                ExtendedDefinition<?, ?> parentTypeDefinition) {
        return Utils.capitalize(parentTypeDefinition.getName()) +
                Utils.capitalize(fieldDefinition.getName()) +
                MappingConfigConstants.DEFAULT_PARAMETRIZED_INPUT_SUFFIX;
    }

    /**
     * Get java package name for model class.
     *
     * @param mappingContext Global mapping context
     * @return model package name if present. Generic package name otherwise
     */
    public static String getModelPackageName(MappingContext mappingContext) {
        if (Utils.isNotBlank(mappingContext.getModelPackageName())) {
            return mappingContext.getModelPackageName();
        } else {
            return mappingContext.getPackageName();
        }
    }

    /**
     * Builds a className suffix based on the input values.
     * Examples:
     * 1. fieldDefinition has some input values:
     * "ids" becomes "ByIds"
     * "category", "status" becomes "ByCategoryAndStatus"
     *
     * @param fieldDefinition field definition that has some InputValueDefinitions
     * @return className suffix based on the input values
     */
    static String getClassNameSuffixWithInputValues(ExtendedFieldDefinition fieldDefinition) {
        var inputValueNamesJoiner = new StringJoiner("And");
        fieldDefinition.getInputValueDefinitions().stream()
                .map(InputValueDefinition::getName).map(Utils::capitalize)
                .forEach(inputValueNamesJoiner::add);
        var inputValueNames = inputValueNamesJoiner.toString();
        if (inputValueNames.isEmpty()) {
            return inputValueNames;
        }
        return "By" + inputValueNames;
    }

    /**
     * Scan document and return all interfaces that given type implements.
     *
     * @param definition GraphQL definition that might implement some interfaces
     * @param document   GraphQL document
     * @return all interfaces that given type implements.
     */
    static List<ExtendedInterfaceTypeDefinition> getInterfacesOfType(
            ExtendedImplementingTypeDefinition<?, ?> definition,
            ExtendedDocument document) {
        if (definition.getImplements().isEmpty()) {
            return Collections.emptyList();
        }
        var typeImplements = definition.getImplements().stream()
                .filter(type -> TypeName.class.isAssignableFrom(type.getClass()))
                .map(TypeName.class::cast)
                .map(TypeName::getName)
                .collect(Collectors.toSet());
        return document.getInterfaceDefinitions().stream()
                .filter(def -> typeImplements.contains(def.getName()))
                .collect(Collectors.toList());
    }

    private static final Set<String> JAVA_RESTRICTED_KEYWORDS = new HashSet<>(Arrays.asList(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
        "default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto",
        "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package",
        "private", "protected", "public", "return", "record", "short", "static", "strictfp", "super", "switch",
        "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while"));

    private static final Set<String> JAVA_RESTRICTED_METHOD_NAMES = new HashSet<>(Arrays.asList(
        "getClass", "notify", "notifyAll", "wait"));


    /**
     * Capitalize field name if it is language-restricted.
     * Examples:
     * * 'class' becomes 'Class'
     * * 'int' becomes 'Int'
     *
     * @param fieldName any string
     * @return capitalized value if it is restricted in java, same value as parameter otherwise
     */
    public String capitalizeIfRestricted(String fieldName) {
        if (JAVA_RESTRICTED_KEYWORDS.contains(fieldName)) {
            return Utils.capitalize(fieldName);
        }
        return fieldName;
    }

    /**
     * Capitalize method name if it is language-restricted.
     * Examples:
     * * 'getClass' becomes 'GetClass'
     * * 'wait' becomes 'Wait'
     * * 'this' becomes 'This'
     *
     * @param methodName any string
     * @return capitalized value if it is restricted in java, same value as parameter otherwise
     */
    public String capitalizeMethodNameIfRestricted(String methodName) {
        if (JAVA_RESTRICTED_KEYWORDS.contains(methodName)) {
            return Utils.capitalize(methodName);
        }
        if (JAVA_RESTRICTED_METHOD_NAMES.contains(methodName)) {
            return Utils.capitalize(methodName);
        }
        return methodName;
    }

}
