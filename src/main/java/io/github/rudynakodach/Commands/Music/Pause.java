package io.github.rudynakodach.Commands.Music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import static io.github.rudynakodach.Main.*;
public class Pause extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("pause")) {
            latestChan = event.getInteraction().getChannel().asTextChannel();
            trackScheduler.togglePaused();
            if(player.isPaused()) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("JSracz");
                eb.addField(":pause_button:","DEBUG", false);
                eb.addField("isPaused", String.valueOf(player.isPaused()), false);
                eb.setColor(new Color(217, 124, 24));
                event.getInteraction().replyEmbeds(eb.build()).queue();
            } else {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("JSracz");
                eb.addField(":arrow_forward:","DEBUG", false);
                eb.addField("isPaused", String.valueOf(player.isPaused()), false);
                eb.setColor(new Color(128, 204, 29));
                event.getInteraction().replyEmbeds(eb.build()).queue();
            }
        }
    }
}
