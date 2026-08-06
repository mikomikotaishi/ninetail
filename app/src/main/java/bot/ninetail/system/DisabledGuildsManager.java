package bot.ninetail.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import lombok.experimental.UtilityClass;

/**
 * Manages the guilds in which the bot has been disabled.
 * A disabled guild is one where the bot ignores everything except the commands reserved for the
 * bot master, so that it can be re-enabled again.
 */
@UtilityClass
public final class DisabledGuildsManager {
    @Nonnull
    private static final System.Logger LOGGER = System.getLogger(DisabledGuildsManager.class.getName());

    /**
     * In-memory cache of disabled guild IDs for fast lookup.
     * Every message and command the bot receives is checked against this, so it is not read from
     * the database each time.
     */
    @Nonnull
    private static final Set<Long> disabledGuildCache = ConcurrentHashMap.newKeySet();

    /**
     * Loads all disabled guilds from the database into the memory cache.
     */
    public static void loadDisabledGuilds() {
        disabledGuildCache.clear();

        if (BotDatabaseManager.getInstance() == null || BotDatabaseManager.getInstance().getData() == null) {
            LOGGER.log(System.Logger.Level.WARNING, "Database not available, no disabled guilds loaded");
            return;
        }

        try (Connection conn = BotDatabaseManager.getInstance().getData().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT guild_id FROM disabled_guilds")) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                disabledGuildCache.add(rs.getLong("guild_id"));
            }

            LOGGER.log(System.Logger.Level.INFO, "Loaded {0} disabled guilds from database", disabledGuildCache.size());

        } catch (SQLException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to load disabled guilds from database: {0}", e.getMessage());
        }
    }

    /**
     * Checks whether the bot is disabled in a guild.
     *
     * @param guildId The guild ID to check.
     * @return Whether the bot is disabled in that guild.
     */
    public static boolean isDisabled(long guildId) {
        return disabledGuildCache.contains(guildId);
    }

    /**
     * Disables the bot in a guild.
     *
     * @param guildId The guild ID to disable the bot in.
     * @param disabledBy The ID of the user who disabled the bot.
     * @param reason The reason the bot was disabled (optional).
     * @return Whether the guild was disabled, false if it already was or the database is unavailable.
     */
    public static boolean disableGuild(long guildId, long disabledBy, @Nullable String reason) {
        if (BotDatabaseManager.getInstance() == null || BotDatabaseManager.getInstance().getData() == null) {
            LOGGER.log(System.Logger.Level.ERROR, "Database not available, cannot disable guild");
            return false;
        }

        try (Connection conn = BotDatabaseManager.getInstance().getData().getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO disabled_guilds (guild_id, disabled_by, reason) VALUES (?, ?, ?) ON CONFLICT DO NOTHING"
            )) {

            stmt.setLong(1, guildId);
            stmt.setLong(2, disabledBy);
            stmt.setString(3, reason);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                disabledGuildCache.add(guildId);
                LOGGER.log(System.Logger.Level.INFO, "Bot disabled in guild {0} by {1}. Reason: {2}",
                    guildId, disabledBy, reason != null ? reason : "No reason provided"
                );
                return true;
            } else {
                LOGGER.log(System.Logger.Level.WARNING, "Bot was already disabled in guild {0}", guildId);
                return false;
            }

        } catch (SQLException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to disable guild {0}: {1}", guildId, e.getMessage());
            return false;
        }
    }

    /**
     * Re-enables the bot in a guild.
     *
     * @param guildId The guild ID to re-enable the bot in.
     * @return Whether the guild was re-enabled, false if it was not disabled or the database is unavailable.
     */
    public static boolean enableGuild(long guildId) {
        if (BotDatabaseManager.getInstance() == null || BotDatabaseManager.getInstance().getData() == null) {
            LOGGER.log(System.Logger.Level.ERROR, "Database not available, cannot enable guild");
            return false;
        }

        try (Connection conn = BotDatabaseManager.getInstance().getData().getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM disabled_guilds WHERE guild_id = ?")) {

            stmt.setLong(1, guildId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                disabledGuildCache.remove(guildId);
                LOGGER.log(System.Logger.Level.INFO, "Bot re-enabled in guild {0}", guildId);
                return true;
            } else {
                LOGGER.log(System.Logger.Level.WARNING, "Bot was not disabled in guild {0}", guildId);
                return false;
            }

        } catch (SQLException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to enable guild {0}: {1}", guildId, e.getMessage());
            return false;
        }
    }

    /**
     * Gets the total number of guilds the bot is disabled in.
     *
     * @return The number of disabled guilds.
     */
    public static int getDisabledGuildCount() {
        return disabledGuildCache.size();
    }
}
