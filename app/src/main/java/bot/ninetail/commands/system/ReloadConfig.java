package bot.ninetail.commands.system;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.logger.*;
import bot.ninetail.structures.commands.JdaCommand;
import bot.ninetail.system.ConfigLoader;
import bot.ninetail.util.exceptions.IncorrectMasterIdException;
import bot.ninetail.util.exceptions.IncorrectPasswordException;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to reload the config files.
 * Can only be called by the bot master.
 * 
 * @implements JdaCommand
 */
@UtilityClass
public final class ReloadConfig implements JdaCommand {
    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     * @param instance The JDA instance.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event, @Nonnull JDA instance) {
        Logger.log(LogLevel.INFO, "Config reload command attempted by %s (%s) of guild %s (%s)", 
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
            
            event.reply("Reloading config files.").setEphemeral(true).queue();
            Logger.log(LogLevel.INFO, "Reloading config files");
            ConfigLoader.reloadConfig();
        } catch (IncorrectPasswordException e) {
            Logger.log(LogLevel.INFO, "Attempted (failed) config reload by %s (%s) due to incorrect password", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("Incorrect master password!").setEphemeral(true).queue();
        } catch (IncorrectMasterIdException e) {
            Logger.log(LogLevel.INFO, "Attempted (failed) config reload by %s (%s) due to incorrect ID", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("Incorrect bot master ID!").setEphemeral(true).queue();
        }
    }
}
