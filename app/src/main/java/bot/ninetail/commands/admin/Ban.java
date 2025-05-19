package bot.ninetail.commands.admin;

import java.util.concurrent.TimeUnit;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.BasicCommand;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Command to ban a user from the server.
 * 
 * @implements BasicCommand
 */
public final class Ban implements BasicCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private Ban() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("Ban command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild().getName(),
                                                event.getGuild().getId())
        );

        Member member = event.getOption("user").getAsMember();
        User user = event.getOption("user").getAsUser();

        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            Logger.log(LogLevel.INFO, String.format("Attempted (failed) ban attempt by %s (%s)", event.getUser().getGlobalName(), event.getUser().getId()));
            hook.sendMessage("You do not have the required permissions to ban users from this server.").queue();
            return;
        }

        Member selfMember = event.getGuild().getSelfMember();
        if (!selfMember.hasPermission(Permission.BAN_MEMBERS)) {
            Logger.log(LogLevel.INFO, String.format("Attempted (failed) shutdown attempt by %s (%s)", event.getUser().getGlobalName(), event.getUser().getId()));
            hook.sendMessage("I don't have the required permissions to ban users from this server.").queue();
            return;
        }

        if (member != null && !selfMember.canInteract(member)) {
            Logger.log(LogLevel.INFO, String.format("Attempted (failed) shutdown attempt by %s (%s)", event.getUser().getGlobalName(), event.getUser().getId()));
            hook.sendMessage("This user is too powerful for me to ban.").queue();
            return;
        }

        int delDays = event.getOption("del_days", 0, OptionMapping::getAsInt);

        String reason = event.getOption("reason",
                () -> ("Banned by " + event.getUser().getName()),
                OptionMapping::getAsString);

        event.getGuild().ban(user, delDays, TimeUnit.DAYS)
            .reason(reason)
            .flatMap(v -> hook.sendMessage("Banned user " + user.getName()))
            .queue();

        Logger.log(LogLevel.INFO, String.format("Ban executed by %s (%s) on %s (%s)", 
            event.getUser().getGlobalName(), event.getUser().getId(), user.getName(), user.getId()));
    }
}
