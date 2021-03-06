/*
 * Copyright (c) 1996, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package java.awt;

import java.io.ObjectStreamException;
import sun.awt.AWTAccessor;

/**
 * A class to encapsulate symbolic colors representing the color of
 * native GUI objects on a system.  For systems which support the dynamic
 * update of the system colors (when the user changes the colors)
 * the actual RGB values of these symbolic colors will also change
 * dynamically.  In order to compare the "current" RGB value of a
 * {@code SystemColor} object with a non-symbolic Color object,
 * {@code getRGB} should be used rather than {@code equals}.
 * <p>
 * Note that the way in which these system colors are applied to GUI objects
 * may vary slightly from platform to platform since GUI objects may be
 * rendered differently on each platform.
 * <p>
 * System color values may also be available through the {@code getDesktopProperty}
 * method on {@code java.awt.Toolkit}.
 *
 * @author Carl Quinn
 * @author Amy Fowler
 * @see Toolkit#getDesktopProperty
 */
public final class SystemColor extends Color {

  /**
   * The array index for the
   * {@link #desktop} system color.
   *
   * @see SystemColor#desktop
   */
  public static final int DESKTOP = 0;

  /**
   * The array index for the
   * {@link #activeCaption} system color.
   *
   * @see SystemColor#activeCaption
   */
  public static final int ACTIVE_CAPTION = 1;

  /**
   * The array index for the
   * {@link #activeCaptionText} system color.
   *
   * @see SystemColor#activeCaptionText
   */
  public static final int ACTIVE_CAPTION_TEXT = 2;

  /**
   * The array index for the
   * {@link #activeCaptionBorder} system color.
   *
   * @see SystemColor#activeCaptionBorder
   */
  public static final int ACTIVE_CAPTION_BORDER = 3;

  /**
   * The array index for the
   * {@link #inactiveCaption} system color.
   *
   * @see SystemColor#inactiveCaption
   */
  public static final int INACTIVE_CAPTION = 4;

  /**
   * The array index for the
   * {@link #inactiveCaptionText} system color.
   *
   * @see SystemColor#inactiveCaptionText
   */
  public static final int INACTIVE_CAPTION_TEXT = 5;

  /**
   * The array index for the
   * {@link #inactiveCaptionBorder} system color.
   *
   * @see SystemColor#inactiveCaptionBorder
   */
  public static final int INACTIVE_CAPTION_BORDER = 6;

  /**
   * The array index for the
   * {@link #window} system color.
   *
   * @see SystemColor#window
   */
  public static final int WINDOW = 7;

  /**
   * The array index for the
   * {@link #windowBorder} system color.
   *
   * @see SystemColor#windowBorder
   */
  public static final int WINDOW_BORDER = 8;

  /**
   * The array index for the
   * {@link #windowText} system color.
   *
   * @see SystemColor#windowText
   */
  public static final int WINDOW_TEXT = 9;

  /**
   * The array index for the
   * {@link #menu} system color.
   *
   * @see SystemColor#menu
   */
  public static final int MENU = 10;

  /**
   * The array index for the
   * {@link #menuText} system color.
   *
   * @see SystemColor#menuText
   */
  public static final int MENU_TEXT = 11;

  /**
   * The array index for the
   * {@link #text} system color.
   *
   * @see SystemColor#text
   */
  public static final int TEXT = 12;

  /**
   * The array index for the
   * {@link #textText} system color.
   *
   * @see SystemColor#textText
   */
  public static final int TEXT_TEXT = 13;

  /**
   * The array index for the
   * {@link #textHighlight} system color.
   *
   * @see SystemColor#textHighlight
   */
  public static final int TEXT_HIGHLIGHT = 14;

  /**
   * The array index for the
   * {@link #textHighlightText} system color.
   *
   * @see SystemColor#textHighlightText
   */
  public static final int TEXT_HIGHLIGHT_TEXT = 15;

