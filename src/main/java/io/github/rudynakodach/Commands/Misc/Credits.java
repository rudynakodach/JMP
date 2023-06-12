package io.github.rudynakodach.Commands.Misc;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Credits extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("credits")) {
            event.getInteraction().reply("JMP SemVer v1.5.1a\n`JMP` ~ rudynakodach aka rudydev#4987\nsrc: https://github.com/rudynakodach/JMP")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
