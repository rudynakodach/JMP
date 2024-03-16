package io.github.rudynakodach.Utils.Events;

import io.github.rudynakodach.Utils.Audio.TrackScheduler;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildLeave extends ListenerAdapter {

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        TrackScheduler.get(event.getGuild()).destroy();
    }
}
