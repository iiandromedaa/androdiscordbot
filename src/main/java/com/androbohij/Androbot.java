package com.androbohij;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.time.format.DateTimeFormatter;
import java.util.Collections;


import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.Permission;

public class Androbot extends ListenerAdapter {

    public final String TOKEN = "";

    public static void main(String[] args) throws IOException {
        String TOKEN = new Secrets().TOKEN;

        String path = Androbot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");

        Teller teller = new Teller(new File(decodedPath+"com/androbohij/cash.csv"));
        teller.loadToMap();
        System.out.println(teller.getMap());

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
                .addOptions(new OptionData(OptionType.USER, "user", "who you're sending money to", true))
                .addOptions(new OptionData(OptionType.NUMBER, "amount", "amount of money to send", true)),
            Commands.slash("close_acc", "close your account (all your money WILL be removed)"),
            Commands.slash("prune", "prunes messages")
                .setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
                .addOptions(new OptionData(OptionType.INTEGER, "number", "number of messages to prune (default 50)")),
            Commands.slash("get_snowflake", "prints out your discord id")
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
                User recipi = event.getOption("user", OptionMapping::getAsUser);
                transfer(event, recipi);
                break;
            case "close_acc":
                closeAcc(event);
                break;
            case "prune":
                prune(event);
                break;
            case "get_snowflake":
                getSnowflake(event);
                break;
            default:
                event.reply("nuh uh").setEphemeral(true).queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) 
            return;
        // Message msg = event.getMessage();
        msgToLog(event);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {
        String[] id = event.getComponentId().split(":"); // this is the custom id we specified in our button
        String authorId = id[0];
        String type = id[1];
        // Check that the button is for the user that clicked it, otherwise just ignore the event (let interaction fail)
        if (!authorId.equals(event.getUser().getId()))
            return;
        event.deferEdit().queue(); // acknowledge the button was clicked, otherwise the interaction will fail
 
        MessageChannel channel = event.getChannel();
        switch (type)
        {
            case "prune":
                int amount = Integer.parseInt(id[2]);
                event.getChannel().getIterableHistory()
                    .skipTo(event.getMessageIdLong())
                    .takeAsync(amount)
                    .thenAccept(channel::purgeMessages);
                // fallthrough delete the prompt message with our buttons
            case "delete":
                event.getHook().deleteOriginal().queue();
        }
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
            else if (ping >= 200)
                return event.getHook().editOriginalFormat("pong: %d ms <:sanahuh:1174119708023337010>", ping);
            else if (ping >= 100)
                return event.getHook().editOriginalFormat("pong: %d ms <:sanablep:1166253687899967488>", ping);
            else
                return event.getHook().editOriginalFormat("pong: %d ms <:sanayippee:1166253600763293707>", ping);
        }).queue();
    }

    void openAcc(SlashCommandInteractionEvent event) {
        // TODO implement opening account
        
        event.reply("are you sure you want to open an account?").setEphemeral(true).queue();
        
    }

    void transfer(SlashCommandInteractionEvent event, User recipi) {
        // TODO implement transfering
        
    }

    void closeAcc(SlashCommandInteractionEvent event) {
        // TODO implement closing account
        throw new UnsupportedOperationException("Unimplemented method 'closeAcc'");
    }

    //taken straight from the JDA examples so i can figure out how any of this works
    void prune(SlashCommandInteractionEvent event) {
        OptionMapping amountOption = event.getOption("number"); // This is configured to be optional so check for null
        int amount;
        
        if (amountOption == null)
            amount = 100;
        else
            amount = (int) Math.min(200, Math.max(2, amountOption.getAsLong()));

        //i changed the ternary to long form just because its more readable for me
        //im not used to java shorthand :(

        String userId = event.getUser().getId();
        event.reply("this deletes " + amount + " messages.\nreally?") // prompt the user with a button menu
            .addActionRow(// this means "<style>(<id>, <label>)", you can encode anything you want in the id (up to 100 characters)
                Button.secondary(userId + ":delete", "nah!"),
                Button.danger(userId + ":prune:" + amount, "yep!")) // the first parameter is the component id we use in onButtonInteraction above
            .queue();
    }

    void getSnowflake(SlashCommandInteractionEvent event) {
        event.reply(event.getInteraction().getMember().getId()).setEphemeral(true).queue();
    }

    void msgToLog(MessageReceivedEvent event) {
        System.out.println(event.getMessage().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME)
            + " " + event.getAuthor().getEffectiveName() + ": " + event.getMessage().getContentDisplay());
    }
}