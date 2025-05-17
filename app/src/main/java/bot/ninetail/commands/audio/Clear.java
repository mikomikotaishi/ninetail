package bot.ninetail.commands.audio;

import jakarta.annotation.Nonnull;

import bot.ninetail.audio.BotAudio;
import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.AudioCommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to clear the audio queue.
 * 
 * @implements AudioCommand
 */
public final class Clear implements AudioCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private Clear() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("Clear queue command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        long guildId = event.getGuild().getIdLong();
        BotAudio botAudio = BotAudio.getInstance(guildId);
        botAudio.updateLastActiveTime();
        int queueSize = botAudio.getScheduler().getQueue().size();

        if (queueSize == 0) {
            event.reply("The queue is already empty.").queue();
            return;
        }

        botAudio.getScheduler().clear();
        event.reply(String.format("Cleared **%d** tracks from the queue.", queueSize)).queue();
    }
}