package bot.ninetail.commands.audio;

import jakarta.annotation.Nonnull;

import bot.ninetail.audio.*;
import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.AudioCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * Command to play audio.
 * 
 * @implements AudioCommand
 */
@UtilityClass
public final class Play implements AudioCommand {
    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, "Play command invoked by %s (%s) of guild %s (%s)", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild().getName(),
            event.getGuild().getId()
        );
        
        if (event.getMember().getVoiceState() == null || !event.getMember().getVoiceState().inAudioChannel()) {
            event.reply("You need to be in a voice channel to use this command!").queue();
            return;
        }
        long guildId = event.getGuild().getIdLong();
        BotAudio botAudio = BotAudio.getInstance(guildId);
        botAudio.updateLastActiveTime();
        AudioManager audioManager = event.getGuild().getAudioManager();

        botAudio.setAudioManager(audioManager);
        botAudio.setTextChannel(event.getChannel());
        AudioChannel userChannel = event.getMember().getVoiceState().getChannel();

        if (!botAudio.isActive()) {
            Logger.log(LogLevel.INFO, "Bot audio currently inactive. Activating...");
            botAudio.setVoiceChannel(userChannel);
            botAudio.activate();
            audioManager.openAudioConnection(userChannel);
            audioManager.setSendingHandler(new AudioPlayerSendHandler(botAudio.getAudioPlayer()));
        } else if (!botAudio.getVoiceChannel().equals(userChannel)) {
            Logger.log(LogLevel.INFO, "Voice channel and user channel not the same!");
            botAudio.setVoiceChannel(userChannel);
            audioManager.openAudioConnection(userChannel);
        }

        String query = event.getOption("query").getAsString();
        String identifier = query.startsWith("http") ? query : "ytsearch:" + query;

        event.reply("Queueing...").queue();
        botAudio.getAudioPlayerManager().loadItem(identifier, 
            new AudioPlayerLoadResultHandler(botAudio.getTextChannel(), botAudio.getAudioPlayer(), botAudio.getScheduler())
        );
    }
}