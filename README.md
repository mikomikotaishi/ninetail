# Ninetail
<div align="center">
    <img src="./assets/ninetail.png" alt="Bot profile picture" width="25%">
</div>

## Overview
🦊 A general-use fox-themed Discord bot, written in Java with [JDA (Java Discord API)](https://github.com/discord-jda/JDA) and C++ (via `java.lang.foreign`).

## Usage:
To use the bot, ensure that Java is installed.

Before starting the bot, you must create a file `config.properties` in `app/src/main/resources`, containing a bot token and a shutdown password.
In that directory, there is a file called `INSTRUCTIONS.txt`.

To start the bot, use `./gradlew run`.

To shut off the bot, run the `shutdown` bot command with your shutdown password. (`Ctrl + C` works too, but the former is preferred.)

## TODO:
Upcoming features:
- Include support for other streaming services
- Implement imageboard image grabbing utilities
- Implement simple games with the bot (using C++)

## Terms of Service
By inviting or using Ninetail, you agree to the following terms:

### 1. Acceptable Use
- You must comply with Discord's Terms of Service and Community Guidelines
- You must not use the bot for any illegal activities or to harass other users
- You must not attempt to exploit, hack, or abuse the bot's functionality
- You must not use the bot to spam or flood channels

### 2. Service Availability
- The bot is provided "as is" without warranties of any kind
- We reserve the right to terminate or restrict access to the bot at any time
- The bot may experience downtime for maintenance or unforeseen issues
- We are not liable for any damages resulting from the use or inability to use the bot

### 3. User Conduct
- Misuse of admin commands (ban, kick, webhook impersonation) may result in being banned from using the bot
- The bot owner reserves the right to globally ban users who violate these terms
- Appeals for bans can be directed to the bot owner

### 4. Modifications
- We reserve the right to modify these terms at any time
- Continued use of the bot after changes constitutes acceptance of new terms

## Privacy Policy
**Last Updated:** 7 December, 2025

### Data We Collect
When you use Ninetail, we collect and store the following information:

1. **User Identifiers**: Discord user IDs and usernames for command logging and functionality
2. **Guild Information**: Server (guild) IDs and names for operational purposes
3. **Usage Logs**: Command invocations, timestamps, and associated user/guild information stored in local log files
4. **Database Storage**:
   - User wallet balances for economy features
   - Globally banned user IDs and ban reasons
   - Timestamps of certain user actions

By using features that query the OpenAI API, the data of your query is subject to the privacy policy of OpenAI.

### How We Use Your Data
Your data is used solely for:
- Operating core bot functionality (commands, games, features)
- Maintaining economy systems (user wallets)
- Enforcing bot-level bans and moderation
- Debugging and improving bot performance
- Logging for security and accountability purposes
All data that is obtained or consumed by the bot is obtained ethically or through the Discord API only.

### Data Retention
- **Logs**: Stored locally and retained indefinitely for operational purposes
- **Database Records**: Retained as long as necessary for bot functionality
- **Banned User Records**: Retained until the ban is lifted
Data may be stored on the local device for those who run this bot locally.

### Data Sharing
We do **not** sell, rent, or share your personal data with third parties. Your data remains within the bot's operational environment.

### Your Rights
You have the right to:
- Request information about what data we store about you
- Request deletion of your data (where feasible without breaking bot functionality)
- Opt-out by removing the bot from your server or ceasing to use bot commands

### Third-Party Services
This bot uses:
- **Discord API**: Subject to Discord's Privacy Policy
- **External APIs**: For features like location information and image services (if configured)

### Security
We implement reasonable security measures to protect stored data. However, no system is completely secure, and we cannot guarantee absolute security.

### Children's Privacy
This bot does not knowingly collect data from users under 13. If you believe a child under 13 has provided data through this bot, please contact us.

### Contact
For questions, concerns, or data requests regarding this Privacy Policy, please contact the bot owner through the repository or Discord.
