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
import bot.ninetail.commands.webhook.*;
import bot.ninetail.structures.InteractionHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Class to handle responses to slash commands.
 * This class is used to handle slash commands received by the bot.
 * 
 * @extends InteractionHandler
 */
public class CommandHandler extends InteractionHandler {
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
