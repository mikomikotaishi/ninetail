package bot.ninetail.commands.system;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.commands.JdaCommand;
import bot.ninetail.system.ConfigLoader;
import bot.ninetail.util.IncorrectMasterIdException;
import bot.ninetail.util.IncorrectPasswordException;
import bot.ninetail.util.MutablePair;
import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to wipe all existing webhooks on the bot.
 * Can only be called by the bot master.
 * 
 * @implements JdaCommand
 */
@UtilityClass
public final class DeleteAllWebhooks implements JdaCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(DeleteAllWebhooks.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     * @param instance The JDA instance.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event, @Nonnull JDA instance) {
        LOGGER.log(Level.INFO, "Wipe all webhooks command attempted by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        String password = event.getOption("password").getAsString();
        try {
            if (!password.equals(ConfigLoader.getMasterPassword())) {
                throw new IncorrectPasswordException();
            } else if (!event.getUser().getId().equals(ConfigLoader.getBotMasterId())) {
                throw new IncorrectMasterIdException();
            }
            
            event.deferReply(true).queue();
            
            LOGGER.log(Level.INFO, "Successful webhook wipe initiated by {0} ({1})", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            
            // [processed, skipped]
            MutablePair<Integer, Integer> guildCounts = new MutablePair<>(0, 0);
            
            for (Guild guild : instance.getGuilds()) {
                if (guild.getSelfMember().hasPermission(Permission.MANAGE_WEBHOOKS)) {
                    guildCounts.setFirst(guildCounts.getFirst() + 1);
                    guild.retrieveWebhooks().queue(
                        webhooks -> {
                        for (Webhook webhook: webhooks) {
                            webhook.delete().queue(
                                success -> LOGGER.log(Level.INFO, "Deleted webhook: {0} in guild: {1} ({2})", 
                                    webhook.getName(), guild.getName(), guild.getId()
                                ),
                                error -> LOGGER.log(Level.ERROR, "Failed to delete webhook: {0} in guild: {1} ({2}) - {3}", 
                                    webhook.getName(), guild.getName(), guild.getId(), error.getMessage()
                                )
                            );
                        }
                        }, 
                        error -> LOGGER.log(Level.ERROR, "Failed to retrieve webhooks for guild: {0} ({1}) - {2}", 
                            guild.getName(), guild.getId(), error.getMessage())
                        );
                } else {
                    guildCounts.setSecond(guildCounts.getSecond() + 1);
                    LOGGER.log(Level.INFO, "Skipped guild: {0} ({1}) - Missing MANAGE_WEBHOOKS permission", 
                        guild.getName(), guild.getId()
                    );
                }
            }
            
            event.getHook().editOriginal(
                String.format("Webhook cleanup completed. Processed %d guilds, cleaned %d guilds, skipped %d guilds due to lack of permissions.", 
                    guildCounts.getFirst() + guildCounts.getSecond(), guildCounts.getFirst(), guildCounts.getSecond()
                )
            ).queue();
        } catch (IncorrectPasswordException e) {
            LOGGER.log(Level.INFO, "Attempted (failed) webhook wipe by {0} ({1}) due to incorrect password", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("Incorrect master password!").setEphemeral(true).queue();
        } catch (IncorrectMasterIdException e) {
            LOGGER.log(Level.INFO, "Attempted (failed) webhook wipe by {0} ({1}) due to incorrect ID", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("Incorrect bot master ID!").setEphemeral(true).queue();
        }
    }
}
