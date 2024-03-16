package io.github.rudynakodach.Utils.Embeds;

import io.github.rudynakodach.Main;
import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import io.github.rudynakodach.Utils.QueuePosition;
import net.dv8tion.jda.api.EmbedBuilder;
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

public class QueueEmbed extends ListenerAdapter {

    public static EmbedData getEmbed(List<QueuePosition> tracks, int page) {
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor("JMP", Main.CLIENT.getSelfUser().getEffectiveAvatarUrl())
                .setTitle("Kolejka")
                .setColor(new Color(66, 135, 245))
                .setFooter("Strona %s".formatted(page+1))
                .setTimestamp(Instant.now());

        eb.addField("Teraz", "", false);
        QueuePosition now = tracks.get(0);
        eb.addField("%s".formatted(now.track().getInfo().title),"%s `[%s/%s]`\nDodane przez <@%s>".formatted(now.track().getInfo().author, TrackScheduler.formatDuration(now.track().getPosition()), TrackScheduler.formatDuration(now.track().getDuration()), now.member().getId()), false);
        eb.addBlankField(false);
        Collection<Button> jumpButtons = new ArrayList<>();
        Collection<Button> removeButtons = new ArrayList<>();
        for (int i = 1 + 5 * page; i < Math.min(1 + 5 * (page + 1), tracks.size()); i++) {
            QueuePosition pos = tracks.get(i);
            jumpButtons.add(Button.success("queue jump %s".formatted(i-1), "%s".formatted(i)));
            removeButtons.add(Button.danger("queue remove %s".formatted(i-1), "%s".formatted(i)));
            eb.addField("`[%s]` %s".formatted(i, pos.track().getInfo().title),"%s `[%s]`\nDodane przez <@%s>".formatted(pos.track().getInfo().author, TrackScheduler.formatDuration(pos.track().getDuration()), pos.member().getId()), false);
        }
        Collection<LayoutComponent> components = new ArrayList<>();
        if(!jumpButtons.isEmpty()) {
            components.add(ActionRow.of(jumpButtons));
        }
        if(!removeButtons.isEmpty()) {
            components.add(ActionRow.of(removeButtons));
        }
        Button back = Button.primary("queue page %s".formatted(page-1), Emoji.fromUnicode("U+2B05")).asDisabled();
        if(page > 0) {
            back = back.asEnabled();
        }
        Button forward = Button.primary("queue page %s".formatted(page+1), Emoji.fromUnicode("U+27A1"));
        if(tracks.size() < 1 + 5 * (page + 1)) {
            forward = forward.asEnabled();
        }
        components.add(ActionRow.of(back, forward));
        return new EmbedData(eb, components);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(!Objects.requireNonNull(event.getButton().getId()).startsWith("queue")) {
            return;
        } else if(event.getMember() == null || event.getGuild() == null) {
            return;
        }
        int currentPage = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(event.getMessage().getEmbeds().get(0).getFooter()).getText()).split(" ")[1]) - 1; //the footer contains the page number, one-indexed
        String[] data = event.getButton().getId().split(" ");
        switch(data[1].toLowerCase()) {
            case "jump" -> {
                int idx = Integer.parseInt(data[2]);
                TrackScheduler.get(event.getGuild()).jump(idx);
            }
            case "remove" -> {
                int idx = Integer.parseInt(data[2]);
                TrackScheduler.get(event.getGuild()).removeAt(idx);
            }
            case "page" -> {
                int page = Integer.parseInt(data[2]);
                EmbedData ed = getEmbed(TrackScheduler.get(event.getGuild()).queue.getQueue(true), page);
                event.editMessageEmbeds(ed.embed().build()).setComponents(ed.layout()).queue();
                return;
            }
        }
        EmbedData ed = getEmbed(TrackScheduler.get(event.getGuild()).queue.getQueue(true), currentPage);
        event.editMessageEmbeds(ed.embed().build()).setComponents(ed.layout()).queue();
    }
}

