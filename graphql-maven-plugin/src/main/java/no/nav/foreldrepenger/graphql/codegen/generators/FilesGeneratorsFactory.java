package no.nav.foreldrepenger.graphql.codegen.generators;

import java.util.List;

import no.nav.foreldrepenger.graphql.codegen.generators.impl.EnumsGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.impl.InputGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.impl.InterfaceGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.impl.JacksonTypeIdResolverGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.impl.ParametrizedInputGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.impl.RequestResponseGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.impl.ResponseProjectionGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.impl.TypeGenerator;
import no.nav.foreldrepenger.graphql.codegen.generators.impl.UnionGenerator;
import no.nav.foreldrepenger.graphql.codegen.mapper.DataModelMapperFactory;
import no.nav.foreldrepenger.graphql.codegen.model.MappingContext;

/**
 * Factory for building files generators
 */
public class FilesGeneratorsFactory {

    private FilesGeneratorsFactory() {
    }

    /**
     * Factory method for building files generators
     *
     * @param context                Global mapping context
     * @param dataModelMapperFactory Data model mapper factory
     * @return a list of all files generators
     */
    public static List<FilesGenerator> getAll(MappingContext context,
                                              DataModelMapperFactory dataModelMapperFactory) {
        return List.of(
                new EnumsGenerator(context, dataModelMapperFactory),
                new InterfaceGenerator(context, dataModelMapperFactory),
                new TypeGenerator(context, dataModelMapperFactory),
                new ResponseProjectionGenerator(context, dataModelMapperFactory),
                new ParametrizedInputGenerator(context, dataModelMapperFactory),
                new InputGenerator(context, dataModelMapperFactory),
                new UnionGenerator(context, dataModelMapperFactory),
                new RequestResponseGenerator(context, dataModelMapperFactory),
                new JacksonTypeIdResolverGenerator(context));
    }

}
