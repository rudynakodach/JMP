package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

import static io.github.rudynakodach.Main.*;

public class LoadPlaylist extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("loadplaylist")) {
            String encodedPlaylist = Objects.requireNonNull(event.getInteraction().getOption("data")).getAsString();
            byte[] decodedBytes;
            try {
                 decodedBytes = Base64.getDecoder().decode(encodedPlaylist);
            } catch (IllegalFormatException e) {
                event.getInteraction().reply("Dane niepoprawnie sformatowane!").queue();
                return;
            }
            encodedPlaylist = new String(decodedBytes);
            String[] playlistSplit = encodedPlaylist.split(" \\| ");
            for (String s : playlistSplit) {
                playerManager.loadItem(s.trim(), new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        trackScheduler.queue(track);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {}
                    @Override
                    public void noMatches() {}
                    @Override
                    public void loadFailed(FriendlyException exception) {}
                });
            }
            event.getInteraction().reply("Dodano `" + playlistSplit.length + "` elementów.").queue();
        }
    }
}
