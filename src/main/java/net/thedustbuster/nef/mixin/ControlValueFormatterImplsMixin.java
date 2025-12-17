package net.thedustbuster.nef.mixin;

import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatterImpls;
import net.thedustbuster.nef.NotEnoughFPSSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ControlValueFormatterImpls.class)
public abstract class ControlValueFormatterImplsMixin {
  @ModifyConstant(
    method = "lambda$fpsLimit$2",
    constant = @Constant(intValue = 260)
  )
  private static int overrideMaxFPS(int original) {
    return NotEnoughFPSSettings.MAX_FRAMERATE;
  }
}