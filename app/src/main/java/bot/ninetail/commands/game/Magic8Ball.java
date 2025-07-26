package bot.ninetail.commands.game;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.logger.*;
import bot.ninetail.structures.commands.ContentResponder;
import bot.ninetail.structures.commands.GameCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to provide a Magic 8 Ball response.
 * 
 * @extends ContentResponder
 * @implements GameCommand
 */
@UtilityClass
public final class Magic8Ball extends ContentResponder implements GameCommand {
    /**
     * Static block to load contents.
     */
    static {
        setContents(Magic8Ball.class, 
            new String[]{
                // Affirmative
                "It is certain ✅",
                "It is decidedly so ✅",
                "Without a doubt ✅",
                "Yes definitely ✅",
                "You may rely on it ✅",
                "As I see it, yes ✅",
                "Most likely ✅",
                "Outlook good ✅",
                "Yes ✅",
                "Signs point to yes ✅",
                // Neutral
                "Reply hazy, try again 🔶",
                "Ask again later 🔶",
                "Better not tell you now 🔶",
                "Cannot predict now 🔶",
                "Concentrate and ask again 🔶",
                // Negative
                "Don't count on it ❌",
                "My reply is no ❌",
                "My sources say no ❌",
                "Outlook not so good ❌",
                "Very doubtful ❌"
            }
        );
    }

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, "Magic 8 Ball command invoked by %s (%s) of guild %s (%s)", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        event.reply(Magic8Ball.getRandomContent()).queue();
    }
}
