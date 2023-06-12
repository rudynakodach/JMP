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
import java.time.Instant;
import java.util.*;
import java.util.List;

import static io.github.rudynakodach.Main.*;
public class ButtonInteractionHandler extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(event.getMember().getVoiceState().getChannel() == null) {
            return;
        }

        if(latestChan == null) {
            latestChan = event.getInteraction().getChannel().asTextChannel();
        }

        if (event.getComponentId().startsWith("YTPLAY")) {
            if(!audioHandlerSetMap.get(event.getGuild().getId())) {
                Objects.requireNonNull(event.getGuild()).getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                audioHandlerSetMap.put(Objects.requireNonNull(event.getGuild()).getId(), true);
            }
            String trackId = event.getComponentId().split(":")[1].trim();
            playerManager.loadItem(trackId, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    event.getInteraction().reply("Załadowany bicior: `" + track.getInfo().title + "`").queue();
                    trackScheduler.queue(track);
                    if(player.getPlayingTrack() == null) {
                        trackScheduler.nextTrack(true);
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
                    .setColor(new Color(230, 25, 216))
                    .setTimestamp(Instant.now())
                    .setThumbnail(client.getSelfUser().getEffectiveAvatarUrl());

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

        else if(event.getComponentId().startsWith("REMOVE") && !event.getComponentId().startsWith("REMOVEAT")) {
            String[] dataSplit = event.getComponentId().split("\\|");
            String authorName = dataSplit[1].split(":")[1].trim();

            if(authorName.equals(event.getUser().getName()) || Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR) || event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.getMessage().delete().queue();
            }
        }

        else if(event.getComponentId().startsWith("SKIP")) {
            latestChan = event.getInteraction().getChannel().asTextChannel();
            trackScheduler.nextTrack(true);
            if(player.getPlayingTrack() != null) {
                AudioTrack track = player.getPlayingTrack();

                String durationString = trackScheduler.formatProgress(track);

                AudioTrack nextSong = trackScheduler.nextElement();

                Button replayButton = Button.primary("REPLAY | ID: " + player.getPlayingTrack().getInfo().identifier, Emoji.fromUnicode("U+23EE"));
                Button stopButton = Button.primary("STOP", Emoji.fromUnicode("U+23F9"));
                Button pauseActionButton = Button.primary("TOGGLEPAUSE", trackScheduler.getPausedStatus() ? Emoji.fromUnicode("U+23F8") : Emoji.fromUnicode("U+25B6"));
                Button skipButton;
                Button removeButton = Button.danger("REMOVE | AUTHOR: " + event.getInteraction().getUser().getName(), Emoji.fromUnicode("U+1F5D1"));
                if(trackScheduler.getQueue(false).size() > 0) {
                    skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asEnabled();
                } else {
                    skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asDisabled();
                }
                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(new Color(202, 23, 255))
                        .setAuthor("JMP")
                        .setTimestamp(Instant.now())
                        .setThumbnail(client.getSelfUser().getEffectiveAvatarUrl())
                        .addField(player.getPlayingTrack().getInfo().title, durationString, false)
                        .setFooter((trackScheduler.isQueueLooped ? "KOLEJKA ZAPĘTLONA [" + trackScheduler.queueToLoop.size() + " elem.]" : "") + (trackScheduler.isLooped ?  ( trackScheduler.isQueueLooped ? "  |  " : "") + "UTWÓR ZAPĘTLONY" : ""));

                if (nextSong != null) {
                    eb.addField("Następne", "`" + nextSong.getInfo().title + "`", false);
                } else {
                    eb.addField("Następne", "`Brak`", false);
                }

                Button loopControl = Button.primary("TOGGLELOOP", Emoji.fromUnicode(trackScheduler.isLooped ? "U+27A1" : "U+1F502"));
                Button queueLoopControl = Button.primary("TOGGLEQUEUELOOP", trackScheduler.isQueueLooped ? "DLQ" : "LQ");
                Button shuffleQueueButton = Button.primary("SHUFFLE", Emoji.fromUnicode("U+1F500"));

                Collection<LayoutComponent> components = new ArrayList<>();
                components.add(ActionRow.of(replayButton, stopButton, pauseActionButton, skipButton, removeButton));
                components.add(ActionRow.of(loopControl, shuffleQueueButton, queueLoopControl));
                event.getInteraction().editMessageEmbeds(eb.build())
                        .setComponents(components).queue();
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
            if(trackScheduler.getQueue(false).size() > 0) {
                skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asEnabled();
            } else {
                skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asDisabled();
            }
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(new Color(202, 23, 255))
                    .setAuthor("JMP")
                    .setTimestamp(Instant.now())
                    .setThumbnail(client.getSelfUser().getEffectiveAvatarUrl())
                    .addField(player.getPlayingTrack().getInfo().title, durationString, false)
                    .setFooter((trackScheduler.isQueueLooped ? "KOLEJKA ZAPĘTLONA [" + trackScheduler.queueToLoop.size() + " elem.]" : "") + (trackScheduler.isLooped ?  ( trackScheduler.isQueueLooped ? "  |  " : "") + "UTWÓR ZAPĘTLONY" : ""));

            if (nextSong != null) {
                eb.addField("Następne", "`" + nextSong.getInfo().title + "`", false);
            } else {
                eb.addField("Następne", "`Brak`", false);
            }

            Button loopControl = Button.primary("TOGGLELOOP", Emoji.fromUnicode(trackScheduler.isLooped ? "U+27A1" : "U+1F502"));
            Button queueLoopControl = Button.primary("TOGGLEQUEUELOOP", trackScheduler.isQueueLooped ? "DLQ" : "LQ");
            Button shuffleQueueButton = Button.primary("SHUFFLE", Emoji.fromUnicode("U+1F500"));

            Collection<LayoutComponent> components = new ArrayList<>();
            components.add(ActionRow.of(replayButton, stopButton, pauseActionButton, skipButton, removeButton));
            components.add(ActionRow.of(loopControl, shuffleQueueButton, queueLoopControl));
            event.getInteraction().editMessageEmbeds(eb.build())
                    .setComponents(components).queue();
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

        else if(event.getComponentId().equalsIgnoreCase("shuffle")) {
            trackScheduler.shufflePlaylist(false);
            event.getInteraction().deferEdit().queue();
        }

        else if(event.getComponentId().equalsIgnoreCase("toggleloop")) {
            trackScheduler.toggleLoop();
            AudioTrack track = player.getPlayingTrack();

            String durationString = trackScheduler.formatProgress(track);

            AudioTrack nextSong = trackScheduler.nextElement();

            Button replayButton = Button.primary("REPLAY | ID: " + player.getPlayingTrack().getInfo().identifier, Emoji.fromUnicode("U+23EE"));
            Button stopButton = Button.primary("STOP", Emoji.fromUnicode("U+23F9"));
            Button pauseActionButton = Button.primary("TOGGLEPAUSE", trackScheduler.getPausedStatus() ? Emoji.fromUnicode("U+23F8") : Emoji.fromUnicode("U+25B6"));
            Button skipButton;
            Button removeButton = Button.danger("REMOVE | AUTHOR: " + event.getInteraction().getUser().getName(), Emoji.fromUnicode("U+1F5D1"));
            if(trackScheduler.getQueue(false).size() > 0) {
                skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asEnabled();
            } else {
                skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asDisabled();
            }
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(new Color(202, 23, 255))
                    .setAuthor("JMP")
                    .setTimestamp(Instant.now())
                    .setThumbnail(client.getSelfUser().getEffectiveAvatarUrl())
                    .addField(player.getPlayingTrack().getInfo().title, durationString, false)
                    .setFooter((trackScheduler.isQueueLooped ? "KOLEJKA ZAPĘTLONA [" + trackScheduler.queueToLoop.size() + " elem.]" : "") + (trackScheduler.isLooped ?  ( trackScheduler.isQueueLooped ? "  |  " : "") + "UTWÓR ZAPĘTLONY" : ""));

            if (nextSong != null) {
                eb.addField("Następne", "`" + nextSong.getInfo().title + "`", false);
            } else {
                eb.addField("Następne", "`Brak`", false);
            }

            Button loopControl = Button.primary("TOGGLELOOP", Emoji.fromUnicode(trackScheduler.isLooped ? "U+27A1" : "U+1F502"));
            Button queueLoopControl = Button.primary("TOGGLEQUEUELOOP", trackScheduler.isQueueLooped ? "DLQ" : "LQ");
            Button shuffleQueueButton = Button.primary("SHUFFLE", Emoji.fromUnicode("U+1F500"));

            Collection<LayoutComponent> components = new ArrayList<>();
            components.add(ActionRow.of(replayButton, stopButton, pauseActionButton, skipButton, removeButton));
            components.add(ActionRow.of(loopControl, shuffleQueueButton, queueLoopControl));
            event.getInteraction().editMessageEmbeds(eb.build())
                    .setComponents(components).queue();
        }

        else if (event.getComponentId().equalsIgnoreCase("togglequeueloop")) {
            trackScheduler.toggleQueueLoop(trackScheduler.getQueue(true));
            AudioTrack track = player.getPlayingTrack();

            String durationString = trackScheduler.formatProgress(track);

            AudioTrack nextSong = trackScheduler.nextElement();

            Button replayButton = Button.primary("REPLAY | ID: " + player.getPlayingTrack().getInfo().identifier, Emoji.fromUnicode("U+23EE"));
            Button stopButton = Button.primary("STOP", Emoji.fromUnicode("U+23F9"));
            Button pauseActionButton = Button.primary("TOGGLEPAUSE", trackScheduler.getPausedStatus() ? Emoji.fromUnicode("U+23F8") : Emoji.fromUnicode("U+25B6"));
            Button skipButton;
            Button removeButton = Button.danger("REMOVE | AUTHOR: " + event.getInteraction().getUser().getName(), Emoji.fromUnicode("U+1F5D1"));
            if(trackScheduler.getQueue(false).size() > 0) {
                skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asEnabled();
            } else {
                skipButton = Button.primary("SKIP", Emoji.fromUnicode("U+23E9")).asDisabled();
            }
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(new Color(202, 23, 255))
                    .setAuthor("JMP")
                    .setTimestamp(Instant.now())
                    .setThumbnail(client.getSelfUser().getEffectiveAvatarUrl())
                    .addField(player.getPlayingTrack().getInfo().title, durationString, false)
                    .setFooter((trackScheduler.isQueueLooped ? "KOLEJKA ZAPĘTLONA [" + trackScheduler.queueToLoop.size() + " elem.]" : "") + (trackScheduler.isLooped ?  ( trackScheduler.isQueueLooped ? "  |  " : "") + "UTWÓR ZAPĘTLONY" : ""));

            if (nextSong != null) {
                eb.addField("Następne", "`" + nextSong.getInfo().title + "`", false);
            } else {
                eb.addField("Następne", "`Brak`", false);
            }

            Button loopControl = Button.primary("TOGGLELOOP", Emoji.fromUnicode(trackScheduler.isLooped ? "U+27A1" : "U+1F502"));
            Button queueLoopControl = Button.primary("TOGGLEQUEUELOOP", trackScheduler.isQueueLooped ? "DLQ" : "LQ");
            Button shuffleQueueButton = Button.primary("SHUFFLE", Emoji.fromUnicode("U+1F500"));

            Collection<LayoutComponent> components = new ArrayList<>();
            components.add(ActionRow.of(replayButton, stopButton, pauseActionButton, skipButton, removeButton));
            components.add(ActionRow.of(loopControl, shuffleQueueButton, queueLoopControl));
            event.getInteraction().editMessageEmbeds(eb.build())
                    .setComponents(components).queue();
        }

        else if(event.getComponentId().startsWith("JUMP")) {
            // replace the queue -- jump
            String[] data = event.getComponentId().split(" ");
            int pos = Integer.parseInt(data[1].trim());

            AudioTrack[] oldQueue = trackScheduler.getQueue(false).toArray(new AudioTrack[0]);

            Collection<AudioTrack> newQueue = new ArrayList<>(List.of(Arrays.copyOfRange(oldQueue, pos - 1, oldQueue.length)));

            trackScheduler.replaceQueue(newQueue, true);

            //now update the embed
            int amt = 5;
            if(amt > trackScheduler.getQueue(false).size()) {
                amt = trackScheduler.getQueue(false).size();
            }
            Collection<AudioTrack> currentQueue = trackScheduler.getQueue(amt);
            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor("JMP")
                    .setTimestamp(Instant.now())
                    .setThumbnail(client.getSelfUser().getEffectiveAvatarUrl())
                    .setColor(new Color(66, 135, 245))
                    .setFooter("Wyświetlanie " + currentQueue.size() + " z " + trackScheduler.getQueue(false).size() + " elementów." + (trackScheduler.isQueueLooped ? "  |  KOLEJKA ZAPĘTLONA [" + trackScheduler.queueToLoop.size() + " elem.]" : "") + (trackScheduler.isLooped ? "  |  UTWÓR ZAPĘTLONY" : ""));

            Collection<Button> jumpButtons = new ArrayList<>();
            Collection<Button> rmButtons = new ArrayList<>();
            for (int i = 0; i < amt; i++) {
                Button jumpButton = Button.success("JUMP " + i, String.valueOf(i+1));
                Button removeAtButton = Button.danger("REMOVEAT " + i, String.valueOf(i+1));
                jumpButtons.add(jumpButton);
                rmButtons.add(removeAtButton);
            }
            Collection<LayoutComponent> components = new ArrayList<>();
            components.add(ActionRow.of(jumpButtons));
            components.add(ActionRow.of(rmButtons));
            if(player.getPlayingTrack() != null) {
                eb.setTitle("Teraz");
                eb.addField(player.getPlayingTrack().getInfo().title, player.getPlayingTrack().getInfo().author + " `[" + trackScheduler.formatProgress(player.getPlayingTrack()) + "]`", false);
                eb.addBlankField(false);
            }
            if(amt == 0) {
                eb.addField("Kolejka jest pusta!", "Dodaj coś za pomocą `/search`, `/play` lub `/sp`.", false);
            } else {
                eb.addField("Kolejka", "-----------------------------------------", false);
                for (int i = 0; i < amt; i++) {
                    AudioTrack e = currentQueue.stream().toList().get(i);
                    eb.addField("`[" + (i + 1) + "]` " + e.getInfo().title, e.getInfo().author + " `[" + trackScheduler.formatDuration(e) + "]`", false);
                }
            }

            //and send it
            if(components.stream().toList().get(0).getComponents().size() > 0) {
                event.getInteraction().replyEmbeds(eb.build())
                        .setComponents(components)
                        .queue();
            } else {
                event.getInteraction().replyEmbeds(eb.build())
                        .queue();
            }
        }

        else if (event.getComponentId().startsWith("REMOVEAT")) {
            String[] data = event.getComponentId().split(" ");
            int pos = Integer.parseInt(data[1].trim());

            //remove the song
            List<AudioTrack> oldQ = trackScheduler.getQueue(false).stream().toList();

            if (pos < 0 || pos >= oldQ.size()) {
                event.getInteraction().reply("Nie znaleziono pozycji w kolejce o indeksie `" + pos + "`").queue();
                return;
            }
            List<AudioTrack> newQ = new ArrayList<>(oldQ);
            newQ.remove(pos);

            if(trackScheduler.isQueueLooped) {
                pos = trackScheduler.queueToLoop.size() - (oldQ.size() + pos);
                Collection<AudioTrack> oldQueueToLoop = trackScheduler.queueToLoop;
                List<AudioTrack> newQueueToLoop = new ArrayList<>(oldQueueToLoop);
                newQueueToLoop.remove(pos);
                trackScheduler.queueToLoop = newQueueToLoop;
            }

            //make the new embed
            int amt = 5;
            if(amt > trackScheduler.getQueue(false).size()) {
                amt = trackScheduler.getQueue(false).size();
            }
            Collection<AudioTrack> currentQueue = trackScheduler.getQueue(amt);
            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor("JMP")
                    .setTimestamp(Instant.now())
                    .setThumbnail(client.getSelfUser().getEffectiveAvatarUrl())
                    .setColor(new Color(66, 135, 245))
                    .setFooter("Wyświetlanie " + currentQueue.size() + " z " + trackScheduler.getQueue(false).size() + " elementów." + (trackScheduler.isQueueLooped ? "  |  KOLEJKA ZAPĘTLONA [" + trackScheduler.queueToLoop.size() + " elem.]" : "") + (trackScheduler.isLooped ? "  |  UTWÓR ZAPĘTLONY" : ""));

            Collection<Button> jumpButtons = new ArrayList<>();
            Collection<Button> rmButtons = new ArrayList<>();
            for (int i = 0; i < amt; i++) {
                Button jumpButton = Button.success("JUMP " + i, String.valueOf(i+1));
                Button removeAtButton = Button.danger("REMOVEAT " + i, String.valueOf(i+1));
                jumpButtons.add(jumpButton);
                rmButtons.add(removeAtButton);
            }
            Collection<LayoutComponent> components = new ArrayList<>();
            components.add(ActionRow.of(jumpButtons));
            components.add(ActionRow.of(rmButtons));
            if(player.getPlayingTrack() != null) {
                eb.setTitle("Teraz");
                eb.addField(player.getPlayingTrack().getInfo().title, player.getPlayingTrack().getInfo().author + " `[" + trackScheduler.formatProgress(player.getPlayingTrack()) + "]`", false);
                eb.addBlankField(false);
            }
            if(amt == 0) {
                eb.addField("Kolejka jest pusta!", "Dodaj coś za pomocą `/search`, `/play` lub `/sp`.", false);
            } else {
                eb.addField("Kolejka", "-----------------------------------------", false);
                for (int i = 0; i < amt; i++) {
                    AudioTrack e = currentQueue.stream().toList().get(i);
                    eb.addField("`[" + (i + 1) + "]` " + e.getInfo().title, e.getInfo().author + " `[" + trackScheduler.formatDuration(e) + "]`", false);
                }
            }

            //and now send it
            event.getInteraction().editMessageEmbeds(eb.build())
                    .setComponents(components)
                    .queue();
        }
    }
}
