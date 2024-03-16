package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import io.github.rudynakodach.Utils.Embeds.PauseEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Pause extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("pause")) {
            return;
        } else if(event.getMember() == null || event.getGuild() == null) {
            return;
        }

        TrackScheduler.get(event.getGuild()).player.setPaused(
                !TrackScheduler.get(event.getGuild()).player.isPaused()
        );

        event.replyEmbeds(PauseEmbed.getEmbed(TrackScheduler.get(event.getGuild()).player.isPaused()).build()).queue();
    }
}
