package io.github.rudynakodach;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import io.github.rudynakodach.Commands.Misc.*;
import io.github.rudynakodach.Commands.Music.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.HashMap;

public class Main {
    public static AudioManager audioManager;
    public static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    public static final AudioPlayer player = playerManager.createPlayer();
    public static final TrackScheduler trackScheduler = new TrackScheduler(player);
    public static TextChannel latestChan;

    public static HashMap<String, Boolean> audioHandlerSetMap = new HashMap<>();
    public static JDA client;
    public static void main(String[] args) {
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        player.addListener(trackScheduler);

        client = JDABuilder.createDefault(args[0])
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(new ButtonInteractionHandler())
                .build();

        for (Guild g : client.getGuilds()) {
            String guildId = g.getId();
            System.out.println("Added guild " + guildId + ".");
            audioHandlerSetMap.put(guildId, false);
        }

        client.updateCommands().addCommands(

                // music commands
                Commands.slash("join","dolacza na kanal"),

                Commands.slash("leave", "opuszcza kanal"),

                Commands.slash("loadplaylist", "załadowywuje kolejke")
                                .addOption(OptionType.STRING, "data", ".", true),

                Commands.slash("play", ".")
                        .addOption(OptionType.STRING, "url", "adres URL do filmu", true),

                Commands.slash("pause", "zatrzymuje albo wlacza muzyke lol"),

                Commands.slash("loop", "zapetla "),

                Commands.slash("skip", "pomija utwor"),

                Commands.slash("np", "wyswietla grany utwor"),

                Commands.slash("queue", "wyswietla kolejke")
                        .addOption(OptionType.INTEGER, "ilosc", "ilosc elementow widocznych na raz", false),

                Commands.slash("stop", "wylacza muzyke i usuwa kolejke"),

                Commands.slash("search", "wyszukuje utworow na youtubie")
                        .addOption(OptionType.STRING, "query", "co wyszukac", true)
                        .addOption(OptionType.INTEGER, "page", "strona wyszukiwania", false),

                Commands.slash("jump", "przeskakuje do miejsca w kolejce uwuajac poprzednie rzeczy")
                        .addOption(OptionType.INTEGER, "position","pozycja na ktora przeskoczyc",true),

                Commands.slash("sp", "sp")
                        .addOption(OptionType.STRING, "query", "co wyszukac", true),

                Commands.slash("rm", "usuwa z kolejki")
                        .addOption(OptionType.INTEGER, "pos", "pozycja do usuniecia z kolejki", true),

                Commands.slash("saveplaylist", "zapisuje playliste jako tekst"),

                Commands.slash("copy", "kopiuje utwor na koniec kolejki")
                        .addOption(OptionType.INTEGER, "pos", "postion", true),

                Commands.slash("seek", "przeskakuje na dany czas w sekundach w granym utworze")
                        .addOption(OptionType.INTEGER, "t", "czas w sekundach na jaki przejsc", true),

                Commands.slash("shuffle", "losowo zmienia liste"),

                Commands.slash("volume", "zmienia glosnosc")
                        .addOption(OptionType.INTEGER, "volume", "nowa glosnosc w zakresie 0-1000", true),

                Commands.slash("speak", "."),

                Commands.slash("loopqueue", "zapętla kolejke. kolejka zostanie dodana na nowo gdy nie ma żandych utworów w kolejce"),

                // misc commands
                Commands.slash("bugreport", "report a bug")

        ).queue();

        System.out.println("Registering command handlers...");

        //guild join handler
        GuildJoinHandler guildJoinHandler = new GuildJoinHandler();
        Credits creditsHandler = new Credits();

        //music command handlers
        Copy copyHandler = new Copy();
        Join joinHandler = new Join();
        Jump jumpHandler = new Jump();
        Leave leaveHandler = new Leave();
        LoadPlaylist loadPlaylistHandler = new LoadPlaylist();
        Loop loopHandler = new Loop();
        LoopQueue lqHandler = new LoopQueue();
        NowPlaying npHandler = new NowPlaying();
        Pause pauseHandler = new Pause();
        Play playHandler = new Play();
        Queue queueHandler = new Queue();
        RemoveAt rmHandler = new RemoveAt();
        SavePlaylist savePlaylistHandler = new SavePlaylist();
        Search searchHandler = new Search();
        SearchPlay spHandler = new SearchPlay();
        Seek seekHandler = new Seek();
        Shuffle shuffleHandler = new Shuffle();
        Skip skipHandler = new Skip();
        Speak speakHandler = new Speak();
        Stop stopHandler = new Stop();
        Volume volumeHandler = new Volume();

        //miscellaneous command handlers
        BugReport bugReportHandler = new BugReport();

        client.addEventListener(
                guildJoinHandler,
                creditsHandler,

                //music handlers
                copyHandler,
                joinHandler,
                jumpHandler,
                leaveHandler,
                loadPlaylistHandler,
                loopHandler,
                lqHandler,
                npHandler,
                pauseHandler,
                playHandler,
                queueHandler,
                rmHandler,
                savePlaylistHandler,
                searchHandler,
                spHandler,
                seekHandler,
                shuffleHandler,
                skipHandler,
                speakHandler,
                stopHandler,
                volumeHandler,

                // misc handlers
                bugReportHandler
        );

        System.out.println("Command handlers registered!");
    }
}