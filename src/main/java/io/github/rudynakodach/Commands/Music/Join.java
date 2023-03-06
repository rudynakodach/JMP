package io.github.rudynakodach.Commands.Music;

import io.github.rudynakodach.AudioPlayerSendHandler;
import io.github.rudynakodach.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.rudynakodach.Main.*;

public class Join extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("join")) {
            if(!isAudioHandlerSet) {
                event.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                isAudioHandlerSet = !isAudioHandlerSet;
            }
            latestChan = event.getInteraction().getChannel().asTextChannel();
            audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
            audioManager.openAudioConnection(Objects.requireNonNull(event.getMember()).getVoiceState().getChannel().asVoiceChannel());
            event.getInteraction().reply("Ruszyłem swe tłuste dupsko na `"+event.getMember().getVoiceState().getChannel().getName() + "`").queue();
        }
    }
}
