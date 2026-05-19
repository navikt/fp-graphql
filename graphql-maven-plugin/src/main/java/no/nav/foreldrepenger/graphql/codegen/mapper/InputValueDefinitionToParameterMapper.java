package no.nav.foreldrepenger.graphql.codegen.mapper;

import java.util.List;

import graphql.language.InputValueDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.ParameterDefinition;
import no.nav.foreldrepenger.graphql.codegen.model.builders.DeprecatedDefinitionBuilder;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

/**
 * Mapper from GraphQL's InputValueDefinition to a Freemarker-understandable format
 *
 * @author kobylynskyi
 */
public class InputValueDefinitionToParameterMapper {
    private final ValueMapper valueMapper;
    private final GraphQLTypeMapper graphQLTypeMapper;
    private final AnnotationsMapper annotationsMapper;
    private final DataModelMapper dataModelMapper;

    public InputValueDefinitionToParameterMapper(MapperFactory mapperFactory) {
        this.valueMapper = mapperFactory.getValueMapper();
        this.graphQLTypeMapper = mapperFactory.getGraphQLTypeMapper();
        this.annotationsMapper = mapperFactory.getAnnotationsMapper();
        this.dataModelMapper = mapperFactory.getDataModelMapper();
    }

    /**
     * Map input value definition to a Freemarker-understandable data model type
     *
     * @param mappingContext   Global mapping context
     * @param valueDefinitions List of GraphQL value definitions
     * @param parentTypeName   Name of the parent GraphQL type
     * @return Freemarker data model of the GraphQL input value definition
     */
    public List<ParameterDefinition> map(MappingContext mappingContext, List<InputValueDefinition> valueDefinitions,
                                         String parentTypeName) {
        return valueDefinitions.stream()
                .map(inputValueDef -> map(mappingContext, inputValueDef, parentTypeName))
                .toList();
    }

    /**
     * Map GraphQL's InputValueDefinition to a Freemarker-understandable format of operation
     *
     * @param mappingContext       Global mapping context
     * @param inputValueDefinition GraphQL input value definition
     * @param parentTypeName       Name of the parent type
     * @return Freemarker-understandable format of parameter (field)
     */
    private ParameterDefinition map(MappingContext mappingContext, InputValueDefinition inputValueDefinition,
                                    String parentTypeName) {
        var namedDefinition = graphQLTypeMapper
                .getLanguageType(mappingContext, inputValueDefinition.getType(), inputValueDefinition.getName(),
                        parentTypeName);

        var parameter = new ParameterDefinition();
        parameter.setName(dataModelMapper.capitalizeIfRestricted(inputValueDefinition.getName()));
        parameter.setOriginalName(inputValueDefinition.getName());
        parameter.setType(graphQLTypeMapper.wrapApiInputTypeIfRequired(mappingContext, namedDefinition
        ));
        parameter.setDefaultValue(getDefaultValue(mappingContext, inputValueDefinition
        ));
        parameter.setAnnotations(annotationsMapper.getAnnotations(mappingContext, inputValueDefinition.getType(),
                inputValueDefinition, false));
        parameter.setDeprecated(DeprecatedDefinitionBuilder.build(inputValueDefinition));
        parameter.setMandatory(namedDefinition.isMandatory());
        parameter.setGetterMethodName(dataModelMapper.capitalizeMethodNameIfRestricted(
            "get" + Utils.capitalize(inputValueDefinition.getName())));
        return parameter;
    }

    private String getDefaultValue(MappingContext mappingContext, InputValueDefinition inputValueDefinition) {
        var value = valueMapper.map(mappingContext, inputValueDefinition.getDefaultValue(),
                inputValueDefinition.getType());
        return graphQLTypeMapper.wrapApiDefaultValueIfRequired(
            value);
    }

}
