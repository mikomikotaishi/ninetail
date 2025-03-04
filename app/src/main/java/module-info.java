module bot.ninetail {
    requires java.base;
    requires java.net.http;
    requires jakarta.annotation;
    requires transitive jakarta.json;
    requires org.slf4j;
    requires org.mockito;
    requires net.dv8tion.jda;

    opens bot.ninetail.core to jakarta.json;

    exports bot.ninetail.audio;
    exports bot.ninetail.clients;
    exports bot.ninetail.commands.admin;
    exports bot.ninetail.commands.audio;
    exports bot.ninetail.commands.cryptography;
    exports bot.ninetail.commands.game;
    exports bot.ninetail.commands.general;
    exports bot.ninetail.commands.imageboard;
    exports bot.ninetail.commands.system;
    exports bot.ninetail.core;
    exports bot.ninetail.game.chess;
    exports bot.ninetail.game.poker;
    exports bot.ninetail.system;
}