  /**
   * The array index for the
   * {@link #textInactiveText} system color.
   *
   * @see SystemColor#textInactiveText
   */
  public static final int TEXT_INACTIVE_TEXT = 16;

  /**
   * The array index for the
   * {@link #control} system color.
   *
   * @see SystemColor#control
   */
  public static final int CONTROL = 17;

  /**
   * The array index for the
   * {@link #controlText} system color.
   *
   * @see SystemColor#controlText
   */
  public static final int CONTROL_TEXT = 18;

  /**
   * The array index for the
   * {@link #controlHighlight} system color.
   *
   * @see SystemColor#controlHighlight
   */
  public static final int CONTROL_HIGHLIGHT = 19;

  /**
   * The array index for the
   * {@link #controlLtHighlight} system color.
   *
   * @see SystemColor#controlLtHighlight
   */
  public static final int CONTROL_LT_HIGHLIGHT = 20;

  /**
   * The array index for the
   * {@link #controlShadow} system color.
   *
   * @see SystemColor#controlShadow
   */
  public static final int CONTROL_SHADOW = 21;

  /**
   * The array index for the
   * {@link #controlDkShadow} system color.
   *
   * @see SystemColor#controlDkShadow
   */
  public static final int CONTROL_DK_SHADOW = 22;

  /**
   * The array index for the
   * {@link #scrollbar} system color.
   *
   * @see SystemColor#scrollbar
   */
  public static final int SCROLLBAR = 23;

  /**
   * The array index for the
   * {@link #info} system color.
   *
   * @see SystemColor#info
   */
  public static final int INFO = 24;

  /**
   * The array index for the
   * {@link #infoText} system color.
   *
   * @see SystemColor#infoText
   */
  public static final int INFO_TEXT = 25;

