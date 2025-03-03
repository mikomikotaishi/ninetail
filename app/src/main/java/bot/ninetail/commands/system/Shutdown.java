package bot.ninetail.commands.system;

import bot.ninetail.structures.commands.JDACommand;
import bot.ninetail.system.ConfigLoader;

import jakarta.annotation.Nonnull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to shut down the bot.
 * 
 * @implements JDACommand
 */
public final class Shutdown implements JDACommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private Shutdown() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     * @param instance The JDA instance.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event, @Nonnull JDA instance) {
        System.out.println("Shutdown command attempted.");
        String password = event.getOption("password").getAsString();
        if (password.equals(ConfigLoader.getMasterPassword())) {
            event.reply("Shutting down bot.").setEphemeral(true).queue();
            instance.shutdown();
        } else {
            System.out.println(String.format("Attempted (failed) shutdown attempt by %s (%s)", event.getUser().getGlobalName(), event.getUser().getId()));
            event.reply("Incorrect shutdown password!").setEphemeral(true).queue();
        }
    }
}