package bot.ninetail.core;

import java.sql.SQLException;

import javax.sql.DataSource;

import bot.ninetail.core.logger.*;
import bot.ninetail.system.BotDatabaseManager;
import bot.ninetail.util.database.DatabaseManager;

import lombok.experimental.UtilityClass;

import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;

/**
 * Class to handle operations with databases.
 * This class is used to handle operations with databases used by the bot.
 */
@UtilityClass
public final class DatabaseHandler {
    /**
     * Loads the database for all resources used by the bot.
     */
    public static void loadDatabase() {
        try {
            Logger.log(LogLevel.INFO, "Initialising database connection...");
            DataSource dataSource = DatabaseManager.loadDatabase();
            BotDatabaseManager.initDatabase(dataSource);
            BotDatabaseManager.init(dataSource);
            Logger.log(LogLevel.INFO, "Database connection successful.");
        } catch (SQLException e) {
            Logger.log(LogLevel.ERROR, "Failed to initialise database due to SQL exception: %s", e.getMessage());
            Logger.log(LogLevel.WARN, "Social commands that require the database will be unavailable!");
        } catch (PoolInitializationException e) {
            Logger.log(LogLevel.ERROR, "Failed to initialise database due to database handling exception: %s", e.getMessage());
            Logger.log(LogLevel.WARN, "Social commands that require the database will be unavailable!");
        } catch (Exception e) {
            Logger.log(LogLevel.ERROR, "Failed to initialise database: %s", e.getMessage());
            Logger.log(LogLevel.WARN, "Social commands that require the database will be unavailable!");
        }
    }
}
