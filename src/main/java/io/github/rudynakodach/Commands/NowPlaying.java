package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import io.github.rudynakodach.Utils.Embeds.EmbedData;
import io.github.rudynakodach.Utils.Embeds.NowPlayingEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class NowPlaying extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("np")) {
            return;
        } else if(event.getGuild() == null || event.getMember() == null) {
            return;
        }
        EmbedData ed = NowPlayingEmbed.getEmbed(TrackScheduler.get(event.getGuild()));
        event.replyEmbeds(ed.embed().build()).setComponents(ed.layout()).queue();
    }
}
