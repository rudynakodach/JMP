package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.rudynakodach.AudioPlayerSendHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

import static io.github.rudynakodach.Main.*;
public class SearchPlay extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("sp")) {
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

            playerManager.loadItem("ytsearch: " + event.getInteraction().getOption("query").getAsString(), new FunctionalResultHandler(null, playlist -> {
                AudioTrack e = playlist.getTracks().get(0);
                trackScheduler.queue(e);
                event.getInteraction().reply("DJ załadował biciora: `" + e.getInfo().title + "`").queue();
                if(player.getPlayingTrack() == null) {
                    trackScheduler.nextTrack(true);
                }
                if(trackScheduler.isQueueLooped) {
                    trackScheduler.queueToLoop.add(e);
                }
            }, null, null));
        }
    }
}