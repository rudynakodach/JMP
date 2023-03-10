package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.github.rudynakodach.Main.*;
public class LoopQueue extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("loopqueue")) {
            if (!trackScheduler.isQueueLooped) {
                Collection<AudioTrack> queueToLoop = new java.util.ArrayList<>(List.of(player.getPlayingTrack().makeClone()));
                queueToLoop.addAll(trackScheduler.getQueue());
                trackScheduler.toggleQueueLoop(queueToLoop);
                event.getInteraction().reply("Zapętlono `" + queueToLoop.size() + "` elementów.").queue();
            } else {
                event.getInteraction().reply("Usunięto pętle kolejki `[" + trackScheduler.queueToLoop.size() + " elem.]`").queue();
                trackScheduler.toggleQueueLoop(new ArrayList<>());
            }
        }
    }
}