  /**
   * The number of system colors in the array.
   */
  public static final int NUM_COLORS = 26;
  /**
   * The color rendered for the background of the desktop.
   */
  public static final SystemColor desktop = new SystemColor((byte) DESKTOP);
  /**
   * The color rendered for the window-title background of the currently active window.
   */
  public static final SystemColor activeCaption = new SystemColor((byte) ACTIVE_CAPTION);
  /**
   * The color rendered for the window-title text of the currently active window.
   */
  public static final SystemColor activeCaptionText = new SystemColor((byte) ACTIVE_CAPTION_TEXT);
  /**
   * The color rendered for the border around the currently active window.
   */
  public static final SystemColor activeCaptionBorder
      = new SystemColor((byte) ACTIVE_CAPTION_BORDER);
  /**
   * The color rendered for the window-title background of inactive windows.
   */
  public static final SystemColor inactiveCaption = new SystemColor((byte) INACTIVE_CAPTION);
  /**
   * The color rendered for the window-title text of inactive windows.
   */
  public static final SystemColor inactiveCaptionText
      = new SystemColor((byte) INACTIVE_CAPTION_TEXT);
  /**
   * The color rendered for the border around inactive windows.
   */
  public static final SystemColor inactiveCaptionBorder
      = new SystemColor((byte) INACTIVE_CAPTION_BORDER);
  /**
   * The color rendered for the background of interior regions inside windows.
   */
  public static final SystemColor window = new SystemColor((byte) WINDOW);
  /**
   * The color rendered for the border around interior regions inside windows.
   */
  public static final SystemColor windowBorder = new SystemColor((byte) WINDOW_BORDER);
  /**
   * The color rendered for text of interior regions inside windows.
   */
  public static final SystemColor windowText = new SystemColor((byte) WINDOW_TEXT);
  /**
   * The color rendered for the background of menus.
   */
  public static final SystemColor menu = new SystemColor((byte) MENU);
  /**
   * The color rendered for the text of menus.
   */
  public static final SystemColor menuText = new SystemColor((byte) MENU_TEXT);
  /**
   * The color rendered for the background of text control objects, such as
   * textfields and comboboxes.
   */
  public static final SystemColor text = new SystemColor((byte) TEXT);
  /**
   * The color rendered for the text of text control objects, such as textfields
   * and comboboxes.
   */
  public static final SystemColor textText = new SystemColor((byte) TEXT_TEXT);
  /**
   * The color rendered for the background of selected items, such as in menus,
   * comboboxes, and text.
   */
  public static final SystemColor textHighlight = new SystemColor((byte) TEXT_HIGHLIGHT);
  /**
   * The color rendered for the text of selected items, such as in menus, comboboxes,
   * and text.
   */
  public static final SystemColor textHighlightText = new SystemColor((byte) TEXT_HIGHLIGHT_TEXT);
  /**
   * The color rendered for the text of inactive items, such as in menus.
   */
  public static final SystemColor textInactiveText = new SystemColor((byte) TEXT_INACTIVE_TEXT);
  /**
   * The color rendered for the background of control panels and control objects,
   * such as pushbuttons.
   */
  public static final SystemColor control = new SystemColor((byte) CONTROL);
  /**
   * The color rendered for the text of control panels and control objects,
   * such as pushbuttons.
   */
  public static final SystemColor controlText = new SystemColor((byte) CONTROL_TEXT);
  /**
   * The color rendered for light areas of 3D control objects, such as pushbuttons.
   * This color is typically derived from the {@code control} background color
   * to provide a 3D effect.
   */
  public static final SystemColor controlHighlight = new SystemColor((byte) CONTROL_HIGHLIGHT);
  /**
   * The color rendered for highlight areas of 3D control objects, such as pushbuttons.
   * This color is typically derived from the {@code control} background color
   * to provide a 3D effect.
   */
  public static final SystemColor controlLtHighlight = new SystemColor((byte) CONTROL_LT_HIGHLIGHT);
  /**
   * The color rendered for shadow areas of 3D control objects, such as pushbuttons.
   * This color is typically derived from the {@code control} background color
   * to provide a 3D effect.
   */
  public static final SystemColor controlShadow = new SystemColor((byte) CONTROL_SHADOW);
  /**
   * The color rendered for dark shadow areas on 3D control objects, such as pushbuttons.
   * This color is typically derived from the {@code control} background color
   * to provide a 3D effect.
   */
  public static final SystemColor controlDkShadow = new SystemColor((byte) CONTROL_DK_SHADOW);
  /**
   * The color rendered for the background of scrollbars.
   */
  public static final SystemColor scrollbar = new SystemColor((byte) SCROLLBAR);
  /**
   * The color rendered for the background of tooltips or spot help.
   */
  public static final SystemColor info = new SystemColor((byte) INFO);
  /**
   * The color rendered for the text of tooltips or spot help.
   */
  public static final SystemColor infoText = new SystemColor((byte) INFO_TEXT);
  /*
   * JDK 1.1 serialVersionUID.
   */
  private static final long serialVersionUID = 4503142729533789064L;
  /******************************************************************************************/

