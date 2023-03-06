package io.github.rudynakodach.Commands.Music;

import io.github.rudynakodach.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Join extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("join")) {
            Main.latestChan = event.getInteraction().getChannel().asTextChannel();
            Main.audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
            Main.audioManager.openAudioConnection(Objects.requireNonNull(event.getMember()).getVoiceState().getChannel().asVoiceChannel());
            event.getInteraction().reply("Ruszyłem swe tłuste dupsko na `"+event.getMember().getVoiceState().getChannel().getName() + "`").queue();
        }
    }
}
