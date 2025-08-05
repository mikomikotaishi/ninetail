package bot.ninetail.system;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import lombok.experimental.UtilityClass;

/**
 * Manages globally banned users from the bot.
 */
@UtilityClass
public final class BannedUsersManager {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(BannedUsersManager.class.getName());

    /**
     * In-memory cache of banned user IDs for fast lookup.
     */
    @Nonnull
    private static final Set<Long> bannedUserCache = ConcurrentHashMap.newKeySet();
    
    /**
     * Loads all banned users from database and config into memory cache.
     */
    public static void loadBannedUsers() {
        bannedUserCache.clear();
        
        // First, load pre-banned IDs from config
        List<Long> prebannedIds = ConfigLoader.getPrebannedUserIds();
        if (!prebannedIds.isEmpty()) {
            bannedUserCache.addAll(prebannedIds);
            LOGGER.log(Level.INFO, "Loaded {0} pre-banned user IDs from config", prebannedIds.size());
        }
        
        // Then, load banned IDs from database
        if (BotDatabaseManager.getInstance() == null || BotDatabaseManager.getInstance().getData() == null) {
            LOGGER.log(Level.WARNING, "Database not available, only config-based banned users loaded");
            return;
        }
        
        try (Connection conn = BotDatabaseManager.getInstance().getData().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM banned_users")) {
            
            ResultSet rs = stmt.executeQuery();
            int dbBannedCount = 0;
            
            while (rs.next()) {
                long userId = rs.getLong("user_id");
                if (bannedUserCache.add(userId)) {
                    dbBannedCount++;
                }
            }
            
            LOGGER.log(Level.INFO, "Loaded {0} banned users from database, {1} total banned users", 
                dbBannedCount, bannedUserCache.size());
            
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Failed to load banned users from database: {0}", e.getMessage());
        }
    }
    
    /**
     * Checks if a user is globally banned.
     * 
     * @param userId The user ID to check
     * @return true if the user is banned, false otherwise
     */
    public static boolean isBanned(long userId) {
        return bannedUserCache.contains(userId);
    }
    
    /**
     * Bans a user globally from the bot.
     * 
     * @param userId The user ID to ban
     * @param bannedBy The ID of the user who issued the ban
     * @param reason The reason for the ban (optional)
     * @return true if the ban was successful, false otherwise
     */
    public static boolean banUser(long userId, long bannedBy, @Nullable String reason) {
        if (BotDatabaseManager.getInstance() == null || BotDatabaseManager.getInstance().getData() == null) {
            LOGGER.log(Level.ERROR, "Database not available, cannot ban user");
            return false;
        }
        
        try (Connection conn = BotDatabaseManager.getInstance().getData().getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO banned_users (user_id, banned_by, reason) VALUES (?, ?, ?) ON CONFLICT DO NOTHING"
            )) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, bannedBy);
            stmt.setString(3, reason);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                bannedUserCache.add(userId);
                LOGGER.log(Level.INFO, "User {0} banned by {1}. Reason: {2}", userId, bannedBy, reason != null ? reason : "No reason provided");
                return true;
            } else {
                LOGGER.log(Level.WARNING, "User {0} was already banned", userId);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Failed to ban user {0}: {1}", userId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Unbans a user globally from the bot.
     * 
     * @param userId The user ID to unban
     * @return true if the unban was successful, false otherwise
     */
    public static boolean unbanUser(long userId) {
        if (BotDatabaseManager.getInstance() == null || BotDatabaseManager.getInstance().getData() == null) {
            LOGGER.log(Level.ERROR, "Database not available, cannot unban user");
            return false;
        }
        
        try (Connection conn = BotDatabaseManager.getInstance().getData().getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM banned_users WHERE user_id = ?")) {
            
            stmt.setLong(1, userId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                bannedUserCache.remove(userId);
                LOGGER.log(Level.INFO, "User {0} unbanned", userId);
                return true;
            } else {
                LOGGER.log(Level.WARNING, "User {0} was not banned", userId);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Failed to unban user {0}: {1}", userId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the total number of banned users.
     * 
     * @return Number of banned users
     */
    public static int getBannedUserCount() {
        return bannedUserCache.size();
    }
    
    /**
     * Adds a pre-banned user from config to the cache (used during startup).
     * 
     * @param userId The user ID to add to cache
     */
    public static void addPrebannedUser(long userId) {
        bannedUserCache.add(userId);
    }
}
