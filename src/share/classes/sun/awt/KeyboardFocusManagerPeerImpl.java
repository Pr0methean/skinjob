/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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
package sun.awt;

import android.util.Log;
import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import sun.awt.AWTAccessor.KeyboardFocusManagerAccessor;
import sun.awt.CausedFocusEvent.Cause;

public abstract class KeyboardFocusManagerPeerImpl implements KeyboardFocusManagerPeer {

  // The constants are copied from java.awt.KeyboardFocusManager
  public static final int SNFH_FAILURE = 0;
  public static final int SNFH_SUCCESS_HANDLED = 1;
  public static final int SNFH_SUCCESS_PROCEED = 2;
  private static final String TAG = "KeyboardFocusMgrPeerImpl";
  private static final KeyboardFocusManagerAccessor kfmAccessor
      = AWTAccessor.getKeyboardFocusManagerAccessor();

  /*
   * WARNING: Don't call it on the Toolkit thread.
   *
   * Checks if the component:
   * 1) accepts focus on click (in general)
   * 2) may be a focus owner (in particular)
   */
  @SuppressWarnings("deprecation")
  public static boolean shouldFocusOnClick(Component component) {
    boolean acceptFocusOnClick;

    // A component is generally allowed to accept focus on click
    // if its peer is focusable. There're some exceptions though.

    // CANVAS & SCROLLBAR accept focus on click
    if (component instanceof Canvas || component instanceof Scrollbar) {
      acceptFocusOnClick = true;

      // PANEL, empty only, accepts focus on click
    } else if (component instanceof Panel) {
      acceptFocusOnClick = ((Container) component).getComponentCount() == 0;

      // Other components
    } else {
      ComponentPeer peer = component != null ? component.getPeer() : null;
      acceptFocusOnClick = peer != null && peer.isFocusable();
    }
    return acceptFocusOnClick && AWTAccessor.getComponentAccessor().canBeFocusOwner(component);
  }

  /*
   * Posts proper lost/gain focus events to the event queue.
   */
  @SuppressWarnings("deprecation")
  public static boolean deliverFocus(
      Component lightweightChild, Component target, boolean temporary,
      boolean focusedWindowChangeAllowed, long time, Cause cause,
      Component currentFocusOwner) // provided by the descendant peers
  {
    if (lightweightChild == null) {
      lightweightChild = target;
    }

    Component currentOwner = currentFocusOwner;
    if (currentOwner != null && currentOwner.getPeer() == null) {
      currentOwner = null;
    }
    if (currentOwner != null) {
      FocusEvent fl = new CausedFocusEvent(currentOwner,
          FocusEvent.FOCUS_LOST,
          false,
          lightweightChild,
          cause);

      Log.v(TAG, "Posting focus event: " + fl);
      SunToolkit.postEvent(SunToolkit.targetToAppContext(currentOwner), fl);
    }

    FocusEvent fg = new CausedFocusEvent(lightweightChild,
        FocusEvent.FOCUS_GAINED,
        false,
        currentOwner,
        cause);

    Log.v(TAG, "Posting focus event: " + fg);
    SunToolkit.postEvent(SunToolkit.targetToAppContext(lightweightChild), fg);
    return true;
  }

  // WARNING: Don't call it on the Toolkit thread.
  public static boolean requestFocusFor(Component target, Cause cause) {
    return AWTAccessor.getComponentAccessor().requestFocus(target, cause);
  }

  // WARNING: Don't call it on the Toolkit thread.
  public static int shouldNativelyFocusHeavyweight(
      Component heavyweight, Component descendant, boolean temporary,
      boolean focusedWindowChangeAllowed, long time, Cause cause) {
    return kfmAccessor.shouldNativelyFocusHeavyweight(heavyweight,
        descendant,
        temporary,
        focusedWindowChangeAllowed,
        time,
        cause);
  }

  public static void removeLastFocusRequest(Component heavyweight) {
    kfmAccessor.removeLastFocusRequest(heavyweight);
  }

  // WARNING: Don't call it on the Toolkit thread.
  public static boolean processSynchronousLightweightTransfer(
      Component heavyweight, Component descendant, boolean temporary,
      boolean focusedWindowChangeAllowed, long time) {
    return kfmAccessor.processSynchronousLightweightTransfer(heavyweight,
        descendant,
        temporary,
        focusedWindowChangeAllowed,
        time);
  }

  /*
     * Post AWTEvent of high priority.
     */
  public static void postPriorityEvent(AWTEvent e) {
    PeerEvent pe = new PeerEvent(Toolkit.getDefaultToolkit(), new Runnable() {
      @Override
      public void run() {
        AWTAccessor.getAWTEventAccessor().setPosted(e);
        ((Component) e.getSource()).dispatchEvent(e);
      }
    }, PeerEvent.ULTIMATE_PRIORITY_EVENT);
    SunToolkit.postEvent(SunToolkit.targetToAppContext(e.getSource()), pe);
  }

  @Override
  public void clearGlobalFocusOwner(Window activeWindow) {
    if (activeWindow != null) {
      Component focusOwner = activeWindow.getFocusOwner();
      Log.d(TAG, "Clearing global focus owner " + focusOwner);
      if (focusOwner != null) {
        FocusEvent fl = new CausedFocusEvent(focusOwner,
            FocusEvent.FOCUS_LOST,
            false,
            null,
            Cause.CLEAR_GLOBAL_FOCUS_OWNER);
        postPriorityEvent(fl);
      }
    }
  }
}
