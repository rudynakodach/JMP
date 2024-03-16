package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import io.github.rudynakodach.Utils.QueuePosition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Skip extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("skip")) {
            return;
        } else if(event.getGuild() == null || event.getMember() == null) {
            return;
        }

        TrackScheduler scheduler = TrackScheduler.get(event.getGuild());
        QueuePosition now = scheduler.queue.getQueue(true).get(0);
        event.reply("Pominięto `%s`. *Dodane przez <@%s>*".formatted(now.track().getInfo().title, now.member().getId())).queue();
        scheduler.skip();
    }
}
