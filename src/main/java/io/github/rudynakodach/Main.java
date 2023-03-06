package io.github.rudynakodach;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import io.github.rudynakodach.Commands.Music.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
    public static AudioManager audioManager;
    public static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    public static AudioPlayer player = playerManager.createPlayer();
    public static TrackScheduler trackScheduler = new TrackScheduler(player);
    public static TextChannel latestChan;
    public static boolean isAudioHandlerSet = false;
    static JDA client;
    public static void main(String[] args) {
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        player.addListener(trackScheduler);

        client = JDABuilder.createDefault(args[0])
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(new ButtonInteractionHandler())
                .build();

        client.updateCommands().addCommands(
                Commands.slash("join","no"),

                Commands.slash("leave", "e"),

                Commands.slash("play", "e")
                        .addOption(OptionType.STRING, "url", "jasiu podaj biciora", true),

                Commands.slash("pause", "zatrzymuje albo wlacza muzyke lol"),

                Commands.slash("loop", "jesli nie wiesz co to robi to jestes gupi lolololol"),

                Commands.slash("skip", "sracz"),

                Commands.slash("np", "kupsko"),

                Commands.slash("queue", "tomasz to furas")
                        .addOption(OptionType.INTEGER, "ilosc", "ilosc elementow widocznych na raz", false),

                Commands.slash("stop", "wylacza muzyke i usuwa kolejke xdxdxdxdx bob"),

                Commands.slash("search", "quandale sus the dingle")
                        .addOption(OptionType.STRING, "query", "ok", true)
                        .addOption(OptionType.INTEGER, "page", "strona wyszukiwania", false),

                Commands.slash("jump", "przeskakuje do miejsca w kolejce uwuajac poprzednie rzeczy")
                        .addOption(OptionType.INTEGER, "position","kupa",true),

                Commands.slash("sp", "sp")
                        .addOption(OptionType.STRING, "query", "co wyszukac", true),

                Commands.slash("rm", "usuwa z kolejki")
                        .addOption(OptionType.INTEGER, "pos", "position", true)
        ).queue();

        System.out.println("Registering command handlers...");

        Join joinHandler = new Join();
        Jump jumpHandler = new Jump();
        Leave leaveHandler = new Leave();
        Loop loopHandler = new Loop();
        NowPlaying npHandler = new NowPlaying();
        Pause pauseHandler = new Pause();
        Play playHandler = new Play();
        Queue queueHandler = new Queue();
        RemoveAt rmHandler = new RemoveAt();
        Search searchHandler = new Search();
        SearchPlay spHandler = new SearchPlay();
        Skip skipHandler = new Skip();
        Stop stopHandler = new Stop();
        client.addEventListener(joinHandler, jumpHandler, leaveHandler, loopHandler, npHandler, pauseHandler, playHandler, queueHandler, rmHandler, searchHandler, spHandler, skipHandler, stopHandler);

    }
}