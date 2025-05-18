package bot.ninetail.commands.social;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.annotation.Nonnull;

import bot.ninetail.social.CoinsRegistry;
import bot.ninetail.structures.commands.SocialCommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command for obtaining daily 100 coins.
 * 
 * @implements SocialCommand
 */
public final class Daily implements SocialCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private Daily() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        try (Connection cn = CoinsRegistry.getInstance().getData().getConnection()) {
            PreparedStatement statement = cn.prepareStatement("""
              INSERT INTO user_wallet (user_id,balance,last_claim)
              VALUES (?, 100, now())
              ON CONFLICT (user_id)
              DO UPDATE SET
                 balance    = user_wallet.balance + 100,
                 last_claim = now()
              WHERE now() - user_wallet.last_claim >= INTERVAL '24 hours'
              RETURNING balance, now() - last_claim < INTERVAL '5 seconds' AS claimed
            """);
            statement.setLong(1, event.getUser().getIdLong());
            ResultSet rs = statement.executeQuery(); 
            rs.next();
            long balance = rs.getLong("balance");

            if (rs.getBoolean("claimed")) 
                event.reply(String.format("💰 You claimed 100 coins! Balance: **%d**", balance)).queue();
            else
                event.reply("⏳ You already claimed today.").setEphemeral(true).queue();
        } catch (SQLException e) { 
            event.reply("DB error").setEphemeral(true).queue(); 
        }
    }
}
