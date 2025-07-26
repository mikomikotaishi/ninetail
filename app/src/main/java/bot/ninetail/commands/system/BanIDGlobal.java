package bot.ninetail.commands.system;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.logger.*;
import bot.ninetail.structures.commands.JdaCommand;
import bot.ninetail.system.BannedUsersManager;
import bot.ninetail.system.ConfigLoader;
import bot.ninetail.util.exceptions.IncorrectMasterIdException;
import bot.ninetail.util.exceptions.IncorrectPasswordException;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Command to globally ban a user ID from the bot.
 * Can only be called by the bot master.
 * 
 * @implements JdaCommand
 */
@UtilityClass
public final class BanIDGlobal implements JdaCommand {
    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     * @param instance The JDA instance.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event, @Nonnull JDA instance) {
        Logger.log(LogLevel.INFO, "Ban ID globally command attempted by %s (%s) of guild %s (%s)", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        String password = event.getOption("password").getAsString();
        String targetUserId = event.getOption("id").getAsString();
        String reason = event.getOption("reason", "No reason provided", OptionMapping::getAsString);
        
        try {
            if (!password.equals(ConfigLoader.getMasterPassword())) {
                throw new IncorrectPasswordException();
            } else if (!event.getUser().getId().equals(ConfigLoader.getBotMasterId())) {
                throw new IncorrectMasterIdException();
            }
            
            event.deferReply(true).queue();
            
            long userIdLong = Long.parseLong(targetUserId);
            
            if (BannedUsersManager.banUser(userIdLong, event.getUser().getIdLong(), reason)) {
                event.getHook().editOriginal(
                    String.format("✅ User ID `%s` has been globally banned from the bot.\n**Reason:** %s", 
                        targetUserId, reason
                    )
                ).queue();
                
                Logger.log(LogLevel.INFO, "User %s globally banned by %s (%s). Reason: %s", 
                    targetUserId, event.getUser().getGlobalName(), event.getUser().getId(), reason
                );
            } else {
                event.getHook().editOriginal("❌ Failed to ban user. They may already be banned.").queue();
            }
            
        } catch (NumberFormatException e) {
            event.reply("❌ Invalid user ID format!").setEphemeral(true).queue();
        } catch (IncorrectPasswordException e) {
            Logger.log(LogLevel.INFO, "Attempted (failed) ban by %s (%s) due to incorrect password", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("❌ Incorrect master password!").setEphemeral(true).queue();
        } catch (IncorrectMasterIdException e) {
            Logger.log(LogLevel.INFO, "Attempted (failed) ban by %s (%s) due to incorrect ID", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("❌ Incorrect bot master ID!").setEphemeral(true).queue();
        }
    }
}
