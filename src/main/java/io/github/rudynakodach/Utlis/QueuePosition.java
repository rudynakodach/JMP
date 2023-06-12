package io.github.rudynakodach.Utlis;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;

public record QueuePosition(Member member, AudioTrack track) { }
