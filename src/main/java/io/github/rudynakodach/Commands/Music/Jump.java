package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

import static io.github.rudynakodach.Main.*;
public class Jump extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("jump")) {
            if(event.getMember().getVoiceState().getChannel() == null) {
                return;
            }
            int position = Objects.requireNonNull(event.getInteraction().getOption("position")).getAsInt();
            AudioTrack[] oldQueue = trackScheduler.getQueue().toArray(new AudioTrack[trackScheduler.getQueue().size()]);
            position = Math.max(1, Math.min(oldQueue.length, position));

            Collection<AudioTrack> newQueue = new ArrayList<>(List.of(Arrays.copyOfRange(oldQueue, position - 1, oldQueue.length)));

            trackScheduler.replaceQueue(newQueue, true);
            if(player.getPlayingTrack() != null) {
                AudioTrack e = player.getPlayingTrack();
                event.getInteraction().reply("Przeskoczono na `" + e.getInfo().title + "`").queue();
            }
        }
    }
}
