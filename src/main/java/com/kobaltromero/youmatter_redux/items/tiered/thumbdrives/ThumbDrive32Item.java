package com.kobaltromero.youmatter_redux.items.tiered.thumbdrives;

public class ThumbDrive32Item extends ThumbDriveItem {

    public ThumbDrive32Item(Properties properties, Tier tier) {
        super(properties, tier);
    }

    @Override
    public int getMaxStorageInKb() {
        return 32;
    }
}
