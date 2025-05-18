package bot.ninetail.structures;

public class NinetailBotException extends Exception {
    public NinetailBotException() {
        super(); // calls Exception()
    }

    public NinetailBotException(String message) {
        super(message);
    }

    public NinetailBotException(String message, Throwable cause) {
        super(message, cause);
    }

    public NinetailBotException(Throwable cause) {
        super(cause);
    }
}
