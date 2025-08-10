package bot.ninetail.commands.game.chess;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;

import bot.ninetail.game.natives.chess.*;
import bot.ninetail.structures.commands.GameCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to start a new chess game.
 * 
 * @implements GameCommand
 */
@UtilityClass
public final class NewChess implements GameCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(NewChess.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "New chess game command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        ChessGameManager.startNewGame();
        ChessEngine chessEngine = ChessGameManager.getChessEngine();
        
        String boardState = chessEngine.getBoardState();
        LOGGER.log(Level.INFO, "NewChess command - received board state: {0}", boardState);
        
        if (boardState.startsWith("ERROR:")) {
            boardState = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        }
        
        String boardDisplay = chessEngine.convertFenToEmoji(boardState);
        event.reply("New game started!\n" + boardDisplay).queue();
    }
}
