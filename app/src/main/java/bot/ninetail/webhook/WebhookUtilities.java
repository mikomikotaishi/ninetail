package bot.ninetail.webhook;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@UtilityClass
public final class WebhookUtilities {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(WebhookUtilities.class.getName());

    /**
     * Sends a message through the webhook with a member's name and avatar
     * 
     * @param event The event that triggered the command
     * @param webhook The webhook
     * @param member The member of the guild to impersonate
     * @param message The message the webhook will send
     * @param guild The guild to send the message in
     */
    public static void sendImpersonatedMessage(@Nonnull SlashCommandInteractionEvent event, Webhook webhook, Member member, String message, Guild guild) {
        webhook.sendMessage(message)
            .setUsername(member.getEffectiveName())
            .setAvatarUrl(member.getEffectiveAvatarUrl())
            .queue(
                success -> {
                    event.getHook().editOriginal(String.format("Message sent as **%s**", member.getEffectiveName())).queue();
                    LOGGER.log(Level.INFO, "Sent webhook message as {0} in guild {1}", 
                        member.getEffectiveName(), guild.getName()
                    );
                }, 
                error -> {
                    event.getHook().editOriginal(String.format("Failed to send message: **%s**", member.getEffectiveName())).queue();
                    LOGGER.log(Level.ERROR, "Failed to send webhook message in guild {0}: {1}", 
                        guild.getName(), error.getMessage()
                    );
                }
            );
    }

    /**
     * Sends a message through the webhook with a user's name and avatar
     * 
     * @param event The event that triggered the command
     * @param webhook The webhook
     * @param username The display username of the webhook
     * @param avatarUrl The display avatar of the webhook
     * @param message The message the webhook will send
     * @param guild The guild to send the message in
     */
    public static void sendImpersonatedMessage(@Nonnull SlashCommandInteractionEvent event, Webhook webhook, String username, String avatarUrl, String message, Guild guild) {
        webhook.sendMessage(message)
            .setUsername(username)
            .setAvatarUrl(avatarUrl)
            .queue(
                success -> {
                    event.getHook().editOriginal(String.format("Message sent as **%s**", username)).queue();
                    LOGGER.log(Level.INFO, "Sent webhook message as {0} in guild {1}", 
                        username, guild.getName()
                    );
                }, 
                error -> {
                    event.getHook().editOriginal(String.format("Failed to send message: **%s**", username)).queue();
                    LOGGER.log(Level.ERROR, "Failed to send webhook message in guild {0}: {1}", 
                        guild.getName(), error.getMessage()
                    );
                }
            );
    }
}
