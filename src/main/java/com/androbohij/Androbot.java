package com.androbohij;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
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
            Commands.slash("ping", "gets ping"),
            Commands.slash("random_sana", "Prints a random Sana emoji"),
            Commands.slash("open_acc", "opens a new bank account (fails if you already have one)"),
            Commands.slash("transfer", "transfers tomilliens to another user's account")
                .addOptions(new OptionData(OptionType.USER, "user", "who you're sending money to"))
                .addOptions(new OptionData(OptionType.NUMBER, "amount", "amount of money to send")),
            Commands.slash("close_acc", "close your account (all your money WILL be removed)")
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ping":
                ping(event);
                break;
            case "random_sana":
                randomSana(event);
                break;
            case "open_acc":
                openAcc(event);
                break;
            case "transfer":
                transfer(event);
                break;
            case "close_acc":
                closeAcc(event);
                break;
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) 
            return;
        // Message msg = event.getMessage();
        msgToLog(event);
    }

    void randomSana(SlashCommandInteractionEvent event) {
        int random = (int)Math.floor(Math.random() * Storage.sanas.length);
        String s = Storage.sanas[random];
        event.reply(s).queue();
        event.getChannel().sendMessage("you got " + StringUtils.substringBetween(s, ":", ":")).queue();
    }

    void ping(SlashCommandInteractionEvent event) {
        long time = System.currentTimeMillis();
        event.reply("pong!").setEphemeral(true)
        .flatMap(v -> {
            long ping = System.currentTimeMillis() - time;
            if (ping >= 500.0)
                return event.getHook().editOriginalFormat("pong: %d ms <:sanadepressed:1164768043294015530>", ping);
            else if (ping >= 350)
                return event.getHook().editOriginalFormat("pong: %d ms <:sanadisappointed:1166238574061027338>", ping);
            else if (ping >= 150)
                return event.getHook().editOriginalFormat("pong: %d ms <:sanahuh:1174119708023337010>", ping);
            else
                return event.getHook().editOriginalFormat("pong: %d ms <:sanayippee:1166253600763293707>", ping);
        }).queue();
    }

    void openAcc(SlashCommandInteractionEvent event) {
        // TODO implement opening account
        event.reply("are you sure you want to open an account?").setEphemeral(true).queue();
    }

    void transfer(SlashCommandInteractionEvent event) {
        // TODO implement transfering
        throw new UnsupportedOperationException("Unimplemented method 'openAcc'");
    }

    void closeAcc(SlashCommandInteractionEvent event) {
        // TODO implement closing account
        throw new UnsupportedOperationException("Unimplemented method 'closeAcc'");
    }

    void msgToLog(MessageReceivedEvent event) {
        System.out.println(event.getMessage().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME)
            + " " + event.getAuthor().getEffectiveName() + ": " + event.getMessage().getContentDisplay());
    }
}