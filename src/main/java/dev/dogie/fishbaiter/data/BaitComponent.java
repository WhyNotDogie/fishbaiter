package dev.dogie.fishbaiter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.Optional;
import java.util.function.Consumer;

public record BaitComponent(String loot_table, Optional<Integer> tooltip_color, Optional<String> rarity) implements TooltipProvider {
    public static final Codec<BaitComponent> BAIT_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("loot_table").forGetter(BaitComponent::loot_table),
                    Codec.INT.optionalFieldOf("tooltip_color").forGetter(BaitComponent::tooltip_color),
                    Codec.STRING.optionalFieldOf("rarity").forGetter(BaitComponent::rarity)
            ).apply(instance, BaitComponent::new)
    );
    public static final StreamCodec<ByteBuf, BaitComponent> BAIT_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, BaitComponent::loot_table,
            ByteBufCodecs.optional(ByteBufCodecs.INT), BaitComponent::tooltip_color,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), BaitComponent::rarity,
            BaitComponent::new
    );

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> componentConsumer, TooltipFlag flag) {
        String s = "Fishing Bait";
        if (this.rarity.isPresent()) {
            s = this.rarity.get() + " " + s;
        }
        int c = (100 << 16) | (100 << 8) | 100;
        if (this.tooltip_color.isPresent()) {
            c = this.tooltip_color.get();
        }
        componentConsumer.accept(Component.literal(s).withColor(c));
    }
}