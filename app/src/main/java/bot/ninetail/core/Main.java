package bot.ninetail.core;

import java.util.EnumSet;

import javax.security.auth.login.LoginException;

import jakarta.annotation.Nonnull;
import bot.ninetail.system.*;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

/**
 * The main class (entry point) of the entire bot program.
 */
@UtilityClass
public final class Main {
    /**
     * Main method for the bot.
     * 
     * @param args Command line arguments
     * 
     * @throws LoginException If the bot fails to log in
     * @throws InterruptedException If the bot is interrupted
     */
    public static void main(String[] args) throws LoginException, InterruptedException {
        @Nonnull 
        final String BOT_TOKEN = ConfigLoader.getBotToken();

        EnumSet<GatewayIntent> INTENTS = EnumSet.of(
            GatewayIntent.GUILD_EXPRESSIONS,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.SCHEDULED_EVENTS
        );

        Logger.log(LogLevel.INFO, "Starting bot.");
        JDA api = JDABuilder.createDefault(BOT_TOKEN, INTENTS)
            .build()
            .awaitReady();

        CommandListUpdateAction commands = api.updateCommands();

        CommandHandler.loadCommands(commands);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.log(LogLevel.INFO, "Invoking shutdown hook");
            System.out.println("Shutting down bot.");
            Logger.close();
        }));

        DatabaseHandler.loadDatabase();

        Logger.log(LogLevel.INFO, "Instantiating instance.");
        Ninetail botInstance = new Ninetail(api);
        api.addEventListener(botInstance);
    }
}
