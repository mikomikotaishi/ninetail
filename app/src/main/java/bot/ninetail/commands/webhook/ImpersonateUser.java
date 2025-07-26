package bot.ninetail.commands.webhook;

import java.util.function.Consumer;

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
public final class ImpersonateUser implements WebhookCommand {
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
        Logger.log(LogLevel.INFO, "Global impersonate user command invoked by %s (%s) of guild %s (%s)", 
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
        String userId  = event.getOption("id").getAsString();
        @Nonnull 
        String message = event.getOption("message").getAsString();

        event.deferReply(true).queue();

        channel.retrieveWebhooks().queue(webhooks -> {
            Webhook webhook = webhooks.stream()
                .filter(w -> w.getName().equals(IMPERSONATOR_WEBHOOK_NAME))
                .findFirst()
                .orElse(null);

            Consumer<Webhook> sendWithWebhook = hook -> 
                event.getJDA().retrieveUserById(userId).queue(
                    user -> {
                        String username  = user.getEffectiveName();
                        String avatarUrl = user.getAvatarUrl();
                        WebhookUtilities.sendImpersonatedMessage(event, hook, username, avatarUrl, message, guild);
                    }, 
                    error -> {
                        event.getHook().editOriginal("Couldn't fetch user: " + error.getMessage()).queue();
                        Logger.log(LogLevel.ERROR, "Failed to fetch user %s: %s", 
                            userId, error.getMessage()
                        );
                    }
                );

            if (webhook != null) {
                Logger.log(LogLevel.DEBUG, "Using existing impersonator webhook in #%s of guild %s",
                    channel.getName(), guild.getName()
                );
                sendWithWebhook.accept(webhook);
            } else {
                Logger.log(LogLevel.INFO, "Creating impersonator webhook in #%s of guild %s",
                    channel.getName(), guild.getName()
                );
                channel.createWebhook(IMPERSONATOR_WEBHOOK_NAME).queue(
                    newWebhook -> sendWithWebhook.accept(newWebhook),
                    error -> {
                        event.getHook().editOriginal(String.format("Failed to create webhook: %s", error.getMessage())).queue();
                        Logger.log(LogLevel.ERROR, "Failed to create webhook in guild %s: %s", 
                            guild.getName(), error.getMessage()
                        );
                    }
                );
            }
        }, error -> {
            event.getHook().editOriginal("Failed to retrieve webhooks: " + error.getMessage()).queue();
            Logger.log(LogLevel.ERROR, "Failed to retrieve webhooks for guild %s: %s", 
                guild.getName(), error.getMessage()
            );
        });
    }
}
