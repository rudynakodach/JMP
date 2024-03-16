package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import io.github.rudynakodach.Utils.Embeds.EmbedData;
import io.github.rudynakodach.Utils.Embeds.QueueEmbed;
import io.github.rudynakodach.Utils.QueuePosition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class Queue extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("queue")) {
            return;
        } else if(event.getMember() == null || event.getGuild() == null) {
            return;
        }

        int page = 0;
        if(event.getOption("page") != null) {
            page = event.getOption("page").getAsInt() - 1;
        }

        TrackScheduler scheduler = TrackScheduler.get(event.getGuild());
        List<QueuePosition> queue = scheduler.queue.getQueue(true);
        EmbedData ed = QueueEmbed.getEmbed(queue, page);
        event.replyEmbeds(ed.embed().build()).setComponents(ed.layout()).queue();
    }
}
