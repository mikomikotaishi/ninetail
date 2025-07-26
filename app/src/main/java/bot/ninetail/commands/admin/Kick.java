package bot.ninetail.commands.admin;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.logger.*;
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
    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, "Kick command invoked by %s (%s) of guild %s (%s)", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild().getName(),
            event.getGuild().getId()
        );

        Member member = event.getOption("user").getAsMember();
        User user = event.getOption("user").getAsUser();

        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            Logger.log(LogLevel.INFO, "Attempted (failed) kick attempt by %s (%s)", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            hook.sendMessage("You do not have the required permissions to kick users from this server.").queue();
            return;
        }

        Member selfMember = event.getGuild().getSelfMember();
        if (!selfMember.hasPermission(Permission.KICK_MEMBERS)) {
            Logger.log(LogLevel.INFO, "Attempted (failed) kick attempt by %s (%s)", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            hook.sendMessage("I don't have the required permissions to kick users from this server.").queue();
            return;
        }

        if (member != null && !selfMember.canInteract(member)) {
            Logger.log(LogLevel.INFO, "Attempted (failed) kick attempt by %s (%s)", 
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

        Logger.log(LogLevel.INFO, "Kick executed by %s (%s) on %s (%s)", 
            event.getUser().getGlobalName(), event.getUser().getId(), user.getName(), user.getId()
        );
    }
}
