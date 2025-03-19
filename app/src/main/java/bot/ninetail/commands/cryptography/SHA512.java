package bot.ninetail.commands.cryptography;

import java.security.NoSuchAlgorithmException;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.CryptographyCommand;
import bot.ninetail.utilities.cryptography.Hash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to hash a message using SHA-512.
 * 
 * @implements CryptographyCommand
 */
public final class SHA512 implements CryptographyCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private SHA512() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("SHA-512 command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        String message = event.getOption("message").getAsString();
        try {
            String hashedMessage = Hash.hash(message, "SHA-512");
            event.reply("SHA-512 hash: " + hashedMessage).queue();
        } catch (NoSuchAlgorithmException e) {
            event.reply("Error: " + e.getMessage()).queue();
        }
    }
}