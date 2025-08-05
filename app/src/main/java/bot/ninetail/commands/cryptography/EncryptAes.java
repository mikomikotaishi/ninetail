package bot.ninetail.commands.cryptography;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.commands.CryptographyCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to encrypt a message using AES.
 * 
 * @implements CryptographyCommand
 */
@UtilityClass
public final class EncryptAes implements CryptographyCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(EncryptAes.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "Encrypt AES command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
    }
}
