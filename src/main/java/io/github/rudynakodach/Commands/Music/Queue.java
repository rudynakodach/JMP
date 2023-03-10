package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

import static io.github.rudynakodach.Main.*;
public class Queue extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("queue")) {
            if(event.getMember().getVoiceState().getChannel() == null) {
                return;
            }
            latestChan = event.getInteraction().getChannel().asTextChannel();

            int amt = 5;
            if (event.getInteraction().getOption("ilosc") != null) {
                amt = Objects.requireNonNull(event.getInteraction().getOption("ilosc")).getAsInt();
            }
            if(amt > trackScheduler.getQueue().size()) {
                amt = trackScheduler.getQueue().size();
            }
            Collection<AudioTrack> currentQueue = trackScheduler.getQueue(amt);
            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor("JMP")
                    .setTimestamp(Instant.now())
                    .setThumbnail(client.getSelfUser().getEffectiveAvatarUrl())
                    .setColor(new Color(66, 135, 245))
                    .setFooter("Wyświetlanie " + currentQueue.size() + " z " + trackScheduler.getQueue().size() + " elementów." + (trackScheduler.isQueueLooped ? "  |  KOLEJKA ZAPĘTLONA [" + trackScheduler.queueToLoop.size() + " elem.]" : "") + (trackScheduler.isLooped ? "  |  UTWÓR ZAPĘTLONY" : ""));

            if(player.getPlayingTrack() != null) {
                eb.setTitle("Teraz");
                eb.addField(player.getPlayingTrack().getInfo().title, player.getPlayingTrack().getInfo().author + " `[" + trackScheduler.formatProgress(player.getPlayingTrack()) + "]`", false);
                eb.addBlankField(false);
            }
            if(amt == 0) {
                eb.addField("Kolejka jest pusta!", "Dodaj coś za pomocą `/search`, `/play` lub `/sp`.", false);
            } else {
                eb.addField("Kolejka", "-----------------------------------------", false);
                for (int i = 0; i < amt; i++) {
                    AudioTrack e = currentQueue.stream().toList().get(i);
                    eb.addField("`[" + (i + 1) + "]` " + e.getInfo().title, e.getInfo().author + " `[" + trackScheduler.formatDuration(e) + "]`", false);
                }
            }
            event.getInteraction().replyEmbeds(eb.build()).queue();
        }
    }
}
