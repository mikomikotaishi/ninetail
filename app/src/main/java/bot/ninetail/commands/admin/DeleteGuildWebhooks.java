package bot.ninetail.commands.admin;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.commands.AdminCommand;
import bot.ninetail.structures.commands.WebhookCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

/**
 * Command to delete all webhooks in a guild.
 * 
 * @implements AdminCommand
 * @implements WebhookCommand
 */
@UtilityClass
public final class DeleteGuildWebhooks implements AdminCommand, WebhookCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(DeleteGuildWebhooks.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "Delete guild webhooks command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild().getName(),
            event.getGuild().getId()
        );

        @Nonnull Guild guild = event.getGuild();
        
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        
        if (!event.getMember().hasPermission(Permission.MANAGE_WEBHOOKS)) {
            LOGGER.log(Level.INFO, "Attempted (failed) webhook deletion by {0} ({1})", event.getUser().getGlobalName(), event.getUser().getId());
            hook.sendMessage("You do not have the required permissions to delete webhooks in this server.").queue();
            return;
        }

        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_WEBHOOKS)) {
            LOGGER.log(Level.INFO, "Attempted (failed) webhook deletion by {0} ({1}) - Bot lacks permission", event.getUser().getGlobalName(), event.getUser().getId());
            hook.sendMessage("I don't have the required permissions to delete webhooks in this server.").queue();
            return;
        }

        AtomicInteger deletedCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        
        hook.sendMessage("Retrieving and deleting all webhooks in this server...").queue();
        
        guild.retrieveWebhooks().queue(webhooks -> {
            int totalWebhooks = webhooks.size();
            
            if (totalWebhooks == 0) {
                hook.editOriginal("No webhooks found in this server.").queue();
                return;
            }
            
            for (Webhook webhook: webhooks) {
                webhook.delete().queue(
                    success -> {
                        LOGGER.log(Level.INFO, "Deleted webhook: {0} in guild: {1} ({2})", webhook.getName(), guild.getName(), guild.getId());
                        
                        int deleted = deletedCount.incrementAndGet();
                        if (deleted + failedCount.get() == totalWebhooks) {
                            hook.editOriginal(String.format("Webhook cleanup completed. Deleted %d webhooks, failed to delete %d webhooks.", 
                                                            deleted, failedCount.get())).queue();
                        }
                    },
                    error -> {
                        LOGGER.log(Level.ERROR, "Failed to delete webhook: {0} in guild: {1} ({2}) - {3}", webhook.getName(), guild.getName(), guild.getId(), error.getMessage());
                        
                        int failed = failedCount.incrementAndGet();
                        if (deletedCount.get() + failed == totalWebhooks) {
                            hook.editOriginal(String.format("Webhook cleanup completed. Deleted %d webhooks, failed to delete %d webhooks.", 
                                                            deletedCount.get(), failed)).queue();
                        }
                    }
                );
            }
        }, error -> {
            LOGGER.log(Level.ERROR, "Failed to retrieve webhooks for guild: {0} ({1}) - {2}", guild.getName(), guild.getId(), error.getMessage());
            hook.editOriginal("Failed to retrieve webhooks: " + error.getMessage()).queue();
        });
    }
}
