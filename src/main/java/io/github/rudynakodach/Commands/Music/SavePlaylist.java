package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Base64;
import java.util.Collection;

import static io.github.rudynakodach.Main.*;
public class SavePlaylist extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("saveplaylist")) {
            latestChan = event.getInteraction().getChannel().asTextChannel();
            StringBuilder encodedPlaylistBuilder = new StringBuilder();
            Collection<AudioTrack> playlist = trackScheduler.getQueue(true);

            if(playlist.size() == 0) {
                event.getInteraction().reply("Nie można zapisać pustej kolejki.").queue();
                return;
            }

            for (AudioTrack e : playlist) {
                encodedPlaylistBuilder.append(e.getIdentifier()).append(" | ");
            }
            encodedPlaylistBuilder.setLength(encodedPlaylistBuilder.length() - 3);
            String encodedPlaylist = Base64.getEncoder().encodeToString(encodedPlaylistBuilder.toString().getBytes());

            event.getInteraction().reply(encodedPlaylistBuilder + " -> `" + encodedPlaylist + "`").queue();
        }
    }
}
