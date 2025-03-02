package com.kobaltromero.youmatter_redux.items.tiered.thumbdrives;

import com.kobaltromero.youmatter_redux.util.ITier;

public class ThumbDrive8Item extends ThumbDriveItem {

    public ThumbDrive8Item(Properties properties, ITier tier) {
        super(properties, tier);
    }

    @Override
    public int getMaxStorageInKb() {
        return 8;
    }
}
