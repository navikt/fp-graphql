<#if package?has_content>
package ${package};

</#if>

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
public interface ${className} {

}
