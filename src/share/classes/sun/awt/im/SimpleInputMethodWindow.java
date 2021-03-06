/*
 * Copyright (c) 1999, 2011, Oracle and/or its affiliates. All rights reserved.
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

package sun.awt.im;

import java.awt.Frame;
import java.awt.geom.Area;

/**
 * Implements a simple input method window that provides the minimal
 * functionality as specified in
 * {@link java.awt.im.spi.InputMethodContext#createInputMethodWindow}.
 */
public class SimpleInputMethodWindow extends Frame implements InputMethodWindow {

  // Proclaim serial compatibility with 1.7.0
  private static final long serialVersionUID = 5093376647036461555L;
  InputContext inputContext;
  private Area contentPane;

  /**
   * Constructs a simple input method window.
   */
  public SimpleInputMethodWindow(String title, InputContext context) {
    super(title);
    if (context != null) {
      inputContext = context;
    }
    setFocusableWindowState(false);
  }

  @Override
  public java.awt.im.InputContext getInputContext() {
    return inputContext != null ? inputContext : super.getInputContext();
  }

  @Override
  public void setInputContext(InputContext inputContext) {
    this.inputContext = inputContext;
  }

  public Area getContentPane() {
    return contentPane;
  }
}
