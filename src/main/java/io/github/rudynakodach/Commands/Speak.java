package io.github.rudynakodach.Commands;

import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Speak extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("speak")) {
            return;
        } else if(event.getGuild() == null || event.getMember() == null) {
            return;
        }

        if(event.getMember().getVoiceState() == null) {
            event.reply("Musisz być połączony ze sceną!").queue();
        } else if(event.getMember().getVoiceState().getChannel() == null) {
            event.reply("Musisz być połączony ze sceną!").queue();
        } else {
            if(event.getMember().getVoiceState().getChannel() instanceof StageChannel stage) {
                stage.requestToSpeak().queue();
                event.reply(":thumbsup:").queue();
            }
        }
    }
}
