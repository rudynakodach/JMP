package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import io.github.rudynakodach.Utils.QueuePosition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RemoveAt extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("rm")) {
            return;
        } else if(event.getMember() == null || event.getGuild() == null) {
            return;
        }

        QueuePosition removed = TrackScheduler.get(event.getGuild()).removeAt(
                event.getOption("idx").getAsInt()
        );
        event.reply("Usunięto `%s` z kolejki. Dodane przez <@%s>".formatted(removed.track().getInfo().title, removed.member().getId())).queue();
    }
}
