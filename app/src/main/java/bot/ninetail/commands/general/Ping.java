package bot.ninetail.commands.general;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.BasicCommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to provide a simple ping response.
 * 
 * @implements BasicCommand
 */
public final class Ping implements BasicCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private Ping() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("Ping command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        long startTime = System.currentTimeMillis();
        event.reply("Konkon!").queue(response -> {
            long endTime = System.currentTimeMillis();
            long latency = endTime - startTime;
            response.editOriginal(String.format("Konkon! (Latency: %dms)", latency)).queue();
            Logger.log(LogLevel.INFO, String.format("Ping executed with latency %dms", latency));
        });
    }
}
