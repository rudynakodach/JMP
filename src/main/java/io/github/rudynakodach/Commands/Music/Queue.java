package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
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
            if(amt > trackScheduler.getQueue(false).size()) {
                amt = trackScheduler.getQueue(false).size();
            }
            Collection<AudioTrack> currentQueue = trackScheduler.getQueue(amt);
            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor("JMP")
                    .setTimestamp(Instant.now())
                    .setThumbnail(client.getSelfUser().getEffectiveAvatarUrl())
                    .setColor(new Color(66, 135, 245))
                    .setFooter("Wyświetlanie " + currentQueue.size() + " z " + trackScheduler.getQueue(false).size() + " elementów." + (trackScheduler.isQueueLooped ? "  |  KOLEJKA ZAPĘTLONA [" + trackScheduler.queueToLoop.size() + " elem.]" : "") + (trackScheduler.isLooped ? "  |  UTWÓR ZAPĘTLONY" : ""));

            Collection<Button> jumpButtons = new ArrayList<>();
            Collection<Button> rmButtons = new ArrayList<>();
            for (int i = 0; i < amt; i++) {
                Button jumpButton = Button.success("JUMP " + i, String.valueOf(i+1));
                Button removeAtButton = Button.danger("REMOVEAT " + i, String.valueOf(i+1));
                jumpButtons.add(jumpButton);
                rmButtons.add(removeAtButton);
            }
            Collection<LayoutComponent> components = new ArrayList<>();
            if(jumpButtons.size() > 0) {
                components.add(ActionRow.of(jumpButtons));
                components.add(ActionRow.of(rmButtons));
            }

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

            if(components.stream().toList().size() > 0) {
                event.getInteraction().replyEmbeds(eb.build())
                        .setComponents(components)
                        .queue();
            } else {
                event.getInteraction().replyEmbeds(eb.build())
                        .queue();
            }
        }
    }
}
