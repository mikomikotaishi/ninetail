package bot.ninetail.structures;

/**
 * Base exception class for all bot-specific exceptions.
 */
public class NinetailBotException extends Exception {
    /**
     * Constructor for NinetailBotException
     */
    public NinetailBotException() {
        super();
    }

    /**
     * Constructor for NinetailBotException
     * 
     * @param message
     */
    public NinetailBotException(String message) {
        super(message);
    }

    /**
     * Constructor for NinetailBotException
     * 
     * @param message
     * @param cause
     */
    public NinetailBotException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for NinetailBotException
     * 
     * @param cause
     */
    public NinetailBotException(Throwable cause) {
        super(cause);
    }
}
