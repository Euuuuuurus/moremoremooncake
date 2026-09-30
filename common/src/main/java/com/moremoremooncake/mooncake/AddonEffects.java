package com.moremoremooncake.mooncake;

import com.moremooncake.mooncake.mooncake.MooncakeState;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;
import java.util.List;

/**
 * Effect logic shared by every addon double-filling mooncake, mirroring the base mod's
 * {@code MooncakeEffects}: each of the two fillings' effects applies at level = tier + 1,
 * duration = 15 s + 5 s per tier; waxed doubles the duration; oxidized (unwaxed) also applies a
 * short Nausea because it has gone bad.
 */
public final class AddonEffects {
    private AddonEffects() {
    }

    public static List<MobEffectInstance> effectsFor(List<Holder<MobEffect>> fillings, MooncakeState state) {
        int level = state.getTier() + 1;
        int durationTicks = 20 * (15 + 5 * state.getTier());
        if (state.isWaxed()) {
            durationTicks *= 2;
        }
        List<MobEffectInstance> out = new ArrayList<>();
        for (Holder<MobEffect> effect : fillings) {
            out.add(new MobEffectInstance(effect, durationTicks, level - 1, false, true, true));
        }
        if (state.isOxidized() && !state.isWaxed()) {
            out.add(new MobEffectInstance(MobEffects.NAUSEA, 20 * 4, 0, false, true, true));
        }
        return out;
    }
}