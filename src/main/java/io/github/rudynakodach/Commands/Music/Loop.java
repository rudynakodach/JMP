package io.github.rudynakodach.Commands.Music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;

import static io.github.rudynakodach.Main.*;

public class Loop extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("loop")) {
            if(event.getMember() == null) {
                return;
            } else if(event.getMember().getVoiceState() == null) {
                return;
            } else if(event.getMember().getVoiceState().getChannel() == null) {
                return;
            }

            latestChan = event.getInteraction().getChannel().asTextChannel();
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("JMP")
                    .setThumbnail(client.getSelfUser().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now());
            if(trackScheduler.isLooped) {
                eb.setColor(new Color(255, 25,  0));
                eb.addField("Pętla", "Wyłączono zapętlenie.", false);
                event.getInteraction().replyEmbeds(eb.build()).queue();
            } else {
                eb.setColor(new Color(25, 255,  0));
                eb.addField("Pętla", "Zapętlono `" + trackScheduler.getCurrentlyPlayingTrack().getInfo().title + "`.", false);
                event.getInteraction().replyEmbeds(eb.build()).queue();
            }
            trackScheduler.toggleLoop();

        }
    }
}
