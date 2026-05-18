package no.nav.foreldrepenger.graphql;

/**
 * Class that represents a key-value pair.
 *
 * @param <K> key
 * @param <V> value
 */
public record Pair<K, V>(K key, V value) {

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
