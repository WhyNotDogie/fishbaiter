package dev.dogie.fishbaiter;

import com.mojang.logging.LogUtils;
import dev.dogie.fishbaiter.data.BaitComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Fishbaiter.MODID)
public class Fishbaiter {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "fishbaiter";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Fishbaiter(IEventBus modEventBus, ModContainer modContainer) {
        // epic event
        NeoForge.EVENT_BUS.addListener(this::onFish);
        NeoForge.EVENT_BUS.addListener(this::onRightClickItem);
        NeoForge.EVENT_BUS.addListener(this::onToolTip);

        modEventBus.addListener(this::modifyComponents);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        FishDataComponents.REGISTRAR.register(modEventBus);
    }

    // thank you gigaherz for this better code then i originally had
    public static boolean hasBait(Player playerIn) {
        ItemStack ohstack = playerIn.getItemBySlot(EquipmentSlot.OFFHAND);
        if (ohstack.has(FishDataComponents.BAIT_COMPONENT))
        {
            return true;
        }
        Inventory inv = playerIn.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            ItemStack stack = inv.getItem(i);
            if (stack.has(FishDataComponents.BAIT_COMPONENT))
            {
                return true;
            }
        }
        return false;
    }
    public static ItemStack getBait(Player playerIn) {
        ItemStack ohstack = playerIn.getItemBySlot(EquipmentSlot.OFFHAND);
        if (ohstack.has(FishDataComponents.BAIT_COMPONENT))
        {
            return ohstack;
        }
        Inventory inv = playerIn.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            ItemStack stack = inv.getItem(i);
            if (stack.has(FishDataComponents.BAIT_COMPONENT))
            {
                return stack;
            }
        }
        return null;
    }

    // again thanks gigaherz
    public static void useBait(Player playerIn)
    {
        ItemStack ohstack = playerIn.getItemBySlot(EquipmentSlot.OFFHAND);
        if (ohstack.has(FishDataComponents.BAIT_COMPONENT))
        {
            ohstack.grow(-1);
            if (ohstack.getCount() <= 0)
                playerIn.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            return;
        }
        Inventory inv = playerIn.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            ItemStack stack = inv.getItem(i);
            if (stack.has(FishDataComponents.BAIT_COMPONENT))
            {
                stack.grow(-1);
                if (stack.getCount() <= 0)
                    inv.setItem(i, ItemStack.EMPTY);
                return;
            }
        }
    }

    private void displayBaitMessage(Player targetPlayer) {
        targetPlayer.displayClientMessage(Component.literal("You need bait to fish."), true);
    }

    //yeah

    // you used to call me on my cellphone
    private void onFish(ItemFishedEvent event) {
        // its a me
        // i undertsnad my own comments
        Player player = event.getEntity();

        if (player.getAbilities().instabuild) {
            return;
        }


        if (!hasBait(player)) {
            event.setCanceled(true);
            displayBaitMessage(player);
            return;
        }

        useBait(player);
        // one day if im feeling evil i'll uncomment this and publish a new version
//        level.explode(null, player.getX(), player.getY(), player.getZ(), 5, Level.ExplosionInteraction.MOB);

    }

    private void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();

        if (player.getAbilities().instabuild) {
            return;
        }

        InteractionHand hand = event.getHand();

        ItemStack held = player.getItemInHand(hand);

        if (!held.is(Items.FISHING_ROD)) return;
        if (hasBait(player)) {
            return;
        };

        displayBaitMessage(player);
        event.setCanceled(true);
    }

    public void modifyComponents(ModifyDefaultComponentsEvent event) {
        event.modify(Items.ROTTEN_FLESH, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/crap", Optional.empty(), Optional.of("Common")))
        );
        event.modify(Items.WHEAT_SEEDS, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/crap", Optional.empty(), Optional.of("Common")))
        );
        event.modify(Items.POISONOUS_POTATO, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/crap", Optional.empty(), Optional.of("Common")))
        );
        event.modify(Items.SWEET_BERRIES, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("minecraft:gameplay/fishing", Optional.of((105 << 16) | (185 << 8) | 125), Optional.of("Uncommon")))
        );
        event.modify(Items.SPIDER_EYE, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("minecraft:gameplay/fishing", Optional.of((105 << 16) | (185 << 8) | 125), Optional.of("Uncommon")))
        );
        event.modify(Items.EGG, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/egg", Optional.of((105 << 16) | (185 << 8) | 125), Optional.of("Uncommon")))
        );
        event.modify(Items.ENCHANTED_GOLDEN_APPLE, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/egap", Optional.of((250 << 16) | (25 << 8) | 150), Optional.of("Mythic")))
        );
        event.modify(Items.TROPICAL_FISH, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/tropical_fish", Optional.of((65 << 16) | (165 << 8) | 195), Optional.of("Rare")))
        );
        event.modify(Items.CHICKEN, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/meat", Optional.of((105 << 16) | (185 << 8) | 125), Optional.of("Uncommon")))
        );
        event.modify(Items.BEEF, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/meat", Optional.of((105 << 16) | (185 << 8) | 125), Optional.of("Uncommon")))
        );
        event.modify(Items.PORKCHOP, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/meat", Optional.of((105 << 16) | (185 << 8) | 125), Optional.of("Uncommon")))
        );
        event.modify(Items.MUTTON, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/meat", Optional.of((105 << 16) | (185 << 8) | 125), Optional.of("Uncommon")))
        );
        event.modify(Items.RABBIT, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/meat", Optional.of((105 << 16) | (185 << 8) | 125), Optional.of("Uncommon")))
        );
        event.modify(Items.GOLDEN_CARROT, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/epic_luckier", Optional.of((135 << 16) | (25 << 8) | 190), Optional.of("Epic")))
        );
        event.modify(Items.GOLDEN_APPLE, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/legendary_luckier", Optional.of((205 << 16) | (185 << 8) | 40), Optional.of("Legendary")))
        );
//        event.modify(Items.PRISMARINE_SHARD, builder ->
//                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/archeology", Optional.of((205 << 16) | (185 << 8) | 40), Optional.of("Legendary")))
//        );
//        event.modify(Items.NAUTILUS_SHELL, builder ->
//                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/archeology", Optional.of((205 << 16) | (185 << 8) | 40), Optional.of("Legendary")))
//        );
        event.modify(Items.CAKE, builder ->
                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/cake", Optional.of((135 << 16) | (25 << 8) | 190), Optional.of("Epic")))
        );
        // future expansion
//        event.modify(Items.ECHO_SHARD, builder ->
//                builder.set(FishDataComponents.BAIT_COMPONENT.get(), new BaitComponent("fishbaiter:fishing/echo_shard", Optional.of((205 << 16) | (185 << 8) | 40), Optional.of("Legendary")))
//        );
    }

    public void onToolTip(ItemTooltipEvent event) {
        ItemStack item = event.getItemStack();
        List<Component> componentList = event.getToolTip();
        Item.TooltipContext tooltipContext = event.getContext();
        TooltipFlag flag = event.getFlags();

        if (item.has(FishDataComponents.BAIT_COMPONENT)) {
            item.addToTooltip(FishDataComponents.BAIT_COMPONENT, tooltipContext, componentList::add, flag);
        }
    }
}
