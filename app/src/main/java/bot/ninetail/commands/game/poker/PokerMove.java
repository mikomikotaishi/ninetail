package bot.ninetail.commands.game.poker;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.game.poker.*;
import bot.ninetail.structures.commands.GameCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to make a move in the current poker game.
 * 
 * @implements GameCommand
 */
@UtilityClass
public final class PokerMove implements GameCommand {
    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, "Poker move command invoked by %s (%s) of guild %s (%s)", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        String moveType = event.getOption("move").getAsString();
        int amount = event.getOption("amount") != null ? event.getOption("amount").getAsInt() : 0;

        PokerEngine pokerEngine = PokerGameManager.getPokerEngine();
        if (pokerEngine == null) {
            event.reply("No poker game is currently running. Start a new game first.").queue();
            return;
        }

        int action;
        switch (moveType.toLowerCase()) {
            case "fold":
                action = 0;
                break;
            case "check":
                action = 1;
                break;
            case "call":
                action = 2;
                break;
            case "raise":
                action = 3;
                break;
            case "allin":
                action = 4;
                break;
            default:
                event.reply("Invalid move type. Valid moves are: `fold`, `check`, `call`, `raise`, `allin`.").queue();
                return;
        }

        String result = pokerEngine.executeAction(action, amount);
        event.reply(result).queue();
    }
}
