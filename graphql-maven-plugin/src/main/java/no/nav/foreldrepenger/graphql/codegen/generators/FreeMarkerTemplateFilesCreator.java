package no.nav.foreldrepenger.graphql.codegen.generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;

import freemarker.template.Template;
import no.nav.foreldrepenger.graphql.codegen.model.DataModelFields;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;
import no.nav.foreldrepenger.graphql.codegen.model.exception.UnableToCreateFileException;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

public class FreeMarkerTemplateFilesCreator {

    private FreeMarkerTemplateFilesCreator() {
    }

    public static File create(MappingContext mappingContext,
                              FreeMarkerTemplateType templateType,
                              Map<String, Object> dataModel) {
        var fileName = dataModel.get(DataModelFields.CLASS_NAME) + ".java";
        var fileOutputDir = getFileTargetDirectory(dataModel, mappingContext.getOutputDirectory());
        var javaSourceFile = new File(fileOutputDir, fileName);

        try {
            if (!javaSourceFile.createNewFile()) {
                throw new FileAlreadyExistsException("File already exists: " + javaSourceFile.getPath());
            }
        } catch (IOException e) {
            throw new UnableToCreateFileException(e);
        }

        try (var fileWriter = new FileWriter(javaSourceFile)) {
            var template = getTemplate(templateType);
            template.process(dataModel, fileWriter);
        } catch (Exception e) {
            throw new UnableToCreateFileException(e);
        }
        return javaSourceFile;
    }

    private static Template getTemplate(FreeMarkerTemplateType templateType) {
        return FreeMarkerTemplatesRegistry.getTemplate(templateType);
    }

    private static File getFileTargetDirectory(Map<String, Object> dataModel, File outputDir) {
        File targetDir;
        var packageName = dataModel.get(DataModelFields.PACKAGE);
        if (packageName != null && Utils.isNotBlank(packageName.toString())) {
            targetDir = new File(outputDir, packageName.toString().replace(".", File.separator));
        } else {
            targetDir = outputDir;
        }
        Utils.createDirIfAbsent(targetDir);
        return targetDir;
    }

}
