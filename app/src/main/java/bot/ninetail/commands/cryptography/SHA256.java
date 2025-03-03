package bot.ninetail.commands.cryptography;

import java.security.NoSuchAlgorithmException;

import bot.ninetail.structures.commands.CryptographyCommand;
import bot.ninetail.utilities.cryptography.Hash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to hash a message using SHA-256.
 * 
 * @implements CryptographyCommand
 */
public final class SHA256 implements CryptographyCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private SHA256() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(SlashCommandInteractionEvent event) {
        String message = event.getOption("message").getAsString();
        try {
            String hashedMessage = Hash.hash(message, "SHA-256");
            event.reply("SHA-256 hash: " + hashedMessage).queue();
        } catch (NoSuchAlgorithmException e) {
            event.reply("Error: " + e.getMessage()).queue();
        }
    }
}