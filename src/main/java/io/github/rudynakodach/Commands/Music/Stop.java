package io.github.rudynakodach.Commands.Music;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

import static io.github.rudynakodach.Main.*;
public class Stop extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("stop")) {
            if(event.getMember().getVoiceState().getChannel() == null) {
                return;
            }
            player.stopTrack();
            trackScheduler.clearQueue();
            if(trackScheduler.isQueueLooped)  {
                trackScheduler.toggleQueueLoop(new ArrayList<>());
            }
            event.getInteraction().reply("Zatrzymano odtwarzanie i usunięto kolejke.").queue();
        }
    }
}
