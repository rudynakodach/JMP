package io.github.rudynakodach.Commands;

import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import io.github.rudynakodach.Main;
import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import io.github.rudynakodach.Utils.QueuePosition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class SearchPlay extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("sp")) {
            return;
        } else if(event.getMember() == null || event.getGuild() == null) {
            return;
        }

        String query = event.getOption("query").getAsString();

        Main.AUDIO_PLAYER_MANAGER.loadItem("ytsearch: %s".formatted(query), new FunctionalResultHandler(null, audioPlaylist -> {
            QueuePosition pos = new QueuePosition(event.getMember(), audioPlaylist.getTracks().get(0));
            TrackScheduler.get(event.getGuild()).addToQueue(List.of(pos));
            event.reply("Załadowany bicior: %s".formatted(pos.track().getInfo().title)).queue();
        }, null, null));
    }
}
