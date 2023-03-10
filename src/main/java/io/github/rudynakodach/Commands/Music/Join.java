package io.github.rudynakodach.Commands.Music;

import io.github.rudynakodach.AudioPlayerSendHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.rudynakodach.Main.*;

public class Join extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("join")) {
            if(event.getMember().getVoiceState().getChannel() == null) {
                event.getInteraction().reply("Nie wykryto kanału głosowego.").queue();
                return;
            }

            if(!audioHandlerSetMap.containsKey(event.getGuild().getId())) {
                Objects.requireNonNull(event.getGuild()).getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                audioHandlerSetMap.put(Objects.requireNonNull(event.getGuild()).getId(), true);
            } else if(!audioHandlerSetMap.get(Objects.requireNonNull(event.getGuild()).getId())) {
                Objects.requireNonNull(event.getGuild()).getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                audioHandlerSetMap.put(Objects.requireNonNull(event.getGuild()).getId(), true);
            }

            latestChan = event.getInteraction().getChannel().asTextChannel();
            audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
            audioManager.openAudioConnection(Objects.requireNonNull(event.getMember()).getVoiceState().getChannel());

            event.getInteraction().reply("Dołączono na `" + event.getMember().getVoiceState().getChannel().getName() + "` :thumbsup:").queue();
        }
    }
}
