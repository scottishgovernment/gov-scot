package scot.gov.publications.repo;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 *  Utility to load SQL queries resource files.
 */
class QueryLoader {

    /**
     * Load an SQL query.
     *
     * @param path Path to load the SQL from
     * @return The SQL query contained in the file.
     * @throws IllegalArgumentException If no such file exists.
     */
    String loadSQL(String path) {
        try {
            InputStream inputStream = QueryLoader.class.getResourceAsStream(path);
            if (inputStream == null) {
                throw new IllegalArgumentException("No such sql query on the classpath: " + path);
            }
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load SQL query from classpath", e);
        }
    }

}
