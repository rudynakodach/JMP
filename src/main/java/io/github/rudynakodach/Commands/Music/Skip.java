package io.github.rudynakodach.Commands.Music;

import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static io.github.rudynakodach.Main.*;

public class Skip extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("skip")) {
            if(event.getMember().getVoiceState().getChannel() == null) {
                return;
            }
            latestChan = event.getInteraction().getChannel().asTextChannel();
            event.getInteraction().reply("Pominięto `" + player.getPlayingTrack().getInfo().title + "`.").queue();
            trackScheduler.nextTrack();
        }
    }
}
