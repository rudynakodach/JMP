package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class QueueLoop extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("loopqueue")) {
            return;
        } else if(event.getGuild() == null || event.getMember() == null) {
            return;
        }

        TrackScheduler scheduler = TrackScheduler.get(event.getGuild());
        scheduler.toggleQueueLoop();
        if(scheduler.isQueueLooped) {
            event.reply("Zapętlono `%s` elementów.".formatted(scheduler.queue.getQueue(true).size())).queue();
        } else {
            event.reply("Wyłączono pętle kolejki.").queue();
        }
    }
}
