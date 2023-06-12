package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

import static io.github.rudynakodach.Main.*;
public class RemoveAt extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("rm")) {
            if(event.getMember().getVoiceState().getChannel() == null) {
                return;
            }
            latestChan = event.getInteraction().getChannel().asTextChannel();
            int pos = Objects.requireNonNull(event.getInteraction().getOption("pos")).getAsInt() - 1;
            List<AudioTrack> oldQ = trackScheduler.getQueue(false).stream().toList();

            if (pos < 0 || pos >= oldQ.size()) {
                event.getInteraction().reply("Nie znaleziono pozycji w kolejce o indeksie `" + pos + "`").queue();
                return;
            }
            List<AudioTrack> newQ = new ArrayList<>(oldQ);
            newQ.remove(pos);
            AudioTrack removed = oldQ.get(pos);

            if(trackScheduler.isQueueLooped) {
                pos = trackScheduler.queueToLoop.size() - (oldQ.size() + pos);
                Collection<AudioTrack> oldQueueToLoop = trackScheduler.queueToLoop;
                List<AudioTrack> newQueueToLoop = new ArrayList<>(oldQueueToLoop);
                newQueueToLoop.remove(pos);
                trackScheduler.queueToLoop = newQueueToLoop;
            }

            event.getInteraction().reply("Usunięto `" + removed.getInfo().title + "`").queue();
            trackScheduler.replaceQueue(newQ, false);
        }
    }
}
