package bot.ninetail.commands.game.poker;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;

import bot.ninetail.game.poker.*;
import bot.ninetail.structures.commands.GameCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to start a new poker game.
 * 
 * @implements GameCommand
 */
@UtilityClass
public final class NewPoker implements GameCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(NewPoker.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "New poker game command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        PokerGameManager.startNewGame();
        LOGGER.log(Level.INFO, "A new poker game has been started by {0} of guild {1}", event.getUser(), event.getGuild());
        event.reply("A new poker game has been started!").queue();
    }
}
