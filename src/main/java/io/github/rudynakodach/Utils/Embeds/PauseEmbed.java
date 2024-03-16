package io.github.rudynakodach.Utils.Embeds;

import io.github.rudynakodach.Main;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;

public class PauseEmbed {
    public static EmbedBuilder getEmbed(boolean isPaused) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTimestamp(Instant.now());
        eb.setAuthor("JMP");
        eb.setThumbnail(Main.CLIENT.getSelfUser().getEffectiveAvatarUrl());

        eb.setTitle("Pauza");
        eb.setDescription("%spauzowano %s!".formatted(isPaused ? "Za" : "Od", isPaused ? ":pause_button:" : ":arrow_forward:"));

        return eb;
    }
}
