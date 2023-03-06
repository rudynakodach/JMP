package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.*;
import static io.github.rudynakodach.Main.*;
public class Search extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("search")) {
            audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
            audioManager.openAudioConnection(event.getMember().getVoiceState().getChannel().asVoiceChannel());
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setAuthor("JSracz")
                    .setTitle(event.getInteraction().getOption("querry").getAsString())
                    .setColor(new Color(230, 25, 216));
            HashMap<String, String> songMap = new HashMap<>();
            playerManager.loadItem("ytsearch: " + event.getInteraction().getOption("querry").getAsString(), new FunctionalResultHandler(null, playlist -> {
                int page = 0;
                if (event.getInteraction().getOption("page") != null) {
                    page = Objects.requireNonNull(event.getInteraction().getOption("page")).getAsInt() - 1;
                }
                for (int i = page * 5; i < page * 5 + 5; i++) {
                    AudioTrack e = playlist.getTracks().get(i);
                    songMap.put(String.valueOf(i), e.getIdentifier());
                    embedBuilder.addField("`[" + i + "]` " + e.getInfo().title, e.getInfo().author + " `" + trackScheduler.formatDuration(e) + "`", false);
                }


                Collection<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
                for (Map.Entry<String, String> entry : songMap.entrySet()) {
                    String id = "YTPLAY: " + entry.getValue();
                    String label = entry.getKey();
                    net.dv8tion.jda.api.interactions.components.buttons.Button button = Button.primary(id, label);
                    buttons.add(button);
                }

                event.getInteraction().replyEmbeds(embedBuilder.build()).addActionRow(
                        buttons
                ).queue();
            }, null, null));
        }
    }
}
