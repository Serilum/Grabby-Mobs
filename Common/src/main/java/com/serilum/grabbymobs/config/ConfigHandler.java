package com.serilum.grabbymobs.config;

import com.natamus.collective.config.DuskConfig;
import com.serilum.grabbymobs.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<>();

	@Entry(registry = "entity_type", filter = "mobs") public static String mobsThatCanGrab = "minecraft:drowned,minecraft:evoker,minecraft:husk,minecraft:piglin,minecraft:piglin_brute,minecraft:skeleton,minecraft:stray,minecraft:vex,minecraft:vindicator,minecraft:witch,minecraft:wither_skeleton,minecraft:zombie,minecraft:zombie_villager,minecraft:zombified_piglin";

	@Entry public static boolean mobsGrabWhenMobGriefingIsOff = false;
	@Entry public static boolean mobsDropGrabbedItemsOnDeath = true;
	@Entry(min = 0, max = 3600) public static int secondsBeforeRegrabbingAnItem = 30;

	public static void initConfig() {
		configMetaData.put("mobsThatCanGrab", Arrays.asList(
			"The mobs that always pick up items from the floor. Modded mobs can be added too, for example 'somemod:*'."
		));
		configMetaData.put("mobsGrabWhenMobGriefingIsOff", Arrays.asList(
			"Whether the mobs still pick up items when the mobGriefing gamerule is off."
		));
		configMetaData.put("mobsDropGrabbedItemsOnDeath", Arrays.asList(
			"Whether items a mob picked up drop when it dies. When disabled, those items are lost."
		));
		configMetaData.put("secondsBeforeRegrabbingAnItem", Arrays.asList(
			"How many seconds a mob waits before picking up an item it let go of again. Set to 0 to turn this off."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}
