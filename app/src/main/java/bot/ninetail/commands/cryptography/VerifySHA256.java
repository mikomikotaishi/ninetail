package bot.ninetail.commands.cryptography;

import java.security.NoSuchAlgorithmException;

import bot.ninetail.structures.commands.CryptographyCommand;
import bot.ninetail.utilities.cryptography.Hash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to verify a SHA-256 hash.
 * 
 * @implements CryptographyCommand
 */
public final class VerifySHA256 implements CryptographyCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private VerifySHA256() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(SlashCommandInteractionEvent event) {
        String message = event.getOption("message").getAsString();
        String hash = event.getOption("hash").getAsString();
        try {
            boolean isValid = Hash.verifyHash(message, hash, "SHA-256");
            event.reply("SHA-256 verification: " + (isValid ? "Valid" : "Invalid")).queue();
        } catch (NoSuchAlgorithmException e) {
            event.reply("Error: " + e.getMessage()).queue();
        }
    }
}