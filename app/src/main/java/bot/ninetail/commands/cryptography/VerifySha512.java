package bot.ninetail.commands.cryptography;

import java.security.NoSuchAlgorithmException;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.CryptographyCommand;
import bot.ninetail.utilities.cryptography.Hash;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to verify a SHA-512 hash.
 * 
 * @implements CryptographyCommand
 */
@UtilityClass
public final class VerifySha512 implements CryptographyCommand {
    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("Verify SHA-512 command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        String message = event.getOption("message").getAsString();
        String hash = event.getOption("hash").getAsString();
        try {
            boolean isValid = Hash.verifyHash(message, hash, "SHA-512");
            event.reply("SHA-512 verification: " + (isValid ? "Valid" : "Invalid")).queue();
        } catch (NoSuchAlgorithmException e) {
            event.reply("Error: " + e.getMessage()).queue();
        }
    }
}