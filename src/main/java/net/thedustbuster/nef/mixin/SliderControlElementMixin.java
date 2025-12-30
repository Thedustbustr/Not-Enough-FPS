package net.thedustbuster.nef.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.caffeinemc.mods.sodium.api.config.option.SteppedValidator;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlElement;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.thedustbuster.nef.adaptors.minecraft.text.TextBuffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl$SliderControlElement")
public abstract class SliderControlElementMixin {
  @Final @Shadow
  private IntegerOption option;

  @Unique
  public ControlElement getControlElement() {
    return (ControlElement) (Object) this;
  }

  @Unique
  private boolean typing = false;

  @Unique
  private final StringBuilder textBuffer = new StringBuilder();

  @Inject(
    method = "mouseClicked",
    at = @At("RETURN")
  )
  private void onMouseClicked(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
    if (!option.isEnabled()) return;

    typing = true;
    textBuffer.setLength(0);
    textBuffer.append(option.getValidatedValue());
  }

  @ModifyArg(
    method = "render",
    at = @At(
      value = "INVOKE",
      target = "Lnet/caffeinemc/mods/sodium/client/gui/options/control/SliderControl$SliderControlElement;drawString(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/network/chat/Component;III)V"
    ),
    index = 1
  )
  private Component overrideLabelText(Component original) {
    if (!typing) return original;

    if (!getControlElement().isHovered()) {
      this.typing = false;
      return original;
    }

    boolean blink = (System.currentTimeMillis() / 500L) % 2 == 0;
    return new TextBuffer()
      .addText(String.format(textBuffer + "%s", blink ? "_" : ""))
      .build();
  }

  @Inject(
    method = "keyPressed",
    at = @At("HEAD"),
    cancellable = true
  )
  private void onKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
    if (!typing) return;

    if (event.key() == InputConstants.KEY_ESCAPE) {
      typing = false;
      cir.setReturnValue(true);
      return;
    }

    if (event.key() == InputConstants.KEY_RETURN) {
      commitTypedValue();
      typing = false;
      cir.setReturnValue(true);
      return;
    }

    if (event.key() == InputConstants.KEY_BACKSPACE) {
      if (!textBuffer.isEmpty()) textBuffer.deleteCharAt(textBuffer.length() - 1);
      cir.setReturnValue(true);
      return;
    }

    if (event.getDigit() != -1 && textBuffer.length() < 4) {
      textBuffer.append(event.getDigit());
    }

    cir.setReturnValue(true);
  }

  @Inject(
    method = "mouseDragged",
    at = @At(
      value = "INVOKE",
      target = "Lnet/caffeinemc/mods/sodium/client/gui/options/control/SliderControl$SliderControlElement;setValueFromMouse(D)V",
      shift = At.Shift.BEFORE
    )
  )
  public void mouseDragged(MouseButtonEvent event, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
    if (typing) this.typing = false;
  }

  @Unique
  private void commitTypedValue() {
    int value = Integer.parseInt(textBuffer.toString());
    SteppedValidator validator = option.getSteppedValidator();
    value = Mth.clamp(value, validator.min(), validator.max());
    option.modifyValue(value);
  }
}