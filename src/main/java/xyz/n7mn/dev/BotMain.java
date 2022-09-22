package xyz.n7mn.dev;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import xyz.n7mn.dev.data.Profiler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class BotMain {

    private static BotMain main;

    private Profiler profiler;


    public static void main(String[] args) throws Exception {
        main = new BotMain().init();
    }

    public BotMain init() throws Exception {
        File file = new File("./token.dat");

        if (!file.exists() && !file.createNewFile()) {
            throw new IllegalStateException("Didn't create File (" + file.getPath() + ")");
        }

        String token = String.join("", Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));

        profiler = new Profiler(JDABuilder.createLight(token, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS)
                .addEventListeners(new BotListener())
                .enableCache(CacheFlag.VOICE_STATE)
                .enableCache(CacheFlag.EMOJI)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build());

        return this;
    }

    public static BotMain getInstance() {
        return main;
    }

    public Profiler getProfiler() {
        return profiler;
    }
}