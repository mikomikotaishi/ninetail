package bot.ninetail.commands.system;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.core.ResponseHandler;
import bot.ninetail.structures.commands.JdaCommand;
import bot.ninetail.system.ConfigLoader;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to reload the response files.
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
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        String password = event.getOption("password").getAsString();
        if (password.equals(ConfigLoader.getMasterPassword())) {
            event.reply("Reloading response files.").setEphemeral(true).queue();
            Logger.log(LogLevel.INFO, "Reloading response files");
            ResponseHandler.reloadResponses();
        } else {
            Logger.log(LogLevel.INFO, String.format("Attempted (failed) response reload attempt by %s (%s)", event.getUser().getGlobalName(), event.getUser().getId()));
            event.reply("Incorrect master password!").setEphemeral(true).queue();
        }
    }
}
