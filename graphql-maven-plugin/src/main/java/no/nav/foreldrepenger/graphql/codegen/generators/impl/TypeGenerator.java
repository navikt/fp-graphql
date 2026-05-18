package no.nav.foreldrepenger.graphql.codegen.generators.impl;

import java.io.File;
import java.util.List;

import no.nav.foreldrepenger.graphql.codegen.generators.FilesGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateFilesCreator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateType;
import no.nav.foreldrepenger.graphql.codegen.mapper.DataModelMapperFactory;
import no.nav.foreldrepenger.graphql.codegen.mapper.TypeDefinitionToDataModelMapper;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;

/**
 * Generates files for types
 */
public class TypeGenerator implements FilesGenerator {

    private final MappingContext mappingContext;
    private final TypeDefinitionToDataModelMapper typeDefinitionMapper;

    public TypeGenerator(MappingContext mappingContext, DataModelMapperFactory dataModelMapperFactory) {
        this.mappingContext = mappingContext;
        this.typeDefinitionMapper = dataModelMapperFactory.getTypeDefinitionMapper();
    }

    @Override
    public List<File> generate() {
        return mappingContext.getDocument().getTypeDefinitions().stream()
                .map(def -> typeDefinitionMapper.map(mappingContext, def))
                .map(dataModel -> FreeMarkerTemplateFilesCreator.create(mappingContext, FreeMarkerTemplateType.TYPE, dataModel))
                .toList();
    }
}
