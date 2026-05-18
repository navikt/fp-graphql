package no.nav.foreldrepenger.graphql;

/**
 * Class which represents GraphQL Request
 */
public class GraphQLRequest {

    private final String operationName;
    private final GraphQLOperationRequest request;
    private final GraphQLResponseProjection responseProjection;

    public GraphQLRequest(GraphQLOperationRequest request) {
        this(null, request, null);
    }

    public GraphQLRequest(String operationName, GraphQLOperationRequest request) {
        this(operationName, request, null);
    }

    public GraphQLRequest(GraphQLOperationRequest request, GraphQLResponseProjection responseProjection) {
        this(null, request, responseProjection);
    }

    public GraphQLRequest(String operationName, GraphQLOperationRequest request,
                          GraphQLResponseProjection responseProjection) {
        this.operationName = operationName;
        this.request = request;
        this.responseProjection = responseProjection;
    }

    public GraphQLOperationRequest getRequest() {
        return request;
    }

    public GraphQLResponseProjection getResponseProjection() {
        return responseProjection;
    }

    public String getOperationName() {
        return operationName;
    }

    /**
     * Turns a GraphQL request into an object that can be serialized and sent to producer.
     *
     * @return object containing the serialized graphql request
     */
    public GraphQLQueryObject toQueryObject() {
        return new GraphQLQueryObject(GraphQLRequestSerializer.toQueryString(this));
    }

    /**
     * Serializes GraphQL request as raw query string
     *
     * @return the serialized request
     */
    public String toQueryString() {
        return GraphQLRequestSerializer.toQueryString(this);
    }
}
