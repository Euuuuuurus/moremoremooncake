package com.moremoremooncake.fabric;

import com.moremoremooncake.Moremoremooncake;
import net.fabricmc.api.ModInitializer;

public class MoremoremooncakeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Moremoremooncake.init();
    }
}