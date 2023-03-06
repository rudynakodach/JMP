package io.github.rudynakodach.Commands.Music;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static io.github.rudynakodach.Main.*;
public class Stop extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("stop")) {
            player.stopTrack();
            trackScheduler.clearQueue();
        }
    }
}
