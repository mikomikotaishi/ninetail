package bot.ninetail.commands.game.poker;

import bot.ninetail.game.poker.*;
import bot.ninetail.structures.commands.GameCommand;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to make a move in the current poker game.
 * 
 * @implements GameCommand
 */
public final class PokerMove implements GameCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private PokerMove() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
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
