package io.github.rudynakodach.Commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.rudynakodach.Main;
import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import io.github.rudynakodach.Utils.QueuePosition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.Objects;

public class Play extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("play")) {
            return;
        } else if(event.getGuild() == null || event.getMember() == null) {
            return;
        }
        String query = Objects.requireNonNull(event.getOption("url")).getAsString();

        Main.AUDIO_PLAYER_MANAGER.loadItem(query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                TrackScheduler.get(event.getGuild()).addToQueue(List.of(new QueuePosition(event.getMember(), track)));
                event.reply("Załadowano `%s`".formatted(track.getInfo().title)).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                TrackScheduler scheduler = TrackScheduler.get(event.getGuild());
                List<QueuePosition> tracks = playlist.getTracks().stream()
                        .map(e -> new QueuePosition(event.getMember(), e))
                        .toList();

                scheduler.addToQueue(tracks);

                event.reply("Załadowano `%s` element/ów.".formatted(tracks.size())).queue();
            }

            @Override
            public void noMatches() {
                event.getInteraction().reply("Nie znaleziono utworu.").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.reply("Nie udało się załadować utworu: ```\n%s\n```".formatted(exception.getMessage())).queue();
            }
        });
    }
}
