package xyz.n7mn.dev.data;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Profiler {
    private final JDA bot;
    private String categoryId = "837595084529598464";
    private String mapperRoleId = "810726799876423680";
    private String mapTextChannelId = "835577159133298768";

    public Profiler(JDA bot) {
        this.bot = bot;
    }

    public JDA getJDA() {
        return bot;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getRoleId() {
        return mapperRoleId;
    }

    public long getRoleIdLong() {
        return Long.parseLong(mapperRoleId);
    }

    public void setRoleId(String mapperRoleId) {
        this.mapperRoleId = mapperRoleId;
    }

    public String getMapTextChannelId() {
        return mapTextChannelId;
    }

    public void setMapTextChannelId(String textChannelId) {
        this.mapTextChannelId = textChannelId;
    }

    public TextChannel getTextChannel() {
        return bot.getTextChannelById(mapTextChannelId);
    }

    public Role getRole() {
        return bot.getRoleById(mapperRoleId);
    }

    public Category getCategory() {
        return bot.getCategoryById(categoryId);
    }

    public Profiler setInfo(String categoryId, String mapperRoleId, String mapTextChannelId) {
        this.categoryId = categoryId;
        this.mapperRoleId = mapperRoleId;
        this.mapTextChannelId = mapTextChannelId;

        return this;
    }

    public void registerSlashCommand() {
        getTextChannel().getGuild().updateCommands()
                .addCommands(Commands.slash("map", "マップを登録します"))
                .queue();
    }
}