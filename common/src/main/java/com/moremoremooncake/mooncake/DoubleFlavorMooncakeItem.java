package com.moremoremooncake.mooncake;

import com.moremooncake.mooncake.item.MooncakeFood;
import com.moremooncake.mooncake.mooncake.MooncakeState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import com.moremoremooncake.registry.Registry;

import java.util.List;

/**
 * An addon double-filling mooncake slice, in the independent {@code moremoremooncake} mod.
 * It carries two fillings and applies both effects at once (the "sum" of the two).
 * <p>
 * It implements the base mod's {@link MooncakeFood} extension point, so it flows through the
 * base grand-mooncake assembly, the placed pie's eat behaviour, the block entity renderer tint
 * and the scrape/rust recipe exactly like a base slice - but is a genuinely separate mod whose
 * items live in the {@code moremoremooncake} namespace.
 */
public class DoubleFlavorMooncakeItem extends Item implements MooncakeFood {
    private final DoubleFlavor flavor;
    private final MooncakeState state;

    public DoubleFlavorMooncakeItem(DoubleFlavor flavor, MooncakeState state) {
        super(createProperties(flavor, state));
        this.flavor = flavor;
        this.state = state;
    }

    private static Item.Properties createProperties(DoubleFlavor flavor, MooncakeState state) {
        FoodProperties food = new FoodProperties.Builder()
                .nutrition(1)
                .saturationModifier(0.15F)
                .alwaysEdible()
                .build();
        Consumable consumable = Consumable.builder()
                .consumeSeconds(1.6F)
                .animation(ItemUseAnimation.EAT)
                .sound(SoundEvents.GENERIC_EAT)
                .hasConsumeParticles(true)
                .onConsume(new ApplyStatusEffectsConsumeEffect(AddonEffects.effectsFor(flavor.effects(), state)))
                .build();
        return new Item.Properties().food(food, consumable);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                net.minecraft.world.item.component.TooltipDisplay tooltipDisplay,
                                java.util.function.Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.accept(Component.translatable("tooltip.more_mooncake.festival").withStyle(ChatFormatting.GOLD));
        tooltip.accept(Component.literal(flavor.first().getZhName() + " + " + flavor.second().getZhName())
                .withStyle(ChatFormatting.GOLD));
        tooltip.accept(Component.translatable("tooltip.more_mooncake.slice_hint").withStyle(ChatFormatting.DARK_GRAY));
        if (state.isWaxed()) {
            tooltip.accept(Component.translatable("tooltip.more_mooncake.waxed").withStyle(ChatFormatting.GRAY));
        }
        if (state.isOxidized() && !state.isWaxed()) {
            tooltip.accept(Component.translatable("tooltip.more_mooncake.oxidized_side_effect").withStyle(ChatFormatting.DARK_RED));
        }
    }

    public DoubleFlavor getFlavor() {
        return flavor;
    }

    @Override
    public MooncakeState mooncakeState() {
        return state;
    }

    @Override
    public List<MobEffectInstance> effectsFor(MooncakeState ignore) {
        return AddonEffects.effectsFor(flavor.effects(), state);
    }

    @Override
    public int[] fillingColor() {
        return flavor.blendColor();
    }

    @Override
    public ItemStack scrapedTo(MooncakeState target) {
        return new ItemStack(Registry.getItem(flavor, target));
    }
}