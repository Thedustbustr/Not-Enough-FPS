package net.thedustbuster.nef.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.thedustbuster.nef.NotEnoughFPSSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin {
  @Shadow @Final @Mutable
  private OptionInstance<Integer> framerateLimit;

  @Inject(method = "<init>",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/Options;load()V",
      shift = At.Shift.BEFORE
    )
  )
  private void overrideFpsLimit(CallbackInfo ci) {
    this.framerateLimit = new OptionInstance<>(
      "options.framerateLimit",
      OptionInstance.noTooltip(),
      (component, integer) -> {
        if (integer == NotEnoughFPSSettings.MAX_FRAMERATE) return Options.genericValueLabel(component, Component.translatable("options.framerateLimit.max"));
        return Options.genericValueLabel(component, Component.translatable("options.framerate", integer));
      },
      new OptionInstance.IntRange(1, NotEnoughFPSSettings.MAX_FRAMERATE).xmap(i -> i, integer -> integer, true),
      Codec.intRange(10, NotEnoughFPSSettings.MAX_FRAMERATE), 120, integer -> Minecraft.getInstance().getFramerateLimitTracker().setFramerateLimit(integer)
    );
  }
}