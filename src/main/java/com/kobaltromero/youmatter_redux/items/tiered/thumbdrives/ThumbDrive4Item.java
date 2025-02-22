package com.kobaltromero.youmatter_redux.items.tiered.thumbdrives;

public class ThumbDrive4Item extends ThumbDriveItem {

    public ThumbDrive4Item(Properties properties, Tier tier) {
        super(properties, tier);
    }

    @Override
    public int getMaxStorageInKb() {
        return 4;
    }
}
