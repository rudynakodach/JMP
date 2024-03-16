package io.github.rudynakodach.Utils.Events;

import io.github.rudynakodach.Main;
import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnStart extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        for (Guild g : Main.CLIENT.getGuilds()) {
            String guildId = g.getId();
            new TrackScheduler(g);
            System.out.printf("Created a track scheduler for guild %s%n", guildId);
        }
    }
}
