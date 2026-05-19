package no.nav.foreldrepenger.graphql.codegen.model.definitions;

import java.util.Collections;
import java.util.List;

import graphql.language.Comment;
import graphql.language.FieldDefinition;
import no.nav.foreldrepenger.graphql.codegen.utils.Utils;

public class ExtendedFieldDefinition extends FieldDefinition {

    protected ExtendedFieldDefinition(FieldDefinition f) {
        super(f.getName(), f.getType(), f.getInputValueDefinitions(), f.getDirectives(),
                f.getDescription(), f.getSourceLocation(), f.getComments(), f.getIgnoredChars(),
                f.getAdditionalData());
    }

    public List<String> getJavaDoc() {
        var description = getDescription();
        if (description != null && Utils.isNotBlank(description.getContent())) {
            return Collections.singletonList(description.getContent().trim());
        }
        return getComments().stream()
                .map(Comment::getContent).filter(Utils::isNotBlank)
                .map(String::trim)
                .toList();
    }
}
