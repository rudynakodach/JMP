package io.github.rudynakodach.Commands.Music;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static io.github.rudynakodach.Main.*;
public class Shuffle extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("shuffle")) {
            latestChan = event.getInteraction().getChannel().asTextChannel();
            if(event.getMember().getVoiceState() == null) {
                event.getInteraction().reply("Musisz być połączony z kanałem głosowym/sceną!").queue();
                return;
            }
            trackScheduler.shufflePlaylist(true);
            event.getInteraction().reply(":thumbsup:").queue();
        }
    }
}
