package io.github.rudynakodach.Utils.Events;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandInteraction extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getMember() == null || event.getGuild() == null) {
            return;
        }
        TrackScheduler.get(event.getGuild()).channel = event.getInteraction().getChannel().asTextChannel();
    }
}
