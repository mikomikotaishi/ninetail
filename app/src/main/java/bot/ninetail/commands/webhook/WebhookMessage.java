package bot.ninetail.commands.webhook;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.commands.WebhookCommand;
import bot.ninetail.webhook.WebhookUtilities;

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
    @Nonnull
    private static final Logger LOGGER = System.getLogger(WebhookMessage.class.getName());

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
        LOGGER.log(Level.INFO, "Global impersonate user command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild().getName(),
            event.getGuild().getId()
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
                LOGGER.log(Level.DEBUG, "Using existing impersonator webhook in #{0} of guild {1}",
                    channel.getName(), guild.getName()
                );
                WebhookUtilities.sendImpersonatedMessage(event, webhook, username, avatarUrl, message, guild);
            } else {
                LOGGER.log(Level.INFO, "Creating impersonator webhook in #{0} of guild {1}",
                    channel.getName(), guild.getName()
                );
                channel.createWebhook(IMPERSONATOR_WEBHOOK_NAME).queue(
                    newWebhook -> WebhookUtilities.sendImpersonatedMessage(event, newWebhook, username, avatarUrl, message, guild),
                    error -> {
                        event.getHook().editOriginal(String.format("Failed to create webhook: %s", error.getMessage())).queue();
                        LOGGER.log(Level.ERROR, "Failed to create webhook in guild {0}: {1}", 
                            guild.getName(), error.getMessage()
                        );
                    }
                );
            }
        }, error -> {
            event.getHook().editOriginal(String.format("Failed to retrieve webhooks: %s", error.getMessage())).queue();
            LOGGER.log(Level.ERROR, "Failed to retrieve webhooks for guild {0}: {1}", 
                guild.getName(), error.getMessage()
            );
        });
    }
}
