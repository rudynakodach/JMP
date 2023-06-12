package io.github.rudynakodach.Commands.Levelling;

import io.github.rudynakodach.Modules.Levelling.UserLevelContainer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static io.github.rudynakodach.Modules.Levelling.LevellingHandler.levelMap;

public class Level extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("level")) {
            if(event.getUser().isBot()) {return;}
            if(event.getMember() == null) {return;}

            if(!levelMap.containsKey(event.getMember().getIdLong())) {
                event.getInteraction().reply("Nie masz jeszcze poziomu noobie").queue();
                return;
            }
            UserLevelContainer levelContainer = levelMap.get(event.getMember().getIdLong());

            String builder = "`" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "`\t\t" +
                    "**" + levelContainer.getExperience() + "**" +
                    "/**" + levelContainer.getExperienceNeeded() + "**\n" +
                    levelContainer.getLevel() + " Lvl";

            event.getInteraction().reply(builder).queue();

        }
    }
}
