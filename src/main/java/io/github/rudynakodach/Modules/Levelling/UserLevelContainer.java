package io.github.rudynakodach.Modules.Levelling;

public class UserLevelContainer {
    private int level;
    private int experience;
    private int experienceNeeded;
    private long lastTimeMessageSent;

    public UserLevelContainer() {
        experience = 0;
        level = 0;
        lastTimeMessageSent = System.currentTimeMillis();
        experience = calculateExperienceNeeded();
    }

    private int calculateExperienceNeeded() {
        return (level+1)^2*100;
    }

    public void addExperience(long amt) {
        System.out.println("Previous exp: " + this.experience);
        System.out.println("exp needed: " + this.experienceNeeded);
        this.experience += amt;
        if(this.experience >= this.experienceNeeded) {
            this.experience -= experienceNeeded;
            this.level += 1;
            this.experienceNeeded = calculateExperienceNeeded();
        }
    }

    public boolean canEarnExp() {
        return  (this.lastTimeMessageSent+5000) - System.currentTimeMillis() > 0;
    }

    public void setLastTimeMessageSent(long t) {
        this.lastTimeMessageSent = t;
    }

    public long getLevel() {
        return this.level;
    }

    public long getExperience() {
        return this.experience;
    }

    public long getExperienceNeeded() {
        return  this.experienceNeeded;
    }
}
