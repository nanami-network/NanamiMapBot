package xyz.n7mn.dev;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BotListener extends ListenerAdapter {

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("map")) {
            return;
        }

        event.deferReply(true).queue();

        if (event.getMember().getRoles().stream().noneMatch(v -> v.getIdLong() == BotMain.getInstance().getProfiler().getRoleIdLong())) {
            event.getHook().sendMessage("このコマンドはマッパーロールを持っていないと実行できませんよ！").queue();
        } else {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("どちらで作成しましたか？")
                    .setColor(Color.GREEN)
                    .setDescription("下のボタンの**どちらか**を選んでください")
                    .addField("該当しませんか？", "該当しない場合は 報告/質問 をしてください！", false)
                    .addField("メンテナンス", "現在 `それ以外の場合` は使用できません！申し訳ありません", false)
                    .setTimestamp(new Date().toInstant())
                    .setFooter(format.format(new Date()))
                    .build();

            event.getHook().sendMessageEmbeds(embed)
                    .addActionRow(Button.of(ButtonStyle.SUCCESS, "multiplayer", "マップサーバーで作成した"), Button.of(ButtonStyle.SECONDARY, "legacy", "それ以外の場合").asDisabled())
                    .queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId().equals("multiplayer")) {
            Modal.Builder modal = Modal.create("multiplayer", "マップを作成したことを報告する！").addActionRows(
                    ActionRow.of(TextInput.create("map", "ワールド名", TextInputStyle.SHORT).setMaxLength(60).setRequired(true).build()),
                    ActionRow.of(TextInput.create("startingPos", "スタート位置", TextInputStyle.SHORT).setRequired(true).build()),
                    ActionRow.of(TextInput.create("description", "説明", TextInputStyle.PARAGRAPH).setMaxLength(1024).setRequired(false).build()));

            event.replyModal(modal.build()).queue();
        } else if (event.getButton().getId().equals("legacy")) {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("World Uploaderの操作手順")
                    .setDescription(
                            "1. ワールドを作ったPCで下のURLをクリックして開きます。\n" +
                            "https://map.n7mn.xyz/public/\n" +
                            "2. TCTマップの場合は「TCT」を 雪合戦マップの場合は「雪合戦」を選択してください。\n" +
                            "3. Discordの名前には「Discordのユーザー名」を入れてください。(例:`茅野ななみ#2669`)\n" +
                            "4. Minecraft IDには「MinecraftのID」を入れてください。 (例：`7mi_chan`)\n" +
                            "5. 補足/説明には「スタート位置の座標」などを入れてください。(なければ何も書かなくていいです。)\n" +
                            "6. フォルダは以下のように選択してアップロードボタンを押してください。\n" +
                            "https://map.n7mn.xyz/map.png")
                    .setColor(Color.GREEN).build();
            event.editMessageEmbeds(embed)
                    .setActionRow(event.getMessage().getButtonById("multiplayer").asDisabled(), event.getMessage().getButtonById("legacy").asDisabled(), Button.of(ButtonStyle.LINK, "https://map.n7mn.xyz/public/", "URLを開く"))
                    .queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals("multiplayer")) {
            event.deferEdit().queue();

            event.getHook().editOriginalEmbeds(event.getMessage().getEmbeds())
                    .setActionRow(event.getMessage().getButtonById("multiplayer").asDisabled(), event.getMessage().getButtonById("legacy").asDisabled())
                    .queue();

            String description = event.getValue("description").getAsString();

            BotMain.getInstance().getProfiler().getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setTitle("完成報告")
                            .addField("ワールド名", event.getValue("map").getAsString(), false)
                            .addField("スタート位置", event.getValue("startingPos").getAsString(), false)
                            .addField("説明", description.isBlank() ? "N/A" : description, false)
                            .addField("Discord", event.getMember().getAsMention() + "(" + event.getMember().getIdLong() + ")", false)
                            .setColor(Color.GREEN)
                            .setTimestamp(new Date().toInstant())
                            .setFooter(format.format(new Date()))
                            .build())
                    .queue();

            event.getHook().sendMessage("マップの完成を報告しました！ありがとうございました！")
                    .setEphemeral(true).queue();
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        BotMain.getInstance().getProfiler().registerSlashCommand();
    }
}