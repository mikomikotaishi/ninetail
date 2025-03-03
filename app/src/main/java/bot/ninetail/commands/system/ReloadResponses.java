package bot.ninetail.commands.system;

import bot.ninetail.core.ResponseHandler;
import bot.ninetail.structures.commands.JDACommand;
import bot.ninetail.system.ConfigLoader;

import jakarta.annotation.Nonnull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to reload the response files.
 * 
 * @implements JDACommand
 */
public final class ReloadResponses implements JDACommand {
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
        System.out.println("Config reload command attempted.");
        String password = event.getOption("password").getAsString();
        if (password.equals(ConfigLoader.getMasterPassword())) {
            event.reply("Reloading response files.").setEphemeral(true).queue();
            ResponseHandler.reloadResponses();
        } else {
            System.out.println(String.format("Attempted (failed) response reload attempt by %s (%s)", event.getUser().getGlobalName(), event.getUser().getId()));
            event.reply("Incorrect master password!").setEphemeral(true).queue();
        }
    }
}