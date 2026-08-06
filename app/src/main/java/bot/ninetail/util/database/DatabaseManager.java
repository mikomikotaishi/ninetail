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
        String url = ConfigLoader.getCoinsRegistryDbUrl();
        if (url == null || url.isBlank()) {
            // Reported here rather than left to the connection pool, whose own message does not
            // say which setting is missing.
            throw new IllegalStateException(
                "DB_URL is not set in config.properties. DB_URL, DB_USERNAME and DB_PASSWORD are all required."
            );
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
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
