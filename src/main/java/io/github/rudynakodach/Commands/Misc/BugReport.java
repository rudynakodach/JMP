package io.github.rudynakodach.Commands.Misc;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BugReport extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("bugreport")) {
            event.getInteraction().reply("Report a bug: https://github.com/rudynakodach/JMP/issues/new").setEphemeral(true).queue();
        }
    }
}
