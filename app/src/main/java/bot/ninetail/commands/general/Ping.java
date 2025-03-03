package bot.ninetail.commands.general;

import bot.ninetail.structures.commands.BasicCommand;

import jakarta.annotation.Nonnull;

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
        System.out.println("Ping command invoked.");
        long startTime = System.currentTimeMillis();
        event.reply("Konkon!").queue(response -> {
            long endTime = System.currentTimeMillis();
            long latency = endTime - startTime;
            response.editOriginal(String.format("Konkon! (Latency: %dms)", latency)).queue();
            System.out.println("Ping executed with latency %dms" + latency);
        });
    }
}