    /*
     * System colors with default initial values, overwritten by toolkit if
     * system values differ and are available.
     * Should put array initialization above first field that is using
     * SystemColor constructor to initialize.
     */
  private static final int[] systemColors = {
      0xFF005C5C,  // desktop = new Color(0,92,92);
      0xFF000080,  // activeCaption = new Color(0,0,128);
      0xFFFFFFFF,  // activeCaptionText = Color.white;
      0xFFC0C0C0,  // activeCaptionBorder = Color.lightGray;
      0xFF808080,  // inactiveCaption = Color.gray;
      0xFFC0C0C0,  // inactiveCaptionText = Color.lightGray;
      0xFFC0C0C0,  // inactiveCaptionBorder = Color.lightGray;
      0xFFFFFFFF,  // window = Color.white;
      0xFF000000,  // windowBorder = Color.black;
      0xFF000000,  // windowText = Color.black;
      0xFFC0C0C0,  // menu = Color.lightGray;
      0xFF000000,  // menuText = Color.black;
      0xFFC0C0C0,  // text = Color.lightGray;
      0xFF000000,  // textText = Color.black;
      0xFF000080,  // textHighlight = new Color(0,0,128);
      0xFFFFFFFF,  // textHighlightText = Color.white;
      0xFF808080,  // textInactiveText = Color.gray;
      0xFFC0C0C0,  // control = Color.lightGray;
      0xFF000000,  // controlText = Color.black;
      0xFFFFFFFF,  // controlHighlight = Color.white;
      0xFFE0E0E0,  // controlLtHighlight = new Color(224,224,224);
      0xFF808080,  // controlShadow = Color.gray;
      0xFF000000,  // controlDkShadow = Color.black;
      0xFFE0E0E0,  // scrollbar = new Color(224,224,224);
      0xFFE0E000,  // info = new Color(224,224,0);
      0xFF000000,  // infoText = Color.black;
  };
  private static final SystemColor[] systemColorObjects = {
      desktop, activeCaption, activeCaptionText, activeCaptionBorder, inactiveCaption,
      inactiveCaptionText, inactiveCaptionBorder, window, windowBorder, windowText, menu, menuText,
      text, textText, textHighlight, textHighlightText, textInactiveText, control, controlText,
      controlHighlight, controlLtHighlight, controlShadow, controlDkShadow, scrollbar, info,
      infoText};

  static {
    AWTAccessor.setSystemColorAccessor(SystemColor::updateSystemColors);
    updateSystemColors();
  }

  /*
   * An index into either array of SystemColor objects or values.
   */
  private final transient int index;

  /**
   * Creates a symbolic color that represents an indexed entry into system
   * color cache. Used by above static system colors.
   */
  private SystemColor(byte index) {
    super(systemColors[index]);
    this.index = index;
  }

  /**
   * Called from {@code <init>} and toolkit to update the above systemColors cache.
   */
  private static void updateSystemColors() {
    Toolkit.getDefaultToolkit().loadSystemColors(systemColors);
    for (int i = 0; i < systemColors.length; i++) {
      systemColorObjects[i].value = systemColors[i];
    }
  }

  /**
   * Returns a string representation of this {@code Color}'s values.
   * This method is intended to be used only for debugging purposes,
   * and the content and format of the returned string may vary between
   * implementations.
   * The returned string may be empty but may not be {@code null}.
   *
   * @return a string representation of this {@code Color}
   */
  public String toString() {
    return getClass().getName() + "[i=" + index + "]";
  }

  /**
   * The design of the {@code SystemColor} class assumes that
   * the {@code SystemColor} object instances stored in the
   * static final fields above are the only instances that can
   * be used by developers.
   * This method helps maintain those limits on instantiation
   * by using the index stored in the value field of the
   * serialized form of the object to replace the serialized
   * object with the equivalent static object constant field
   * of {@code SystemColor}.
   * See the {@link #writeReplace} method for more information
   * on the serialized form of these objects.
   *
   * @return one of the {@code SystemColor} static object
   * fields that refers to the same system color.
   */
  private Object readResolve() {
    // The instances of SystemColor are tightly controlled and
    // only the canonical instances appearing above as static
    // constants are allowed.  The serial form of SystemColor
    // objects stores the color index as the value.  Here we
    // map that index back into the canonical instance.
    return systemColorObjects[value];
  }

  /**
   * Returns a specialized version of the {@code SystemColor}
   * object for writing to the serialized stream.
   *
   * @return a proxy {@code SystemColor} object with its value
   * replaced by the corresponding system color index.
   * @serialData The value field of a serialized {@code SystemColor} object
   * contains the array index of the system color instead of the
   * rgb data for the system color.
   * This index is used by the {@link #readResolve} method to
   * resolve the deserialized objects back to the original
   * static constant versions to ensure unique instances of
   * each {@code SystemColor} object.
   */
  private Object writeReplace() throws ObjectStreamException {
    // we put an array index in the SystemColor.value while serialize
    // to keep compatibility.
    SystemColor color = new SystemColor((byte) index);
    color.value = index;
    return color;
  }
}
