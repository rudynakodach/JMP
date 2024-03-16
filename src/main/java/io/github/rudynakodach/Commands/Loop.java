package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import io.github.rudynakodach.Utils.Embeds.LoopStatusEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Loop extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("loop")) {
            return;
        } else if(event.getMember() == null || event.getGuild() == null) {
            return;
        }

        TrackScheduler.get(event.getGuild()).isLooped = !TrackScheduler.get(event.getGuild()).isLooped;
        event.replyEmbeds(LoopStatusEmbed.getEmbed(TrackScheduler.get(event.getGuild()).isLooped, TrackScheduler.get(event.getGuild())).build()).queue();
    }
}
