/**
 * The module of the entire bot system.
 */
module bot.ninetail {
    requires java.base;
    requires java.net.http;
    requires java.scripting;
    requires transitive java.sql;

    requires jakarta.annotation;
    requires transitive jakarta.json;

    requires kotlin.stdlib;

    requires static lombok;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.zaxxer.hikari;
    // requires lavaplayer;
    requires transitive net.dv8tion.jda;
    requires org.mockito;
    requires org.postgresql.jdbc;
    requires org.slf4j;
    
    exports bot.ninetail.audio;
    exports bot.ninetail.clients;
    exports bot.ninetail.commands.admin;
    exports bot.ninetail.commands.audio;
    exports bot.ninetail.commands.cryptography;
    exports bot.ninetail.commands.game;
    exports bot.ninetail.commands.general;
    exports bot.ninetail.commands.imageboard;
    exports bot.ninetail.commands.social;
    exports bot.ninetail.commands.system;
    exports bot.ninetail.commands.webhook;
    // exports bot.ninetail.core;
    exports bot.ninetail.core.config;
    exports bot.ninetail.game;
    exports bot.ninetail.game.chess;
    exports bot.ninetail.game.poker;
    exports bot.ninetail.system;
}
