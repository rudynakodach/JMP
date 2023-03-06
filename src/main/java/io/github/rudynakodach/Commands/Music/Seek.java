package io.github.rudynakodach.Commands.Music;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static io.github.rudynakodach.Main.*;
public class Seek extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("seek")) {
            if(player.getPlayingTrack() == null) {
                event.getInteraction().reply("Nie wykryto utworu.").queue();
                return;
            }
            int time = event.getInteraction().getOption("t").getAsInt();
            player.getPlayingTrack().setPosition(time * 1000L);

            int minutes = time / 60;
            int secondsRemaining = time % 60;

            String formattedTime = String.format("%02d:%02d", minutes, secondsRemaining);
            event.getInteraction().reply("Przeskoczono na `[" + formattedTime +"]` :thumbsup:").queue();
        }
    }
}
