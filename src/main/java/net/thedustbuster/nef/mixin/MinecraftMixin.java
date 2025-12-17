package net.thedustbuster.nef.mixin;

import net.minecraft.client.Minecraft;
import net.thedustbuster.nef.NotEnoughFPSSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
  @ModifyConstant(
    method = "runTick",
    constant = @Constant(intValue = 260)
  )
  private int replaceUnlimitedCutoff(int original) {
    return NotEnoughFPSSettings.MAX_FRAMERATE;
  }
}
