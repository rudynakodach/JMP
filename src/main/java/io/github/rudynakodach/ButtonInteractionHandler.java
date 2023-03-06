package io.github.rudynakodach;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static io.github.rudynakodach.Main.*;
public class ButtonInteractionHandler extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().startsWith("YTPLAY: ")) {
            String trackId = event.getComponentId().split(":")[1].trim();
            playerManager.loadItem(trackId, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    event.getInteraction().reply("Załadowany bicior: " + track.getInfo().title).queue();
                    trackScheduler.queue(track);
                    if(player.getPlayingTrack() == null) {
                        trackScheduler.nextTrack();
                    }
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                        trackScheduler.queue(track);
                    }
                    event.getInteraction().reply("Dodano " + playlist.getTracks().size() + " elementów do kolejki.").queue();
                }

                @Override
                public void noMatches() {
                    event.getInteraction().reply("Moje murzyńskie moce nie znalazły takiego filmu").queue();
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    event.getInteraction().reply("Coś sie wylało: \n```" + exception.getMessage() + "\n```").queue();
                }
            });
        }
    }
}
