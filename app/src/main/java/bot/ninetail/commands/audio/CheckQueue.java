package bot.ninetail.commands.audio;

import java.util.Iterator;
import java.util.Queue;

import jakarta.annotation.Nonnull;

import bot.ninetail.audio.BotAudio;
import bot.ninetail.core.logger.*;
import bot.ninetail.structures.commands.AudioCommand;
import bot.ninetail.util.TemporalFormatting;

import lombok.experimental.UtilityClass;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to check the music queue.
 * 
 * @implements AudioCommand
 */
@UtilityClass
public final class CheckQueue implements AudioCommand {
    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, "Check queue command invoked by %s (%s) of guild %s (%s)", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild().getName(),
            event.getGuild().getId()
        );
        
        BotAudio.getInstance(event.getGuild().getIdLong()).updateLastActiveTime();
        long guildId = event.getGuild().getIdLong();
        BotAudio botAudio = BotAudio.getInstance(guildId);
        Queue<AudioTrack> queue = botAudio.getScheduler().getQueue();
        if (queue.isEmpty()) {
            Logger.log(LogLevel.INFO, "Music queue empty.");
            event.reply("Music queue empty.").queue();
            return;
        }
        Iterator<AudioTrack> iterator = queue.iterator();
        int currentIndex = 1;
        StringBuilder fullList = new StringBuilder();
        Logger.log(LogLevel.INFO, "Beginning to parse queue.");
        while (iterator.hasNext()) {
            AudioTrack track = iterator.next();
            String trackName = track.getInfo().title;
            long songLength = track.getInfo().length;
            fullList.append(String.format("%d. %s (%s)\n", 
                currentIndex++, trackName, TemporalFormatting.getFormattedTime(songLength))
            );
        }
        Logger.log(LogLevel.INFO, "Queue completed parsing.");
        Logger.log(LogLevel.INFO, fullList.toString());
        event.reply(fullList.toString()).queue();
    }
}