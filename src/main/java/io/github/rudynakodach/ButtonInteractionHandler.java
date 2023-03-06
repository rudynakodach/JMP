package io.github.rudynakodach;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static io.github.rudynakodach.Main.*;
public class ButtonInteractionHandler extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().startsWith("YTPLAY: ")) {
            if(!isAudioHandlerSet) {
                event.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                isAudioHandlerSet = !isAudioHandlerSet;
            }
            String trackId = event.getComponentId().split(":")[1].trim();
            playerManager.loadItem(trackId, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    event.getInteraction().reply("Załadowany bicior: " + track.getInfo().title).queue();
                    trackScheduler.queue(track);
                    if(player.getPlayingTrack() == null) {
                        trackScheduler.nextTrack();
                    }
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                        trackScheduler.queue(track);
                    }
                    event.getInteraction().reply("Dodano " + playlist.getTracks().size() + " elementów do kolejki.").queue();
                }

                @Override
                public void noMatches() {
                    event.getInteraction().reply("Moje murzyńskie moce nie znalazły takiego filmu").queue();
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    event.getInteraction().reply("Coś sie wylało: \n```" + exception.getMessage() + "\n```").queue();
                }
            });
        }

        else if(event.getComponentId().startsWith("YTSEARCH")) {
            String[] searchData = event.getComponentId().split("\\|");
            String query = searchData[0].split(":")[1].trim();
            int page = Integer.parseInt(searchData[1].split(":")[1].trim());

            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor("JSracz")
                    .setTitle(query)
                    .setColor(new Color(230, 25, 216));

            playerManager.loadItem("ytsearch: " + query, new FunctionalResultHandler(null, playlist -> {

                HashMap<String, String> songMap = new HashMap<>();
                for (int i = page * 5; i < page * 5 + 5; i++) {
                    AudioTrack e = playlist.getTracks().get(i);
                    songMap.put(String.valueOf(i+1), e.getIdentifier());
                    eb.addField("`[" + (i+1) + "]` " + e.getInfo().title, e.getInfo().author + " `" + trackScheduler.formatDuration(e) + "`", false);
                }

                Collection<LayoutComponent> components = new ArrayList<>();

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

                Button removeMessage = Button.danger("REMOVE | AUTHOR: " + event.getInteraction().getUser().getName(), Emoji.fromUnicode("U+1F5D1"));
                pageButtons.add(removeMessage);

                components.add(ActionRow.of(buttons));
                components.add(ActionRow.of(pageButtons));

                eb.setFooter("Strona " + page);
                event.getInteraction().editMessageEmbeds(eb.build())
                        .setComponents(components).queue();
            }, null, null));
        }

        else if(event.getComponentId().startsWith("REMOVE")) {
            String[] dataSplit = event.getComponentId().split("\\|");
            String authorName = dataSplit[1].split(":")[1].trim();

            if(authorName.equals(event.getUser().getName()) || event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.getMessage().delete().queue();
            }
        }
    }
}
