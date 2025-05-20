package bot.ninetail.commands.system;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.JdaCommand;
import bot.ninetail.system.ConfigLoader;
import bot.ninetail.utilities.exceptions.*;

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
public final class DeleteAllWebhooks implements JdaCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private DeleteAllWebhooks() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     * @param instance The JDA instance.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event, @Nonnull JDA instance) {
        Logger.log(LogLevel.INFO, String.format("Wipe all webhooks command attempted by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A")
        );
        
        String password = event.getOption("password").getAsString();
        try {
            if (!password.equals(ConfigLoader.getMasterPassword()))
                throw new IncorrectPasswordException();
            else if (!event.getUser().getId().equals(ConfigLoader.getBotMasterId()))
                throw new IncorrectMasterIdException();
            
            event.deferReply(true).queue();
            
            Logger.log(LogLevel.INFO, String.format("Successful webhook wipe initiated by %s (%s)", event.getUser().getGlobalName(), event.getUser().getId()));
            
            // [processed, skipped]
            int[] guildCounts = {0, 0};
            
            for (Guild guild : instance.getGuilds()) {
                if (guild.getSelfMember().hasPermission(Permission.MANAGE_WEBHOOKS)) {
                    ++guildCounts[0];
                    guild.retrieveWebhooks().queue(webhooks -> {
                        for (Webhook webhook: webhooks) {
                            webhook.delete().queue(
                                success -> Logger.log(LogLevel.INFO, String.format("Deleted webhook: %s in guild: %s (%s)", webhook.getName(), guild.getName(), guild.getId())),
                                error -> Logger.log(LogLevel.ERROR, String.format("Failed to delete webhook: %s in guild: %s (%s) - %s", 
                                                                                webhook.getName(), guild.getName(), guild.getId(), error.getMessage()))
                            );
                        }
                    }, error -> Logger.log(LogLevel.ERROR, String.format("Failed to retrieve webhooks for guild: %s (%s) - %s", guild.getName(), guild.getId(), error.getMessage())));
                } else {
                    ++guildCounts[1];
                    Logger.log(LogLevel.INFO, String.format("Skipped guild: %s (%s) - Missing MANAGE_WEBHOOKS permission", guild.getName(), guild.getId()));
                }
            }
            
            event.getHook().editOriginal(String.format("Webhook cleanup completed. Processed %d guilds, cleaned %d guilds, skipped %d guilds due to lack of permissions.", 
                                                        guildCounts[0] + guildCounts[1], guildCounts[0], guildCounts[1])).queue();
        } catch (IncorrectPasswordException e) {
            Logger.log(LogLevel.INFO, String.format("Attempted (failed) webhook wipe by %s (%s) due to incorrect password", event.getUser().getGlobalName(), event.getUser().getId()));
            event.reply("Incorrect master password!").setEphemeral(true).queue();
        } catch (IncorrectMasterIdException e) {
            Logger.log(LogLevel.INFO, String.format("Attempted (failed) webhook wipe by %s (%s) due to incorrect ID", event.getUser().getGlobalName(), event.getUser().getId()));
            event.reply("Incorrect bot master ID!").setEphemeral(true).queue();
        }
    }
}
