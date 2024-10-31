package ch.hearc.ig.orderresto.persistence.mappers;

import ch.hearc.ig.orderresto.persistence.IdentityMap;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

public abstract class BaseMapper<T> {
    protected String url;
    protected String username;
    protected String password;
    protected final IdentityMap<T> identityMap = new IdentityMap<>();

    public BaseMapper() {
        loadProperties();
    }

    protected void loadProperties() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            url = prop.getProperty("db.url");
            username = prop.getProperty("db.username");
            password = prop.getProperty("db.password");
        } catch (IOException ex) {
            System.err.println("Error loading properties: " + ex.getMessage());
            throw new RuntimeException("Database configuration error", ex);
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    protected Optional<T> findInCache(Long id) {
        return Optional.ofNullable(identityMap.get(id));
    }

    protected void addToCache(Long id, T entity) {
        identityMap.put(id, entity);
    }

    protected void updateInCache(Long id, T entity) {
        identityMap.put(id, entity);
    }

    protected void removeFromCache(Long id) {
        identityMap.remove(id);
    }
}

