package bot.ninetail.commands.audio;

import java.util.Iterator;
import java.util.Queue;

import jakarta.annotation.Nonnull;

import bot.ninetail.audio.BotAudio;
import bot.ninetail.structures.commands.AudioCommand;
import bot.ninetail.utilities.Temporal;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to check the music queue.
 * 
 * @implements AudioCommand
 */
public final class CheckQueue implements AudioCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private CheckQueue() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        System.out.println("Check queue command invoked.");
        long guildId = event.getGuild().getIdLong();
        BotAudio botAudio = BotAudio.getInstance(guildId);
        Queue<AudioTrack> queue = botAudio.getScheduler().getQueue();
        if (queue.isEmpty()) {
            System.out.println("Music queue empty.");
            event.reply("Music queue empty.").queue();
            return;
        }
        Iterator<AudioTrack> iterator = queue.iterator();
        int currentIndex = 1;
        StringBuilder fullList = new StringBuilder();
        System.out.println("Beginning to parse queue.");
        while (iterator.hasNext()) {
            AudioTrack track = iterator.next();
            String trackName = track.getInfo().title;
            long songLength = track.getInfo().length;
            fullList.append(String.format("%d. %s (%s)\n", currentIndex++, trackName, Temporal.getFormattedTime(songLength)));
        }
        System.out.println("Queue completed parsing.");
        System.out.println(fullList);
        event.reply(fullList.toString()).queue();
    }
}