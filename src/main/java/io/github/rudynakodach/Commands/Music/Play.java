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
            if(event.getMember().getVoiceState().getChannel() == null) {
                return;
            }
            latestChan = event.getInteraction().getChannel().asTextChannel();
            audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
            audioManager.openAudioConnection(event.getMember().getVoiceState().getChannel().asVoiceChannel());

            if(!audioHandlerSetMap.containsKey(event.getGuild().getId())) {
                Objects.requireNonNull(event.getGuild()).getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                audioHandlerSetMap.put(Objects.requireNonNull(event.getGuild()).getId(), true);
            } else if(!audioHandlerSetMap.get(Objects.requireNonNull(event.getGuild()).getId())) {
                Objects.requireNonNull(event.getGuild()).getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                audioHandlerSetMap.put(Objects.requireNonNull(event.getGuild()).getId(), true);
            }

            String target = Objects.requireNonNull(event.getOption("url")).getAsString();

            playerManager.loadItem(target, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    event.getInteraction().reply("Za??adowany bicior: " + track.getInfo().title).queue();
                    trackScheduler.queue(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                        trackScheduler.queue(track);
                    }
                    event.getInteraction().reply("Dodano " + playlist.getTracks().size() + " element??w do kolejki.").queue();
                }

                @Override
                public void noMatches() {
                    event.getInteraction().reply("Nie znaleziono utworu.").queue();
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    event.getInteraction().reply("Co?? sie wyla??o: \n```" + exception.getMessage() + "\n```").queue();
                }
            });
        }
    }
}
