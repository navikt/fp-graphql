<#if package?has_content>
package ${package};

</#if>
import no.nav.foreldrepenger.graphql.GraphQLOperation;
import no.nav.foreldrepenger.graphql.GraphQLOperationRequest;
import java.util.LinkedHashMap;
import java.util.Map;

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
public class ${className} implements GraphQLOperationRequest {

    public static final String OPERATION_NAME = "${operationName}";
    public static final GraphQLOperation OPERATION_TYPE = GraphQLOperation.${operationType};

    private String alias;
    private final Map<String, Object> input = new LinkedHashMap<>();

    public ${className}() {
    }

    public ${className}(String alias) {
        this.alias = alias;
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
    public void set${field.name?cap_first}(${field.type} ${field.name}) {
        this.input.put("${field.originalName}", ${field.name});
    }

</#list>
</#if>
    @Override
    public GraphQLOperation getOperationType() {
        return OPERATION_TYPE;
    }

    @Override
    public String getOperationName() {
        return OPERATION_NAME;
    }

    @Override
    public String getAlias() {
        return alias != null ? alias : OPERATION_NAME;
    }

    @Override
    public Map<String, Object> getInput() {
        return input;
    }

<#if builder>
    public static ${className}.Builder builder() {
        return new ${className}.Builder();
    }

    @jakarta.annotation.Generated("no.nav.foreldrepenger.graphql.GraphQLCodegen")
    public static class Builder {

        private String $alias;
<#if fields?has_content>
<#list fields as field>
        private ${field.type} ${field.name}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if>;
</#list>
</#if>

        public Builder() {
        }

        public Builder alias(String alias) {
            this.$alias = alias;
            return this;
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
            ${className} obj = new ${className}($alias);
<#if fields?has_content>
<#list fields as field>
            obj.set${field.name?cap_first}(${field.name});
</#list>
</#if>
            return obj;
        }

    }
</#if>
}
