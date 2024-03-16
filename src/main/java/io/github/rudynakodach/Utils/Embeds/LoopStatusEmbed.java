package io.github.rudynakodach.Utils.Embeds;

import io.github.rudynakodach.Main;
import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import io.github.rudynakodach.Utils.QueuePosition;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.Instant;

public class LoopStatusEmbed {
    public static EmbedBuilder getEmbed(boolean loopStatus, TrackScheduler scheduler) {
        EmbedBuilder eb = new EmbedBuilder()
            .setAuthor("JMP", Main.CLIENT.getSelfUser().getEffectiveAvatarUrl())
            .setColor(loopStatus ? new Color(25, 255,  0) : new Color(255, 25,  0))
            .setTimestamp(Instant.now())
            .setTitle("Pętla")
            .setDescription("Pętla w%słączona!".formatted(loopStatus ? "" : "y"));

        QueuePosition current = scheduler.queue.current;
        if(current != null)
            eb.addField("%s".formatted(current.track().getInfo().title), "%s `[%s/%s]`\n*Dodane przez <@%s>*".formatted(current.track().getInfo().author, TrackScheduler.formatDuration(current.track().getPosition()), TrackScheduler.formatDuration(current.track().getDuration()), current.member().getId()), false);
        return eb;
    }
}
