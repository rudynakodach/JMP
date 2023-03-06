package io.github.rudynakodach;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static io.github.rudynakodach.Main.*;
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    public boolean isLooped = false;
    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        if(player.getPlayingTrack() != null) {
            queue.offer(track);
        } else {
            queue.offer(track);
            nextTrack();
        }
    }

    public void togglePaused() {
        player.setPaused(!player.isPaused());
    }

    public void toggleLoop() {
        isLooped = !isLooped;
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        if(queue.size() > 0) {
            AudioTrack nextTrack = queue.poll();
            latestChan.sendMessage("Zapodany bicior: `" + nextTrack.getInfo().title + "`").queue();
            player.playTrack(nextTrack);
        } else {
            latestChan.sendMessage("Koniec kolejki.").queue();
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(isLooped && endReason.mayStartNext) {
            player.playTrack(track.makeClone());
            return;
        }
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    public Collection<AudioTrack> getQueue(int amt) {
        if(amt > queue.size()) {
            amt = queue.size();
        }
        Collection<AudioTrack> e = new ArrayList<>();
        Object[] queueArr = queue.toArray();
        for (int i = 0; i < amt; i++) {
            e.add((AudioTrack)queueArr[i]);
        }
        return e;
    }
    public AudioTrack[] getQueue() {
        return queue.toArray(new AudioTrack[0]);
    }

    public void replaceQueue(Collection<AudioTrack> newQueue, boolean playNext) {
        queue.clear();
        queue.addAll(newQueue);
        if(playNext) {
            nextTrack();
        }
    }

    public @Nullable AudioTrack nextElement() {
        if(queue.size() > 0) {
            return queue.element();
        } else return null;
    }

    public void clearQueue() {
        queue.clear();
    }

    public String formatProgress(AudioTrack track) {
        long position = track.getPosition() / 1000;
        long duration = track.getInfo().length / 1000;
        String formattedPosition = String.format("%d:%02d", position / 60, position % 60);
        String formattedDuration = String.format("%d:%02d", duration / 60, duration % 60);
        return formattedPosition + "/" + formattedDuration;
    }
    public String formatDuration(AudioTrack e) {
        long duration = e.getInfo().length / 1000;
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
}