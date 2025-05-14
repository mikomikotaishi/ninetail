package bot.ninetail.core;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

import java.util.EnumSet;

import javax.security.auth.login.LoginException;

import jakarta.annotation.Nonnull;

import bot.ninetail.commands.admin.*;
import bot.ninetail.commands.audio.*;
import bot.ninetail.commands.cryptography.*;
import bot.ninetail.commands.game.*;
import bot.ninetail.commands.game.chess.*;
import bot.ninetail.commands.game.poker.*;
import bot.ninetail.commands.general.*;
import bot.ninetail.commands.imageboard.*;
import bot.ninetail.commands.system.*;
import bot.ninetail.commands.webhook.*;
import bot.ninetail.system.*;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

/**
 * The main class for the bot.
 * This class initialises the bot, registers commands, and listens for events.
 * 
 * @extends ListenerAdapter
 */
public class Ninetail extends ListenerAdapter {
    /**
     * The JDA instance to use.
     */
    @Nonnull private final JDA jda;

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

        Logger.log(LogLevel.INFO, "Loading bot commands.");
        commands.addCommands(
            // ====== Admin commands ======
            // Ban command
            Commands.slash("ban", "Ban a user from this server. Requires permission to ban users.")
                .addOptions(new OptionData(USER, "user", "The user to ban")
                    .setRequired(true))
                .addOptions(new OptionData(INTEGER, "del_days", "Delete messages from the past days.")
                    .setRequiredRange(0, 7))
                .addOptions(new OptionData(STRING, "reason", "The ban reason to use (default: Banned by <user>)"))
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)),
            
            // ====== Audio commands ======
            // Check queue command
            Commands.slash("checkqueue", "Sends the list of all songs in the current queue")
                .setContexts(InteractionContextType.GUILD),
            // Clear command
            Commands.slash("clear", "Clears all music in the current queue")
                .setContexts(InteractionContextType.GUILD),
            // Disconnect command
            Commands.slash("disconnect", "Disconnects the bot from voice channel")
                .setContexts(InteractionContextType.GUILD),
            // Play command
            Commands.slash("play", "Plays audio in the voice channel of the user")
                .addOptions(new OptionData(STRING, "query", "The query for YouTube")
                    .setRequired(true))
                .setContexts(InteractionContextType.GUILD),
            // Skip command
            Commands.slash("skip", "Skips the current song")
                .setContexts(InteractionContextType.GUILD),

            // ====== Cryptography commands ======
            // Encrypt AES command
            Commands.slash("encryptaes", "Encrypt using AES")
                .addOptions(new OptionData(STRING, "message", "The message to encrypt")
                    .setRequired(true)),
            // SHA-256 command
            Commands.slash("sha256", "Hash a message using SHA-256")
                .addOptions(new OptionData(STRING, "message", "The message to hash")
                    .setRequired(true)),
            // SHA-512 command
            Commands.slash("sha512", "Hash a message using SHA-512")
                .addOptions(new OptionData(STRING, "message", "The message to hash")
                    .setRequired(true)),
            // Verify SHA-256 command
            Commands.slash("verifysha256", "Verify a SHA-256 hash")
                .addOptions(new OptionData(STRING, "message", "The message to verify")
                    .setRequired(true))
                .addOptions(new OptionData(STRING, "hash", "The hash to verify against")
                    .setRequired(true)),
            // Verify SHA-512 command
            Commands.slash("verifysha512", "Verify a SHA-512 hash")
                .addOptions(new OptionData(STRING, "message", "The message to verify")
                    .setRequired(true))
                .addOptions(new OptionData(STRING, "hash", "The hash to verify against")
                    .setRequired(true)),

            // ====== Game commands ======
            // Magic 8 Ball command
            Commands.slash("magic8ball", "Consults the Magic 8 Ball")
                .addOptions(new OptionData(STRING, "query", "The question to ask the magic 8 ball")
                    .setRequired(true)),
            // === Chess ===
            // New chess command
            Commands.slash("newchess", "Begins a new chess game")
                .addOptions(new OptionData(STRING, "colour", "The colour to play (black/white)")
                    .setRequired(true)),
            // Chess move command
            Commands.slash("chessmove", "Makes a move on the current chess game (if any)")
                .addOptions(new OptionData(STRING, "move", "The chess move to make (in algebraic notation)")
                    .setRequired(true)),
        
            // === Poker ===
            // New poker command
            Commands.slash("newpoker", "Begins a new poker game"),
            // Poker move command
            Commands.slash("pokermove", "Makes a move on the current poker game (if any)")
                .addOptions(new OptionData(STRING, "move", "The poker move to make")
                    .setRequired(true))
                .addOptions(new OptionData(INTEGER, "amount", "The amount to apply to the move (if applicable)")),
            
            // ====== General commands ======
            // Fox fact command
            Commands.slash("foxfacts", "Get an interesting fact about foxes"),
            // Ping command
            Commands.slash("ping", "Reports the ping of the bot"),
            // Random fox command
            Commands.slash("randomfox", "Get a random image of a fox"),
            // Uwuify command
            Commands.slash("uwuify", "Uwuify some text")
                .addOptions(new OptionData(STRING, "text", "The text to uwuify")
                    .setRequired(true)),
            // Weather command
            Commands.slash("weather", "Obtain weather information for a specified location")
                .addOptions(new OptionData(STRING, "location", "The location to search")
                    .setRequired(true)),

            // ====== Imageboard commands ======
            // Danbooru command
            Commands.slash("danbooru", "Query danbooru")
                .addOptions(new OptionData(STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(STRING, "tag2", "The second tag to search")),
            // e621 command
            Commands.slash("e621", "Query e621")
                .addOptions(new OptionData(STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(STRING, "tag2", "The second tag to search")),
            // Gelbooru command
            Commands.slash("gelbooru", "Query gelbooru")
                .addOptions(new OptionData(STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(STRING, "tag2", "The second tag to search")),
            // Gyate Booru command
            Commands.slash("gyatebooru", "Query gyate booru")
                .addOptions(new OptionData(STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(STRING, "tag2", "The second tag to search")),
            // Rule34 command
            Commands.slash("rule34", "Query Rule34")
                .addOptions(new OptionData(STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(STRING, "tag2", "The second tag to search")),

            // ====== System commands ======            
            // Reload config command
            Commands.slash("reloadconfig", "Reloads config.properties")
                .addOptions(new OptionData(STRING, "password", "The master pasword (specified in config.properties)")
                    .setRequired(true)),
            // Reload responses command
            Commands.slash("reloadresponses", "Reloads responses.json")
                .addOptions(new OptionData(STRING, "password", "The master pasword (specified in config.properties)")
                    .setRequired(true)),
            // Shutdown command
            Commands.slash("shutdown", "Shuts down the bot")
                .addOptions(new OptionData(STRING, "password", "The master password (specified in config.properties)")
                    .setRequired(true)),

            // ====== Webhook commands ======
            // User webhook command
            Commands.slash("userwebhook", "Send a webhook message as a user")
                .addOptions(new OptionData(USER, "user", "The user to impersonate")
                    .setRequired(true))
                .addOptions(new OptionData(STRING, "message", "The message to send")
                    .setRequired(true))
                .setContexts(InteractionContextType.GUILD)
        ).queue();
        Logger.log(LogLevel.INFO, "Commands finished logging!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.log(LogLevel.INFO, "Invoking shutdown hook");
            Logger.close();
        }));

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
        switch (event.getName()) {
            // Admin
            case "ban":
                Ban.invoke(event);
                break;
            // Audio
            case "checkqueue":
                CheckQueue.invoke(event);
                break;
            case "clear":
                Clear.invoke(event);
                break;
            case "disconnect":
                Disconnect.invoke(event);
                break;
            case "play":
                Play.invoke(event);
                break;
            case "skip":
                Skip.invoke(event);
                break;
            // Cryptography
            case "sha256":
                SHA256.invoke(event);
                break;
            case "sha512":
                SHA512.invoke(event);
                break;
            case "verifysha256":
                VerifySHA256.invoke(event);
                break;
            case "verifysha512":
                VerifySHA512.invoke(event);
                break;
            // Game
            case "magic8ball":
                Magic8Ball.invoke(event);
                break;
            /// Chess
            case "newchess":
                NewChess.invoke(event);
                break;
            case "chessmove":
                ChessMove.invoke(event);
                break;
            /// Poker
            case "newpoker":
                NewPoker.invoke(event);
                break;
            case "pokermove":
                PokerMove.invoke(event);
                break;
            // General
            case "foxfacts":
                FoxFacts.invoke(event);
                break;
            case "ping":
                Ping.invoke(event);
                break;
            case "randomfox":
                RandomFox.invoke(event);
                break;
            case "weather":
                Weather.invoke(event);
                break;
            case "uwuify":
                Uwuify.invoke(event);
                break;
            // Imageboard
            case "danbooru":
                Danbooru.invoke(event);
                break;
            case "e621":
                E621.invoke(event);
                break;
            case "gelbooru":
                Gelbooru.invoke(event);
                break;
            case "gyatebooru":
                GyateBooru.invoke(event);
                break;
            case "rule34":
                Rule34.invoke(event);
                break;
            // System
            case "reloadconfig":
                ReloadConfig.invoke(event, jda);
                break;
            case "reloadresponses":
                ReloadResponses.invoke(event, jda);
                break;
            case "shutdown":
                Shutdown.invoke(event, jda);
                break;
            // Webhook
            case "userwebhook":
                UserWebhook.invoke(event);
                break;
            // Default
            default:
                event.reply("Invalid command!").setEphemeral(true).queue();
                Logger.log(LogLevel.INFO, String.format("Invalid command called by %s of guild %s", event.getUser(), event.getGuild()));
        }
    }
}
