package bot.ninetail.commands.social;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.commands.SocialCommand;
import bot.ninetail.system.BotDatabaseManager;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command for obtaining daily 100 coins.
 * 
 * @implements SocialCommand
 */
@UtilityClass
public final class Daily implements SocialCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(Daily.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "Daily command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );

        if (BotDatabaseManager.getInstance() == null || BotDatabaseManager.getInstance().getData() == null) {
            event.reply("Sorry, database features are currently unavailable.").setEphemeral(true).queue();
            return;
        }

        try (Connection conn = BotDatabaseManager.getInstance().getData().getConnection()) {
            PreparedStatement statement = conn.prepareStatement("""
            INSERT INTO user_wallet (user_id,balance,last_claim)
            VALUES (?, 100, now())
            ON CONFLICT (user_id)
            DO UPDATE SET
                balance    = user_wallet.balance + 100,
                last_claim = now()
            WHERE now() - user_wallet.last_claim >= INTERVAL '24 hours'
            RETURNING balance, now() - last_claim < INTERVAL '5 seconds' AS claimed
            """
            );
            statement.setLong(1, event.getUser().getIdLong());
            ResultSet result = statement.executeQuery(); 
            result.next();
            long balance = result.getLong("balance");

            if (result.getBoolean("claimed")) {
                event.reply(String.format("💰 You claimed 100 coins! Balance: **%d**", balance)).queue();
            } else {
                try {
                    PreparedStatement timeStatement = conn.prepareStatement(
                        "SELECT 24 - EXTRACT(HOUR FROM now() - last_claim) AS hours_left, " +
                        "60 - EXTRACT(MINUTE FROM now() - last_claim) AS minutes_left " +
                        "FROM user_wallet WHERE user_id = ?"
                    );
                    timeStatement.setLong(1, event.getUser().getIdLong());
                    ResultSet timeResult = timeStatement.executeQuery();
                    
                    if (timeResult.next()) {
                        int hoursLeft = Math.max(0, timeResult.getInt("hours_left"));
                        int minutesLeft = Math.max(0, timeResult.getInt("minutes_left"));
                        event.reply(
                            String.format("⏳ You already claimed today. Next claim available in %d hours and %d minutes.", hoursLeft, minutesLeft)
                        ).setEphemeral(true).queue();
                    } else {
                        event.reply("⏳ You already claimed today.").setEphemeral(true).queue();
                    }
                } catch (SQLException e) {
                    event.reply("⏳ You already claimed today.").setEphemeral(true).queue();
                }
            }
        } catch (SQLException e) { 
            LOGGER.log(Level.ERROR, "Database error in Daily command: {0}", e.getMessage());
            event.reply("Sorry, I couldn't process your daily coins right now. Try again later!").setEphemeral(true).queue();
        }
    }
}
