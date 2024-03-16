package io.github.rudynakodach;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import io.github.rudynakodach.Commands.*;
import io.github.rudynakodach.Utils.Embeds.NowPlayingEmbed;
import io.github.rudynakodach.Utils.Embeds.QueueEmbed;
import io.github.rudynakodach.Utils.Embeds.SearchEmbed;
import io.github.rudynakodach.Utils.Events.OnGuildJoin;
import io.github.rudynakodach.Utils.Events.OnGuildLeave;
import io.github.rudynakodach.Utils.Events.OnStart;
import io.github.rudynakodach.Utils.Events.SlashCommandInteraction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
    public static JDA CLIENT;
    public static AudioPlayerManager AUDIO_PLAYER_MANAGER = new DefaultAudioPlayerManager();
    public static void main(String[] args) {
        AudioSourceManagers.registerRemoteSources(AUDIO_PLAYER_MANAGER);
        AudioSourceManagers.registerLocalSource(AUDIO_PLAYER_MANAGER);

        CLIENT = JDABuilder.createDefault(args[0])
                .addEventListeners(new OnStart(), new OnGuildJoin(), new OnGuildLeave(), new SlashCommandInteraction())
                .enableCache(CacheFlag.VOICE_STATE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        CLIENT.updateCommands().addCommands(

                // music commands
                Commands.slash("join","dołącza na kanał"),

                Commands.slash("leave", "opuszcza kanal"),

                Commands.slash("loadplaylist", "załadowuje kolejke")
                        .addOption(OptionType.STRING, "data", ".", true),

                Commands.slash("play", ".")
                        .addOption(OptionType.STRING, "url", "adres URL do filmu", true),

                Commands.slash("pause", "zatrzymuje albo wlacza muzyke lol"),

                Commands.slash("loop", "zapętla utwór"),

                Commands.slash("skip", "pomija utwor"),

                Commands.slash("np", "wyswietla grany utwor"),

                Commands.slash("queue", "wyswietla kolejke")
                        .addOption(OptionType.INTEGER, "page", "strona kolejki", false),

                Commands.slash("stop", "wylacza muzyke i usuwa kolejke"),

                Commands.slash("search", "wyszukuje utworów na youtubie")
                        .addOption(OptionType.STRING, "query", "co wyszukac", true)
                        .addOption(OptionType.INTEGER, "page", "strona wyszukiwania", false),

                Commands.slash("jump", "przeskakuje do miejsca w kolejce uwuajac poprzednie rzeczy")
                        .addOption(OptionType.INTEGER, "idx","pozycja na ktora przeskoczyc",true),

                Commands.slash("sp", "sp")
                        .addOption(OptionType.STRING, "query", "co wyszukać", true),

                Commands.slash("rm", "usuwa z kolejki")
                        .addOption(OptionType.INTEGER, "pos", "pozycja do usuniecia z kolejki", true),

                Commands.slash("saveplaylist", "zapisuje playliste jako fragment tekstu"),

                Commands.slash("copy", "kopiuje utwor na koniec kolejki")
                        .addOption(OptionType.INTEGER, "pos", "postion", true),

                Commands.slash("seek", "przeskakuje na dany czas w sekundach w granym utworze")
                        .addOption(OptionType.INTEGER, "t", "czas w sekundach na jaki przejsc", true),

                Commands.slash("shuffle", "losowo zmienia liste"),

                Commands.slash("volume", "zmienia głośnośc")
                        .addOption(OptionType.INTEGER, "volume", "nowa głośnośc w zakresie 0-1000", true),

                Commands.slash("speak", "jeśli na scenie, zaczyna być mówiącym"),

                Commands.slash("loopqueue", "zapętla kolejke. kolejka zostanie dodana na nowo gdy nie ma żandych utworów w kolejce")
        ).queue();

        CLIENT.addEventListener(
                new Copy(),
                new Join(),
                new Jump(),
                new Leave(),
                new Loop(),
                new Play(),
                new Pause(),
                new QueueLoop(),
                new Stop(),
                new Search(),
                new SearchEmbed(),
                new SearchPlay(),
                new Queue(),
                new QueueEmbed(),
                new Jump(),
                new Seek(),
                new Shuffle(),
                new RemoveAt(),
                new Skip(),
                new NowPlaying(),
                new NowPlayingEmbed(),
                new Speak(),
                new Volume()
        );
    }
}