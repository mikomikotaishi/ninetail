package bot.ninetail.commands.admin;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.commands.BasicCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Command to ban a user by ID from the server.
 * 
 * @implements BasicCommand
 */
@UtilityClass
public final class BanId implements BasicCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(BanId.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "BanId command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild().getName(),
            event.getGuild().getId()
        );

        @Nonnull 
        String userId = event.getOption("id").getAsString();

        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            LOGGER.log(Level.INFO, "Attempted (failed) ban attempt by {0} ({1})", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            hook.sendMessage("You do not have the required permissions to ban users from this server.").queue();
            return;
        }

        Member selfMember = event.getGuild().getSelfMember();
        if (!selfMember.hasPermission(Permission.BAN_MEMBERS)) {
            LOGGER.log(Level.INFO, "Attempted (failed) ban attempt by {0} ({1})", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            hook.sendMessage("I don't have the required permissions to ban users from this server.").queue();
            return;
        }

        int delDays = event.getOption("del_days", 0, OptionMapping::getAsInt);

        String reason = event.getOption("reason",
                () -> ("Banned by " + event.getUser().getName()),
                OptionMapping::getAsString
        );

        event.getGuild().ban(User.fromId(userId), delDays, TimeUnit.DAYS)
            .reason(reason)
            .flatMap(v -> hook.sendMessage("Banned user with ID: " + userId))
            .queue();

        LOGGER.log(Level.INFO, "Ban executed by {0} ({1}) on user ID {2}", 
            event.getUser().getGlobalName(), event.getUser().getId(), userId
        );
    }
}
