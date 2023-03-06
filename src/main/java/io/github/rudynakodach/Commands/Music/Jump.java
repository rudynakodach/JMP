package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import static io.github.rudynakodach.Main.*;
public class Jump extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("jump")) {
            int position = Objects.requireNonNull(event.getInteraction().getOption("position")).getAsInt();
            AudioTrack[] oldQueue = trackScheduler.getQueue();
            Collection<AudioTrack> newQueue = List.of(Arrays.copyOfRange(oldQueue, position, oldQueue.length));
            trackScheduler.replaceQueue(newQueue, true);
        }
    }
}
