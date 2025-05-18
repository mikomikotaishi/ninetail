package bot.ninetail.social;

import javax.sql.DataSource;

import jakarta.annotation.Nonnull;

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
     * Gets the singleton instance of CoinsRegistry.
     * 
     * @param db The data source to use if creating a new instance
     * @return The singleton instance
     */
    public static synchronized CoinsRegistry getInstance(@Nonnull DataSource db) {
        if (instance == null)
            instance = new CoinsRegistry(db);
        return instance;
    }
    
    /**
     * Gets the singleton instance of CoinsRegistry.
     * Note: This method should only be called after the registry has been initialised with a data source.
     * 
     * @return The singleton instance
     * @throws IllegalStateException if the registry has not been initialised
     */
    public static CoinsRegistry getInstance() {
        if (instance == null)
            throw new IllegalStateException("CoinsRegistry has not been initialized with a data source");
        return instance;
    }

    /**
     * Obtains the DataSource
     * 
     * @return 
     */
    public DataSource getData() {
        return db;
    }
}
