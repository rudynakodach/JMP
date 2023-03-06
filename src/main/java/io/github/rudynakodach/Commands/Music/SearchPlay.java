package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

import static io.github.rudynakodach.Main.*;
public class SearchPlay extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("sp")) {
            audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
            audioManager.openAudioConnection(event.getMember().getVoiceState().getChannel().asVoiceChannel());
            playerManager.loadItem("ytsearch: " + event.getInteraction().getOption("querry").getAsString(), new FunctionalResultHandler(null, playlist -> {
                AudioTrack e = playlist.getTracks().get(0);
                trackScheduler.queue(e);
                event.getInteraction().reply("DJ załadował biciora: `" + e.getInfo().title + "`").queue();
            }, null, null));
        }
    }
}