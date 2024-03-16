package io.github.rudynakodach.Utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public record QueuePosition(@NotNull Member member, @NotNull AudioTrack track) { }
