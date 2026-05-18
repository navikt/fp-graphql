package no.nav.foreldrepenger.graphql.codegen.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;

import no.nav.foreldrepenger.graphql.codegen.model.exception.UnableToCreateDirectoryException;
import no.nav.foreldrepenger.graphql.codegen.model.exception.UnableToDeleteDirectoryException;

/**
 * Various utilities
 *
 * @author kobylynskyi
 */
public final class Utils {

    private static final Set<String> GRAPHQL_OPERATIONS = Set.of("QUERY", "MUTATION", "SUBSCRIPTION");

    private Utils() {
    }

    /**
     * Check whether the given type definition is either Query or Mutation or Subscription.
     *
     * @param typeDef type definition name
     * @return {@code true} if the given type definition is GraphQL operation
     */
    public static boolean isGraphqlOperation(String typeDef) {
        var typeDefNormalized = typeDef.toUpperCase();
        return GRAPHQL_OPERATIONS.contains(typeDefNormalized);
    }

    /**
     * Capitalize a string. Make first letter as capital
     *
     * @param stringToCapitalize string to capitalize
     * @return capitalized string
     */
    public static String capitalize(String stringToCapitalize) {
        var chars = stringToCapitalize.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * Inverted copy of org.apache.commons.lang3.StringUtils.isBlank(CharSequence cs)
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code false} if the CharSequence is null, empty or whitespace only
     */
    public static boolean isNotBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return false;
        }
        for (var i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get content of the file.
     *
     * @param filePath path of the file.
     * @return content of the file.
     * @throws IOException unable to read the file.
     */
    public static String getFileContent(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Deletes a given directory recursively.
     *
     * @param dir directory to delete
     * @throws UnableToDeleteDirectoryException if unable to delete a directory
     */
    public static void deleteDir(File dir) {
        if (!dir.exists()) {
            return;
        }
        var files = dir.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (var subFile : files) {
                if (subFile.isDirectory()) {
                    deleteDir(subFile);
                } else {
                    try {
                        Files.delete(subFile.toPath());
                    } catch (IOException e) {
                        throw new UnableToDeleteDirectoryException(e);
                    }
                }
            }
        }
        try {
            Files.delete(dir.toPath());
        } catch (IOException e) {
            throw new UnableToDeleteDirectoryException(e);
        }
    }

    /**
     * Create directory if it is absent. Will do nothing if it is already present.
     *
     * @param dir to create if it is absent.
     * @throws UnableToCreateDirectoryException if unable to create a directory
     */
    public static void createDirIfAbsent(File dir) {
        if (dir.exists()) {
            return;
        }
        try {
            Files.createDirectories(dir.toPath());
        } catch (IOException e) {
            throw new UnableToCreateDirectoryException(dir.getName(), e);
        }
    }

    /**
     * Check if collection is empty.
     *
     * @param collection that will be checked for emptiness
     * @return true if collection is null or empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Replace leading annotation (@) sign
     *
     * @param value annotation value with/without @ sign
     * @return value without leading @ sign
     */
    public static String replaceLeadingAtSign(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("^@+", "");
    }
}
