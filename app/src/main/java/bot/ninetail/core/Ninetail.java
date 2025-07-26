package bot.ninetail.core;

import jakarta.annotation.Nonnull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * The bot body class.
 * This class initialises the bot, registers commands, and listens for events.
 * 
 * @extends ListenerAdapter
 */
public final class Ninetail extends ListenerAdapter {
    /**
     * The JDA instance to use.
     */
    @Nonnull 
    private final JDA jda;

    /**
     * Constructor for the bot.
     */
    public Ninetail() {
        this.jda = null;
    }

    /**
     * Constructor for the bot.
     * 
     * @param jda The JDA instance to use
     */
    public Ninetail(@Nonnull JDA jda) {
        this.jda = jda;
    }

    /**
     * Method to handle messages received by the bot.
     * 
     * @param event The event to handle
     */
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel textChannel = event.getChannel();
        Member member = event.getMember();
        User author = message.getAuthor();
        String content = message.getContentRaw();
        Guild guild = message.getGuild();

        if (event.getAuthor().isBot() || !event.isFromGuild()) {
            return;
        }

        ResponseHandler.handleMessage(content, textChannel);
    }

    /**
     * Method to handle slash command interactions.
     * @param event The event to handle
     */
    @Override 
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        CommandHandler.handleSlashCommand(jda, event);
    }
}
