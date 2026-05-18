<#if package?has_content>
package ${package};

</#if>
import no.nav.foreldrepenger.graphql.GraphQLRequestSerializer;
import java.util.StringJoiner;

<#if javaDoc?has_content>
/**
    <#list javaDoc as javaDocLine>
 * ${javaDocLine}
    </#list>
 */
</#if>
@jakarta.annotation.Generated("no.nav.foreldrepenger.graphql.GraphQLCodegen")
<#list annotations as annotation>
@${annotation}
</#list>
public class ${className} implements java.io.Serializable<#if implements?has_content><#list implements as interface>, ${interface}<#if interface_has_next></#if></#list></#if> {

    private static final long serialVersionUID = 1L;

<#if fields?has_content>
    <#list fields as field>
        <#if field.deprecated?has_content>
    @${field.deprecated.annotation}
        </#if>
        <#list field.annotations as annotation>
    @${annotation}
        </#list>
    private ${field.type} ${field.name}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if>;
    </#list>
</#if>

    public ${className}() {
    }

<#if fields?has_content>
    public ${className}(<#list fields as field>${field.type} ${field.name}<#if field_has_next>, </#if></#list>) {
    <#list fields as field>
        this.${field.name} = ${field.name};
    </#list>
    }
</#if>

<#if fields?has_content>
    <#list fields as field>
        <#if field.javaDoc?has_content>
    /**
            <#list field.javaDoc as javaDocLine>
     * ${javaDocLine}
            </#list>
     */
        </#if>
        <#if field.deprecated?has_content>
    @${field.deprecated.annotation}
        </#if>
        <#if field.mandatory && field.definitionInParentType?has_content && !field.definitionInParentType.mandatory>
    public ${field.definitionInParentType.type} ${field.getterMethodName}() {
        <#else>
    public ${field.type} ${field.getterMethodName}() {
        </#if>
        return ${field.name};
    }
        <#if field.javaDoc?has_content>
    /**
                <#list field.javaDoc as javaDocLine>
     * ${javaDocLine}
            </#list>
     */
        </#if>
        <#if field.deprecated?has_content>
    @${field.deprecated.annotation}
        </#if>
    public void set${field.name?cap_first}(${field.type} ${field.name}) {
        this.${field.name} = ${field.name};
    }

    </#list>
</#if>

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
    <#if fields?has_content>
        <#list fields as field>
            <#if ["byte", "short", "int", "long", "float", "double", "char", "boolean"]?seq_contains(field.type)>
        joiner.add("${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}));
            <#else>
        if (${field.name} != null) {
            joiner.add("${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}));
        }
            </#if>
        </#list>
    </#if>
        return joiner.toString();
    }

<#if builder>
    public static ${className}.Builder builder() {
        return new ${className}.Builder();
    }

    @jakarta.annotation.Generated("no.nav.foreldrepenger.graphql.GraphQLCodegen")
    public static class Builder {

    <#if fields?has_content>
        <#list fields as field>
        private ${field.type} ${field.name}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if>;
        </#list>
    </#if>

        public Builder() {
        }

    <#if fields?has_content>
        <#list fields as field>
            <#if field.javaDoc?has_content>
        /**
                <#list field.javaDoc as javaDocLine>
         * ${javaDocLine}
                </#list>
         */
            </#if>
            <#if field.deprecated?has_content>
        @${field.deprecated.annotation}
            </#if>
        public Builder set${field.name?cap_first}(${field.type} ${field.name}) {
            this.${field.name} = ${field.name};
            return this;
        }

        </#list>
    </#if>

        public ${className} build() {
            return new ${className}(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>);
        }

    }
</#if>
}
