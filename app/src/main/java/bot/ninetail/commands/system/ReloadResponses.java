package bot.ninetail.commands.system;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.core.ResponseHandler;
import bot.ninetail.structures.commands.JdaCommand;
import bot.ninetail.system.ConfigLoader;
import bot.ninetail.utilities.exceptions.*;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to reload the response files.
 * Can only be called by the bot master.
 * 
 * @implements JdaCommand
 */
public final class ReloadResponses implements JdaCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private ReloadResponses() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     * @param instance The JDA instance.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event, @Nonnull JDA instance) {
        Logger.log(LogLevel.INFO, String.format("Response reload command attempted by %s (%s) of guild %s (%s)", 
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
                
            event.reply("Reloading response files.").setEphemeral(true).queue();
            Logger.log(LogLevel.INFO, "Reloading response files");
            ResponseHandler.reloadResponses();
        } catch (IncorrectPasswordException e) {
            Logger.log(LogLevel.INFO, String.format("Attempted (failed) config reload by %s (%s) due to incorrect password", event.getUser().getGlobalName(), event.getUser().getId()));
            event.reply("Incorrect master password!").setEphemeral(true).queue();
        } catch (IncorrectMasterIdException e) {
            Logger.log(LogLevel.INFO, String.format("Attempted (failed) config reload by %s (%s) due to incorrect ID", event.getUser().getGlobalName(), event.getUser().getId()));
            event.reply("Incorrect bot master ID!").setEphemeral(true).queue();
        }
    }
}
