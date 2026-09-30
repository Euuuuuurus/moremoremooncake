package com.moremoremooncake.registry;

import com.moremoremooncake.Moremoremooncake;
import com.moremoremooncake.mooncake.DoubleFlavor;
import com.moremoremooncake.mooncake.DoubleFlavorMooncakeItem;
import com.moremooncake.mooncake.mooncake.MooncakeState;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Registers all 528 addon double-filling mooncake slice items (66 combinations x 8 states) in the
 * {@code moremoremooncake} namespace, plus a dedicated creative tab. Every slice is a
 * {@code MooncakeFood} that the base grand-mooncake pipeline understands.
 */
public final class Registry {
    private static final String MOD = Moremoremooncake.MOD_ID;

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(MOD, Registries.ITEM);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(MOD, Registries.CREATIVE_MODE_TAB);

    private static final List<RegistrySupplier<Item>> ALL_ITEMS = new ArrayList<>();
    private static final RegistrySupplier<Item>[][] ITEM_GRID;

    static {
        List<DoubleFlavor> combos = DoubleFlavor.ALL;
        MooncakeState[] states = MooncakeState.values();

        @SuppressWarnings("unchecked")
        RegistrySupplier<Item>[][] grid = new RegistrySupplier[combos.size()][states.length];

        for (int c = 0; c < combos.size(); c++) {
            for (int s = 0; s < states.length; s++) {
                final int ci = c;
                final int si = s;
                RegistrySupplier<Item> sup = register(itemId(combos.get(c), states[s]),
                        () -> new DoubleFlavorMooncakeItem(combos.get(ci), states[si]));
                grid[c][s] = sup;
                ALL_ITEMS.add(sup);
            }
        }
        ITEM_GRID = grid;

        TABS.register("more_mooncake_double_tab", () -> CreativeTabRegistry.create(builder -> {
            builder.title(Component.translatable("itemGroup.moremoremooncake"));
            builder.icon(() -> new ItemStack(ALL_ITEMS.get(0).get()));
            builder.displayItems((params, output) -> {
                for (RegistrySupplier<Item> item : ALL_ITEMS) {
                    output.accept(new ItemStack(item.get()));
                }
            });
        }));
    }

    private Registry() {
    }

    public static void register() {
        TABS.register();
        ITEMS.register();
    }

    /** The slice item for a combination x state pair. */
    public static Item getItem(DoubleFlavor combo, MooncakeState state) {
        return ITEM_GRID[comboIndex(combo)][state.ordinal()].get();
    }

    private static int comboIndex(DoubleFlavor combo) {
        return DoubleFlavor.ALL.indexOf(combo);
    }

    public static String itemId(DoubleFlavor combo, MooncakeState state) {
        String suffix = state.getSuffix();
        return "mooncake_" + combo.registryName() + (suffix.isEmpty() ? "" : "_" + suffix);
    }

    public static ItemStack byId(String id) {
        ResourceLocation loc = id.contains(":") ? ResourceLocation.parse(id)
                : ResourceLocation.fromNamespaceAndPath(MOD, id);
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(loc)
                .map(ItemStack::new)
                .orElse(ItemStack.EMPTY);
    }

    private static RegistrySupplier<Item> register(String name, Supplier<Item> supplier) {
        return ITEMS.register(name, supplier);
    }
}