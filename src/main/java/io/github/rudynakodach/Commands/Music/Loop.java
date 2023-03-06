package io.github.rudynakodach.Commands.Music;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static io.github.rudynakodach.Main.*;

public class Loop extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("loop")) {
            latestChan = event.getInteraction().getChannel().asTextChannel();
            trackScheduler.toggleLoop();
            if(trackScheduler.isLooped) {
                event.getInteraction().reply(":arrows_clockwise:").queue();
            } else {
                event.getInteraction().reply(":arrow_right:").queue();
            }
        }
    }
}
