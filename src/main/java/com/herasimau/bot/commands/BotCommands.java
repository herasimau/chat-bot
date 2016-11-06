package com.herasimau.bot.commands;

import java.util.HashSet;

/**
 * Created by herasimau on 05/11/16.
 */
public class BotCommands {
    private static HashSet<String> botCommands = new HashSet<>();
    static {
        botCommands.add("!stats");
        botCommands.add("!mystat");
    }
    public static HashSet<String> getBotCommands (){
        return botCommands;
    }
}
