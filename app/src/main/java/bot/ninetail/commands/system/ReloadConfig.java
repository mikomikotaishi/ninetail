package bot.ninetail.commands.system;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.JdaCommand;
import bot.ninetail.system.ConfigLoader;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to reload the config files.
 * 
 * @implements JdaCommand
 */
public final class ReloadConfig implements JdaCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private ReloadConfig() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     * @param instance The JDA instance.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event, @Nonnull JDA instance) {
        Logger.log(LogLevel.INFO, String.format("Config reload command attempted by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        String password = event.getOption("password").getAsString();
        if (password.equals(ConfigLoader.getMasterPassword())) {
            event.reply("Reloading config files.").setEphemeral(true).queue();
            Logger.log(LogLevel.INFO, "Reloading config files");
            ConfigLoader.reloadConfig();
        } else {
            Logger.log(LogLevel.INFO, String.format("Attempted (failed) config reload attempt by %s (%s)", event.getUser().getGlobalName(), event.getUser().getId()));
            event.reply("Incorrect master password!").setEphemeral(true).queue();
        }
    }
}
