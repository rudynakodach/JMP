package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Volume extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("volume")) {
            return;
        } else if(event.getGuild() == null || event.getMember() == null) {
            return;
        }

        int vol = Math.max(0, Math.min(1000, event.getOption("volume").getAsInt()));

        TrackScheduler.get(event.getGuild()).player.setVolume(vol);
        event.reply("Głośność ustawiona na `%s`".formatted(vol)).queue();
    }
}
