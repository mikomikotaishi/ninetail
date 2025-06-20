package bot.ninetail.commands.admin;

import java.util.concurrent.TimeUnit;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.BasicCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.channel.attribute.IInviteContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Command to generate an invite link for the server.
 * 
 * @implements BasicCommand
 */
@UtilityClass
public final class GenerateInvite implements BasicCommand {
    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("Generate invite command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild().getName(),
                                                event.getGuild().getId())
        );

        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();

        if (!event.getMember().hasPermission(Permission.CREATE_INSTANT_INVITE)) {
            Logger.log(LogLevel.INFO, String.format("Failed invite generation attempt by %s (%s) - insufficient permissions", 
                event.getUser().getGlobalName(), event.getUser().getId()));
            hook.sendMessage("❌ You don't have permission to create invites for this server.").queue();
            return;
        }

        if (!event.getGuild().getSelfMember().hasPermission(Permission.CREATE_INSTANT_INVITE)) {
            Logger.log(LogLevel.WARN, "Bot lacks CREATE_INSTANT_INVITE permission");
            hook.sendMessage("❌ I don't have permission to create invites for this server.").queue();
            return;
        }

        int maxAge = event.getOption("max_age", 0, OptionMapping::getAsInt);
        int maxUses = event.getOption("max_uses", 0, OptionMapping::getAsInt);
        boolean temporary = event.getOption("temporary", false, OptionMapping::getAsBoolean);
        boolean unique = event.getOption("unique", false, OptionMapping::getAsBoolean);
        
        GuildChannel channel = event.getOption("channel",
            () -> event.getGuildChannel(),
            OptionMapping::getAsChannel);

        if (!(channel instanceof IInviteContainer targetChannel)) {
            hook.sendMessage("❌ The selected channel cannot have invites.").queue();
            return;
        }

        targetChannel.createInvite()
            .setMaxAge((long)maxAge, TimeUnit.SECONDS)
            .setMaxUses(maxUses)
            .setTemporary(temporary)
            .setUnique(unique)
            .queue(
                invite -> {
                    String inviteInfo = buildInviteInfo(invite, maxAge, maxUses, temporary);
                    hook.sendMessage(String.format("🔗 **Invite Created Successfully!**\n\n**Invite Link:** %s\n\n%s", invite.getUrl(), inviteInfo)).queue();

                    Logger.log(LogLevel.INFO, String.format("Invite created by %s (%s) - %s", 
                        event.getUser().getGlobalName(), event.getUser().getId(), invite.getCode()));
                },
                error -> {
                    Logger.log(LogLevel.ERROR, String.format("Failed to create invite: %s", error.getMessage()));
                    hook.sendMessage("❌ Failed to create invite. Please try again later.").queue();
                }
            );
    }

    /**
     * Builds the invite information string.
     *
     * @param invite The created invite
     * @param maxAge Maximum age in seconds
     * @param maxUses Maximum uses
     * @param temporary Whether the invite grants temporary membership
     * @return Formatted invite information
     */
    @Nonnull
    private static String buildInviteInfo(@Nonnull Invite invite, int maxAge, int maxUses, boolean temporary) {
        StringBuilder info = new StringBuilder();
        
        info.append("**Invite Details:**\n");
        info.append("📺 **Channel:** ").append(invite.getChannel().getName()).append("\n");
        
        if (maxAge == 0)
            info.append("⏰ **Expires:** Never\n");
        else
            info.append("⏰ **Expires:** <t:").append((System.currentTimeMillis() / 1000) + maxAge).append(":R>\n");
        
        if (maxUses == 0)
            info.append("🔢 **Max Uses:** Unlimited\n");
        else
            info.append("🔢 **Max Uses:** ").append(maxUses).append("\n");
        
        info.append("👥 **Temporary Membership:** ").append(temporary ? "Yes" : "No").append("\n");
        info.append("🆔 **Invite Code:** `").append(invite.getCode()).append("`");
        
        return info.toString();
    }
}
