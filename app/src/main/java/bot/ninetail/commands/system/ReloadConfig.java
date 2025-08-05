package bot.ninetail.commands.system;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;

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
    @Nonnull
    private static final Logger LOGGER = System.getLogger(ReloadConfig.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     * @param instance The JDA instance.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event, @Nonnull JDA instance) {
        LOGGER.log(Level.INFO, "Config reload command attempted by {0} ({1}) of guild {2} ({3})", 
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
            LOGGER.log(Level.INFO, "Reloading config files");
            ConfigLoader.reloadConfig();
        } catch (IncorrectPasswordException e) {
            LOGGER.log(Level.INFO, "Attempted (failed) config reload by {0} ({1}) due to incorrect password", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("Incorrect master password!").setEphemeral(true).queue();
        } catch (IncorrectMasterIdException e) {
            LOGGER.log(Level.INFO, "Attempted (failed) config reload by {0} ({1}) due to incorrect ID", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("Incorrect bot master ID!").setEphemeral(true).queue();
        }
    }
}
