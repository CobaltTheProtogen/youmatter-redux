package com.kobaltromero.youmatter_redux.items.tiered.thumbdrives;

public class ThumbDrive16Item extends ThumbDriveItem {

    public ThumbDrive16Item(Properties properties, Tier tier) {
        super(properties, tier);
    }

    @Override
    public int getMaxStorageInKb() {
        return 16;
    }
}
