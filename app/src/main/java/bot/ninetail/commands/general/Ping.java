package bot.ninetail.commands.general;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.commands.BasicCommand;
import bot.ninetail.structures.commands.ContentResponder;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to provide a simple ping response.
 * 
 * @extends ContentResponder
 * @implements BasicCommand
 */
@UtilityClass
public final class Ping extends ContentResponder implements BasicCommand {
    @Nonnull
    private static final System.Logger LOGGER = System.getLogger(Ping.class.getName());

    /**
     * Static block to load contents.
     */
    static {
        setContents(Ping.class, 
            new String[]{
                "Konkon!",
                "Konkon~",
                "こんこん〜",
                "Mofumofu!",
                "Mofumofu~",
                "もふもふ〜",
                "Fuwafuwa!",
                "Fuwafuwa~",
                "ふわふわ〜"
            }
        );
    }

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(System.Logger.Level.INFO, "Ping command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        long startTime = System.currentTimeMillis();
        final String pingMessage = getRandomContent();
        event.reply(pingMessage).queue(response -> {
            long endTime = System.currentTimeMillis();
            long latency = endTime - startTime;
            response.editOriginal(String.format("%s (Latency: %dms)", pingMessage, latency)).queue();
            LOGGER.log(System.Logger.Level.INFO, "Ping executed with latency {0}ms", latency);
        });
    }
}
