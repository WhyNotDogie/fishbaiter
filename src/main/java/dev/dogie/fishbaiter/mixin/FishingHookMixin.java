package dev.dogie.fishbaiter.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.logging.LogUtils;
import dev.dogie.fishbaiter.FishDataComponents;
import dev.dogie.fishbaiter.data.BaitComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import dev.dogie.fishbaiter.Fishbaiter;

import javax.annotation.Nullable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {

    @Shadow @Nullable public abstract Player getPlayerOwner();

    @WrapOperation(method = "retrieve", at= @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"))
    public LootTable getLootTable(ReloadableServerRegistries.Holder instance, ResourceKey<LootTable> originalTable, Operation<LootTable> original) {
        Player player = this.getPlayerOwner();
        if (player == null) {
            return original.call(instance, originalTable);
        }
        // shitty ass code but if it works dont touch it
        if (Fishbaiter.hasBait(player)) {
            ItemStack bait = Fishbaiter.getBait(player);
            assert bait != null;
            BaitComponent baitComponent = bait.get(FishDataComponents.BAIT_COMPONENT);
            assert baitComponent != null;
            String lt = baitComponent.loot_table();
            return original.call(instance, ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(lt)));
        }
        // return it cause it doesnt matter its gonna get cancelled anyways
        return original.call(instance, originalTable);
    }
}
