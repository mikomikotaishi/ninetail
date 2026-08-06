package bot.ninetail.commands.audio;

import java.util.Iterator;
import java.util.Queue;

import jakarta.annotation.Nonnull;

import bot.ninetail.audio.BotAudio;
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
    @Nonnull
    private static final System.Logger LOGGER = System.getLogger(CheckQueue.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(System.Logger.Level.INFO, "Check queue command invoked by {0} ({1}) of guild {2} ({3})", 
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
            LOGGER.log(System.Logger.Level.INFO, "Music queue empty.");
            event.reply("Music queue empty.").queue();
            return;
        }
        Iterator<AudioTrack> iterator = queue.iterator();
        int currentIndex = 1;
        StringBuilder fullList = new StringBuilder();
        LOGGER.log(System.Logger.Level.INFO, "Beginning to parse queue.");
        while (iterator.hasNext()) {
            AudioTrack track = iterator.next();
            String trackName = track.getInfo().title;
            long songLength = track.getInfo().length;
            fullList.append(String.format("%d. %s (%s)\n", 
                currentIndex++, trackName, TemporalFormatting.getFormattedTime(songLength))
            );
        }
        LOGGER.log(System.Logger.Level.INFO, "Queue completed parsing.");
        LOGGER.log(System.Logger.Level.INFO, fullList.toString());
        event.reply(fullList.toString()).queue();
    }
}