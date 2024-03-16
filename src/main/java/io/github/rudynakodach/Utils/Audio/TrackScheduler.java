package io.github.rudynakodach.Utils.Audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import io.github.rudynakodach.Main;
import io.github.rudynakodach.Utils.TrackQueue;
import io.github.rudynakodach.Utils.QueuePosition;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.*;

import static java.util.List.of;

public class TrackScheduler extends AudioEventAdapter {
    /**
     * HashMap containing all track schedulers for this application, where {@code String} is the Guild ID.
     * Purpose: supporting multiple servers at once.
     */
    public static HashMap<String, TrackScheduler> SCHEDULERS = new HashMap<>();
    public static TrackScheduler get(Guild guild) {
        return SCHEDULERS.get(guild.getId());
    }
    public boolean isLooped = false;
    public boolean isQueueLooped = false;
    public final TrackQueue<QueuePosition> queue;
    public TextChannel channel;
    public final AudioPlayer player = Main.AUDIO_PLAYER_MANAGER.createPlayer();
    private AudioPlayerSendHandler audioHandler;

    public TrackScheduler(Guild guild) {
        SCHEDULERS.put(guild.getId(), this);

        player.addListener(this);
        queue = new TrackQueue<>();
        this.audioHandler = new AudioPlayerSendHandler(player);
        guild.getAudioManager().setSendingHandler(audioHandler);
    }


    public void addToQueue(List<QueuePosition> tracks) {
        tracks = tracks.stream()
                .map(e -> new QueuePosition(e.member(), e.track().makeClone())) //avoid adding duplicate tracks to the queue
                .toList();
        queue.addAll(tracks);
        if(player.getPlayingTrack() == null) {
            nextTrack(true);
        }
    }

    private void nextTrack(boolean suppressMessage) {
        if(queue.getQueue(false).size() > 0) {
            QueuePosition next = queue.poll();
            AudioTrack track = next.track();

            player.playTrack(track);
            if(isQueueLooped) {
                if(queue.current != null) {
                    addToQueue(of(new QueuePosition(queue.current.member(), queue.current.track().makeClone())));
                }
            }
            if(!suppressMessage) {
                channel.sendMessage("Zapodany bicior: `%s`".formatted(track.getInfo().title)).queue();
            }
        } else {
            channel.sendMessage("Koniec kolejki.").queue();
        }
    }

    private int failedLoadAttempts = 0;
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        if(reason == AudioTrackEndReason.LOAD_FAILED && failedLoadAttempts <= 3) {
            failedLoadAttempts++;
            System.out.printf("Failed to load track in guild %s. Attempt %s%n", channel.getGuild().getId(), failedLoadAttempts);
            //add the position once again
            QueuePosition pos = queue.current;
            addToQueue(of(pos));
        } else if (reason.mayStartNext) {
            failedLoadAttempts = 0; //clear it if everything was loaded correctly

            if(isLooped) {
                QueuePosition pos = new QueuePosition(queue.current.member(), queue.current.track().makeClone());
                List<QueuePosition> newQueue = new ArrayList<>(List.of(pos));
                newQueue.addAll(queue.getQueue(false));

                queue.clear();
                queue.addAll(newQueue);
                nextTrack(true);
                return;
            }
            nextTrack(false);
        }
    }

    public void toggleQueueLoop() {
        isQueueLooped = !isQueueLooped;
    }

    /**
     * Removes the QueuePosition from TrackQueue at specified index.
     * @param idx - the index to remove the QueuePosition at
     * @return the removed QueuePosition
     */
    public QueuePosition removeAt(int idx) {
        List<QueuePosition> queue = this.queue.getQueue(false);
        QueuePosition removed = queue.get(idx);
        queue.remove(idx);
        this.queue.clear();
        addToQueue(queue);
        return removed;
    }

    public void stop() {
        player.stopTrack();
        queue.clear();
    }

//    TODO: jumping over skipped tracks will add it to the queue if isQueueLooped is true
    public void jump(int idx) {
        List<QueuePosition> queue = this.queue.getQueue(true);
        List<QueuePosition> newQueue = queue.subList(idx + 1, queue.size());
        this.queue.clear();
        addToQueue(newQueue);
        nextTrack(false);
    }

    public static String formatDuration(long length) {
        long duration = length / 1000;
        long hours = duration / 3600;
        long minutes = (duration % 3600) / 60;
        long seconds = duration % 60;

        String formattedDuration = "";
        if (hours > 0) {
            formattedDuration += String.format("%d:", hours);
        }

        formattedDuration += String.format("%02d:%02d", minutes, seconds);

        return formattedDuration;
    }

    public void destroy() {
        player.destroy();
        audioHandler = null;
        SCHEDULERS.remove(channel.getGuild().getId());
    }

    public void skip() {
        nextTrack(false);
    }
}
