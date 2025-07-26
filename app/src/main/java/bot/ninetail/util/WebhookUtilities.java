package bot.ninetail.util;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.logger.LogLevel;
import bot.ninetail.core.logger.Logger;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@UtilityClass
public final class WebhookUtilities {
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
                    Logger.log(LogLevel.INFO, "Sent webhook message as %s in guild %s", 
                        member.getEffectiveName(), guild.getName()
                    );
                }, 
                error -> {
                    event.getHook().editOriginal(String.format("Failed to send message: **%s**", member.getEffectiveName())).queue();
                    Logger.log(LogLevel.ERROR, "Failed to send webhook message in guild %s: %s", 
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
                    Logger.log(LogLevel.INFO, "Sent webhook member.getEffectiveName()message as %s in guild %s", 
                        username, guild.getName()
                    );
                }, 
                error -> {
                    event.getHook().editOriginal(String.format("Failed to send message: **%s**", username)).queue();
                    Logger.log(LogLevel.ERROR, "Failed to send webhook message in guild %s: %s", 
                        username, error.getMessage()
                    );
                }
            );
    }
}
