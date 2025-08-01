package bot.ninetail.util.database;

import java.util.Properties;

import javax.sql.DataSource;

import bot.ninetail.system.ConfigLoader;

import lombok.experimental.UtilityClass;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Manager for handling databases used by the bot.
 */
@UtilityClass
public final class DatabaseManager {
    /**
     * Loads the Coins Registry.
     * 
     * @return The DataSource
     */
    public static DataSource loadDatabase() {
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
