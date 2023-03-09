package io.github.rudynakodach.Commands.Music;

import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static io.github.rudynakodach.Main.*;
public class Volume extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("volume")) {
            if(event.getMember().getVoiceState().getChannel() == null) {
                return;
            }
            int newVolume = event.getInteraction().getOption("volume").getAsInt();
            int oldVolume = player.getVolume();
            player.setVolume(newVolume);
            event.getInteraction().reply("Zmieniono głośność z `" + oldVolume + "` na `" + newVolume + "`").queue();
        }
    }
}
