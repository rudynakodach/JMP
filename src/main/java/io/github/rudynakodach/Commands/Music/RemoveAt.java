package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.Objects;

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
            AudioTrack[] oldQ = trackScheduler.getQueue();
            AudioTrack[] newQ = new AudioTrack[oldQ.length - 1];
            AudioTrack removed = trackScheduler.getQueue()[pos];
            if (pos == 0) {
                System.arraycopy(oldQ, 1, newQ, 0, oldQ.length - 1);
            } else if (pos == oldQ.length - 1) {
                System.arraycopy(oldQ, 0, newQ, 0, oldQ.length - 1);
            } else if (pos < oldQ.length - 1) {
                System.arraycopy(oldQ, 0, newQ, 0, pos);
                System.arraycopy(oldQ, pos + 1, newQ, pos, oldQ.length - pos - 1);
            } else {
                event.getInteraction().reply("Nie znaleziono pozycji w kolejce o indeksie `" + pos + "`").queue();
                return;
            }

            if(trackScheduler.isQueueLooped) {
                pos = trackScheduler.queueToLoop.size() - (oldQ.length + pos);
                AudioTrack[] oldQueueToLoop = trackScheduler.queueToLoop.toArray(new AudioTrack[trackScheduler.queueToLoop.size() - 1]);
                AudioTrack[] newQueueToLoop = new AudioTrack[oldQueueToLoop.length - 1];
                if (pos == 0) {
                    System.arraycopy(oldQueueToLoop, 1, newQueueToLoop, 0, oldQueueToLoop.length - 1);
                } else if (pos == oldQueueToLoop.length - 1) {
                    System.arraycopy(oldQueueToLoop, 0, newQueueToLoop, 0, oldQueueToLoop.length - 1);
                } else if (pos < oldQueueToLoop.length - 1) {
                    System.arraycopy(oldQueueToLoop, 0, newQueueToLoop, 0, pos);
                    System.arraycopy(oldQueueToLoop, pos + 1, newQueueToLoop, pos, oldQueueToLoop.length - pos - 1);
                }
                trackScheduler.queueToLoop = List.of(newQueueToLoop);
            }

            event.getInteraction().reply("Usunięto `" + removed.getInfo().title + "`").queue();
            trackScheduler.replaceQueue(List.of(newQ), false);
        }
    }
}
