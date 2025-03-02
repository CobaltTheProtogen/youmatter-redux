package com.kobaltromero.youmatter_redux.util;

public enum Tier implements ITier {
    BASIC(0x03F288, 1.0f, 0.0125f),
    ADVANCED(0xD22C20, 2.0f, 0.025f),
    ELITE(0x31E1DF, 4.0f, 0.05f),
    ULTIMATE(0xA300F0, 8.0f, 0.075f),
    CREATIVE(0x636363, 16.0f, 1.0f);

    private final int color;
    private final float base_amplifier;
    private final float probability;

    Tier(int color, float base_amplifier, float probability) {
        this.color = color;
        this.base_amplifier = base_amplifier;
        this.probability = probability;
    }

    public int getColor() {
        return color;
    }

    public float getBaseAmplifier() {
        return base_amplifier;
    }

    public float getProbability() {
        return probability;
    }
}
