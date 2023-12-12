package com.natamus.grabbymobs.fabric.mixin;

import com.natamus.grabbymobs.util.Util;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Mob.class, priority = 1001)
public class MobMixin {
	@Inject(method = "canPickUpLoot()Z", at = @At(value = "HEAD"), cancellable = true)
	public void canPickUpLoot(CallbackInfoReturnable<Boolean> cir) {
		if (Util.canPickUp((Mob)(Object)this)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "wantsToPickUp(Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "HEAD"), cancellable = true)
	public void wantsToPickUp(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (Util.wantsToPickUp((Mob)(Object)this, itemStack)) {
			cir.setReturnValue(true);
		}
	}
}
