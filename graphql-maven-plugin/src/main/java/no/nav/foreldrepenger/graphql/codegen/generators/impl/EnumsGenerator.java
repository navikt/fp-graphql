package no.nav.foreldrepenger.graphql.codegen.generators.impl;

import java.io.File;
import java.util.List;

import no.nav.foreldrepenger.graphql.codegen.generators.FilesGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateFilesCreator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateType;
import no.nav.foreldrepenger.graphql.codegen.mapper.DataModelMapperFactory;
import no.nav.foreldrepenger.graphql.codegen.mapper.EnumDefinitionToDataModelMapper;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;

/**
 * Generates files for enums
 */
public class EnumsGenerator implements FilesGenerator {

    private final MappingContext mappingContext;
    private final EnumDefinitionToDataModelMapper enumDefinitionMapper;

    public EnumsGenerator(MappingContext mappingContext,
                          DataModelMapperFactory dataModelMapperFactory) {
        this.mappingContext = mappingContext;
        this.enumDefinitionMapper = dataModelMapperFactory.getEnumDefinitionMapper();
    }

    @Override
    public List<File> generate() {
        return mappingContext.getDocument().getEnumDefinitions().stream()
                .map(def -> enumDefinitionMapper.map(mappingContext, def))
                .map(dataModel -> FreeMarkerTemplateFilesCreator.create(mappingContext, FreeMarkerTemplateType.ENUM, dataModel))
                .toList();
    }

}
