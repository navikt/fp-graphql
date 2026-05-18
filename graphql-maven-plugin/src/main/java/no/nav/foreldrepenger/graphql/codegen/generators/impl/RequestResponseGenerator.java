package no.nav.foreldrepenger.graphql.codegen.generators.impl;

import java.io.File;
import java.util.List;

import graphql.language.FieldDefinition;
import no.nav.foreldrepenger.graphql.codegen.generators.FilesGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateFilesCreator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateType;
import no.nav.foreldrepenger.graphql.codegen.mapper.DataModelMapperFactory;
import no.nav.foreldrepenger.graphql.codegen.mapper.RequestResponseDefinitionToDataModelMapper;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;

/**
 * Generates files for requests and responses
 */
public class RequestResponseGenerator implements FilesGenerator {

    private final MappingContext mappingContext;
    private final RequestResponseDefinitionToDataModelMapper requestResponseDefinitionMapper;

    public RequestResponseGenerator(MappingContext mappingContext,
                                    DataModelMapperFactory dataModelMapperFactory) {
        this.mappingContext = mappingContext;
        this.requestResponseDefinitionMapper = dataModelMapperFactory.getRequestResponseDefinitionMapper();
    }

    @Override
    public List<File> generate() {
        return mappingContext.getDocument().getOperationDefinitions().stream()
                .flatMap(definition -> {
                    var fieldNames = definition.getFieldDefinitions().stream()
                            .map(FieldDefinition::getName)
                            .toList();
                    return definition.getFieldDefinitions().stream()
                            .flatMap(operationDef -> {
                                var requestDataModel = requestResponseDefinitionMapper
                                        .mapRequest(mappingContext, operationDef, definition.getName(), fieldNames);
                                var responseDataModel = requestResponseDefinitionMapper
                                        .mapResponse(mappingContext, operationDef, definition.getName(), fieldNames);
                                return java.util.stream.Stream.of(
                                        FreeMarkerTemplateFilesCreator.create(mappingContext, FreeMarkerTemplateType.REQUEST, requestDataModel),
                                        FreeMarkerTemplateFilesCreator.create(mappingContext, FreeMarkerTemplateType.RESPONSE, responseDataModel));
                            });
                })
                .toList();
    }

}
