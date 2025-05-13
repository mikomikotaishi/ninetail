package bot.ninetail.structures.commands;

import bot.ninetail.utilities.RandomNumberGenerator;

/**
 * Class that holds an array of strings containing contents, to be queried.
 */
public abstract class ContentResponder {
    /**
     * Contains the contents of the ContentResponder.
     */
    protected static String[] CONTENTS = {};

    /**
     * Returns the entire contents of the ContentResponder.
     * 
     * @return The CONTENTS array
     */
    protected static String[] getContents() {
        return CONTENTS;
    }

    /**
     * Retrieves a random item from CONTENTS.
     * 
     * @return A random item from CONTENTS
     */
    protected static String getRandomContent() {
        return CONTENTS[RandomNumberGenerator.generateRandomNumber(CONTENTS.length)];
    }
}
