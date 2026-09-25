package com.moremoremooncake;

import com.moremoremooncake.registry.Registry;

/**
 * Common mod initializer of the independent double-filling mooncake addon.
 * Its slices implement the base mod's {@link com.moremooncake.mooncake.item.MooncakeFood} so they
 * assemble into and split from the base grand mooncake, but they are registered under the
 * {@code moremoremooncake} namespace as a fully separate mod that depends on the base.
 */
public final class Moremoremooncake {
    public static final String MOD_ID = "moremoremooncake";

    private Moremoremooncake() {
    }

    public static void init() {
        Registry.register();
    }
}