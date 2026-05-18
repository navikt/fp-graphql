package no.nav.foreldrepenger.graphql.codegen.generators.impl;

import java.io.File;
import java.util.List;

import no.nav.foreldrepenger.graphql.codegen.generators.FilesGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateFilesCreator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateType;
import no.nav.foreldrepenger.graphql.codegen.mapper.DataModelMapperFactory;
import no.nav.foreldrepenger.graphql.codegen.mapper.InputDefinitionToDataModelMapper;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;

/**
 * Generates files for inputs
 */
public class InputGenerator implements FilesGenerator {

    private final MappingContext mappingContext;
    private final InputDefinitionToDataModelMapper inputDefinitionMapper;

    public InputGenerator(MappingContext mappingContext,
                          DataModelMapperFactory dataModelMapperFactory) {
        this.mappingContext = mappingContext;
        this.inputDefinitionMapper = dataModelMapperFactory.getInputDefinitionMapper();
    }

    @Override
    public List<File> generate() {
        return mappingContext.getDocument().getInputDefinitions().stream()
                .map(def -> inputDefinitionMapper.map(mappingContext, def))
                .map(dataModel -> FreeMarkerTemplateFilesCreator.create(mappingContext, FreeMarkerTemplateType.TYPE, dataModel))
                .toList();
    }

}
