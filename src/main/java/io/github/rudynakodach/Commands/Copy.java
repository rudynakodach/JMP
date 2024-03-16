package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class Copy extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("copy")) {
            return;
        } else if(event.getMember() == null || event.getGuild() == null) {
            return;
        }

        TrackScheduler scheduler = TrackScheduler.get(event.getGuild());
        scheduler.addToQueue(
                List.of(scheduler.queue.getQueue(false).get(0))
        );
    }
}
