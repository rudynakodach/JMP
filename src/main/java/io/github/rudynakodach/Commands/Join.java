package io.github.rudynakodach.Commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Join extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("join")) {
            return;
        }
        if(event.getMember() == null || event.getGuild() == null) {
            return;
        } else if(event.getMember().getVoiceState() == null) {
            event.reply("Połącz się z czatem głosowym!").queue();
            return;
        }

        event.getGuild().getAudioManager().openAudioConnection(
                event.getMember().getVoiceState().getChannel()
        );
        event.reply("Dołączono na `%s`!".formatted(event.getMember().getVoiceState().getChannel().getName())).queue();
    }
}
