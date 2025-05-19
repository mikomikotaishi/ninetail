package bot.ninetail.utilities.database;

import java.util.Properties;

import javax.sql.DataSource;

import bot.ninetail.structures.Manager;
import bot.ninetail.system.ConfigLoader;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class DatabaseManager extends Manager {
    /**
     * Private constructor to prevent instantiation.
     */
    private DatabaseManager() {}

    /**
     * Loads the Coins Registry.
     * 
     * @return The DataSource
     */
    public static DataSource loadCoinsRegistry() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigLoader.getCoinsRegistryDbUrl());
        config.setUsername(ConfigLoader.getCoinsRegistryDbUsername());
        config.setPassword(ConfigLoader.getCoinsRegistryDbPassword());
        config.setMaximumPoolSize(10);

        Properties props = new Properties();
        props.setProperty("ApplicationName", "NinetailBot");
        props.setProperty("reWriteBatchedInserts", "true");
        props.setProperty("autoReconnect", "true");
        config.setDataSourceProperties(props);

        return new HikariDataSource(config);
    }
}
