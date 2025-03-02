package com.kobaltromero.youmatter_redux.items.tiered.thumbdrives;

import com.kobaltromero.youmatter_redux.util.ITier;

public class ThumbDrive4Item extends ThumbDriveItem {

    public ThumbDrive4Item(Properties properties, ITier tier) {
        super(properties, tier);
    }

    @Override
    public int getMaxStorageInKb() {
        return 4;
    }
}
