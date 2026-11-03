package com.serilum.grabbymobs.mixin;

import com.serilum.grabbymobs.util.Util;
import net.minecraft.world.entity.monster.Witch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Witch.class, priority = 1001)
public class WitchMixin {
	@Inject(method = "aiStep()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Witch;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V", ordinal = 1))
	public void aiStep(CallbackInfo ci) {
		Util.dropGrabbedItemBeforeDrinking((Witch)(Object)this);
	}
}
