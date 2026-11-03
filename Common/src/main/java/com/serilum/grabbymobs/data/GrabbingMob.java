package com.serilum.grabbymobs.data;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface GrabbingMob {
	ItemStack grabbyMobs$getGrabbedItem();

	Item grabbyMobs$getReleasedItem();
	long grabbyMobs$getReleasedAtGameTime();
	void grabbyMobs$setReleasedItem(Item item, long gameTime);
}
