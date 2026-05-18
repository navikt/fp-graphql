package no.nav.foreldrepenger.graphql;

import java.util.List;

/**
 * GraphQL response. Contains data and errors
 *
 * @param <T> type of response
 */
public class GraphQLResult<T> {

    private T data;
    private List<GraphQLError> errors;

    public GraphQLResult() {
    }

    public GraphQLResult(T data, List<GraphQLError> errors) {
        this.data = data;
        this.errors = errors;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<GraphQLError> getErrors() {
        return errors;
    }

    public void setErrors(List<GraphQLError> errors) {
        this.errors = errors;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public String getErrorMessage() {
        if (!hasErrors()) {
            return null;
        }
        return errors.stream()
                .map(GraphQLError::message)
                .reduce((a, b) -> a + "; " + b)
                .orElse(null);
    }

}
