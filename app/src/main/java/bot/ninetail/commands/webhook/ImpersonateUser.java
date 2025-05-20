package bot.ninetail.commands.webhook;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.WebhookCommand;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to create a webhook of a user.
 * 
 * @implements Command
 */
public final class ImpersonateUser implements WebhookCommand {
    /**
     * The webhook name used for impersonating users.
     */
    @Nonnull private static final String IMPERSONATOR_WEBHOOK_NAME = "NinetailImpersonator";

    /**
     * Private constructor to prevent instantiation.
     */
    private ImpersonateUser() {}

    /**
     * Invokes the command.
     * 
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("User webhook command invoked by %s (%s) of guild %s (%s)", 
                                              event.getUser().getGlobalName(), 
                                              event.getUser().getId(),
                                              event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                              event.getGuild() != null ? event.getGuild().getId() : "N/A")
        );
        
        Guild guild = event.getGuild();
        TextChannel channel = event.getChannel().asTextChannel();
        Member member = event.getOption("user").getAsMember();
        String message = event.getOption("message").getAsString();

        if (guild == null || member == null) {
            Logger.log(LogLevel.INFO, "Failed to create webhook due to invalid guild or user.");
            event.reply("Invalid guild or user.").setEphemeral(true).queue();
            return;
        }

        event.deferReply(true).queue();

        channel.retrieveWebhooks().queue(webhooks -> {
            Webhook existingWebhook = webhooks.stream()
                .filter(webhook -> webhook.getName().equals(IMPERSONATOR_WEBHOOK_NAME))
                .findFirst()
                .orElse(null);

            if (existingWebhook != null) {
                Logger.log(LogLevel.DEBUG, String.format("Using existing webhook in channel %s of guild %s", 
                                                        channel.getName(), guild.getName()));
                sendImpersonatedMessage(event, existingWebhook, member, message, guild);
            } else {
                Logger.log(LogLevel.INFO, String.format("Creating new impersonation webhook in channel %s of guild %s", 
                                                        channel.getName(), guild.getName()));
                channel.createWebhook(IMPERSONATOR_WEBHOOK_NAME).queue(
                    newWebhook -> sendImpersonatedMessage(event, newWebhook, member, message, guild),
                    error -> {
                        event.getHook().editOriginal(String.format("Failed to create webhook: %s", error.getMessage())).queue();
                        Logger.log(LogLevel.ERROR, String.format("Failed to create webhook in guild %s: %s", guild.getName(), error.getMessage()));
                    }
                );
            }
        }, error -> {
            event.getHook().editOriginal("Failed to retrieve webhooks: " + error.getMessage()).queue();
            Logger.log(LogLevel.ERROR, String.format("Failed to retrieve webhooks for guild %s: %s", guild.getName(), error.getMessage()));
        });
    }

    /**
     * Sends a message through the webhook with the member's name and avatar
     * 
     * @param event
     * @param webhook
     * @param member
     * @param message
     * @param guild
     */
    private static void sendImpersonatedMessage(SlashCommandInteractionEvent event, Webhook webhook, Member member, String message, Guild guild) {
        webhook.sendMessage(message)
            .setUsername(member.getEffectiveName())
            .setAvatarUrl(member.getEffectiveAvatarUrl())
            .queue(success -> {
                event.getHook().editOriginal(String.format("Message sent as %s", member.getEffectiveName())).queue();
                Logger.log(LogLevel.INFO, String.format("Sent webhook message as %s in guild %s", 
                                                    member.getEffectiveName(), guild.getName()));
            }, error -> {
                event.getHook().editOriginal("Failed to send message: " + error.getMessage()).queue();
                Logger.log(LogLevel.ERROR, String.format("Failed to send webhook message in guild %s: %s", 
                                                    guild.getName(), error.getMessage()));
            });
    }
}
