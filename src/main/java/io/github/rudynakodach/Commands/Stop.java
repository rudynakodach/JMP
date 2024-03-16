package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Stop extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("stop")) {
            return;
        } else if(event.getGuild() == null || event.getMember() == null) {
            return;
        }
        TrackScheduler scheduler = TrackScheduler.get(event.getGuild());
        scheduler.stop();

        event.reply("Zatrzymano i usunięto kolejke.").queue();
    }
}
