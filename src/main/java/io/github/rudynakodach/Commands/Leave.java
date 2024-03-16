package io.github.rudynakodach.Commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Leave extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("leave")) {
            return;
        }
        else if(event.getGuild() == null || event.getMember() == null) {
            return;
        }

        if(event.getGuild().getSelfMember().getVoiceState() == null) {
            event.reply("Nie jestem połączony z czatem głosowym.").queue();
        } else {
            event.getGuild().getAudioManager().closeAudioConnection();
            event.reply(":thumbsup:").queue();
        }
    }
}
