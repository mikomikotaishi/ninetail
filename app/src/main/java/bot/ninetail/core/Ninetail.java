package bot.ninetail.core;

import java.util.EnumSet;

import javax.security.auth.login.LoginException;

import jakarta.annotation.Nonnull;

import bot.ninetail.system.*;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

/**
 * The main class for the bot.
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
     * Main method for the bot.
     * 
     * @param args Command line arguments
     * 
     * @throws LoginException If the bot fails to log in
     * @throws InterruptedException If the bot is interrupted
     */
    public static void main(String[] args) throws LoginException, InterruptedException {
        String BOT_TOKEN = ConfigLoader.getBotToken();

        EnumSet<GatewayIntent> INTENTS = EnumSet.of(
            GatewayIntent.GUILD_EXPRESSIONS,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.SCHEDULED_EVENTS
        );

        Logger.log(LogLevel.INFO, "Starting bot.");
        JDA api = JDABuilder.createDefault(BOT_TOKEN, INTENTS)
            .build()
            .awaitReady();

        CommandListUpdateAction commands = api.updateCommands();

        CommandHandler.loadCommands(commands);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.log(LogLevel.INFO, "Invoking shutdown hook");
            System.out.println("Shutting down bot.");
            Logger.close();
        }));

        DatabaseHandler.loadDatabase();

        Logger.log(LogLevel.INFO, "Instantiating instance.");
        Ninetail botInstance = new Ninetail(api);
        api.addEventListener(botInstance);
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

        if (event.getAuthor().isBot() || !event.isFromGuild()) 
            return;

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
