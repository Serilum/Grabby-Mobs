package com.serilum.grabbymobs.util;

import com.natamus.collective.functions.ConfigFunctions;
import com.serilum.grabbymobs.config.ConfigHandler;
import com.serilum.grabbymobs.data.GrabbingMob;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.concurrent.ConcurrentHashMap;

public class Util {
	private static final ConcurrentHashMap<EntityType<?>, Boolean> canGrabByEntityType = new ConcurrentHashMap<>();
	private static String cachedMobsThatCanGrab = "";

	public static boolean canPickUp(Mob mob) {
		if (isDrinkingWitch(mob)) {
			return false;
		}

		return isGrabbingMob(mob);
	}

	public static boolean isGrabbingMob(Mob mob) {
		if (!cachedMobsThatCanGrab.equals(ConfigHandler.mobsThatCanGrab)) {
			canGrabByEntityType.clear();
			cachedMobsThatCanGrab = ConfigHandler.mobsThatCanGrab;
		}

		EntityType<?> entityType = mob.getType();
		Boolean canGrab = canGrabByEntityType.get(entityType);
		if (canGrab == null) {
			canGrab = ConfigFunctions.isInIdList(ConfigHandler.mobsThatCanGrab, getEntityTypeId(entityType));
			canGrabByEntityType.put(entityType, canGrab);
		}

		return canGrab;
	}

	public static boolean grabsWhileMobGriefingIsOff(Mob mob, ServerLevel serverLevel) {
		if (!ConfigHandler.mobsGrabWhenMobGriefingIsOff || serverLevel.getGameRules().get(GameRules.MOB_GRIEFING)) {
			return false;
		}

		return mob.isAlive() && canPickUp(mob);
	}

	public static boolean canGrabItemEntity(Mob mob, ServerLevel serverLevel, ItemEntity itemEntity) {
		if (itemEntity.isRemoved() || itemEntity.getItem().isEmpty() || itemEntity.hasPickUpDelay()) {
			return false;
		}

		return mob.wantsToPickUp(serverLevel, itemEntity.getItem());
	}

	public static boolean keepsGrabbedItemOnDeath(Mob mob) {
		return !ConfigHandler.mobsDropGrabbedItemsOnDeath && isGrabbingMob(mob);
	}

	public static void rememberReleasedItem(Mob mob, ItemStack oldItemStack, ItemStack newItemStack) {
		if (oldItemStack.isEmpty() || newItemStack.is(oldItemStack.getItem())) {
			return;
		}

		((GrabbingMob)mob).grabbyMobs$setReleasedItem(oldItemStack.getItem(), mob.level().getGameTime());
	}

	public static boolean justLetGoOf(Mob mob, ItemStack itemStack) {
		if (ConfigHandler.secondsBeforeRegrabbingAnItem <= 0) {
			return false;
		}

		GrabbingMob grabbingMob = (GrabbingMob)mob;
		if (!itemStack.is(grabbingMob.grabbyMobs$getReleasedItem())) {
			return false;
		}

		long ticksSinceRelease = mob.level().getGameTime() - grabbingMob.grabbyMobs$getReleasedAtGameTime();
		return ticksSinceRelease < ConfigHandler.secondsBeforeRegrabbingAnItem * 20L;
	}

	public static boolean wantsToPickUp(Mob mob, ItemStack itemStack) {
		if (justLetGoOf(mob, itemStack)) {
			return false;
		}

		EquipmentSlot equipmentSlot = mob.getEquipmentSlotForItem(itemStack);
		if (equipmentSlot.isArmor()) {
			return canWearGrabbedArmour(mob);
		}

		if (mob instanceof Witch) {
			return equipmentSlot.equals(EquipmentSlot.MAINHAND);
		}

		return true;
	}

	public static boolean canWearGrabbedArmour(Mob mob) {
		if (mob instanceof Zombie || mob instanceof AbstractSkeleton || mob instanceof AbstractPiglin) {
			return true;
		}

		return !isVanillaMob(mob);
	}

	public static boolean isVanillaMob(Mob mob) {
		return getEntityTypeId(mob.getType()).startsWith("minecraft:");
	}

	public static String getEntityTypeId(EntityType<?> entityType) {
		return EntityType.getKey(entityType).toString();
	}

	public static boolean isDrinkingWitch(Mob mob) {
		return mob instanceof Witch witch && witch.isDrinkingPotion();
	}

	public static void dropGrabbedItemBeforeDrinking(Witch witch) {
		ItemStack heldItem = witch.getMainHandItem();
		if (heldItem.isEmpty() || heldItem != ((GrabbingMob)witch).grabbyMobs$getGrabbedItem()) {
			return;
		}

		if (witch.level() instanceof ServerLevel serverLevel) {
			witch.spawnAtLocation(serverLevel, heldItem);
		}
	}
}
