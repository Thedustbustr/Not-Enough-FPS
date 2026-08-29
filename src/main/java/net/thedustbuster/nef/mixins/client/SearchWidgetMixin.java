package net.thedustbuster.nef.mixins.client;

import net.caffeinemc.mods.sodium.client.gui.widgets.SearchWidget;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SearchWidget.class)
public abstract class SearchWidgetMixin {
  @Shadow
  private EditBox searchBox;

  @Inject(method = "setFocused(Z)V", at = @At("TAIL"))
  private void releaseSearchBoxFocus(boolean focused, CallbackInfo ci) {
    if (!focused) this.searchBox.setFocused(false);
  }
}
