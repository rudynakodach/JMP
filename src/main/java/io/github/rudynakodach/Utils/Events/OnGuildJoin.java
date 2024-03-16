package io.github.rudynakodach.Utils.Events;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildJoin extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        new TrackScheduler(event.getGuild());
        System.out.printf("Joined guild %s and set a TrackScheduler up.", event.getGuild().getId());
    }
}
