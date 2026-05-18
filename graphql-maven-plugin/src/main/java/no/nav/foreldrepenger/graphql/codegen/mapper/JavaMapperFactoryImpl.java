package no.nav.foreldrepenger.graphql.codegen.mapper;

/**
 * A factory of various mappers for Java language
 */
public class JavaMapperFactoryImpl implements MapperFactory {

    private final DataModelMapper dataModelMapper;
    private final ValueMapper valueMapper;
    private final GraphQLTypeMapper graphQLTypeMapper;
    private final AnnotationsMapper annotationsMapper;

    public JavaMapperFactoryImpl() {
        dataModelMapper = new DataModelMapper();
        valueMapper = new ValueMapper(dataModelMapper);
        graphQLTypeMapper = new GraphQLTypeMapper();
        annotationsMapper = new AnnotationsMapper();
    }

    @Override
    public DataModelMapper getDataModelMapper() {
        return dataModelMapper;
    }

    @Override
    public GraphQLTypeMapper getGraphQLTypeMapper() {
        return graphQLTypeMapper;
    }

    @Override
    public AnnotationsMapper getAnnotationsMapper() {
        return annotationsMapper;
    }

    @Override
    public ValueMapper getValueMapper() {
        return valueMapper;
    }

}
