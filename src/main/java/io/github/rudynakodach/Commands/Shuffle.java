package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import io.github.rudynakodach.Utils.QueuePosition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Collections;
import java.util.List;

public class Shuffle extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("shuffle")) {
            return;
        } else if(event.getGuild() == null || event.getMember() == null) {
            return;
        }

        TrackScheduler scheduler = TrackScheduler.get(event.getGuild());
        List<QueuePosition> queue = scheduler.queue.getQueue(false);
        Collections.shuffle(queue);
        scheduler.queue.clear();
        scheduler.addToQueue(queue);

    }
}
