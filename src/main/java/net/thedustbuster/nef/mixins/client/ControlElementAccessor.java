package net.thedustbuster.nef.mixins.client;

import net.caffeinemc.mods.sodium.client.gui.options.control.AbstractOptionList;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ControlElement.class)
public interface ControlElementAccessor {
  @Accessor("list")
  AbstractOptionList nef$getList();
}
