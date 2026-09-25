package com.moremoremooncake.mooncake;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

/**
 * The 12 single fillings of the addon. Each has a signature status effect and a base filling
 * colour. A two-filling (double) mooncake simply applies both fillings' effects, so picking any
 * two flavours equals the "sum" of their effects.
 */
public enum AddonFlavor {
    LIANRONG("lianrong", "莲蓉", "Lotus Paste", MobEffects.ABSORPTION, new int[]{235, 216, 172}),
    DOUSHA("dousha", "豆沙", "Red Bean", MobEffects.REGENERATION, new int[]{122, 42, 42}),
    ZAONI("zaoni", "枣泥", "Date Paste", MobEffects.SATURATION, new int[]{138, 56, 36}),
    WUREN("wuren", "五仁", "Five Kernel", MobEffects.DAMAGE_BOOST, new int[]{240, 230, 206}),
    YERONG("yerong", "椰蓉", "Coconut", MobEffects.HEALTH_BOOST, new int[]{250, 250, 250}),
    BAIGUO("baiguo", "百果", "Assorted Fruit", MobEffects.LUCK, new int[]{210, 170, 120}),
    HEIZHIMA("heizhima", "黑芝麻", "Black Sesame", MobEffects.NIGHT_VISION, new int[]{70, 60, 70}),
    BANLI("banli", "板栗", "Chestnut", MobEffects.DAMAGE_RESISTANCE, new int[]{150, 96, 50}),
    ZISHU("zishu", "紫薯", "Purple Potato", MobEffects.JUMP, new int[]{140, 78, 150}),
    YUNI("yuni", "芋泥", "Taro", MobEffects.SLOW_FALLING, new int[]{160, 130, 190}),
    SHUIGUO("shuiguo", "水果", "Fruit", MobEffects.MOVEMENT_SPEED, new int[]{230, 130, 120}),
    LVDouSha("lvdousha", "绿豆沙", "Mung Bean", MobEffects.DIG_SPEED, new int[]{110, 150, 80});

    private final String registryName;
    private final String zhName;
    private final String enName;
    private final Holder<MobEffect> effect;
    private final int[] color;

    AddonFlavor(String registryName, String zhName, String enName, Holder<MobEffect> effect, int[] color) {
        this.registryName = registryName;
        this.zhName = zhName;
        this.enName = enName;
        this.effect = effect;
        this.color = color;
    }

    public String getRegistryName() {
        return registryName;
    }

    public String getZhName() {
        return zhName;
    }

    public String getEnName() {
        return enName;
    }

    public Holder<MobEffect> getEffect() {
        return effect;
    }

    public int[] getColor() {
        return color;
    }
}