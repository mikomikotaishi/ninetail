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
 * Command to hash a message using SHA-512.
 * 
 * @implements CryptographyCommand
 */
@UtilityClass
public final class Sha512 implements CryptographyCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(Sha512.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "SHA-512 command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        String message = event.getOption("message").getAsString();
        try {
            String hashedMessage = Hash.hash(message, "SHA-512");
            event.reply("SHA-512 hash: " + hashedMessage).queue();
        } catch (NoSuchAlgorithmException e) {
            event.reply("Error: " + e.getMessage()).queue();
        }
    }
}