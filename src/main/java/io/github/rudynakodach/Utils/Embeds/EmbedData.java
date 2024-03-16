package io.github.rudynakodach.Utils.Embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;

import java.util.Collection;

public record EmbedData(EmbedBuilder embed, Collection<LayoutComponent> layout) { }
