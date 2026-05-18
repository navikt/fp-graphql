package no.nav.foreldrepenger.graphql;

import java.util.Map;

/**
 * The contract for GraphQL request
 */
public interface GraphQLOperationRequest {

    GraphQLOperation getOperationType();

    String getOperationName();

    String getAlias();

    Map<String, Object> getInput();

}
