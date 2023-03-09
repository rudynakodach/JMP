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
import java.util.*;
import java.util.List;

import static io.github.rudynakodach.Main.*;
public class ButtonInteractionHandler extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(event.getMember().getVoiceState().getChannel() == null) {
            return;
        }

        if (event.getComponentId().startsWith("YTPLAY")) {
            if(!isAudioHandlerSet) {
                Objects.requireNonNull(event.getGuild()).getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                isAudioHandlerSet = !isAudioHandlerSet;
            }
            String trackId = event.getComponentId().split(":")[1].trim();
            playerManager.loadItem(trackId, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    event.getInteraction().reply("Załadowany bicior: `" + track.getInfo().title + "`").queue();
                    trackScheduler.queue(track);
                    if(player.getPlayingTrack() == null) {
                        trackScheduler.nextTrack();
                    }
                    if(trackScheduler.isQueueLooped) {
                        trackScheduler.queueToLoop.add(track);
                    }
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                        trackScheduler.queue(track);
                    }
                    if(trackScheduler.isQueueLooped) {
                        trackScheduler.queueToLoop.addAll(playlist.getTracks());
                    }
                    event.getInteraction().reply("Dodano " + playlist.getTracks().size() + " elementów do kolejki.").queue();
                }

                @Override
                public void noMatches() {
                    event.getInteraction().reply("Nie znaleziono utworu.").queue();
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
                    .setAuthor("JMP")
                    .setTitle(query)
                    .setColor(new Color(230, 25, 216));

            playerManager.loadItem("ytsearch: " + query, new FunctionalResultHandler(null, playlist -> {

                HashMap<String, String> songMap = new HashMap<>();

                int amt = page*5+5;
                boolean isNextButtonDisabled = false;
                if(amt > playlist.getTracks().size()) {
                    amt = playlist.getTracks().size() - 1;
                    isNextButtonDisabled = true;
                }

                for (int i = page * 5; i < amt; i++) {
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
                if(isNextButtonDisabled) {
                    pgFwd = pgFwd.asDisabled();
                }
                pageButtons.add(pgFwd);

                Button removeMessage = Button.danger("REMOVE | AUTHOR: " + event.getInteraction().getUser().getName(), Emoji.fromUnicode("U+1F5D1"));
                pageButtons.add(removeMessage);

                components.add(ActionRow.of(buttons));
                components.add(ActionRow.of(pageButtons));

                eb.setFooter("Strona " + (page+1));
                event.getInteraction().editMessageEmbeds(eb.build())
                        .setComponents(components).queue();
            }, null, null));
        }

        else if(event.getComponentId().startsWith("REMOVE")) {
            String[] dataSplit = event.getComponentId().split("\\|");
            String authorName = dataSplit[1].split(":")[1].trim();

            if(authorName.equals(event.getUser().getName()) || Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR) || event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.getMessage().delete().queue();
            }
        }

        else if(event.getComponentId().startsWith("SKIP")) {
            latestChan = event.getInteraction().getChannel().asTextChannel();
            trackScheduler.nextTrack();
            if(player.getPlayingTrack() != null) {
                AudioTrack track = player.getPlayingTrack();

                String durationString = trackScheduler.formatProgress(track);

                AudioTrack nextSong = trackScheduler.nextElement();

                Button replayButton = Button.primary("REPLAY | ID: " + player.getPlayingTrack().getInfo().identifier, Emoji.fromUnicode("U+23EE"));
                Button stopButton = Button.primary("STOP", Emoji.fromUnicode("U+23F9"));
                Button pauseActionButton = Button.primary("TOGGLEPAUSE", trackScheduler.getPausedStatus() ? Emoji.fromUnicode("U+23F8") : Emoji.fromUnicode("U+25B6"));
                Button skipButton;
                Button removeButton = Button.danger("REMOVE | AUTHOR: " + event.getInteraction().getUser().getName(), Emoji.fromUnicode("U+1F5D1"));
                if(trackScheduler.getQueue().length > 0) {
                    skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asEnabled();
                } else {
                    skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asDisabled();
                }
                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(new Color(202, 23, 255))
                        .setAuthor("JMP")
                        .addField(player.getPlayingTrack().getInfo().title, durationString, false);
                if (nextSong != null) {
                    eb.addField("Następne", "`" + nextSong.getInfo().title + "`", false);
                } else {
                    eb.addField("Następne", "`Brak`", false);
                }
                event.getInteraction().editMessageEmbeds(eb.build())
                        .setActionRow(
                                replayButton,
                                stopButton,
                                pauseActionButton,
                                skipButton,
                                removeButton
                        ).queue();
            }
        }

        else if(event.getComponentId().startsWith("STOP")) {
            trackScheduler.clearQueue();
            player.stopTrack();
            event.deferEdit().queue();
        }

        else if(event.getComponentId().startsWith("TOGGLEPAUSE")) {
            trackScheduler.togglePause();
            AudioTrack track = player.getPlayingTrack();

            String durationString = trackScheduler.formatProgress(track);

            AudioTrack nextSong = trackScheduler.nextElement();

            Button replayButton = Button.primary("REPLAY | ID: " + player.getPlayingTrack().getInfo().identifier, Emoji.fromUnicode("U+23EE"));
            Button stopButton = Button.primary("STOP", Emoji.fromUnicode("U+23F9"));
            Button pauseActionButton = Button.primary("TOGGLEPAUSE", trackScheduler.getPausedStatus() ? Emoji.fromUnicode("U+23F8") : Emoji.fromUnicode("U+25B6"));
            Button skipButton;
            Button removeButton = Button.danger("REMOVE | AUTHOR: " + event.getInteraction().getUser().getName(), Emoji.fromUnicode("U+1F5D1"));
            if(trackScheduler.getQueue().length > 0) {
                skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asEnabled();
            } else {
                skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asDisabled();
            }
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(new Color(202, 23, 255))
                    .setAuthor("JMP")
                    .addField(player.getPlayingTrack().getInfo().title, durationString, false);
            if (nextSong != null) {
                eb.addField("Następne", "`" + nextSong.getInfo().title + "`", false);
            } else {
                eb.addField("Następne", "`Brak`", false);
            }
            event.getInteraction().editMessageEmbeds(eb.build())
                    .setActionRow(
                            replayButton,
                            stopButton,
                            pauseActionButton,
                            skipButton,
                            removeButton
                    ).queue();
        }

        else if(event.getComponentId().startsWith("REPLAY")) {
            String[] buttonData = event.getComponentId().split("\\|");
            String songIdentifier = buttonData[1].trim().split(":")[1].trim();

            playerManager.loadItem(songIdentifier, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    player.playTrack(track);
                    event.getInteraction().deferEdit().queue();
                }

                @Override
                public void playlistLoaded(AudioPlaylist ignored) {}

                @Override
                public void noMatches() {
                    event.getInteraction().reply("Nie znaleziono utworu.").queue();
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    event.getInteraction().reply("Nie udało się załadować utworu.").queue();
                }
            });
        }
    }
}
