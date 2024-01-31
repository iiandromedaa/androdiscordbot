package com.androbohij;

import java.awt.Color;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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

    public static final String TOKEN = new Secrets().TOKEN;

    static JDA jda = JDABuilder.createDefault(TOKEN, Collections.emptyList())
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .enableIntents(GatewayIntent.GUILD_MESSAGES)
            .enableIntents(GatewayIntent.GUILD_EMOJIS_AND_STICKERS)
            .addEventListeners(new Androbot())
            .setActivity(Activity.customStatus("Crunching numbers"))
            .build();
            
    public static void main(String[] args) throws IOException {
        Teller.loadToMap();
        System.out.println(Teller.getMap());

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
            Commands.slash("get_snowflake", "prints out your discord id"),
            Commands.slash("show_acc", "prints out your account details")
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
            case "prune":
                prune(event);
                break;
            case "get_snowflake":
                getSnowflake(event);
                break;
            case "show_acc":
                showAcc(event);
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
        switch (type) {
            case "prune":
                int amount = Integer.parseInt(id[2]);
                event.getChannel().getIterableHistory()
                    .skipTo(event.getMessageIdLong())
                    .takeAsync(amount)
                    .thenAccept(channel::purgeMessages);
                event.getHook().deleteOriginal().queue();
                break;
            case "yes_acc":
                if (Teller.newAccount(event.getUser().getId(), event.getUser().getName()) != 0) {
                    event.getHook().sendMessage("account creation fail (you already have an account) <:sanadisappointed:1166238574061027338>").setEphemeral(true).queue();
                    event.getHook().deleteOriginal().queue();
                    break;
                } else {
                    System.out.println(Teller.getMap());
                    event.getHook().sendMessage("account creation success <:sanayippee:1166253600763293707>").setEphemeral(true).queue();
                    event.getHook().deleteOriginal().queue();
                    break; 
                }
            case "del_acc":
                if (Teller.getAccount(event.getUser().getId()).isEmpty()) {
                    event.getHook().sendMessage("account creation fail (you dont have an account) <:sanadisappointed:1166238574061027338>").setEphemeral(true).queue();
                    event.getHook().deleteOriginal().queue();
                    break;
                } else {
                    Teller.deleteAccount(event.getUser().getId());
                    event.getHook().sendMessage("account deletion succeeded <:sanadisappointed:1166238574061027338>").setEphemeral(true).queue();
                    event.getHook().deleteOriginal().queue();
                    System.out.println(Teller.getMap());
                    break;
                }
            case "yes_trade":
                if (Teller.getAccount(event.getUser().getId()).isEmpty() | Double.parseDouble(Teller.getAccount(event.getUser().getId()).get("tomilliens")) < Double.parseDouble(id[3])) {
                    event.getHook().sendMessage("transfer failed (you dont have an account or dont have enough money) <:sanadisappointed:1166238574061027338>").setEphemeral(true).queue();
                    event.getHook().deleteOriginal().queue();
                    break;
                } else {
                    Teller.transfer(event.getUser().getId(), id[2], Double.parseDouble(id[3]));
                    event.getHook().sendMessage("transfer succeeded <:sanayippee:1166253600763293707>").setEphemeral(true).queue();
                    event.getHook().deleteOriginal().queue();
                    jda.openPrivateChannelById(id[2]).flatMap(chanl -> chanl.sendMessage(event.getUser().getName() + " has sent you " + id[3] + " tomilliens")).queue();
                    System.out.println(Teller.getMap());
                    break;
                }
            case "delete":
                event.getHook().deleteOriginal().queue();
                break;
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
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("account action");
        eb.setDescription("are you sure you wanna open an account?");
        eb.setColor(new Color(255, 255, 255));
        event.replyEmbeds(eb.build())
            .addActionRow(
                Button.secondary(event.getUser().getId() + ":delete", "nah"),
                Button.success(event.getUser().getId() + ":yes_acc", "ya")
        ).setEphemeral(true).queue();
        
    }

    void transfer(SlashCommandInteractionEvent event) {
        OptionMapping recipiOption = event.getOption("user");
        OptionMapping amountOption = event.getOption("amount");
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("account action");
        eb.setDescription("are you sure you wanna transfer?");
        eb.setColor(new Color(255, 210, 87));
        event.replyEmbeds(eb.build())
            .addActionRow(
                Button.secondary(event.getUser().getId() + ":delete", "nah"),
                Button.success(event.getUser().getId() + ":yes_trade:" + recipiOption.getAsUser().getId() + ":" + amountOption.getAsDouble(), "ya")
        ).setEphemeral(true).queue();
    }

    void closeAcc(SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("account action");
        eb.setDescription("are you *sure* you want to delete ur account?");
        eb.setColor(new Color(255, 0, 0));
        event.replyEmbeds(eb.build())
            .addActionRow(
                Button.secondary(event.getUser().getId() + ":delete", "nah"),
                Button.success(event.getUser().getId() + ":del_acc", "ya")
        ).setEphemeral(true).queue();
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
        event.reply(event.getUser().getId()).setEphemeral(true).queue();
    }

    void showAcc(SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("account overview");
        eb.setThumbnail(event.getUser().getAvatarUrl());
        eb.setColor(new Color(255, 210, 87));
        eb.addField("name", Teller.getAccount(event.getUser().getId()).get("username"), false);
        eb.addField("tomilliens", Teller.getAccount(event.getUser().getId()).get("tomilliens"), false);
		event.replyEmbeds(eb.build()).queue();
	}

    void msgToLog(MessageReceivedEvent event) {
        System.out.println(event.getMessage().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME)
            + " " + event.getAuthor().getEffectiveName() + ": " + event.getMessage().getContentDisplay());
    }
}