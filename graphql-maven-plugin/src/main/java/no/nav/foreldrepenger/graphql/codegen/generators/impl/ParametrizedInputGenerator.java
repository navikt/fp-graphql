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
import no.nav.foreldrepenger.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

/**
 * Generates files for parametrized inputs
 */
public class ParametrizedInputGenerator implements FilesGenerator {

    private final MappingContext mappingContext;
    private final RequestResponseDefinitionToDataModelMapper requestResponseDefinitionMapper;

    public ParametrizedInputGenerator(MappingContext mappingContext,
                                      DataModelMapperFactory dataModelMapperFactory) {
        this.mappingContext = mappingContext;
        this.requestResponseDefinitionMapper = dataModelMapperFactory.getRequestResponseDefinitionMapper();
    }

    @Override
    public List<File> generate() {
        var doc = mappingContext.getDocument();
        return Stream.concat(
                        doc.getInterfaceDefinitions().stream()
                                .flatMap(def -> generateForFields(def, def.getFieldDefinitions())),
                        doc.getTypeDefinitions().stream()
                                .flatMap(def -> generateForFields(def, def.getFieldDefinitions())))
                .toList();
    }

    private Stream<File> generateForFields(ExtendedDefinition<?, ?> definition,
                                           List<ExtendedFieldDefinition> fieldDefinitions) {
        return fieldDefinitions.stream()
                .filter(fd -> !Utils.isEmpty(fd.getInputValueDefinitions()))
                .map(fd -> {
                    var dataModel = requestResponseDefinitionMapper.mapParametrizedInput(
                            mappingContext, fd, definition);
                    return FreeMarkerTemplateFilesCreator.create(
                            mappingContext, FreeMarkerTemplateType.PARAMETRIZED_INPUT, dataModel);
                });
    }

}
