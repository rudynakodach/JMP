package io.github.rudynakodach.Modules.Levelling;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class LevellingHandler extends ListenerAdapter {

    public static HashMap<Long, UserLevelContainer> levelMap = new HashMap<>();
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if(e.getMember() == null) {
            System.out.println("Private message received: " + e.getMessage().getContentRaw() + "\nFrom: " + e.getAuthor());
            return;
        }

        long userId = Objects.requireNonNull(e.getMember()).getIdLong();

        if(!levelMap.containsKey(userId)) {
            levelMap.put(userId, new UserLevelContainer());
        } else {
            UserLevelContainer container = levelMap.get(userId);
            if(container.canEarnExp()) {
                System.out.println("Adding experience.");
                int min = 5;
                int max = 15;

                Random random = new Random();
                int randomNumber = random.nextInt(Math.max(max - min, 1)) + min;

                container.addExperience(randomNumber);
                container.setLastTimeMessageSent(System.currentTimeMillis());
                System.out.println("Adding " + randomNumber + " experience to user " + e.getAuthor().getAsMention());
            }
        }
    }
}
