package no.nav.foreldrepenger.graphql.codegen.generators.impl;

import java.io.File;
import java.util.List;

import no.nav.foreldrepenger.graphql.codegen.generators.FilesGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateFilesCreator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateType;
import no.nav.foreldrepenger.graphql.codegen.mapper.DataModelMapperFactory;
import no.nav.foreldrepenger.graphql.codegen.mapper.UnionDefinitionToDataModelMapper;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;

/**
 * Generates files for unions
 */
public class UnionGenerator implements FilesGenerator {

    private final MappingContext mappingContext;
    private final UnionDefinitionToDataModelMapper unionDefinitionToDataModelMapper;

    public UnionGenerator(MappingContext mappingContext, DataModelMapperFactory dataModelMapperFactory) {
        this.mappingContext = mappingContext;
        this.unionDefinitionToDataModelMapper = dataModelMapperFactory.getUnionDefinitionMapper();
    }

    @Override
    public List<File> generate() {
        return mappingContext.getDocument().getUnionDefinitions().stream()
                .map(def -> unionDefinitionToDataModelMapper.map(mappingContext, def))
                .map(dataModel -> FreeMarkerTemplateFilesCreator.create(mappingContext, FreeMarkerTemplateType.UNION, dataModel))
                .toList();
    }

}
