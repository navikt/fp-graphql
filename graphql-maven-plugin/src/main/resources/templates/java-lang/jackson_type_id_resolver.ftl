<#if package?has_content>
package ${package};

</#if>
import com.fasterxml.jackson.annotation.JsonTypeInfo;
<#if generateJackson3>
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.impl.TypeIdResolverBase;
<#else>
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
</#if>

@jakarta.annotation.Generated("no.nav.foreldrepenger.graphql.GraphQLCodegen")
public class GraphqlJacksonTypeIdResolver extends TypeIdResolverBase {

    private JavaType superType;

    @Override
    public void init(JavaType baseType) {
        superType = baseType;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String typename) {
        try {
            Class<?> clazz = Class.forName(
                <#if package?has_content>"${package}." +
                </#if>typename
            );
            return context.constructSpecializedType(superType, clazz);
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }

<#if generateJackson3>
    @Override
    public String idFromValue(DatabindContext context, Object obj) {
        return idFromValueAndType(context, obj, obj.getClass());
    }

    @Override
    public String idFromValueAndType(DatabindContext context, Object obj, Class<?> subType) {
        return subType.getSimpleName();
    }
<#else>
    @Override
    public String idFromValue(Object obj) {
        return idFromValueAndType(obj, obj.getClass());
    }

    @Override
    public String idFromValueAndType(Object obj, Class<?> subType) {
        return subType.getSimpleName();
    }
</#if>
}
