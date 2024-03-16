package io.github.rudynakodach.Commands;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class Jump extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("jump")) {
            return;
        } else if(event.getGuild() == null || event.getMember() == null) {
            return;
        }

        TrackScheduler.get(event.getGuild()).jump(
                Objects.requireNonNull(event.getOption("idx")).getAsInt()
        );

        event.reply(":thumbs_up:").queue();
    }
}
