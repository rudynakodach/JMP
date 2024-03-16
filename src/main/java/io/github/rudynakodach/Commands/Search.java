package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Embeds.EmbedData;
import io.github.rudynakodach.Utils.Embeds.SearchEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Search extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("search")) {
            return;
        } else if(event.getGuild() == null || event.getMember() == null) {
            return;
        }
        String query = event.getOption("query").getAsString();
        int page = 0;
        if(event.getOption("page") != null) {
            page = event.getOption("page").getAsInt() - 1;
        }

        EmbedData ed = SearchEmbed.getEmbed(query, page);
        event.replyEmbeds(ed.embed().build()).setComponents(ed.layout()).queue();
    }
}
