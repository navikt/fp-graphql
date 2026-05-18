package no.nav.foreldrepenger.graphql;

import java.util.Map;

/**
 * Generic class having content of GraphQL response
 */
public class GraphQLResponse extends GraphQLResult<Map<String, Object>> {

    public GraphQLResponse() {
        super();
    }

    public Object getData(String name) {
        var data = getData();
        return data != null ? data.get(name) : null;
    }

}
