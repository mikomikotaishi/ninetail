package bot.ninetail.commands.cryptography;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.security.NoSuchAlgorithmException;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.commands.CryptographyCommand;
import bot.ninetail.util.cryptography.Hash;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to verify a SHA-256 hash.
 * 
 * @implements CryptographyCommand
 */
@UtilityClass
public final class VerifySha256 implements CryptographyCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(VerifySha256.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "Verify SHA-256 command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
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