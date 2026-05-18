package no.nav.foreldrepenger.graphql.codegen.generators.impl;

import java.io.File;
import java.util.List;

import no.nav.foreldrepenger.graphql.codegen.generators.FilesGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateFilesCreator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateType;
import no.nav.foreldrepenger.graphql.codegen.mapper.DataModelMapperFactory;
import no.nav.foreldrepenger.graphql.codegen.mapper.InterfaceDefinitionToDataModelMapper;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;

/**
 * Generates files for interfaces
 */
public class InterfaceGenerator implements FilesGenerator {

    private final MappingContext mappingContext;
    private final InterfaceDefinitionToDataModelMapper interfaceDefinitionMapper;

    public InterfaceGenerator(MappingContext mappingContext,
                              DataModelMapperFactory dataModelMapperFactory) {
        this.mappingContext = mappingContext;
        this.interfaceDefinitionMapper = dataModelMapperFactory.getInterfaceDefinitionMapper();
    }

    @Override
    public List<File> generate() {
        return mappingContext.getDocument().getInterfaceDefinitions().stream()
                .map(def -> interfaceDefinitionMapper.map(mappingContext, def))
                .map(dataModel -> FreeMarkerTemplateFilesCreator.create(mappingContext, FreeMarkerTemplateType.INTERFACE, dataModel))
                .toList();
    }

}
