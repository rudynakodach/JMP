package io.github.rudynakodach.Commands.Music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import static io.github.rudynakodach.Main.*;
public class Pause extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("pause")) {
            if(event.getMember().getVoiceState().getChannel() == null) {
                return;
            }
            latestChan = event.getInteraction().getChannel().asTextChannel();
            trackScheduler.togglePause();

            EmbedBuilder eb = new EmbedBuilder()
                .setTitle("JMP");

            if(player.isPaused()) {
                eb.addField(":pause_button:","DEBUG", false);
                eb.setColor(new Color(217, 124, 24));
                event.getInteraction().replyEmbeds(eb.build()).queue();
            } else {
                eb.addField(":arrow_forward:","DEBUG", false);
                eb.setColor(new Color(128, 204, 29));
                event.getInteraction().replyEmbeds(eb.build()).queue();
            }
        }
    }
}
