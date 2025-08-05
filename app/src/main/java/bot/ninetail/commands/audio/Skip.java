package bot.ninetail.commands.audio;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;

import bot.ninetail.audio.BotAudio;
import bot.ninetail.structures.commands.AudioCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to skip the current track.
 * 
 * @implements AudioCommand
 */
@UtilityClass
public final class Skip implements AudioCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(Skip.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "Skip command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild().getName(),
            event.getGuild().getId()
        );
        
        long guildId = event.getGuild().getIdLong();
        BotAudio botAudio = BotAudio.getInstance(guildId);
        botAudio.updateLastActiveTime();

        if (botAudio.getAudioPlayer().getPlayingTrack() == null) {
            event.reply("There is no track currently playing.").queue();
            return;
        }

        botAudio.getScheduler().skip();
        event.reply("Skipped the current track.").queue();
    }
}