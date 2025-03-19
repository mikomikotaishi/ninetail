package bot.ninetail.commands.cryptography;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.CryptographyCommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to encrypt a message using AES.
 * 
 * @implements CryptographyCommand
 */
public final class EncryptAES implements CryptographyCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private EncryptAES() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("Encrypt AES command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
    }
}
