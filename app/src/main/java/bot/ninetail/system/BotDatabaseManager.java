package bot.ninetail.system;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import jakarta.annotation.Nonnull;

import lombok.Getter;

/**
 * Manager for handling all utilities requiring a database.
 * Implemented as a thread-safe singleton.
 */
public class BotDatabaseManager {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(BotDatabaseManager.class.getName());

    /**
     * The singleton instance of CoinsRegistry.
     */
    @Nonnull
    private static volatile BotDatabaseManager instance;
    
    /**
     * The data source used by this registry.
     */
    @Getter
    @Nonnull 
    private final DataSource data;
    
    /**
     * Private constructor to prevent instantiation.
     * 
     * @param db The data source to use
     */
    private BotDatabaseManager(@Nonnull DataSource data) {
        this.data = data;
    }

    /**
     * Initialises the database used by the coin registry.
     * 
     * @param dataSource
     * 
     * @throws SQLException
     */
    public static void initDatabase(@Nonnull DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection();
            Statement statement = conn.createStatement()) {
            
            // Existing user_wallet table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS user_wallet (
                    user_id BIGINT PRIMARY KEY,
                    balance BIGINT NOT NULL DEFAULT 0,
                    last_claim TIMESTAMP NOT NULL DEFAULT NOW()
                )
            """);
            
            statement.execute("""
                CREATE TABLE IF NOT EXISTS banned_users (
                    user_id BIGINT PRIMARY KEY,
                    banned_by BIGINT NOT NULL,
                    reason TEXT,
                    banned_at TIMESTAMP NOT NULL DEFAULT NOW()
                )
            """);
            
            LOGGER.log(Level.INFO, "Database tables initialized successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Failed to initialize database: {0}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Initialises the singleton instance. Should be called once at application startup.
     * 
     * @param db The DataSource to use
     */
    public static synchronized void init(@Nonnull DataSource db) {
        if (instance == null) {
            instance = new BotDatabaseManager(db);
        }
    }

    /**
     * Returns the singleton instance.
     * 
     * @return The initialised CoinsRegistry
     * 
     * @throws IllegalStateException if called before initialisation
     */
    public static BotDatabaseManager getInstance() {
        if (instance != null) {
            throw new IllegalStateException("CoinsRegistry has not been initialized");
        }
        return instance;
    }
}
