package no.nav.foreldrepenger.graphql.codegen.generators.impl;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import no.nav.foreldrepenger.graphql.codegen.generators.FilesGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateFilesCreator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateType;
import no.nav.foreldrepenger.graphql.codegen.mapper.DataModelMapperFactory;
import no.nav.foreldrepenger.graphql.codegen.mapper.RequestResponseDefinitionToDataModelMapper;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedDefinition;

/**
 * Generates files for response projections
 */
public class ResponseProjectionGenerator implements FilesGenerator {

    private final MappingContext mappingContext;
    private final RequestResponseDefinitionToDataModelMapper requestResponseDefinitionMapper;

    public ResponseProjectionGenerator(MappingContext mappingContext,
                                       DataModelMapperFactory dataModelMapperFactory) {
        this.mappingContext = mappingContext;
        this.requestResponseDefinitionMapper = dataModelMapperFactory.getRequestResponseDefinitionMapper();
    }

    @Override
    public List<File> generate() {
        var doc = mappingContext.getDocument();
        return Stream.<ExtendedDefinition<?, ?>>concat(
                        Stream.concat(
                                doc.getInterfaceDefinitions().stream().map(d -> (ExtendedDefinition<?, ?>) d),
                                doc.getTypeDefinitions().stream().map(d -> (ExtendedDefinition<?, ?>) d)),
                        doc.getUnionDefinitions().stream().map(d -> (ExtendedDefinition<?, ?>) d))
                .map(this::generate)
                .toList();
    }

    private File generate(ExtendedDefinition<?, ?> definition) {
        var responseProjDataModel = requestResponseDefinitionMapper.mapResponseProjection(
                mappingContext, definition);
        return FreeMarkerTemplateFilesCreator.create(
                mappingContext, FreeMarkerTemplateType.RESPONSE_PROJECTION, responseProjDataModel);
    }

}
