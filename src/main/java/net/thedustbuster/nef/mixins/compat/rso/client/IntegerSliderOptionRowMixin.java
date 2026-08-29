package net.thedustbuster.nef.mixins.compat.rso.client;

import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.thedustbuster.nef.client.gui.SliderTypingController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.IntegerSliderOptionRow")
public abstract class IntegerSliderOptionRowMixin {
  @Unique
  private BaseWidget self() {
    return (BaseWidget) (Object) this;
  }

  @Unique
  private SliderTypingController typing() {
    return SliderTypingController.forWidget(this);
  }

  @Final @Shadow
  private IntegerOption option;

  @Shadow
  private int sliderX() {
    throw new AssertionError("mixin shadow");
  }

  @Inject(
    method = "controlMouseClicked",
    at = @At("RETURN")
  )
  private void onControlMouseClicked(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
    if (!cir.getReturnValue()) return;

    typing().onMouseClicked(option);
  }

  @ModifyArg(
    method = "renderControl",
    at = @At(
      value = "INVOKE",
      target = "Lme/flashyreese/mods/reeses_sodium_options/client/gui/frame/option/IntegerSliderOptionRow;drawString(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/network/chat/Component;III)V"
    ),
    index = 1
  )
  private Component overrideLabelText(Component original) {
    boolean stillValidTarget = ((GuiEventListener) (Object) this).isFocused();
    return typing().overrideLabelText(original, stillValidTarget);
  }

  @Inject(
    method = "renderControl",
    at = @At("TAIL")
  )
  private void drawInteractionOutline(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
    if (!option.isEnabled()) return;
    if (!self().isHovered()) return;

    Component label = option.formatValue(option.getValidatedValue());
    int labelWidth = Minecraft.getInstance().font.width(label);
    int labelRight = sliderX() - 6;

    typing().drawOutline(guiGraphicsExtractor, labelRight, labelWidth, self().getY(), self().getLimitY());
  }

  @Inject(
    method = "controlKeyPressed",
    at = @At("HEAD"),
    cancellable = true
  )
  private void onControlKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
    if (typing().onKeyPressed(event, option)) cir.setReturnValue(true);
  }

  @Inject(
    method = "mouseDragged",
    at = @At("HEAD")
  )
  public void onMouseDragged(MouseButtonEvent event, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
    typing().cancelTyping();
  }
}
