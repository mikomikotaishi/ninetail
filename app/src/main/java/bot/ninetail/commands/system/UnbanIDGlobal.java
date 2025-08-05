package bot.ninetail.commands.system;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.commands.JdaCommand;
import bot.ninetail.system.BannedUsersManager;
import bot.ninetail.system.ConfigLoader;
import bot.ninetail.util.exceptions.IncorrectMasterIdException;
import bot.ninetail.util.exceptions.IncorrectPasswordException;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to globally unban a user ID from the bot.
 * Can only be called by the bot master.
 * 
 * @implements JdaCommand
 */
@UtilityClass
public final class UnbanIDGlobal implements JdaCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(UnbanIDGlobal.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     * @param instance The JDA instance.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event, @Nonnull JDA instance) {
        LOGGER.log(Level.INFO, "Unban ID globally command attempted by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        String password = event.getOption("password").getAsString();
        String targetUserId = event.getOption("id").getAsString();
        
        try {
            if (!password.equals(ConfigLoader.getMasterPassword())) {
                throw new IncorrectPasswordException();
            } else if (!event.getUser().getId().equals(ConfigLoader.getBotMasterId())) {
                throw new IncorrectMasterIdException();
            }
            
            event.deferReply(true).queue();
            
            long userIdLong = Long.parseLong(targetUserId);
            
            if (BannedUsersManager.unbanUser(userIdLong)) {
                event.getHook().editOriginal(
                    String.format("✅ User ID `%s` has been globally unbanned from the bot.", targetUserId)
                ).queue();
                
                LOGGER.log(Level.INFO, "User {0} globally unbanned by {1} ({2})", 
                    targetUserId, event.getUser().getGlobalName(), event.getUser().getId()
                );
            } else {
                event.getHook().editOriginal("❌ Failed to unban user. They may not be banned or database error occurred.").queue();
            }
            
        } catch (NumberFormatException e) {
            event.reply("❌ Invalid user ID format!").setEphemeral(true).queue();
        } catch (IncorrectPasswordException e) {
            LOGGER.log(Level.INFO, "Attempted (failed) unban by {0} ({1}) due to incorrect password", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("❌ Incorrect master password!").setEphemeral(true).queue();
        } catch (IncorrectMasterIdException e) {
            LOGGER.log(Level.INFO, "Attempted (failed) unban by {0} ({1}) due to incorrect ID", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("❌ Incorrect bot master ID!").setEphemeral(true).queue();
        }
    }
}
