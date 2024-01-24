package com.androbohij;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Androbot extends ListenerAdapter {

    public final String TOKEN = "";

    public static void main(String[] args) {
        String TOKEN = new Secrets().TOKEN;
        JDA jda = JDABuilder.createDefault(TOKEN, Collections.emptyList())
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .enableIntents(GatewayIntent.GUILD_MESSAGES)
            .enableIntents(GatewayIntent.GUILD_EMOJIS_AND_STICKERS)
            .addEventListeners(new Androbot())
            .setActivity(Activity.customStatus("Crunching numbers"))
            .build();

        jda.updateCommands().addCommands(
            Commands.slash("random_sana", "Prints a random Sana emoji")
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "random_sana":

        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) 
            return;

        Message msg = event.getMessage();
        msgToLog(event);
    }

    void msgToLog(MessageReceivedEvent event) {
        System.out.println(event.getMessage().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME)
            + " " + event.getAuthor().getEffectiveName() + ": " + event.getMessage().getContentDisplay());
    }
}