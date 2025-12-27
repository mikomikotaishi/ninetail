package bot.ninetail.commands.admin;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

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
 * Command to kick a user from the server.
 * 
 * @implements BasicCommand
 */
@UtilityClass
public final class Kick implements BasicCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(Kick.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "Kick command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild().getName(),
            event.getGuild().getId()
        );

        @Nonnull
        Member member = event.getOption("user").getAsMember();
        @Nonnull
        User user = event.getOption("user").getAsUser();

        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            LOGGER.log(Level.INFO, "Attempted (failed) kick attempt by {0} ({1})", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            hook.sendMessage("You do not have the required permissions to kick users from this server.").queue();
            return;
        }

        Member selfMember = event.getGuild().getSelfMember();
        if (!selfMember.hasPermission(Permission.KICK_MEMBERS)) {
            LOGGER.log(Level.INFO, "Attempted (failed) kick attempt by {0} ({1})", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            hook.sendMessage("I don't have the required permissions to kick users from this server.").queue();
            return;
        }

        if (member != null && !selfMember.canInteract(member)) {
            LOGGER.log(Level.INFO, "Attempted (failed) kick attempt by {0} ({1})", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            hook.sendMessage("This user is too powerful for me to kick.").queue();
            return;
        }

        String reason = event.getOption("reason",
            () -> ("Kicked by " + event.getUser().getName()),
            OptionMapping::getAsString
        );

        event.getGuild().kick(member)
            .reason(reason)
            .flatMap(v -> hook.sendMessage("Kicked user " + user.getName()))
            .queue();

        LOGGER.log(Level.INFO, "Kick executed by {0} ({1}) on {2} ({3})", 
            event.getUser().getGlobalName(), event.getUser().getId(), user.getName(), user.getId()
        );
    }
}
