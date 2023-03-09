package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static io.github.rudynakodach.Main.*;

public class NowPlaying extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("np")) {
            if(event.getMember().getVoiceState().getChannel() == null) {
                return;
            }
            latestChan = event.getInteraction().getChannel().asTextChannel();
            if(player.getPlayingTrack() != null) {
                AudioTrack track = player.getPlayingTrack();

                String durationString = trackScheduler.formatProgress(track);

                AudioTrack nextSong = trackScheduler.nextElement();

                Button replayButton = Button.primary("REPLAY | ID: " + player.getPlayingTrack().getInfo().identifier, Emoji.fromUnicode("U+23EE"));
                Button stopButton = Button.primary("STOP", Emoji.fromUnicode("U+23F9"));
                Button pauseActionButton = Button.primary("TOGGLEPAUSE", trackScheduler.getPausedStatus() ? Emoji.fromUnicode("U+23F8") : Emoji.fromUnicode("U+25B6"));
                Button skipButton;
                Button removeButton = Button.danger("REMOVE | AUTHOR: " + event.getInteraction().getUser().getName(), Emoji.fromUnicode("U+1F5D1"));
                if(trackScheduler.getQueue().length > 0) {
                    skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asEnabled();
                } else {
                    skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asDisabled();
                }
                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(new Color(202, 23, 255))
                        .setAuthor("JMP")
                        .addField(player.getPlayingTrack().getInfo().title, durationString, false);
                if (nextSong != null) {
                    eb.addField("Następne", "`" + nextSong.getInfo().title + "`", false);
                } else {
                    eb.addField("Następne", "`Brak`", false);
                }
                event.getInteraction().replyEmbeds(eb.build())
                        .setActionRow(
                                replayButton,
                                stopButton,
                                pauseActionButton,
                                skipButton,
                                removeButton
                        ).queue();
            } else {
                event.getInteraction().reply("Nie wykryto utworu.").queue();
            }
        }
    }
}
