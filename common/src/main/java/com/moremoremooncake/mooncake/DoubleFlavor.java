package com.moremoremooncake.mooncake;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * An unordered pair of two distinct single fillings - the identity of every addon "double"
 * mooncake. There are C(12, 2) = 66 combinations, kept sorted by enum ordinal so the pair id is
 * order-independent.
 */
public record DoubleFlavor(AddonFlavor first, AddonFlavor second) {

    /** The canonical list of all 66 combinations. */
    public static final List<DoubleFlavor> ALL = all();

    private static List<DoubleFlavor> all() {
        AddonFlavor[] f = AddonFlavor.values();
        List<DoubleFlavor> out = new ArrayList<>();
        for (int i = 0; i < f.length; i++) {
            for (int j = i + 1; j < f.length; j++) {
                out.add(new DoubleFlavor(f[i], f[j]));
            }
        }
        return List.copyOf(out);
    }

    /** Registry id in canonical 'a_b' order, e.g. "lianrong_dousha". */
    public String registryName() {
        return first.getRegistryName() + "_" + second.getRegistryName();
    }

    /** The union of both fillings' effects. */
    public List<Holder<MobEffect>> effects() {
        return List.of(first.getEffect(), second.getEffect());
    }

    /** Average of the two fillings' base colours - the visual identity of the double filling. */
    public int[] blendColor() {
        int[] a = first.getColor();
        int[] b = second.getColor();
        return new int[]{(a[0] + b[0]) / 2, (a[1] + b[1]) / 2, (a[2] + b[2]) / 2};
    }
}