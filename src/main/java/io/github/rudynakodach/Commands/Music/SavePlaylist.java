package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;

import static io.github.rudynakodach.Main.*;
public class SavePlaylist extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("saveplaylist")) {
            latestChan = event.getInteraction().getChannel().asTextChannel();
            StringBuilder encodedPlaylistBuilder = new StringBuilder();
            Collection<AudioTrack> playlist = trackScheduler.getQueue();

            if(playlist.size() == 0) {
                event.getInteraction().reply("Nie można zapisać pustej kolejki.").queue();
            }

            if(player.getPlayingTrack() != null) {
                encodedPlaylistBuilder.append(player.getPlayingTrack());
            }

            for (AudioTrack e : playlist) {
                encodedPlaylistBuilder.append(e.getIdentifier() + " | ");
            }
            encodedPlaylistBuilder.setLength(encodedPlaylistBuilder.length() - 3);
            String encodedPlaylist = Base64.getEncoder().encodeToString(encodedPlaylistBuilder.toString().getBytes());

            event.getInteraction().reply(encodedPlaylist).queue();
        }
    }
}
