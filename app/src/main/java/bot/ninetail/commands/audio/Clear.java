package bot.ninetail.commands.audio;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;

import bot.ninetail.audio.BotAudio;
import bot.ninetail.structures.commands.AudioCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to clear the audio queue.
 * 
 * @implements AudioCommand
 */
@UtilityClass
public final class Clear implements AudioCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(Clear.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "Clear queue command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild().getName(),
            event.getGuild().getId()
        );
        
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