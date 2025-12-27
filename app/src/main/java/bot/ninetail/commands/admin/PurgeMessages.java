package bot.ninetail.commands.admin;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.commands.BasicCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

/**
 * Command to purge messages from a channel.
 * 
 * @implements BasicCommand
 */
@UtilityClass
public final class PurgeMessages implements BasicCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(PurgeMessages.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "PurgeMessages command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild().getName(),
            event.getGuild().getId()
        );

        int count = event.getOption("count").getAsInt();

        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            LOGGER.log(Level.INFO, "Attempted (failed) purge attempt by {0} ({1})", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            hook.sendMessage("You do not have the required permissions to manage messages in this server.").queue();
            return;
        }

        if (!event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            LOGGER.log(Level.INFO, "Attempted (failed) purge attempt by {0} ({1})", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            hook.sendMessage("I don't have the required permissions to manage messages in this server.").queue();
            return;
        }

        if (count < 1 || count > 100) {
            hook.sendMessage("Please provide a number between 1 and 100.").queue();
            return;
        }

        TextChannel channel = event.getChannel().asTextChannel();
        
        channel.getHistory().retrievePast(count).queue(messages -> {
            if (messages.isEmpty()) {
                hook.sendMessage("No messages found to delete.").queue();
                return;
            }

            if (messages.size() == 1) {
                messages.get(0).delete().queue(
                    v -> {
                        hook.sendMessage(String.format("Successfully deleted %s messages.", messages.size())).queue();
                        LOGGER.log(Level.INFO, "PurgeMessages executed by {0} ({1}): deleted {2} message(s)", 
                            event.getUser().getGlobalName(), event.getUser().getId(), messages.size()
                        );
                    },
                    error -> {
                        hook.sendMessage("Failed to delete messages: " + error.getMessage()).queue();
                        LOGGER.log(Level.WARNING, "Failed to delete messages: {0}", error.getMessage());
                    }
                );
            } else {
                List<Message> recentMessages = messages.stream()
                    .filter(msg -> System.currentTimeMillis() - msg.getTimeCreated().toInstant().toEpochMilli() < 1209600000L)
                    .toList();

                if (recentMessages.isEmpty()) {
                    hook.sendMessage("All messages are older than 14 days and cannot be bulk deleted.").queue();
                    return;
                }

                channel.deleteMessages(recentMessages).queue(
                    v -> {
                        hook.sendMessage("Successfully deleted " + recentMessages.size() + " message(s).").queue();
                        LOGGER.log(Level.INFO, "PurgeMessages executed by {0} ({1}): deleted {2} message(s)", 
                            event.getUser().getGlobalName(), event.getUser().getId(), recentMessages.size()
                        );
                    },
                    error -> {
                        hook.sendMessage("Failed to delete messages: " + error.getMessage()).queue();
                        LOGGER.log(Level.WARNING, "Failed to delete messages: {0}", error.getMessage());
                    }
                );
            }
        }, error -> {
            hook.sendMessage("Failed to retrieve messages: " + error.getMessage()).queue();
            LOGGER.log(Level.WARNING, "Failed to retrieve messages: {0}", error.getMessage());
        });
    }
}
