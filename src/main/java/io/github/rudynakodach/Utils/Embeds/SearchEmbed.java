package io.github.rudynakodach.Utils.Embeds;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.rudynakodach.Main;
import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import io.github.rudynakodach.Utils.QueuePosition;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SearchEmbed extends ListenerAdapter {
    public static EmbedData getEmbed(String query, int page) {
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor("JMP")
                .setColor(new Color(230, 25, 216))
                .setTitle(query);

        AtomicReference<Boolean> ready = new AtomicReference<>(false);
        AtomicReference<EmbedData> ed = new AtomicReference<>();
        Main.AUDIO_PLAYER_MANAGER.loadItem("ytsearch: " + query, new FunctionalResultHandler(null, audioPlaylist -> {

            Collection<Button> songButtons = new ArrayList<>();
            for (int i = page * 5; i < Math.min(page * 5 + 5, audioPlaylist.getTracks().size() - 1); i++) {
                AudioTrack track = audioPlaylist.getTracks().get(i);
                eb.addField("[%s] %s".formatted(i+1, track.getInfo().title), "%s `[%s]`".formatted(track.getInfo().author, TrackScheduler.formatDuration(track.getInfo().length)), false);
                songButtons.add(
                        Button.primary("se play %s".formatted(track.getIdentifier()), "%s".formatted(i+1)).asEnabled()
                );
            }

            Button pgBack = Button.secondary("se page %s | query %s".formatted(page - 1, query), Emoji.fromUnicode("U+2B05"));
            pgBack = page > 0 ? pgBack.asEnabled() : pgBack.asDisabled();

            Button pgFwd = Button.secondary("se page %s | query %s".formatted(page + 1, query), Emoji.fromUnicode("U+27A1"));
            pgFwd = page * 5 + 5 > audioPlaylist.getTracks().size() ? pgFwd.asDisabled() : pgFwd.asEnabled();

            Button removeButton = Button.danger("se remove", Emoji.fromUnicode("U+1F5D1")).asEnabled();
            Collection<LayoutComponent> components = new ArrayList<>();
            components.add(ActionRow.of(songButtons));
            components.add(ActionRow.of(pgBack, pgFwd, removeButton));
            ed.set(new EmbedData(eb, components));
            ready.set(true);
        }, null, null));
        while(!ready.get()) {
            //noop
        }
        return ed.get();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(event.getButton().getId() == null || event.getMember() == null || event.getGuild() == null) {
            return;
        } else if(!event.getButton().getId().startsWith("se")) {
            return;
        }
        String[] data = event.getButton().getId().split(" ");
        switch (data[1].toLowerCase()) {
            case "remove" -> {
                if (event.getUser() == event.getInteraction().getUser() || event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    event.getMessage().delete().queue();
                    return;
                }
            }
            case "play" -> {
                TrackScheduler scheduler = TrackScheduler.get(event.getGuild());
                Main.AUDIO_PLAYER_MANAGER.loadItem(data[2], new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        scheduler.addToQueue(List.of(new QueuePosition(event.getMember(), track)));
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        scheduler.addToQueue(playlist.getTracks().stream().map(e -> new QueuePosition(event.getMember(), e)).toList());
                    }

                    @Override
                    public void noMatches() {
                        event.reply("Nie znaleziono utworu.").queue();
                    }

                    @Override
                    public void loadFailed(FriendlyException exception) {
                        event.reply("Ładowanie utworu nie powiodło się: %s".formatted(exception.getMessage())).queue();
                    }
                });
            }
            case "page" -> {
                String page = data[2];
                String query = String.join(" ", Arrays.stream(data).toList().subList(5, data.length));

                EmbedData ed = getEmbed(query, Integer.parseInt(page));
                event.editMessageEmbeds(ed.embed().build()).setComponents(ed.layout()).queue();
                return;
            }
        }
        event.deferEdit().queue();
    }
}

