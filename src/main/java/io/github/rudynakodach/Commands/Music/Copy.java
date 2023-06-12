package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.rudynakodach.Main.*;
public class Copy extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("copy")) {
            if(event.getMember().getVoiceState().getChannel() == null) {
                return;
            }
            int pos = Objects.requireNonNull(event.getInteraction().getOption("pos")).getAsInt() - 1;
            AudioTrack elementToCopy = trackScheduler.getQueue(false).stream().toList().get(pos);
            trackScheduler.queue(elementToCopy.makeClone());
            event.getInteraction().reply("Skopiowano `" + elementToCopy.getInfo().title + "` na koniec kolejki.").queue();
        }
    }
}
