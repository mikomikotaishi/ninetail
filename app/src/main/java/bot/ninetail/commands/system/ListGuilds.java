package bot.ninetail.commands.system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.config.ConfigNames;
import bot.ninetail.core.config.ConfigPaths;
import bot.ninetail.core.logger.*;
import bot.ninetail.structures.commands.JdaCommand;
import bot.ninetail.system.ConfigLoader;
import bot.ninetail.util.TemporalFormatting;
import bot.ninetail.util.exceptions.IncorrectMasterIdException;
import bot.ninetail.util.exceptions.IncorrectPasswordException;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to reload the config files.
 * Can only be called by the bot master.
 * 
 * @implements JdaCommand
 */
@UtilityClass
public final class ListGuilds implements JdaCommand {
    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     * @param instance The JDA instance.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event, @Nonnull JDA instance) {
        Logger.log(LogLevel.INFO, "Guild list command attempted by %s (%s) of guild %s (%s)", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );

        String password = event.getOption("password").getAsString();
        try {
            if (!password.equals(ConfigLoader.getMasterPassword())) {
                throw new IncorrectPasswordException();
            } else if (!event.getUser().getId().equals(ConfigLoader.getBotMasterId())) {
                throw new IncorrectMasterIdException();
            }
            
            Logger.log(LogLevel.INFO, "Successful guild list by %s (%s)", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("Writing list of guilds.").setEphemeral(true).queue();

            File logDir = new File(ConfigPaths.LOG_PATH);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            String fileName = String.format(ConfigNames.LIST_GUILDS_NAME, LocalDateTime.now().format(TemporalFormatting.FILE_NAME_FORMAT));
            File guildListFile = new File(logDir, fileName);

            List<Guild> guilds = instance.getGuilds();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(guildListFile))) {
                writer.write(String.format("Bot is currently in %d guilds:\n", guilds.size()));
                
                for (Guild guild: guilds) {
                    writer.write(String.format("%s (%s)\n", guild.getName(), guild.getId()));
                }
                
                Logger.log(LogLevel.INFO, "Guild list written to %s", guildListFile.getAbsolutePath());
            } catch (IOException e) {
                Logger.log(LogLevel.ERROR, "Failed to write guild list: %s", e.getMessage());
                event.getHook().sendMessage("Error writing guild list to file.").setEphemeral(true).queue();
            }
        } catch (IncorrectPasswordException e) {
            Logger.log(LogLevel.INFO, "Attempted (failed) guild list by %s (%s) due to incorrect password", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("Incorrect master password!").setEphemeral(true).queue();
        } catch (IncorrectMasterIdException e) {
            Logger.log(LogLevel.INFO, "Attempted (failed) guild list by %s (%s) due to incorrect ID", 
                event.getUser().getGlobalName(), event.getUser().getId()
            );
            event.reply("Incorrect bot master ID!").setEphemeral(true).queue();
        }
    }
}
