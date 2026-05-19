package no.nav.foreldrepenger.graphql.codegen.generators.impl;

import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.GENERATE_JACKSON3;
import static no.nav.foreldrepenger.graphql.codegen.model.DataModelFields.PACKAGE;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.foreldrepenger.graphql.codegen.generators.FilesGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateFilesCreator;
import no.nav.foreldrepenger.graphql.codegen.generators.FreeMarkerTemplateType;
import no.nav.foreldrepenger.graphql.codegen.mapper.DataModelMapper;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;

/**
 * Generates a JacksonTypeIdResolver file
 */
public class JacksonTypeIdResolverGenerator implements FilesGenerator {

    private static final String CLASS_NAME_GRAPHQL_JACKSON_TYPE_ID_RESOLVER = "GraphqlJacksonTypeIdResolver";

    private final MappingContext mappingContext;

    public JacksonTypeIdResolverGenerator(MappingContext mappingContext) {
        this.mappingContext = mappingContext;
    }

    @Override
    public List<File> generate() {
        List<File> generatedFiles = new ArrayList<>(1);
        if (Boolean.TRUE.equals(mappingContext.getGenerateJacksonTypeIdResolver())) {
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
            dataModel.put(CLASS_NAME, CLASS_NAME_GRAPHQL_JACKSON_TYPE_ID_RESOLVER);
            dataModel.put(GENERATE_JACKSON3, Boolean.TRUE.equals(mappingContext.getGenerateJackson3()));
            var file = FreeMarkerTemplateFilesCreator.create(mappingContext, FreeMarkerTemplateType.JACKSON_TYPE_ID_RESOLVER, dataModel);
            generatedFiles.add(file);
        }
        return generatedFiles;
    }

}
