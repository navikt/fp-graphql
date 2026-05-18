package no.nav.foreldrepenger.graphql.codegen.mapper;

import java.util.List;
import java.util.StringJoiner;

/**
 * As per https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.6.1 following n
 */
public class ValueFormatter {

    private static final String FORMATTER_TO_STRING = "?toString";
    private static final String FORMATTER_TO_ARRAY = "?toArray";
    private static final String FORMATTER_TO_ARRAY_OF_STRINGS = "?toArrayOfStrings";
    private static final String NULL_VALUE = "null";
    private static final String EMPTY_LIST = "java.util.Collections.emptyList()";

    private ValueFormatter() {
    }

    public static String format(String value, String formatter) {
        return FORMATTER_TO_STRING.equals(formatter) ? "\"" + value + "\"" : value;
    }

    /**
     * Format a list of values to a single string according to a formatter
     *
     * @param values    values to be formatted
     * @param formatter value formatter
     * @return formatted string
     */
    public static String formatList(List<String> values, String formatter) {
        if (values == null) {
            return format(NULL_VALUE, formatter);
        }
        if (formatter == null) {
            if (values.isEmpty()) {
                return EMPTY_LIST;
            } else {
                var listJoiner = getListJoiner();
                values.forEach(listJoiner::add);
                return listJoiner.toString();
            }
        }
        switch (formatter) {
            case FORMATTER_TO_ARRAY_OF_STRINGS:
                var arrayOfStringsJoiner = getArrayJoiner();
                values.forEach(newElement -> arrayOfStringsJoiner.add(
                        format(newElement, FORMATTER_TO_STRING)));
                return arrayOfStringsJoiner.toString();
            case FORMATTER_TO_ARRAY:
            default:
                var arrayJoiner = getArrayJoiner();
                values.forEach(arrayJoiner::add);
                return arrayJoiner.toString();
        }
    }

    private static StringJoiner getListJoiner() {
        return new StringJoiner(", ", "java.util.Arrays.asList(", ")");
    }

    private static StringJoiner getArrayJoiner() {
        return new StringJoiner(", ", "{", "}");
    }


}
