<#if package?has_content>
package ${package};

</#if>
import no.nav.foreldrepenger.graphql.GraphQLParametrizedInput;
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
public class ${className} implements GraphQLParametrizedInput {

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
    public ${className} ${field.name}(${field.type} ${field.name}) {
        this.${field.name} = ${field.name};
        return this;
    }

    </#list>
</#if>
    @Override
    public ${className} deepCopy() {
        ${className} parametrizedInput = new ${className}();
<#if fields?has_content>
    <#list fields as field>
        parametrizedInput.${field.name}(this.${field.name});
    </#list>
</#if>
        return parametrizedInput;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
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

}
