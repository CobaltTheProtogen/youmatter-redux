package com.kobaltromero.youmatter_redux.items.tiered.thumbdrives;

public class ThumbDrive8Item extends ThumbDriveItem {

    public ThumbDrive8Item(Properties properties, Tier tier) {
        super(properties, tier);
    }

    @Override
    public int getMaxStorageInKb() {
        return 8;
    }
}
