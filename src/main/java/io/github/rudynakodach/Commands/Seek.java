package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Seek extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("seek")) {
            return;
        } else if(event.getMember() == null || event.getGuild() == null) {
            return;
        }

        long time = event.getOption("t").getAsLong() * 1000L;
        TrackScheduler.get(event.getGuild()).player.getPlayingTrack().setPosition(time);

        event.reply("Przeskoczono na `[%s/%s]`".formatted(TrackScheduler.formatDuration(time), TrackScheduler.formatDuration(TrackScheduler.get(event.getGuild()).player.getPlayingTrack().getDuration()))).queue();
    }
}
