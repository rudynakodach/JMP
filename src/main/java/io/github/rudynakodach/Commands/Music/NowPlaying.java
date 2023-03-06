package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.rudynakodach.AudioPlayerSendHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import static io.github.rudynakodach.Main.*;

public class NowPlaying extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("np")) {
            latestChan = event.getInteraction().getChannel().asTextChannel();
            if(player.getPlayingTrack() != null) {
                AudioTrack track = player.getPlayingTrack();

                String durationString = trackScheduler.getTrackLength(track);

                AudioTrack nextSong = trackScheduler.nextElement();

                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(new Color(202, 23, 255))
                        .setAuthor("JSracz")
                        .addField(player.getPlayingTrack().getInfo().title, durationString, false);
                if (nextSong != null) {
                    eb.addField("Następne", "`" + nextSong.getInfo().title + "`", false);
                } else {
                    eb.addField("Następne", "`Brak`", false);
                }
                event.getInteraction().replyEmbeds(eb.build()).queue();
            }
        }
    }
}
