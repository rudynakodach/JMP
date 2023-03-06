package io.github.rudynakodach.Commands.Music;

import io.github.rudynakodach.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Leave extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("leave")) {
            Main.latestChan = event.getInteraction().getChannel().asTextChannel();
            Main.audioManager.closeAudioConnection();
            Main.player.stopTrack();
            event.getInteraction().reply("Opuszczono `" + event.getMember().getVoiceState().getChannel().getName() + "` :thumbsup:").queue();
        }
    }
}
