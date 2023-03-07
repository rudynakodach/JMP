package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.rudynakodach.AudioPlayerSendHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

import static io.github.rudynakodach.Main.*;

public class Play extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("play")) {
            latestChan = event.getInteraction().getChannel().asTextChannel();
            audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
            audioManager.openAudioConnection(event.getMember().getVoiceState().getChannel().asVoiceChannel());

            if(!isAudioHandlerSet) {
                event.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                isAudioHandlerSet = !isAudioHandlerSet;
            }

            String target = Objects.requireNonNull(event.getOption("url")).getAsString();

            playerManager.loadItem(target, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    event.getInteraction().reply("Załadowany bicior: " + track.getInfo().title).queue();
                    trackScheduler.queue(track);
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
                    event.getInteraction().reply("Nie znaleziono utworu.").queue();
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    event.getInteraction().reply("Coś sie wylało: \n```" + exception.getMessage() + "\n```").queue();
                }
            });
        }
    }
}
