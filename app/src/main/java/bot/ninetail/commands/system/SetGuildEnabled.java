package bot.ninetail.commands.system;

import jakarta.annotation.Nonnull;

import bot.ninetail.audio.BotAudio;
import bot.ninetail.structures.commands.JdaCommand;
import bot.ninetail.system.ConfigLoader;
import bot.ninetail.system.DisabledGuildsManager;
import bot.ninetail.util.IncorrectMasterIdException;
import bot.ninetail.util.IncorrectPasswordException;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Command to disable or re-enable all bot activity in a guild.
 * Can only be called by the bot master.
 *
 * While a guild is disabled the bot ignores its commands and stays silent in it. The bot master
 * commands still work there, so that the guild can be re-enabled from inside it.
 *
 * @implements JdaCommand
 */
@UtilityClass
public final class SetGuildEnabled implements JdaCommand {
    @Nonnull
    private static final System.Logger LOGGER = System.getLogger(SetGuildEnabled.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     * @param instance The JDA instance.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event, @Nonnull JDA instance) {
        LOGGER.log(System.Logger.Level.INFO, "Set guild enabled command attempted by {0} ({1}) of guild {2} ({3})",
            event.getUser().getGlobalName(),
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );

        String password = event.getOption("password").getAsString();
        String targetGuildId = event.getOption("id").getAsString();
        boolean enabled = event.getOption("enabled").getAsBoolean();
        String reason = event.getOption("reason", "No reason provided", OptionMapping::getAsString);

        try {
            if (!password.equals(ConfigLoader.getMasterPassword())) {
                throw new IncorrectPasswordException();
            } else if (!event.getUser().getId().equals(ConfigLoader.getBotMasterId())) {
                throw new IncorrectMasterIdException();
            }

            // Parsed before deferring, so that a malformed ID can still be answered with a reply.
            long guildId = Long.parseLong(targetGuildId);

            event.deferReply(true).queue();

            if (enabled) {
                enable(event, guildId, targetGuildId);
            } else {
                disable(event, guildId, targetGuildId, reason);
            }

        } catch (NumberFormatException e) {
            event.reply("❌ Invalid guild ID format!").setEphemeral(true).queue();
        } catch (IncorrectPasswordException e) {
            LOGGER.log(System.Logger.Level.INFO, "Attempted (failed) guild toggle by {0} ({1}) due to incorrect password",
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("❌ Incorrect master password!").setEphemeral(true).queue();
        } catch (IncorrectMasterIdException e) {
            LOGGER.log(System.Logger.Level.INFO, "Attempted (failed) guild toggle by {0} ({1}) due to incorrect ID",
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("❌ Incorrect bot master ID!").setEphemeral(true).queue();
        }
    }

    /**
     * Disables all bot activity in a guild.
     *
     * @param event The event that triggered the command.
     * @param guildId The guild ID to disable the bot in.
     * @param targetGuildId The guild ID as supplied, for display.
     * @param reason The reason the bot was disabled.
     */
    private static void disable(@Nonnull SlashCommandInteractionEvent event, long guildId,
                                @Nonnull String targetGuildId, @Nonnull String reason) {
        if (!DisabledGuildsManager.disableGuild(guildId, event.getUser().getIdLong(), reason)) {
            event.getHook().editOriginal(
                "❌ Failed to disable the bot. It may already be disabled there, or the database is unavailable."
            ).queue();
            return;
        }

        // Audio is the one activity that continues without any further command, so it is stopped.
        BotAudio.disconnectIfActive(guildId);

        event.getHook().editOriginal(
            String.format("✅ The bot has been disabled in guild `%s`.\n**Reason:** %s", targetGuildId, reason)
        ).queue();

        LOGGER.log(System.Logger.Level.INFO, "Bot disabled in guild {0} by {1} ({2}). Reason: {3}",
            targetGuildId, event.getUser().getGlobalName(), event.getUser().getId(), reason
        );
    }

    /**
     * Re-enables all bot activity in a guild.
     *
     * @param event The event that triggered the command.
     * @param guildId The guild ID to re-enable the bot in.
     * @param targetGuildId The guild ID as supplied, for display.
     */
    private static void enable(@Nonnull SlashCommandInteractionEvent event, long guildId,
                               @Nonnull String targetGuildId) {
        if (!DisabledGuildsManager.enableGuild(guildId)) {
            event.getHook().editOriginal(
                "❌ Failed to enable the bot. It may not be disabled there, or the database is unavailable."
            ).queue();
            return;
        }

        event.getHook().editOriginal(
            String.format("✅ The bot has been re-enabled in guild `%s`.", targetGuildId)
        ).queue();

        LOGGER.log(System.Logger.Level.INFO, "Bot re-enabled in guild {0} by {1} ({2})",
            targetGuildId, event.getUser().getGlobalName(), event.getUser().getId()
        );
    }
}
