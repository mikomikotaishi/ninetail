package bot.ninetail.commands.webhook;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.WebhookCommand;
import bot.ninetail.utilities.WebhookUtilities;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to create a webhook of a user.
 * 
 * @implements Command
 */
@UtilityClass
public final class WebhookMessage implements WebhookCommand {
    /**
     * The webhook name used for impersonating users.
     */
    @Nonnull 
    private static final String IMPERSONATOR_WEBHOOK_NAME = "NinetailImpersonator";

    /**
     * Invokes the command.
     * 
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("Global impersonate user command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild().getName(),
                                                event.getGuild().getId())
        );
        
        @Nonnull 
        Guild guild = event.getGuild();
        @Nonnull 
        TextChannel channel = event.getChannel().asTextChannel();
        @Nonnull 
        String username  = event.getOption("username").getAsString();
        @Nonnull 
        String avatarUrl = event.getOption("avatar_url").getAsString();
        @Nonnull 
        String message = event.getOption("message").getAsString();

        event.deferReply(true).queue();

        channel.retrieveWebhooks().queue(webhooks -> {
            Webhook webhook = webhooks.stream()
                    .filter(w -> w.getName().equals(IMPERSONATOR_WEBHOOK_NAME))
                    .findFirst()
                    .orElse(null);

            if (webhook != null) {
                Logger.log(LogLevel.DEBUG, String.format(
                    "Using existing impersonator webhook in #%s of guild %s",
                    channel.getName(), guild.getName())
                );
                WebhookUtilities.sendImpersonatedMessage(event, webhook, username, avatarUrl, message, guild);
            } else {
                Logger.log(LogLevel.INFO, String.format(
                    "Creating impersonator webhook in #%s of guild %s",
                    channel.getName(), guild.getName())
                );
                channel.createWebhook(IMPERSONATOR_WEBHOOK_NAME).queue(
                    newWebhook -> WebhookUtilities.sendImpersonatedMessage(event, webhook, username, avatarUrl, message, guild),
                    error -> {
                        event.getHook().editOriginal(String.format("Failed to create webhook: %s", error.getMessage())).queue();
                        Logger.log(LogLevel.ERROR, String.format("Failed to create webhook in guild %s: %s", guild.getName(), error.getMessage()));
                    }
                );
            }
        }, error -> {
            event.getHook().editOriginal(String.format("Failed to retrieve webhooks: %s", error.getMessage())).queue();
            Logger.log(LogLevel.ERROR, String.format("Failed to retrieve webhooks for guild %s: %s", guild.getName(), error.getMessage()));
        });
    }
}
