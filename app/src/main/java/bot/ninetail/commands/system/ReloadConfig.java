package bot.ninetail.commands.system;

import bot.ninetail.structures.commands.JDACommand;
import bot.ninetail.system.ConfigLoader;

import jakarta.annotation.Nonnull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to reload the config files.
 * 
 * @implements JDACommand
 */
public final class ReloadConfig implements JDACommand {
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
        System.out.println("Config reload command attempted.");
        String password = event.getOption("password").getAsString();
        if (password.equals(ConfigLoader.getMasterPassword())) {
            event.reply("Reloading config files.").setEphemeral(true).queue();
            ConfigLoader.reloadConfig();
        } else {
            System.out.println(String.format("Attempted (failed) config reload attempt by %s (%s)", event.getUser().getGlobalName(), event.getUser().getId()));
            event.reply("Incorrect master password!").setEphemeral(true).queue();
        }
    }
}