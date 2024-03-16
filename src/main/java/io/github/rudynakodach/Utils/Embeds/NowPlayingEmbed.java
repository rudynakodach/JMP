package io.github.rudynakodach.Utils.Embeds;

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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

import static io.github.rudynakodach.Main.*;

public class NowPlayingEmbed extends ListenerAdapter {

    public static EmbedData getEmbed(TrackScheduler scheduler) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Color(202, 23, 255))
                .setAuthor("JMP")
                .setTimestamp(Instant.now())
                .setThumbnail(CLIENT.getSelfUser().getEffectiveAvatarUrl())
                .setFooter((scheduler.isQueueLooped ? "KOLEJKA ZAPĘTLONA [" + scheduler.queue.getQueue(true).size() + " elem.]" : "") + (scheduler.isLooped ?  ( scheduler.isQueueLooped ? "  |  " : "") + "UTWÓR ZAPĘTLONY" : ""));

        eb.addField("Teraz grane", "", false);
        if(scheduler.queue.getQueue(true).size() > 0) {
            QueuePosition current = scheduler.queue.current;
            eb.addField("%s".formatted(current.track().getInfo().title), "%s `[%s/%s]`\nDodane przez <@%s>".formatted(current.track().getInfo().author, TrackScheduler.formatDuration(current.track().getPosition()), TrackScheduler.formatDuration(current.track().getDuration()), current.member().getId()), false);
        } else {
            eb.addField("Nic!", "Dodaj utwór za pomocą /play.", false);
        }

        Collection<LayoutComponent> layout = new ArrayList<>();
        layout.add(ActionRow.of(
                //replay button
                Button.primary("np replay", Emoji.fromUnicode("U+23EA")),
                //stop button
                Button.primary("np stop", Emoji.fromUnicode("U+23F9")),
                //toggle-pause button
                Button.primary("np togglepause", Emoji.fromUnicode("U+23EF")),
                //skip button
                Button.primary("np skip", Emoji.fromUnicode("U+23ED")),
                //remove button
                Button.danger("np remove", Emoji.fromUnicode("U+1F5D1"))
        ));
        layout.add(ActionRow.of(
                //toggle-loop button
                Button.secondary("np toggleloop", Emoji.fromUnicode("U+1F502")),
                //toggle-queue-loop button
                Button.secondary("np togglequeueloop", Emoji.fromUnicode("U+1F501"))
        ));

        return new EmbedData(eb, layout);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(event.getButton().getId() == null || event.getGuild() == null || event.getMember() == null) {
            return;
        } else if(!event.getButton().getId().startsWith("np")) {
            return;
        }

        TrackScheduler scheduler = TrackScheduler.get(event.getGuild());
        switch (event.getButton().getId().split(" ")[1]) {
            case "replay" -> scheduler.player.playTrack(scheduler.player.getPlayingTrack().makeClone());
            case "stop" -> scheduler.stop();
            case "togglepause" -> scheduler.player.setPaused(!scheduler.player.isPaused());
            case "skip" -> scheduler.skip();
            case "togglequeueloop" -> scheduler.toggleQueueLoop();
            case "toggleloop" -> scheduler.isLooped = !scheduler.isLooped;
            case "remove" -> {
                if(event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    event.getMessage().delete().queue();
                }
            }
        }
        EmbedData ed = getEmbed(scheduler);
        event.editMessageEmbeds(ed.embed().build()).setComponents(ed.layout()).queue();
    }
}
