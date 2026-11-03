package com.serilum.grabbymobs.mixin;

import com.serilum.grabbymobs.data.GrabbingMob;
import com.serilum.grabbymobs.util.Util;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Mob.class, priority = 1001)
public abstract class MobMixin implements GrabbingMob {
	@Unique private ItemStack grabbyMobs$grabbedItem = ItemStack.EMPTY;
	@Unique private Item grabbyMobs$releasedItem = Items.AIR;
	@Unique private long grabbyMobs$releasedAtGameTime = 0;

	@Shadow protected abstract Vec3i getPickupReach();
	@Shadow protected abstract void pickUpItem(ItemEntity itemEntity);

	@Override
	public ItemStack grabbyMobs$getGrabbedItem() {
		return grabbyMobs$grabbedItem;
	}

	@Override
	public Item grabbyMobs$getReleasedItem() {
		return grabbyMobs$releasedItem;
	}

	@Override
	public long grabbyMobs$getReleasedAtGameTime() {
		return grabbyMobs$releasedAtGameTime;
	}

	@Override
	public void grabbyMobs$setReleasedItem(Item item, long gameTime) {
		grabbyMobs$releasedItem = item;
		grabbyMobs$releasedAtGameTime = gameTime;
	}

	@Inject(method = "canPickUpLoot()Z", at = @At(value = "HEAD"), cancellable = true)
	public void canPickUpLoot(CallbackInfoReturnable<Boolean> cir) {
		if (Util.canPickUp((Mob)(Object)this)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "wantsToPickUp(Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "HEAD"), cancellable = true)
	public void wantsToPickUp(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		Mob mob = (Mob)(Object)this;
		if (Util.isGrabbingMob(mob)) {
			cir.setReturnValue(Util.wantsToPickUp(mob, itemStack));
		}
	}

	@Inject(method = "aiStep()V", at = @At(value = "TAIL"))
	public void aiStep(CallbackInfo ci) {
		Mob mob = (Mob)(Object)this;
		Level level = mob.level();
		if (level.isClientSide() || !Util.grabsWhileMobGriefingIsOff(mob, level)) {
			return;
		}

		Vec3i pickupReach = getPickupReach();
		for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(pickupReach.getX(), pickupReach.getY(), pickupReach.getZ()))) {
			if (Util.canGrabItemEntity(mob, itemEntity)) {
				pickUpItem(itemEntity);
			}
		}
	}

	@Inject(method = "setItemSlotAndDropWhenKilled(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "TAIL"))
	public void setItemSlotAndDropWhenKilled(EquipmentSlot equipmentSlot, ItemStack itemStack, CallbackInfo ci) {
		Mob mob = (Mob)(Object)this;
		grabbyMobs$grabbedItem = itemStack;

		if (Util.keepsGrabbedItemOnDeath(mob)) {
			mob.setDropChance(equipmentSlot, 0.0F);
		}
	}
}
