package bot.ninetail.core;

import jakarta.annotation.Nonnull;

import bot.ninetail.commands.admin.*;
import bot.ninetail.commands.audio.*;
import bot.ninetail.commands.cryptography.*;
import bot.ninetail.commands.game.*;
import bot.ninetail.commands.game.chess.*;
import bot.ninetail.commands.game.poker.*;
import bot.ninetail.commands.general.*;
import bot.ninetail.commands.imageboard.*;
import bot.ninetail.commands.social.*;
import bot.ninetail.commands.system.*;
import bot.ninetail.commands.system.Shutdown;
import bot.ninetail.commands.webhook.*;
import bot.ninetail.structures.InteractionHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

/**
 * Class to handle operations with slash commands.
 * This class is used to handle operations with slash commands recognised by the bot.
 * 
 * @extends InteractionHandler
 */
public final class CommandHandler extends InteractionHandler {
    /**
     * Private constructor to prevent instantiation.
     */
    private CommandHandler() {}

    /**
     * Updates the list of commands to Discord and loads them on to the bot.
     * 
     * @param commands The CommandListUpdateAction of the bot.
     */
    public static void loadCommands(@Nonnull CommandListUpdateAction commands) {
        Logger.log(LogLevel.INFO, "Loading bot commands.");

        commands.addCommands(
            // ====== Admin commands ======
            // Ban command
            Commands.slash("ban", "Ban a user from this server. Requires permission to ban users.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to ban")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.INTEGER, "del_days", "Delete messages from the past days.")
                    .setRequiredRange(0, 7))
                .addOptions(new OptionData(OptionType.STRING, "reason", "The ban reason to use (default: Banned by <user>)"))
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)),
            // Kick command
            Commands.slash("kick", "Kick a user from this server. Requires permission to kick users.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to kick")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.INTEGER, "del_days", "Delete messages from the past days.")
                    .setRequiredRange(0, 7))
                .addOptions(new OptionData(OptionType.STRING, "reason", "The kick reason to use (default: Kicked by <user>)"))
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS)),
            
            // ====== Audio commands ======
            // Check queue command
            Commands.slash("checkqueue", "Sends the list of all songs in the current queue")
                .setContexts(InteractionContextType.GUILD),
            // Clear command
            Commands.slash("clear", "Clears all music in the current queue")
                .setContexts(InteractionContextType.GUILD),
            // Disconnect command.setContexts(InteractionContextType.GUILD)
            Commands.slash("disconnect", "Disconnects the bot from voice channel")
                .setContexts(InteractionContextType.GUILD),
            // Play command
            Commands.slash("play", "Plays audio in the voice channel of the user")
                .addOptions(new OptionData(OptionType.STRING, "query", "The query for YouTube")
                    .setRequired(true))
                .setContexts(InteractionContextType.GUILD),
            // Skip command
            Commands.slash("skip", "Skips the current song")
                .setContexts(InteractionContextType.GUILD),

            // ====== Cryptography commands ======
            // Encrypt AES command
            Commands.slash("encryptaes", "Encrypt using AES")
                .addOptions(new OptionData(OptionType.STRING, "message", "The message to encrypt")
                    .setRequired(true)),
            // SHA-256 command
            Commands.slash("sha256", "Hash a message using SHA-256")
                .addOptions(new OptionData(OptionType.STRING, "message", "The message to hash")
                    .setRequired(true)),
            // SHA-512 command
            Commands.slash("sha512", "Hash a message using SHA-512")
                .addOptions(new OptionData(OptionType.STRING, "message", "The message to hash")
                    .setRequired(true)),
            // Verify SHA-256 command
            Commands.slash("verifysha256", "Verify a SHA-256 hash")
                .addOptions(new OptionData(OptionType.STRING, "message", "The message to verify")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "hash", "The hash to verify against")
                    .setRequired(true)),
            // Verify SHA-512 command
            Commands.slash("verifysha512", "Verify a SHA-512 hash")
                .addOptions(new OptionData(OptionType.STRING, "message", "The message to verify")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "hash", "The hash to verify against")
                    .setRequired(true)),

            // ====== Game commands ======
            // Magic 8 Ball command
            Commands.slash("magic8ball", "Consults the Magic 8 Ball")
                .addOptions(new OptionData(OptionType.STRING, "query", "The question to ask the magic 8 ball")
                    .setRequired(true)),
            
            // === Chess ===
            // New chess command
            Commands.slash("newchess", "Begins a new chess game")
                .addOptions(new OptionData(OptionType.STRING, "colour", "The colour to play (black/white)")
                    .setRequired(true)),
            // Chess move command
            Commands.slash("chessmove", "Makes a move on the current chess game (if any)")
                .addOptions(new OptionData(OptionType.STRING, "move", "The chess move to make (in algebraic notation)")
                    .setRequired(true)),
        
            // === Poker ===
            // New poker command
            Commands.slash("newpoker", "Begins a new poker game"),
            // Poker move command
            Commands.slash("pokermove", "Makes a move on the current poker game (if any)")
                .addOptions(new OptionData(OptionType.STRING, "move", "The poker move to make")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.INTEGER, "amount", "The amount to apply to the move (if applicable)")),
            
            // ====== General commands ======
            // Fox fact command
            Commands.slash("foxfacts", "Get an interesting fact about foxes"),
            // Ping command
            Commands.slash("ping", "Reports the ping of the bot"),
            // Random fox command
            Commands.slash("randomfox", "Get a random image of a fox"),
            // Uwuify command
            Commands.slash("uwuify", "Uwuify some text")
                .addOptions(new OptionData(OptionType.STRING, "text", "The text to uwuify")
                    .setRequired(true)),
            // Weather command
            Commands.slash("weather", "Obtain weather information for a specified location")
                .addOptions(new OptionData(OptionType.STRING, "location", "The location to search")
                    .setRequired(true)),

            // ====== Imageboard commands ======
            // Danbooru command
            Commands.slash("danbooru", "Query danbooru")
                .addOptions(new OptionData(OptionType.STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "tag2", "The second tag to search")),
            // e621 command
            Commands.slash("e621", "Query e621")
                .addOptions(new OptionData(OptionType.STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "tag2", "The second tag to search")),
            // Gelbooru command
            Commands.slash("gelbooru", "Query gelbooru")
                .addOptions(new OptionData(OptionType.STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "tag2", "The second tag to search")),
            // Gyate Booru command
            Commands.slash("gyatebooru", "Query gyate booru")
                .addOptions(new OptionData(OptionType.STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "tag2", "The second tag to search")),
            // Rule34 command
            Commands.slash("rule34", "Query Rule34")
                .addOptions(new OptionData(OptionType.STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "tag2", "The second tag to search")),

            // ====== Social commands ======
            // Daily credits command
            Commands.slash("daily", "Get daily credits"),

            // ====== System commands ======     
            // Delete all webhooks command
            Commands.slash("deleteallwebhooks", "(Bot master only) Deletes all webhooks across all guilds")
                .addOptions(new OptionData(OptionType.STRING, "password", "The master password (specified in config.properties)")
                    .setRequired(true)), 
            // List guilds command
            Commands.slash("listguilds", "(Bot master only) List all guilds the bot is present in")
                .addOptions(new OptionData(OptionType.STRING, "password", "The master pasword (specified in config.properties)")
                    .setRequired(true)),      
            // Reload config command
            Commands.slash("reloadconfig", "(Bot master only) Reloads config.properties")
                .addOptions(new OptionData(OptionType.STRING, "password", "The master pasword (specified in config.properties)")
                    .setRequired(true)),
            // Reload responses command
            Commands.slash("reloadresponses", "(Bot master only) Reloads responses.json")
                .addOptions(new OptionData(OptionType.STRING, "password", "The master pasword (specified in config.properties)")
                    .setRequired(true)),
            // Shutdown command
            Commands.slash("shutdown", "(Bot master only) Shuts down the bot")
                .addOptions(new OptionData(OptionType.STRING, "password", "The master password (specified in config.properties)")
                    .setRequired(true)),

            // ====== Webhook commands ======
            // User webhook command
            Commands.slash("impersonateuser", "Impersonate a user by sending a webhook message as them")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to impersonate")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "message", "The message to send")
                    .setRequired(true))
                .setContexts(InteractionContextType.GUILD)
        ).queue();

        Logger.log(LogLevel.INFO, "Commands finished logging!");
    }

    /**
     * Handles slash commands for the bot.
     * 
     * @param jda The JDA instance
     * @param event The slash command event
     */
    public static void handleSlashCommand(@Nonnull JDA jda, @Nonnull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            // Admin
            case "ban":
                Ban.invoke(event);
                break;
            case "deleteguildwebhooks":
                DeleteGuildWebhooks.invoke(event);
                break;
            case "kick":
                Kick.invoke(event);
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
            case "encryptaes":
                EncryptAes.invoke(event);
                break;
            case "sha256":
                Sha256.invoke(event);
                break;
            case "sha512":
                Sha512.invoke(event);
                break;
            case "verifysha256":
                VerifySha256.invoke(event);
                break;
            case "verifysha512":
                VerifySha512.invoke(event);
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
            // Social
            case "daily":
                Daily.invoke(event);
                break;
            // System
            case "wipeallwebhooks":
                DeleteAllWebhooks.invoke(event, jda);
                break;
            case "listguilds":
                ListGuilds.invoke(event, jda);
                break;
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
            case "impersonateuser":
                ImpersonateUser.invoke(event);
                break;
            // Default
            default:
                event.reply("Invalid command!").setEphemeral(true).queue();
                Logger.log(LogLevel.INFO, String.format("Invalid command called by %s of guild %s", event.getUser(), event.getGuild()));
        }
    }
}
