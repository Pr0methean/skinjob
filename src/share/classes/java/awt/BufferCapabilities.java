/*
 * Copyright (c) 2000, 2011, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Capabilities and properties of buffers.
 *
 * @author Michael Martak
 * @see java.awt.image.BufferStrategy#getCapabilities()
 * @see GraphicsConfiguration#getBufferCapabilities
 * @since 1.4
 */
public class BufferCapabilities implements Cloneable {

  private final ImageCapabilities frontCaps;
  private final ImageCapabilities backCaps;
  private final FlipContents flipContents;

  /**
   * Creates a new object for specifying buffering capabilities
   *
   * @param frontCaps    the capabilities of the front buffer; cannot be
   *                     {@code null}
   * @param backCaps     the capabilities of the back and intermediate buffers;
   *                     cannot be {@code null}
   * @param flipContents the contents of the back buffer after page-flipping,
   *                     {@code null} if page flipping is not used (implies blitting)
   * @throws IllegalArgumentException if frontCaps or backCaps are
   *                                  {@code null}
   */
  public BufferCapabilities(
      ImageCapabilities frontCaps, ImageCapabilities backCaps, FlipContents flipContents) {
    if (frontCaps == null || backCaps == null) {
      throw new IllegalArgumentException("Image capabilities specified cannot be null");
    }
    this.frontCaps = frontCaps;
    this.backCaps = backCaps;
    this.flipContents = flipContents;
  }

  /**
   * @return the image capabilities of the front (displayed) buffer
   */
  public ImageCapabilities getFrontBufferCapabilities() {
    return frontCaps;
  }

  /**
   * @return the image capabilities of all back buffers (intermediate buffers
   * are considered back buffers)
   */
  public ImageCapabilities getBackBufferCapabilities() {
    return backCaps;
  }

  /**
   * @return whether or not the buffer strategy uses page flipping; a set of
   * buffers that uses page flipping
   * can swap the contents internally between the front buffer and one or
   * more back buffers by switching the video pointer (or by copying memory
   * internally).  A non-flipping set of
   * buffers uses blitting to copy the contents from one buffer to
   * another; when this is the case, {@code getFlipContents} returns
   * {@code null}
   */
  public boolean isPageFlipping() {
    return getFlipContents() != null;
  }

  /**
   * @return the resulting contents of the back buffer after page-flipping.
   * This value is {@code null} when the {@code isPageFlipping}
   * returns {@code false}, implying blitting.  It can be one of
   * {@code FlipContents.UNDEFINED}
   * (the assumed default), {@code FlipContents.BACKGROUND},
   * {@code FlipContents.PRIOR}, or
   * {@code FlipContents.COPIED}.
   * @see #isPageFlipping
   * @see FlipContents#UNDEFINED
   * @see FlipContents#BACKGROUND
   * @see FlipContents#PRIOR
   * @see FlipContents#COPIED
   */
  public FlipContents getFlipContents() {
    return flipContents;
  }

  /**
   * @return whether page flipping is only available in full-screen mode.  If this
   * is {@code true}, full-screen exclusive mode is required for
   * page-flipping.
   * @see #isPageFlipping
   * @see GraphicsDevice#setFullScreenWindow
   */
  public boolean isFullScreenRequired() {
    return false;
  }

  /**
   * @return whether or not
   * page flipping can be performed using more than two buffers (one or more
   * intermediate buffers as well as the front and back buffer).
   * @see #isPageFlipping
   */
  public boolean isMultiBufferAvailable() {
    return false;
  }

  /**
   * @return a copy of this BufferCapabilities object.
   */
  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      // Since we implement Cloneable, this should never happen
      throw new InternalError(e);
    }
  }

  // Inner class FlipContents

  /**
   * A type-safe enumeration of the possible back buffer contents after
   * page-flipping
   *
   * @since 1.4
   */
  public static final class FlipContents extends AttributeValue {

    private static final String[] NAMES = {"undefined", "background", "prior", "copied"};
    private static final int I_UNDEFINED = 0;
    /**
     * When flip contents are {@code UNDEFINED}, the
     * contents of the back buffer are undefined after flipping.
     *
     * @see #isPageFlipping
     * @see #getFlipContents
     * @see #BACKGROUND
     * @see #PRIOR
     * @see #COPIED
     */
    public static final FlipContents UNDEFINED = new FlipContents(I_UNDEFINED);
    private static final int I_BACKGROUND = 1;
    /**
     * When flip contents are {@code BACKGROUND}, the
     * contents of the back buffer are cleared with the background color after
     * flipping.
     *
     * @see #isPageFlipping
     * @see #getFlipContents
     * @see #UNDEFINED
     * @see #PRIOR
     * @see #COPIED
     */
    public static final FlipContents BACKGROUND = new FlipContents(I_BACKGROUND);
    private static final int I_PRIOR = 2;
    /**
     * When flip contents are {@code PRIOR}, the
     * contents of the back buffer are the prior contents of the front buffer
     * (a true page flip).
     *
     * @see #isPageFlipping
     * @see #getFlipContents
     * @see #UNDEFINED
     * @see #BACKGROUND
     * @see #COPIED
     */
    public static final FlipContents PRIOR = new FlipContents(I_PRIOR);
    private static final int I_COPIED = 3;
    /**
     * When flip contents are {@code COPIED}, the
     * contents of the back buffer are copied to the front buffer when
     * flipping.
     *
     * @see #isPageFlipping
     * @see #getFlipContents
     * @see #UNDEFINED
     * @see #BACKGROUND
     * @see #PRIOR
     */
    public static final FlipContents COPIED = new FlipContents(I_COPIED);

    private FlipContents(int type) {
      super(type, NAMES);
    }
  } // Inner class FlipContents
}
