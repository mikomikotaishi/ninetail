package bot.ninetail.core;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;

import bot.ninetail.commands.admin.*;
import bot.ninetail.commands.audio.*;
import bot.ninetail.commands.crypto.*;
import bot.ninetail.commands.game.*;
import bot.ninetail.commands.game.chess.*;
import bot.ninetail.commands.game.poker.*;
import bot.ninetail.commands.general.*;
import bot.ninetail.commands.imageboard.*;
import bot.ninetail.commands.social.*;
import bot.ninetail.commands.system.*;
import bot.ninetail.commands.webhook.*;
import bot.ninetail.system.BannedUsersManager;

import lombok.experimental.UtilityClass;

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
 */
@UtilityClass
public final class CommandHandler {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(CommandHandler.class.getName());

    /**
     * Updates the list of commands to Discord and loads them on to the bot.
     * 
     * @param commands The CommandListUpdateAction of the bot.
     */
    public static void loadCommands(@Nonnull CommandListUpdateAction commands) {
        LOGGER.log(Level.INFO, "Loading bot commands.");
        System.out.println("Loading bot commands.");

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
            // Ban by ID command
            Commands.slash("banid", "Ban a user by ID from this server. Requires permission to ban users.")
                .addOptions(new OptionData(OptionType.STRING, "id", "The ID of the user to ban")
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
            // Purge messages command
            Commands.slash("purgemessages", "Delete the last N messages in the current channel. Requires permission to manage messages.")
                .addOptions(new OptionData(OptionType.INTEGER, "count", "The number of messages to delete (1-100)")
                    .setRequired(true)
                    .setRequiredRange(1, 100))
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)),
            // Generate invite command
            Commands.slash("generateinvite", "Generate an invite link for the server. Requires permission to create invites.")
                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel to create an invite for (default: current channel)"))
                .addOptions(new OptionData(OptionType.INTEGER, "max_age", "Maximum age of the invite in seconds (0 = never expires)")
                    .setRequiredRange(0, 604800))
                .addOptions(new OptionData(OptionType.INTEGER, "max_uses", "Maximum number of uses (0 = unlimited)")
                    .setRequiredRange(0, 100))
                .addOptions(new OptionData(OptionType.BOOLEAN, "temporary", "Grant temporary membership"))
                .addOptions(new OptionData(OptionType.BOOLEAN, "unique", "Create a unique invite code"))
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.CREATE_INSTANT_INVITE)),
            // Unban id command
            Commands.slash("unbanid", "Unban a user ID from this server. Requires permission to ban users.")
                .addOptions(new OptionData(OptionType.USER, "user", "The ID of the user to unban"))
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
            // Derpibooru command
            Commands.slash("derpibooru", "Query derpibooru")
                .addOptions(new OptionData(OptionType.STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "tag2", "The second tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag3", "The third tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag4", "The fourth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag5", "The fifth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag6", "The sixth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag7", "The seventh tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag8", "The eighth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag9", "The ninth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag10", "The tenth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag11", "Tag 11"))
                .addOptions(new OptionData(OptionType.STRING, "tag12", "Tag 12"))
                .addOptions(new OptionData(OptionType.STRING, "tag13", "Tag 13"))
                .addOptions(new OptionData(OptionType.STRING, "tag14", "Tag 14"))
                .addOptions(new OptionData(OptionType.STRING, "tag15", "Tag 15"))
                .addOptions(new OptionData(OptionType.STRING, "tag16", "Tag 16"))
                .addOptions(new OptionData(OptionType.STRING, "tag17", "Tag 17"))
                .addOptions(new OptionData(OptionType.STRING, "tag18", "Tag 18"))
                .addOptions(new OptionData(OptionType.STRING, "tag19", "Tag 19"))
                .addOptions(new OptionData(OptionType.STRING, "tag20", "Tag 20"))
                .addOptions(new OptionData(OptionType.STRING, "tag21", "Tag 21"))
                .addOptions(new OptionData(OptionType.STRING, "tag22", "Tag 22"))
                .addOptions(new OptionData(OptionType.STRING, "tag23", "Tag 23"))
                .addOptions(new OptionData(OptionType.STRING, "tag24", "Tag 24"))
                .addOptions(new OptionData(OptionType.STRING, "tag25", "Tag 25")),
            // e621 command
            Commands.slash("e621", "Query e621")
                .addOptions(new OptionData(OptionType.STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "tag2", "The second tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag3", "The third tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag4", "The fourth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag5", "The fifth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag6", "The sixth tag to search")),
            // Gelbooru command
            Commands.slash("gelbooru", "Query gelbooru")
                .addOptions(new OptionData(OptionType.STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "tag2", "The second tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag3", "The third tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag4", "The fourth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag5", "The fifth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag6", "The sixth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag7", "The seventh tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag8", "The eighth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag9", "The ninth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag10", "The tenth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag11", "Tag 11"))
                .addOptions(new OptionData(OptionType.STRING, "tag12", "Tag 12"))
                .addOptions(new OptionData(OptionType.STRING, "tag13", "Tag 13"))
                .addOptions(new OptionData(OptionType.STRING, "tag14", "Tag 14"))
                .addOptions(new OptionData(OptionType.STRING, "tag15", "Tag 15"))
                .addOptions(new OptionData(OptionType.STRING, "tag16", "Tag 16"))
                .addOptions(new OptionData(OptionType.STRING, "tag17", "Tag 17"))
                .addOptions(new OptionData(OptionType.STRING, "tag18", "Tag 18"))
                .addOptions(new OptionData(OptionType.STRING, "tag19", "Tag 19"))
                .addOptions(new OptionData(OptionType.STRING, "tag20", "Tag 20"))
                .addOptions(new OptionData(OptionType.STRING, "tag21", "Tag 21"))
                .addOptions(new OptionData(OptionType.STRING, "tag22", "Tag 22"))
                .addOptions(new OptionData(OptionType.STRING, "tag23", "Tag 23"))
                .addOptions(new OptionData(OptionType.STRING, "tag24", "Tag 24"))
                .addOptions(new OptionData(OptionType.STRING, "tag25", "Tag 25")),
            // Gyate Booru command
            Commands.slash("gyatebooru", "Query gyate booru")
                .addOptions(new OptionData(OptionType.STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "tag2", "The second tag to search")),
            // Rule34 command
            Commands.slash("rule34", "Query Rule34")
                .addOptions(new OptionData(OptionType.STRING, "tag1", "The first tag to search")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "tag2", "The second tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag3", "The third tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag4", "The fourth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag5", "The fifth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag6", "The sixth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag7", "The seventh tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag8", "The eighth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag9", "The ninth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag10", "The tenth tag to search"))
                .addOptions(new OptionData(OptionType.STRING, "tag11", "Tag 11"))
                .addOptions(new OptionData(OptionType.STRING, "tag12", "Tag 12"))
                .addOptions(new OptionData(OptionType.STRING, "tag13", "Tag 13"))
                .addOptions(new OptionData(OptionType.STRING, "tag14", "Tag 14"))
                .addOptions(new OptionData(OptionType.STRING, "tag15", "Tag 15"))
                .addOptions(new OptionData(OptionType.STRING, "tag16", "Tag 16"))
                .addOptions(new OptionData(OptionType.STRING, "tag17", "Tag 17"))
                .addOptions(new OptionData(OptionType.STRING, "tag18", "Tag 18"))
                .addOptions(new OptionData(OptionType.STRING, "tag19", "Tag 19"))
                .addOptions(new OptionData(OptionType.STRING, "tag20", "Tag 20"))
                .addOptions(new OptionData(OptionType.STRING, "tag21", "Tag 21"))
                .addOptions(new OptionData(OptionType.STRING, "tag22", "Tag 22"))
                .addOptions(new OptionData(OptionType.STRING, "tag23", "Tag 23"))
                .addOptions(new OptionData(OptionType.STRING, "tag24", "Tag 24"))
                .addOptions(new OptionData(OptionType.STRING, "tag25", "Tag 25")),

            // ====== Social commands ======
            // Daily credits command
            Commands.slash("daily", "Get daily credits"),

            // ====== System commands ======   
            // Ban ID form bot command
            Commands.slash("banidglobal", "Ban an ID from using the bot globally")
                .addOptions(new OptionData(OptionType.STRING, "password", "The master password (specified in config.properties)")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "id", "The user ID to ban")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "reason", "The reason to ban")),
            // Unban ID form bot command
            Commands.slash("unbanidglobal", "Unban an ID from using the bot globally")
                .addOptions(new OptionData(OptionType.STRING, "password", "The master password (specified in config.properties)")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "id", "The user ID to unban")
                    .setRequired(true)),
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
            // Member webhook command
            Commands.slash("impersonatemember", "Impersonate a member of the guild by sending a webhook message as them")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to impersonate")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "message", "The message to send")
                    .setRequired(true))
                .setContexts(InteractionContextType.GUILD),
            // User webhook command
            Commands.slash("impersonateuser", "Impersonate a user globally by sending a webhook message as them")
                .addOptions(new OptionData(OptionType.STRING, "id", "The user ID")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "message", "The message to send")
                    .setRequired(true))
                .setContexts(InteractionContextType.GUILD),
            // Webhook message command
            Commands.slash("webhookmessage", "Create a webhook with custom username and avatar and send a message as it")
                .addOptions(new OptionData(OptionType.STRING, "username", "The username of the webhook")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "avatar_url", "The avatar URL of the webhook")
                    .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "message", "The message to send")
                    .setRequired(true))
                .setContexts(InteractionContextType.GUILD)
        ).queue();

        LOGGER.log(System.Logger.Level.INFO, "Commands finished loading!");
        System.out.println("Commands finished loading!");
    }

    /**
     * Handles slash commands for the bot.
     * 
     * @param jda The JDA instance
     * @param event The slash command event
     */
    public static void handleSlashCommand(@Nonnull JDA jda, @Nonnull SlashCommandInteractionEvent event) {
        if (BannedUsersManager.isBanned(event.getUser().getIdLong())) {
            event.reply("❌ You are globally banned from using this bot.").setEphemeral(true).queue();
            LOGGER.log(Level.INFO, "Banned user {0} ({1}) attempted to use command: {2}", 
                event.getUser().getGlobalName(), event.getUser().getId(), event.getName()
            );
            return;
        }
        switch (event.getName()) {
            // Admin
            case "ban" -> Ban.invoke(event);
            case "banid" -> BanId.invoke(event);
            case "deleteguildwebhooks" -> DeleteGuildWebhooks.invoke(event);
            case "generateinvite" -> GenerateInvite.invoke(event);
            case "kick" -> Kick.invoke(event);
            case "purgemessages" -> PurgeMessages.invoke(event);
            case "unbanid" -> UnbanId.invoke(event);
            // Audio
            case "checkqueue" -> CheckQueue.invoke(event);
            case "clear" -> Clear.invoke(event);
            case "disconnect" -> Disconnect.invoke(event);
            case "play" -> Play.invoke(event);
            case "skip" -> Skip.invoke(event);
            // Cryptography
            case "encryptaes" -> EncryptAes.invoke(event);
            case "sha256" -> Sha256.invoke(event);
            case "sha512" -> Sha512.invoke(event);
            case "verifysha256" -> VerifySha256.invoke(event);
            case "verifysha512" -> VerifySha512.invoke(event);
            // Game
            case "magic8ball" -> Magic8Ball.invoke(event);
            /// Chess
            case "newchess" -> NewChess.invoke(event);
            case "chessmove" -> ChessMove.invoke(event);
            /// Poker
            case "newpoker" -> NewPoker.invoke(event);
            case "pokermove" -> PokerMove.invoke(event);
            // General
            case "foxfacts" -> FoxFacts.invoke(event);
            case "ping" -> Ping.invoke(event);
            case "randomfox" -> RandomFox.invoke(event);
            case "weather" -> Weather.invoke(event);
            case "uwuify" -> Uwuify.invoke(event);
            // Imageboard
            case "danbooru" -> Danbooru.invoke(event);
            case "derpibooru" -> Derpibooru.invoke(event);
            case "e621" -> E621.invoke(event);
            case "gelbooru" -> Gelbooru.invoke(event);
            case "gyatebooru" -> GyateBooru.invoke(event);
            case "rule34" -> Rule34.invoke(event);
            // Social
            case "daily" -> Daily.invoke(event);
            // System
            case "banidglobal" -> BanIdGlobal.invoke(event, jda);
            case "unbanidglobal" -> UnbanIdGlobal.invoke(event, jda);
            case "wipeallwebhooks" -> DeleteAllWebhooks.invoke(event, jda);
            case "listguilds" -> ListGuilds.invoke(event, jda);
            case "reloadconfig" -> ReloadConfig.invoke(event, jda);
            case "reloadresponses" -> ReloadResponses.invoke(event, jda);
            case "shutdown" -> Shutdown.invoke(event, jda);
            // Webhook
            case "impersonatemember" -> ImpersonateMember.invoke(event);
            case "impersonateuser" -> ImpersonateUser.invoke(event);
            case "webhookmessage" -> WebhookMessage.invoke(event);
            // Default
            default -> event.reply("Invalid command!").setEphemeral(true).queue();
        }
    }
}
