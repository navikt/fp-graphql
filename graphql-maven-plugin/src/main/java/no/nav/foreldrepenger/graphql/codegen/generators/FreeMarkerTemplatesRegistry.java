package no.nav.foreldrepenger.graphql.codegen.generators;

import java.io.IOException;
import java.util.EnumMap;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.PlainTextOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import no.nav.foreldrepenger.graphql.codegen.GraphQLCodegen;
import no.nav.foreldrepenger.graphql.codegen.model.exception.UnableToLoadFreeMarkerTemplateException;

class FreeMarkerTemplatesRegistry {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final Version FREEMARKER_TEMPLATE_VERSION = Configuration.VERSION_2_3_34;

    private static final EnumMap<FreeMarkerTemplateType, Template> templates =
            new EnumMap<>(FreeMarkerTemplateType.class);

    private static final Configuration configuration;

    static {
        try {
            configuration = buildFreeMarkerTemplateConfiguration(
                    new ClassTemplateLoader(GraphQLCodegen.class.getClassLoader(), ""));
            for (var templateType : FreeMarkerTemplateType.values()) {
                templates.put(templateType, configuration.getTemplate(
                        "templates/java-lang/" + templateType.name().toLowerCase() + ".ftl"));
            }
        } catch (IOException e) {
            throw new UnableToLoadFreeMarkerTemplateException(e);
        }
    }

    private FreeMarkerTemplatesRegistry() {
    }

    public static Template getTemplate(FreeMarkerTemplateType templateType) {
        return templates.get(templateType);
    }

    private static Configuration buildFreeMarkerTemplateConfiguration(TemplateLoader templateLoader) {
        var cfg = new Configuration(FREEMARKER_TEMPLATE_VERSION);
        cfg.setTemplateLoader(templateLoader);
        cfg.setDefaultEncoding(DEFAULT_ENCODING);
        cfg.setOutputFormat(PlainTextOutputFormat.INSTANCE);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        return cfg;
    }

}
