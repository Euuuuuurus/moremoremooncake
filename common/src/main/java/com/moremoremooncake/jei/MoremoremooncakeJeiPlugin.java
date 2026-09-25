package com.moremoremooncake.jei;

import com.moremooncake.mooncake.item.WholeMooncakeItem;
import com.moremooncake.mooncake.jei.MooncakeJeiPlugin;
import com.moremooncake.mooncake.jei.MooncakeJeiPlugin.AssemblyJeiRecipe;
import com.moremooncake.mooncake.jei.MooncakeJeiPlugin.ScrapeJeiRecipe;
import com.moremooncake.mooncake.mooncake.MooncakeState;
import com.moremooncake.mooncake.recipe.MooncakeScrapeRecipe;
import com.moremoremooncake.Moremoremooncake;
import com.moremoremooncake.mooncake.AddonFlavor;
import com.moremoremooncake.mooncake.DoubleFlavor;
import com.moremoremooncake.registry.Registry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * JEI integration for the independent {@code moremoremooncake} addon.
 * <p>
 * Everything the addon adds is either a standard {@code crafting_shapeless} recipe (slice, wax,
 * oxidize) which JEI displays automatically, or flows through the base mod's custom recipes
 * (grand-mooncake assembly, axe scrape) which JEI does <b>not</b> discover on its own. This
 * plugin therefore:
 * <ul>
 *   <li>registers a dedicated "double filling" category listing all 66 two-filling combos,</li>
 *   <li>adds addon examples into the base mod's assembly and scrape JEI categories so the
 *       addon slices' survival lifecycle (assemble into a grand mooncake, scrape wax/rust off)
 *       is fully visible.</li>
 * </ul>
 * <p>
 * Lives in the common module and is packaged into both jars. JEI only scans {@code @JeiPlugin}
 * classes when JEI itself is installed, so without JEI nothing here is ever loaded (soft
 * dependency, exactly like the base mod).
 */
@JeiPlugin
public class MoremoremooncakeJeiPlugin implements IModPlugin {
    public static final RecipeType<DoubleFillingJeiRecipe> DOUBLE_FILLING =
            RecipeType.create(Moremoremooncake.MOD_ID, "double_filling", DoubleFillingJeiRecipe.class);

    /** Two fillings' representative ingredients + the resulting fresh double slice. */
    public record DoubleFillingJeiRecipe(ItemStack first, ItemStack second, ItemStack output) {
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Moremoremooncake.MOD_ID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new DoubleFillingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(DOUBLE_FILLING, buildDoubleFillingRecipes());
        // Addon slices also flow through the base grand-mooncake assembly and scrape recipes.
        registration.addRecipes(MooncakeJeiPlugin.ASSEMBLY, buildAssemblyExamples());
        registration.addRecipes(MooncakeJeiPlugin.SCRAPE, buildScrapeExamples());
    }

    /** All 66 combos: representative ingredient of filling A + filling B -> the double slice. */
    private static List<DoubleFillingJeiRecipe> buildDoubleFillingRecipes() {
        List<DoubleFillingJeiRecipe> out = new ArrayList<>();
        for (DoubleFlavor combo : DoubleFlavor.ALL) {
            out.add(new DoubleFillingJeiRecipe(
                    flavorIngredient(combo.first()),
                    flavorIngredient(combo.second()),
                    new ItemStack(Registry.getItem(combo, MooncakeState.NORMAL))));
        }
        return out;
    }

    /** A handful of representative grand assemblies using addon double slices. */
    private static List<AssemblyJeiRecipe> buildAssemblyExamples() {
        List<DoubleFlavor> combos = DoubleFlavor.ALL;
        List<AssemblyJeiRecipe> out = new ArrayList<>();

        // 1) eight different fresh double slices -> assorted grand mooncake
        List<String> fresh = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            fresh.add(Registry.itemId(combos.get(i), MooncakeState.NORMAL));
        }
        out.add(new AssemblyJeiRecipe(ringStacks(fresh), new ItemStack(Items.EGG),
                WholeMooncakeItem.withSlices(fresh)));

        // 2) mixed oxidation states: two of each tier, showing the state mixing
        MooncakeState[] states = {MooncakeState.NORMAL, MooncakeState.RUSTED,
                MooncakeState.WEATHERED, MooncakeState.OXIDIZED,
                MooncakeState.NORMAL, MooncakeState.RUSTED,
                MooncakeState.WEATHERED, MooncakeState.WEATHERED};
        List<String> mixed = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            mixed.add(Registry.itemId(combos.get(8 + i), states[i]));
        }
        out.add(new AssemblyJeiRecipe(ringStacks(mixed), new ItemStack(Items.EGG),
                WholeMooncakeItem.withSlices(mixed)));

        return out;
    }

    /** Representative scrapes covering wax removal and patina scraping on addon slices. */
    private static List<ScrapeJeiRecipe> buildScrapeExamples() {
        List<ScrapeJeiRecipe> out = new ArrayList<>();
        DoubleFlavor[] combos = {DoubleFlavor.ALL.get(0), DoubleFlavor.ALL.get(5),
                DoubleFlavor.ALL.get(12), DoubleFlavor.ALL.get(20), DoubleFlavor.ALL.get(30)};
        MooncakeState[] states = {MooncakeState.WAXED, MooncakeState.WAXED_OXIDIZED,
                MooncakeState.OXIDIZED, MooncakeState.WEATHERED, MooncakeState.RUSTED};
        for (int i = 0; i < states.length; i++) {
            ItemStack input = new ItemStack(Registry.getItem(combos[i], states[i]));
            ItemStack output = MooncakeScrapeRecipe.scraped(input);
            out.add(new ScrapeJeiRecipe(input, new ItemStack(Items.STONE_AXE), output));
        }
        return out;
    }

    private static List<ItemStack> ringStacks(List<String> ids) {
        List<ItemStack> out = new ArrayList<>();
        for (String id : ids) {
            out.add(Registry.byId(id));
        }
        return out;
    }

    /** The crafting ingredient that represents a single filling (mirrors the slice recipes). */
    private static ItemStack flavorIngredient(AddonFlavor flavor) {
        return switch (flavor) {
            case LIANRONG -> new ItemStack(Items.LILY_PAD);
            case DOUSHA -> new ItemStack(Items.COCOA_BEANS);
            case ZAONI -> new ItemStack(Items.SWEET_BERRIES);
            case WUREN -> new ItemStack(Items.PUMPKIN_SEEDS);
            case YERONG -> new ItemStack(Items.SUGAR);
            case BAIGUO -> new ItemStack(Items.APPLE);
            case HEIZHIMA -> new ItemStack(Items.INK_SAC);
            case BANLI -> new ItemStack(Items.BAKED_POTATO);
            case ZISHU -> new ItemStack(Items.BEETROOT);
            case YUNI -> new ItemStack(Items.CARROT);
            case SHUIGUO -> new ItemStack(Items.MELON_SLICE);
            case LVDouSha -> new ItemStack(Items.KELP);
        };
    }
}
