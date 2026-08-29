package net.thedustbuster.nef.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.caffeinemc.mods.sodium.api.config.option.SteppedValidator;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.thedustbuster.nef.adaptors.minecraft.text.TextBuffer;

import java.util.Map;
import java.util.WeakHashMap;

public final class SliderTypingController {
  private static final int TYPING_BORDER_COLOR = 0xFFFFFFFF;
  private static final int HOVER_HINT_COLOR = 0x40FFFFFF;
  private static final int OUTLINE_PADDING = 2;

  private static final Map<Object, SliderTypingController> INSTANCES = new WeakHashMap<>();

  public static SliderTypingController forWidget(Object widget) {
    return INSTANCES.computeIfAbsent(widget, _ -> new SliderTypingController());
  }

  private boolean typing = false;
  private final StringBuilder textBuffer = new StringBuilder();

  private SliderTypingController() {
  }

  public void onMouseClicked(IntegerOption option) {
    typing = true;
    textBuffer.setLength(0);
    textBuffer.append(option.getValidatedValue());
  }

  public void cancelTyping() {
    typing = false;
  }

  public Component overrideLabelText(Component original, boolean stillValidTarget) {
    if (!typing) return original;

    if (!stillValidTarget) {
      typing = false;
      return original;
    }

    boolean blink = (System.currentTimeMillis() / 500L) % 2 == 0;
    return new TextBuffer()
      .addText(String.format(textBuffer + "%s", blink ? "_" : ""))
      .build();
  }

  public boolean onKeyPressed(KeyEvent event, IntegerOption option) {
    if (!typing) return false;

    if (event.key() == InputConstants.KEY_ESCAPE) {
      typing = false;
      return true;
    }

    if (event.key() == InputConstants.KEY_RETURN) {
      commit(option);
      typing = false;
      return true;
    }

    if (event.key() == InputConstants.KEY_BACKSPACE) {
      if (!textBuffer.isEmpty()) textBuffer.deleteCharAt(textBuffer.length() - 1);
      return true;
    }

    if (event.getDigit() != -1 && textBuffer.length() < 4) {
      textBuffer.append(event.getDigit());
    }

    return true;
  }

  private void commit(IntegerOption option) {
    if (textBuffer.isEmpty()) return;

    int value = Integer.parseInt(textBuffer.toString());
    SteppedValidator validator = option.getSteppedValidator();
    value = Mth.clamp(value, validator.min(), validator.max());
    option.modifyValue(value);
  }

  public void drawOutline(GuiGraphicsExtractor guiGraphicsExtractor, int labelRight, int labelWidth, int y1, int y2) {
    int color = typing ? TYPING_BORDER_COLOR : HOVER_HINT_COLOR;

    int x2 = labelRight + OUTLINE_PADDING;
    int x1 = x2 - labelWidth - 2 * OUTLINE_PADDING;

    guiGraphicsExtractor.fill(x1 + 1, y1, x2 - 1, y1 + 1, color);
    guiGraphicsExtractor.fill(x1 + 1, y2 - 1, x2 - 1, y2, color);
    guiGraphicsExtractor.fill(x1, y1, x1 + 1, y2, color);
    guiGraphicsExtractor.fill(x2 - 1, y1, x2, y2, color);
  }
}
