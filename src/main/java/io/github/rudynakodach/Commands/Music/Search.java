package io.github.rudynakodach.Commands.Music;

import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.rudynakodach.AudioPlayerSendHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.awt.*;
import java.util.*;
import static io.github.rudynakodach.Main.*;
public class Search extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("search")) {
            latestChan = event.getInteraction().getChannel().asTextChannel();
            audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
            audioManager.openAudioConnection(event.getMember().getVoiceState().getChannel().asVoiceChannel());
            if(!isAudioHandlerSet) {
                event.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                isAudioHandlerSet = !isAudioHandlerSet;
            }
            String query = event.getInteraction().getOption("query").getAsString();
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setAuthor("JSracz")
                    .setTitle(query)
                    .setColor(new Color(230, 25, 216));
            playerManager.loadItem("ytsearch: " + query, new FunctionalResultHandler(null, playlist -> {
                int page = 0;
                if (event.getInteraction().getOption("page") != null) {
                    page = Objects.requireNonNull(event.getInteraction().getOption("page")).getAsInt() - 1;
                }
                HashMap<String, String> songMap = new HashMap<>();
                for (int i = page * 5; i < page * 5 + 5; i++) {
                    AudioTrack e = playlist.getTracks().get(i);
                    songMap.put(String.valueOf(i), e.getIdentifier());
                    embedBuilder.addField("`[" + i + "]` " + e.getInfo().title, e.getInfo().author + " `" + trackScheduler.formatDuration(e) + "`", false);
                }


                Collection<Button> buttons = new ArrayList<>();
                for (Map.Entry<String, String> entry : songMap.entrySet()) {
                    String id = "YTPLAY: " + entry.getValue();
                    String label = entry.getKey();
                    net.dv8tion.jda.api.interactions.components.buttons.Button button = Button.primary(id, label);
                    buttons.add(button);
                }
                Collection<Button> pageButtons = new ArrayList<>();
                Button pgBack;
                if(page > 0) {
                    pgBack = Button.secondary("YTSEARCH: " + query + " | PAGE: " + (page - 1), Emoji.fromUnicode("U+2B05")).asEnabled();
                } else {
                    pgBack = Button.secondary("YTSEARCH: " + query + " | PAGE: " + (page - 1), Emoji.fromUnicode("U+2B05")).asDisabled();
                }
                pageButtons.add(pgBack);
                Button pgFwd = Button.secondary("YTSEARCH: " + query + " | PAGE: " + (page+1), Emoji.fromUnicode("U+27A1"));
                pageButtons.add(pgFwd);
                embedBuilder.setFooter("Strona " + page);
                event.getInteraction().replyEmbeds(embedBuilder.build()).addActionRow(
                        buttons
                ).addActionRow(
                        pageButtons
                ).queue();
            }, null, null));
        }
    }
}
