package xyz.n7mn.dev;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class BotListener extends ListenerAdapter {

    private String categoryId = "837595084529598464";
    private String mapperRoleID = "810726799876423680";
    private String mapTextChannelId = "835577159133298768";

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getGuild().getId().equals("810724825098092547")){
            categoryId = "837578478186266654";
            mapperRoleID = "837602972451078145";
            mapTextChannelId = "837596567871029248";
        }


        if (event.isWebhookMessage() || event.getAuthor().isBot()){
            return;
        }

        if (event.isFromType(ChannelType.PRIVATE)){
            return;
        }

        if (event.getMessage().getContentRaw().toLowerCase().startsWith("map:")){
            MultiMap(event.getMessage());
            return;
        }

        if (!event.getMessage().getContentRaw().toLowerCase().equals("!map")){
            return;
        }

        Thread thread = new Thread(() -> {

            Message message = event.getMessage();

            boolean found = false;
            List<Role> roles = message.getMember().getRoles();
            for (Role role : roles){
                if (role.getId().equals(mapperRoleID)){
                    found = true;
                    break;
                }
            }
            if (!found){
                message.reply("Mapper権限がないようです。").queue();
                return;
            }

            EmbedBuilder menu = new EmbedBuilder();
            menu.setColor(Color.GREEN);
            menu.setTitle("マップ報告メニュー");
            menu.setDescription("" +
                    "リアクションを下の通りに押してください。\n" +
                    "※ マップのアップデート報告も完成報告と同じ手順でお願いします。\n" +
                    ":one: シングルワールドの場合\n" +
                    ":two: マップ鯖の場合"
            );

            try {

                String mapName = "map"+ event.getAuthor().getId();
                message.getGuild().createRole().setName(mapName).queue(role -> {
                    message.getGuild().addRoleToMember(message.getAuthor().getId(), role).queue();

                    menu.setFooter(mapName);
                    event.getGuild().createTextChannel(mapName, event.getGuild().getCategoryById(categoryId)).syncPermissionOverrides().addRolePermissionOverride(role.getIdLong(), 68672, 0).queue((channel->{
                        channel.sendMessage(message.getAuthor().getAsMention()).embed(menu.build()).queue((message1 -> {
                            message1.addReaction("1\uFE0F\u20E3").queue();
                            message1.addReaction("2\uFE0F\u20E3").queue();

                            message.reply(channel.getAsMention() + " に進んで指示に従ってください。\n(上のチャンネルが見えなくなったら報告完了です。)").queue();
                        }));

                    }));
                });

            } catch (Exception e){
                message.reply("なにかエラーです。").queue();
                e.printStackTrace();
            }

        });
        thread.start();
    }

    @Override
    public void onGenericMessageReaction(@NotNull GenericMessageReactionEvent event) {

        MessageChannel channel = event.getReaction().getChannel();
        String messageId = event.getReaction().getMessageId();

        if (!event.getReaction().getReactionEmote().isEmoji()){
            event.getReaction().removeReaction().queue();
            return;
        }

        String emoji = event.getReaction().getReactionEmote().getEmoji();

        if (event.getUser().isBot()){
            return;
        }

        channel.retrieveMessageById(messageId).queue(message -> {

            if (message.getEmbeds().size() == 0){
                return;
            }

            MessageEmbed embed = message.getEmbeds().get(0);

            if (embed == null){
                return;
            }

            String title = embed.getTitle();

            EmbedBuilder menu = new EmbedBuilder();
            menu.setColor(Color.GREEN);
            menu.setFooter(embed.getFooter().getText());

            if (title != null && title.equals("マップ報告メニュー")){
                message.clearReactions().queue();

                if (emoji.equals("1\uFE0F\u20E3")){
                    menu.setTitle("シングルワールドメニュー");
                    menu.setDescription("" +
                            "1\uFE0F\u20E3 : World Uploaderを利用する (推奨)\n" +
                            "2\uFE0F\u20E3 : Java版WorldUploaderを利用する (上でエラーが出る場合のみ利用してください。)"
                    );

                    message.editMessage(menu.build()).queue((message1 -> {
                        message1.addReaction("1\uFE0F\u20E3").queue();
                        message1.addReaction("2\uFE0F\u20E3").queue();
                    }));

                    return;
                }

                menu.setTitle("マップ鯖メニュー");
                menu.setDescription("" +
                        "以下のテンプレを使って入力してください。\n" +
                        "```\n" +
                        "map:"+message.getTextChannel().getId()+" (左の部分はこのままにしてください)\n" +
                        "ワールド名：\n" +
                        "スタート位置：\n" +
                        "説明(希望日など 1行で簡潔に)：" +
                        "```"
                );

                message.editMessage(menu.build()).queue((message1 -> {
                    message1.addReaction("1\uFE0F\u20E3").queue();
                    message1.addReaction("2\uFE0F\u20E3").queue();
                }));

                return;
            }

            if (title != null && title.equals("シングルワールドメニュー")) {
                message.clearReactions().queue();

                if (emoji.equals("1\uFE0F\u20E3")) {

                    menu.setTitle("World Uploaderの操作手順");
                    menu.setDescription("" +
                            "1. ワールドを作ったPCで下のURLをクリックして開きます。\n" +
                            "https://map.n7mn.xyz/public/\n" +
                            "2. TCTマップの場合は「TCT」を 雪合戦マップの場合は「雪合戦」を選択してください。\n" +
                            "3. Discordの名前には「Discordのユーザー名」を入れてください。(例:`茅野ななみ#2669`)\n" +
                            "4. Minecraft IDには「MinecraftのID」を入れてください。 (例：`7mi_chan`)\n" +
                            "5. 補足/説明には「スタート位置の座標」などを入れてください。(なければ何も書かなくていいです。)\n" +
                            "6. フォルダは以下のように選択してアップロードボタンを押してください。\n" +
                            "https://map.n7mn.xyz/map.png\n" +
                            "```\n" +
                            "場所がわからない場合 (Windows)：\n" +
                            "1.Minecraftの「リソースパック」を開く\n" +
                            "2.左下の「フォルダーを開く」をクリック\n" +
                            "3.開いたフォルダの「↑」をクリック\n" +
                            "4.savesフォルダを右クリック\n" +
                            "5.プロパティをクリック\n" +
                            "6.場所に書かれた「C:￥～」の部分をコピーする\n" +
                            "7.フォルダーのところにコピーしたものを貼り付けてEnter\n" +
                            "8.saveフォルダをダブルクリック\n" +
                            "  その後は上の通りにワールド名と同じフォルダをダブルクリックしてアップロード" +
                            "```\n" +
                            "7. 完了したら下にある :ok: を 失敗したら 下にある :ng: を押してください。"
                    );

                    message.editMessage(menu.build()).queue((message1 -> {
                        message.addReaction("\uD83C\uDD97").queue();
                        message.addReaction("\uD83C\uDD96").queue();
                    }));

                    return;
                }

                menu.setTitle("Java版WorldUploaderの操作手順");
                menu.setDescription("" +
                        "1. https://map.n7mn.xyz/WorldUploader.zip からDLしててきとーに解凍してください。\n" +
                        "2. startと書かれたファイルをダブルクリックして起動してください。\n" +
                        "3. 指示に従ってください。完了メッセージが出たら 下にある :ok: を エラーと出たら 下にある :ng: を押してください。"
                );

                message.editMessage(menu.build()).queue((message1 -> {
                    message.addReaction("\uD83C\uDD97").queue();
                    message.addReaction("\uD83C\uDD96").queue();
                }));

                return;
            }

            if (title != null && title.equals("World Uploaderの操作手順")) {
                String name = embed.getFooter().getText();
                if (emoji.equals("\uD83C\uDD97")){
                    List<Role> roleList = event.getGuild().getRolesByName(name, true);

                    roleList.get(0).delete().queue();
                    event.getGuild().getTextChannelsByName(name, true).get(0).delete().queue();

                    return;
                }

                message.clearReactions().queue();
                menu.setTitle("Java版WorldUploaderの操作手順");
                menu.setDescription("" +
                        "1. https://map.n7mn.xyz/WorldUploader.zip からDLしててきとーに解凍してください。\n" +
                        "2. startと書かれたファイルをダブルクリックして起動してください。\n" +
                        "3. 指示に従ってください。完了メッセージが出たら 下にある :ok: を エラーと出たら 下にある :ng: を押してください。"
                );

                message.editMessage(menu.build()).queue((message1 -> {
                    message.addReaction("\uD83C\uDD97").queue();
                    message.addReaction("\uD83C\uDD96").queue();
                }));

            }

            if (title != null && title.equals("Java版WorldUploaderの操作手順")) {

                String name = embed.getFooter().getText();
                if (emoji.equals("\uD83C\uDD97")){
                    List<Role> roleList = event.getGuild().getRolesByName(name, true);

                    roleList.get(0).delete().queue();
                    event.getGuild().getTextChannelsByName(name, true).get(0).delete().queue();

                    return;
                }

                List<TextChannel> list = event.getGuild().getTextChannelsByName("mapperのつどい", true);

                menu.setTitle("エラー");
                menu.setDescription("" +
                        "以下の場所でエラーが出たことを下のテンプレートをコピペして報告お願いします。\n" +
                        list.get(0).getAsMention() + "\n" +
                        "エラー報告用テンプレート:\n" +
                        "```\n" +
                        "報告botエラー報告\n" +
                        "使用チャンネル " + name +"\n" +
                        "```"
                );
                menu.setColor(Color.RED);
                menu.setFooter("");
                message.clearReactions().queue();
                message.editMessage(menu.build()).queue();
            }



        });
    }


    private void MultiMap(Message message){
        String text = message.getContentRaw();
        TextChannel channel = message.getTextChannel();

        String[] textList = text.split("\n");

        String[] split = textList[0].split(":");
        if (split.length != 2){
            return;
        }

        String[] split1 = split[1].split(" ");
        if (!channel.getId().equals(split1[0])){
            message.delete().queue();
        }

        String[] world = textList[1].split("：");
        String[] start = textList[2].split("：");
        String[] d = textList[3].split("：");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        EmbedBuilder result = new EmbedBuilder();
        result.setTitle("完成報告");
        result.setColor(Color.GREEN);
        result.addField("ワールド名",world[1], false);
        result.addField("スタート位置",start[1], false);
        result.addField("説明",d[1], false);
        result.setFooter(format.format(new Date()));

        TextChannel textChannel = channel.getGuild().getTextChannelById(mapTextChannelId);
        textChannel.sendMessage(result.build()).queue();

        channel.delete().queue();

        List<Role> roleList = message.getGuild().getRolesByName(channel.getName(), true);
        roleList.get(0).delete().queue();


    }
}
