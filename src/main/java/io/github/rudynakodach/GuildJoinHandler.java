package io.github.rudynakodach;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static io.github.rudynakodach.Main.*;

public class GuildJoinHandler extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        System.out.println("Client joined server \"" + event.getGuild().getName() + "\" of ID " + event.getGuild().getId() + ".");
        audioHandlerSetMap.put(event.getGuild().getId(), false);
    }
}
