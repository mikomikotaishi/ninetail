package bot.ninetail.commands.webhook;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.WebhookCommand;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;

/**
 * Command to create a webhook of a user.
 * 
 * @implements Command
 */
public final class UserWebhook implements WebhookCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private UserWebhook() {}

    /**
     * Invokes the command.
     * 
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("User webhook command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        Guild guild = event.getGuild();
        TextChannel channel = event.getChannel().asTextChannel();
        Member member = event.getOption("user").getAsMember();
        String message = event.getOption("message").getAsString();

        if (guild == null || member == null) {
            Logger.log(LogLevel.INFO, "Failed to create webhook due to invalid guild or user.");
            event.reply("Invalid guild or user.").setEphemeral(true).queue();
            return;
        }

        WebhookAction webhookAction = channel.createWebhook(member.getEffectiveName());
        webhookAction.queue(webhook -> {
            webhook.sendMessage(message)
                .setUsername(member.getEffectiveName())
                .setAvatarUrl(member.getEffectiveAvatarUrl())
                .queue();
            event.reply(String.format("Webhook created and message sent as %s of guild %s", member.getEffectiveName(), guild.getName())).setEphemeral(true).queue();
            Logger.log(LogLevel.INFO, String.format("Created webhook for %s of guild %s", member.getEffectiveName(), guild.getName()));
        }, error -> {
            event.reply(String.format("Failed to create webhook: %s of guild %s", error.getMessage(), guild.getName())).setEphemeral(true).queue();
            Logger.log(LogLevel.INFO, String.format("Failed to create webhook for %s of guild %s", member.getEffectiveName(), guild.getName()));
        });
    }
}
