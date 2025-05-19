package bot.ninetail.structures;

import bot.ninetail.core.config.*;

/**
 * Abstract base class for all config files.
 * This class is used for utility classes that store config constants for the bot.
 *
 * 
 * @permits ConfigNames
 * @permits ConfigPaths
 */
public sealed abstract class ConfigFile permits ConfigNames, ConfigPaths {
    
}
