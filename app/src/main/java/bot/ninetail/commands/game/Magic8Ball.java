package bot.ninetail.commands.game;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.ContentResponder;
import bot.ninetail.structures.commands.GameCommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to provide a Magic 8 Ball response.
 * 
 * @extends ContentResponder
 * @implements GameCommand
 */
public final class Magic8Ball extends ContentResponder implements GameCommand {
    static {
        CONTENTS = new String[]{
            // Affirmative
            "It is certain :white_check_mark:",
            "It is decidedly so :white_check_mark:",
            "Without a doubt :white_check_mark:",
            "Yes definitely :white_check_mark:",
            "You may rely on it :white_check_mark:",
            "As I see it, yes :white_check_mark:",
            "Most likely :white_check_mark:",
            "Outlook good :white_check_mark:",
            "Yes :white_check_mark:",
            "Signs point to yes :white_check_mark:",
            // Neutral
            "Reply hazy, try again :large_orange_diamond:",
            "Ask again later :large_orange_diamond:",
            "Better not tell you now :large_orange_diamond:",
            "Cannot predict now :large_orange_diamond:",
            "Concentrate and ask again :large_orange_diamond:",
            // Negative
            "Don't count on it :x:",
            "My reply is no :x:",
            "My sources say no :x:",
            "Outlook not so good :x:",
            "Very doubtful :x:"
        };
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Magic8Ball() {};

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("Magic 8 Ball command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        event.reply(Magic8Ball.getRandomContent()).queue();
    }
}
