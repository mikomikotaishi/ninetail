package bot.ninetail.social;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;

import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

/**
 * Registry for managing user coins.
 * Implemented as a thread-safe singleton.
 */
public class CoinsRegistry {
    /**
     * The singleton instance of CoinsRegistry.
     */
    private static volatile CoinsRegistry instance;
    
    /**
     * The data source used by this registry.
     */
    @Nonnull private final DataSource db;
    
    /**
     * Private constructor to prevent instantiation.
     * 
     * @param db The data source to use
     */
    private CoinsRegistry(@Nonnull DataSource db) {
        this.db = db;
    }

    /**
     * Initialises the database used by the coin registry.
     * 
     * @param dataSource
     * 
     * @throws SQLException
     */
    public static void initDatabase(DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection();
            Statement statement = conn.createStatement()) {
            
            statement.execute("""
                CREATE TABLE IF NOT EXISTS user_wallet (
                    user_id BIGINT PRIMARY KEY,
                    balance BIGINT NOT NULL DEFAULT 0,
                    last_claim TIMESTAMP NOT NULL DEFAULT NOW()
                )
            """);
            
            Logger.log(LogLevel.INFO, "Database tables initialised successfully");
        } catch (PSQLException e) {
            if (PSQLState.CONNECTION_FAILURE.getState().equals(e.getSQLState()))
                Logger.log(LogLevel.ERROR, "Database connection failed: " + e.getMessage());
            else
                Logger.log(LogLevel.ERROR, "PostgreSQL error: " + e.getMessage() + " (SQL State: " + e.getSQLState() + ")");
            throw e;
        }
    }
    
    /**
     * Initialises the singleton instance. Should be called once at application startup.
     * 
     * @param db The DataSource to use
     * 
     * @throws IllegalStateException if the instance is already initialised
     */
    public static synchronized void init(@Nonnull DataSource db) {
        if (instance != null)
            throw new IllegalStateException("CoinsRegistry is already initialised");
        instance = new CoinsRegistry(db);
    }

    /**
     * Returns the singleton instance.
     * 
     * @return The initialised CoinsRegistry
     * 
     * @throws IllegalStateException if called before initialisation
     */
    public static CoinsRegistry getInstance() {
        if (instance == null)
            throw new IllegalStateException("CoinsRegistry has not been initialized");
        return instance;
    }

    /**
     * Obtains the DataSource.
     * 
     * @return The data source itself.
     */
    public DataSource getData() {
        return db;
    }
}
