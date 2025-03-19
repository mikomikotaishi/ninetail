package bot.ninetail.commands.audio;

import jakarta.annotation.Nonnull;

import bot.ninetail.audio.BotAudio;
import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.AudioCommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to disconnect the bot from the voice channel.
 * 
 * @implements AudioCommand
 */
public final class Disconnect implements AudioCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private Disconnect() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("Disconnected command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        long guildId = event.getGuild().getIdLong();
        BotAudio botAudio = BotAudio.getInstance(guildId);
        botAudio.disconnect();
        event.reply("Disconnected from the voice channel.").queue();
    }
}