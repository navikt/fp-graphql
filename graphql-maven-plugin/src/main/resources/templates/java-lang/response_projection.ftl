<#if package?has_content>
package ${package};

</#if>
import no.nav.foreldrepenger.graphql.GraphQLResponseField;
import no.nav.foreldrepenger.graphql.GraphQLResponseProjection;
import java.util.List;

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
public class ${className} extends GraphQLResponseProjection {

    public ${className}() {
    }

    public ${className}(${className} projection) {
        super(projection);
    }

    public ${className}(List<${className}> projections) {
        super(projections);
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
    public ${className} ${field.methodName}(<#if field.type?has_content>${field.type} subProjection</#if>) {
        return ${field.methodName}(<#if field.parametrizedInputClassName?has_content>(String)</#if>null<#if field.type?has_content>, subProjection</#if>);
    }

    public ${className} ${field.methodName}(String alias<#if field.type?has_content>, ${field.type} subProjection</#if>) {
        add$(new GraphQLResponseField("${field.name}").alias(alias)<#if field.type?has_content>.projection(subProjection)</#if>);
        return this;
    }

<#if field.parametrizedInputClassName?has_content>
    public ${className} ${field.methodName}(${field.parametrizedInputClassName} input<#if field.type?has_content>, ${field.type} subProjection</#if>) {
        return ${field.methodName}(null, input<#if field.type?has_content>, subProjection</#if>);
    }

    public ${className} ${field.methodName}(String alias, ${field.parametrizedInputClassName} input<#if field.type?has_content>, ${field.type} subProjection</#if>) {
        add$(new GraphQLResponseField("${field.name}").alias(alias).parameters(input)<#if field.type?has_content>.projection(subProjection)</#if>);
        return this;
    }

</#if>
</#list>
</#if>
    @Override
    public ${className} deepCopy$() {
        return new ${className}(this);
    }

}
