package net.thedustbuster.nef.mixin;

import net.caffeinemc.mods.sodium.api.config.structure.IntegerOptionBuilder;
import net.caffeinemc.mods.sodium.client.gui.SodiumConfigBuilder;
import net.thedustbuster.nef.NotEnoughFPSSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SodiumConfigBuilder.class)
public abstract class SodiumConfigBuilderMixin {
  @Redirect(
    method = "buildGeneralPage",
    at = @At(
      value = "INVOKE",
      target = "Lnet/caffeinemc/mods/sodium/api/config/structure/IntegerOptionBuilder;setRange(III)Lnet/caffeinemc/mods/sodium/api/config/structure/IntegerOptionBuilder;"
    )
  )
  public IntegerOptionBuilder buildGeneralPage(IntegerOptionBuilder builder, int min, int max, int step) {
    if (min == 10 && max == 260 && step == 10) {
      return builder.setRange(min, NotEnoughFPSSettings.MAX_FRAMERATE, 1);
    }

    return builder.setRange(min, max, step);
  }
}
