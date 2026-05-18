package no.nav.foreldrepenger.graphql.codegen.model;

import java.util.List;

/**
 * Freemarker-understandable format of enum value definition
 *
 * @author kobylynskyi
 */
public record EnumValueDefinition(String javaName, String graphqlName, List<String> javaDoc, DeprecatedDefinition deprecated) {

}
