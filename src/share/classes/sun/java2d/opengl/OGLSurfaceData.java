/*
 * Copyright (c) 2003, 2012, Oracle and/or its affiliates. All rights reserved.
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

package sun.java2d.opengl;

import static sun.java2d.opengl.OGLContext.OGLContextCaps.CAPS_EXT_LCD_SHADER;
import static sun.java2d.opengl.OGLContext.OGLContextCaps.CAPS_EXT_TEXRECT;
import static sun.java2d.opengl.OGLContext.OGLContextCaps.CAPS_MULTITEXTURE;
import static sun.java2d.opengl.OGLContext.OGLContextCaps.CAPS_PS30;
import static sun.java2d.opengl.OGLContext.OGLContextCaps.CAPS_TEXNONPOW2;
import static sun.java2d.pipe.BufferedOpCodes.DISPOSE_SURFACE;
import static sun.java2d.pipe.BufferedOpCodes.FLUSH_SURFACE;
import static sun.java2d.pipe.BufferedOpCodes.SWAP_BUFFERS;

import java.awt.AlphaComposite;
import java.awt.Transparency;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.security.AccessController;
import sun.awt.SunHints;
import sun.awt.image.PixelConverter.ArgbPre;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.PixelToParallelogramConverter;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.hw.AccelSurface;

/**
 * This class describes an OpenGL "surface", that is, a region of pixels
 * managed via OpenGL.  An OGLSurfaceData can be tagged with one of three
 * different SurfaceType objects for the purpose of registering loops, etc.
 * This diagram shows the hierarchy of OGL SurfaceTypes:
 * <p>
 * Any
 * /     \
 * OpenGLSurface     OpenGLTexture
 * |
 * OpenGLSurfaceRTT
 * <p>
 * OpenGLSurface
 * This kind of surface can be rendered to using OpenGL APIs.  It is also
 * possible to copy an OpenGLSurface to another OpenGLSurface (or to itself).
 * This is typically accomplished by calling MakeContextCurrent(dstSD, srcSD)
 * and then calling glCopyPixels() (although there are other techniques to
 * achieve the same goal).
 * <p>
 * OpenGLTexture
 * This kind of surface cannot be rendered to using OpenGL (in the same sense
 * as in OpenGLSurface).  However, it is possible to upload a region of pixels
 * to an OpenGLTexture object via glTexSubImage2D().  One can also copy a
 * surface of type OpenGLTexture to an OpenGLSurface by binding the texture
 * to a quad and then rendering it to the destination surface (this process
 * is known as "texture mapping").
 * <p>
 * OpenGLSurfaceRTT
 * This kind of surface can be thought of as a sort of hybrid between
 * OpenGLSurface and OpenGLTexture, in that one can render to this kind of
 * surface as if it were of type OpenGLSurface, but the process of copying
 * this kind of surface to another is more like an OpenGLTexture.  (Note that
 * "RTT" stands for "render-to-texture".)
 * <p>
 * In addition to these SurfaceType variants, we have also defined some
 * constants that describe in more detail the type of underlying OpenGL
 * surface.  This table helps explain the relationships between those
 * "type" constants and their corresponding SurfaceType:
 * <p>
 * OGL Type          Corresponding SurfaceType
 * --------          -------------------------
 * WINDOW            OpenGLSurface
 * PBUFFER           OpenGLSurface
 * TEXTURE           OpenGLTexture
 * FLIP_BACKBUFFER   OpenGLSurface
 * FBOBJECT          OpenGLSurfaceRTT
 */
public abstract class OGLSurfaceData extends SurfaceData implements AccelSurface {

  /**
   * OGL-specific surface types
   *
   * @see AccelSurface
   */
  public static final int PBUFFER = RT_PLAIN;
  public static final int FBOBJECT = RT_TEXTURE;

  /**
   * Pixel formats
   */
  public static final int PF_INT_ARGB = 0;
  public static final int PF_INT_ARGB_PRE = 1;
  public static final int PF_INT_RGB = 2;
  public static final int PF_INT_RGBX = 3;
  public static final int PF_INT_BGR = 4;
  public static final int PF_INT_BGRX = 5;
  public static final int PF_USHORT_565_RGB = 6;
  public static final int PF_USHORT_555_RGB = 7;
  public static final int PF_USHORT_555_RGBX = 8;
  public static final int PF_BYTE_GRAY = 9;
  public static final int PF_USHORT_GRAY = 10;
  public static final int PF_3BYTE_BGR = 11;

  /**
   * SurfaceTypes
   */
  private static final String DESC_OPENGL_SURFACE = "OpenGL Surface";
  static final SurfaceType OpenGLSurface = SurfaceType.Any.deriveSubType(DESC_OPENGL_SURFACE,
      ArgbPre.instance);
  private static final String DESC_OPENGL_SURFACE_RTT = "OpenGL Surface (render-to-texture)";
  static final SurfaceType OpenGLSurfaceRTT = OpenGLSurface.deriveSubType(DESC_OPENGL_SURFACE_RTT);
  private static final String DESC_OPENGL_TEXTURE = "OpenGL Texture";
  static final SurfaceType OpenGLTexture = SurfaceType.Any.deriveSubType(DESC_OPENGL_TEXTURE);
  /**
   * This will be true if the fbobject system property has been enabled.
   */
  private static final boolean isFBObjectEnabled;
  /**
   * This will be true if the lcdshader system property has been enabled.
   */
  private static final boolean isLCDShaderEnabled;
  /**
   * This will be true if the biopshader system property has been enabled.
   */
  private static final boolean isBIOpShaderEnabled;
  /**
   * This will be true if the gradshader system property has been enabled.
   */
  private static final boolean isGradShaderEnabled;
  protected static final OGLRenderer oglRenderPipe;
  protected static final PixelToParallelogramConverter oglTxRenderPipe;
  protected static final ParallelogramPipe oglAAPgramPipe;
  protected static OGLTextRenderer oglTextPipe;
  protected static final OGLDrawImage oglImagePipe;

  static {
    // fbobject currently enabled by default; use "false" to disable
    String fbo = AccessController.doPrivileged(new sun.security.action.GetPropertyAction(
        "sun.java2d.opengl.fbobject"));
    isFBObjectEnabled = !"false".equals(fbo);

    // lcdshader currently enabled by default; use "false" to disable
    String lcd = AccessController.doPrivileged(new sun.security.action.GetPropertyAction(
        "sun.java2d.opengl.lcdshader"));
    isLCDShaderEnabled = !"false".equals(lcd);

    // biopshader currently enabled by default; use "false" to disable
    String biop = AccessController.doPrivileged(new sun.security.action.GetPropertyAction(
        "sun.java2d.opengl.biopshader"));
    isBIOpShaderEnabled = !"false".equals(biop);

    // gradshader currently enabled by default; use "false" to disable
    String grad = AccessController.doPrivileged(new sun.security.action.GetPropertyAction(
        "sun.java2d.opengl.gradshader"));
    isGradShaderEnabled = !"false".equals(grad);

    OGLRenderQueue rq = OGLRenderQueue.getInstance();
    oglImagePipe = new OGLDrawImage();
    oglTextPipe = new OGLTextRenderer(rq);
    oglRenderPipe = new OGLRenderer(rq);
    if (tracingEnabled()) {
      oglTextPipe = oglTextPipe.traceWrap();
      //The wrapped oglRenderPipe will wrap the AA pipe as well...
      //oglAAPgramPipe = oglRenderPipe.traceWrap();
    }
    oglAAPgramPipe = oglRenderPipe.getAAParallelogramPipe();
    oglTxRenderPipe = new PixelToParallelogramConverter(oglRenderPipe,
        oglRenderPipe,
        1.0,
        0.25,
        true);

    OGLBlitLoops.register();
    OGLMaskFill.register();
    OGLMaskBlit.register();
  }

  private final OGLGraphicsConfig graphicsConfig;
  protected final int type;
  // these fields are set from the native code when the surface is
  // initialized
  private int nativeWidth, nativeHeight;

  protected OGLSurfaceData(OGLGraphicsConfig gc, ColorModel cm, int type) {
    super(getCustomSurfaceType(type), cm);
    graphicsConfig = gc;
    this.type = type;
    setBlitProxyKey(gc.getProxyKey());
  }

  /**
   * Returns the appropriate SurfaceType corresponding to the given OpenGL
   * surface type constant (e.g. TEXTURE -> OpenGLTexture).
   */
  private static SurfaceType getCustomSurfaceType(int oglType) {
    switch (oglType) {
      case TEXTURE:
        return OpenGLTexture;
      case FBOBJECT:
        return OpenGLSurfaceRTT;
      case PBUFFER:
      default:
        return OpenGLSurface;
    }
  }

  /**
   * Disposes the native resources associated with the given OGLSurfaceData
   * (referenced by the pData parameter).  This method is invoked from
   * the native Dispose() method from the Disposer thread when the
   * Java-level OGLSurfaceData object is about to go away.  Note that we
   * also pass a reference to the native GLX/WGLGraphicsConfigInfo
   * (pConfigInfo) for the purposes of making a context current.
   */
  static void dispose(long pData, long pConfigInfo) {
    OGLRenderQueue rq = OGLRenderQueue.getInstance();
    rq.lock();
    try {
      // make sure we have a current context before
      // disposing the native resources (e.g. texture object)
      OGLContext.setScratchSurface(pConfigInfo);

      RenderBuffer buf = rq.getBuffer();
      rq.ensureCapacityAndAlignment(12, 4);
      buf.putInt(DISPOSE_SURFACE);
      buf.putLong(pData);

      // this call is expected to complete synchronously, so flush now
      rq.flushNow();
    } finally {
      rq.unlock();
    }
  }

  static void swapBuffers(long window) {
    OGLRenderQueue rq = OGLRenderQueue.getInstance();
    rq.lock();
    try {
      RenderBuffer buf = rq.getBuffer();
      rq.ensureCapacityAndAlignment(12, 4);
      buf.putInt(SWAP_BUFFERS);
      buf.putLong(window);
      rq.flushNow();
    } finally {
      rq.unlock();
    }
  }

  public static SurfaceDataProxy createProxy(
      SurfaceData srcData, OGLGraphicsConfig dstConfig) {
    if (srcData instanceof OGLSurfaceData) {
      // srcData must be a VolatileImage which either matches
      // our pixel format or not - either way we do not cache it...
      return SurfaceDataProxy.UNCACHED;
    }

    return new OGLSurfaceDataProxy(dstConfig, srcData.getTransparency());
  }

  public static boolean tracingEnabled() {
    return GraphicsPrimitive.traceflags != 0;
  }

  protected boolean initTexture(
      long pData, boolean isOpaque, boolean texNonPow2, boolean texRect, int width, int height) {
    // TODO: Native in OpenJDK AWT
    return false;
  }

  protected boolean initFBObject(
      long pData, boolean isOpaque, boolean texNonPow2, boolean texRect, int width, int height) {
    // TODO: Native in OpenJDK AWT
    return false;
  }

  protected boolean initFlipBackbuffer(long pData) {
    // TODO: Native in OpenJDK AWT
    return false;
  }

  protected abstract boolean initPbuffer(
      long pData, long pConfigInfo, boolean isOpaque, int width, int height);

  private int getTextureTarget(long pData) {
    // TODO: Native in OpenJDK AWT
    return 0;
  }

  private int getTextureID(long pData) {
    // TODO: Native in OpenJDK AWT
    return 0;
  }

  @Override
  public SurfaceDataProxy makeProxyFor(SurfaceData srcData) {
    return createProxy(srcData, graphicsConfig);
  }

  /**
   * For now, we can only render LCD text if:
   * - the fragment shader extension is available, and
   * - blending is disabled, and
   * - the source color is opaque
   * - and the destination is opaque
   * <p>
   * Eventually, we could enhance the native OGL text rendering code
   * and remove the above restrictions, but that would require significantly
   * more code just to support a few uncommon cases.
   */
  @Override
  public boolean canRenderLCDText(SunGraphics2D sg2d) {
    return graphicsConfig.isCapPresent(CAPS_EXT_LCD_SHADER) &&
        sg2d.compositeState <= SunGraphics2D.COMP_ISCOPY &&
        sg2d.paintState <= SunGraphics2D.PAINT_OPAQUECOLOR &&
        sg2d.surfaceData.getTransparency() == Transparency.OPAQUE;
  }

  @Override
  public void validatePipe(SunGraphics2D sg2d) {
    TextPipe textpipe;
    boolean validated = false;

    // OGLTextRenderer handles both AA and non-AA text, but
    // only works with the following modes:
    // (Note: For LCD text we only enter this code path if
    // canRenderLCDText() has already validated that the mode is
    // CompositeType.SrcNoEa (opaque color), which will be subsumed
    // by the CompositeType.SrcNoEa (any color) test below.)

    if (/* CompositeType.SrcNoEa (any color) */
        sg2d.compositeState <= SunGraphics2D.COMP_ISCOPY
            && sg2d.paintState <= SunGraphics2D.PAINT_ALPHACOLOR ||

            /* CompositeType.SrcOver (any color) */
            sg2d.compositeState == SunGraphics2D.COMP_ALPHA &&
                sg2d.paintState <= SunGraphics2D.PAINT_ALPHACOLOR &&
                ((AlphaComposite) sg2d.composite).getRule() == AlphaComposite.SRC_OVER ||

            /* CompositeType.Xor (any color) */
            sg2d.compositeState == SunGraphics2D.COMP_XOR
                && sg2d.paintState <= SunGraphics2D.PAINT_ALPHACOLOR) {
      textpipe = oglTextPipe;
    } else {
      // do this to initialize textpipe correctly; we will attempt
      // to override the non-text pipes below
      super.validatePipe(sg2d);
      textpipe = sg2d.textpipe;
      validated = true;
    }

    PixelToParallelogramConverter txPipe = null;
    OGLRenderer nonTxPipe = null;

    if (sg2d.antialiasHint != SunHints.INTVAL_ANTIALIAS_ON) {
      if (sg2d.paintState <= SunGraphics2D.PAINT_ALPHACOLOR) {
        if (sg2d.compositeState <= SunGraphics2D.COMP_XOR) {
          txPipe = oglTxRenderPipe;
          nonTxPipe = oglRenderPipe;
        }
      } else if (sg2d.compositeState <= SunGraphics2D.COMP_ALPHA) {
        if (OGLPaints.isValid(sg2d)) {
          txPipe = oglTxRenderPipe;
          nonTxPipe = oglRenderPipe;
        }
        // custom paints handled by super.validatePipe() below
      }
    } else {
      if (sg2d.paintState <= SunGraphics2D.PAINT_ALPHACOLOR) {
        if (graphicsConfig.isCapPresent(CAPS_PS30) && (sg2d.imageComp == CompositeType.SrcOverNoEa
                                                           || sg2d.imageComp
            == CompositeType.SrcOver)) {
          if (!validated) {
            super.validatePipe(sg2d);
            validated = true;
          }
          PixelToParallelogramConverter aaConverter
              = new PixelToParallelogramConverter(sg2d.shapepipe,
              oglAAPgramPipe,
              1.0 / 8.0,
              0.499,
              false);
          sg2d.drawpipe = aaConverter;
          sg2d.fillpipe = aaConverter;
          sg2d.shapepipe = aaConverter;
        } else if (sg2d.compositeState == SunGraphics2D.COMP_XOR) {
          // install the solid pipes when AA and XOR are both enabled
          txPipe = oglTxRenderPipe;
          nonTxPipe = oglRenderPipe;
        }
      }
      // other cases handled by super.validatePipe() below
    }

    if (txPipe != null) {
      if (sg2d.transformState >= SunGraphics2D.TRANSFORM_TRANSLATESCALE) {
        sg2d.drawpipe = txPipe;
        sg2d.fillpipe = txPipe;
      } else if (sg2d.strokeState != SunGraphics2D.STROKE_THIN) {
        sg2d.drawpipe = txPipe;
        sg2d.fillpipe = nonTxPipe;
      } else {
        sg2d.drawpipe = nonTxPipe;
        sg2d.fillpipe = nonTxPipe;
      }
      // Note that we use the transforming pipe here because it
      // will examine the shape and possibly perform an optimized
      // operation if it can be simplified.  The simplifications
      // will be valid for all STROKE and TRANSFORM types.
      sg2d.shapepipe = txPipe;
    } else {
      if (!validated) {
        super.validatePipe(sg2d);
      }
    }

    // install the text pipe based on our earlier decision
    sg2d.textpipe = textpipe;

    // always override the image pipe with the specialized OGL pipe
    sg2d.imagepipe = oglImagePipe;
  }

  @Override
  protected MaskFill getMaskFill(SunGraphics2D sg2d) {
    if (sg2d.paintState > SunGraphics2D.PAINT_ALPHACOLOR) {
            /*
             * We can only accelerate non-Color MaskFill operations if
             * all of the following conditions hold true:
             *   - there is an implementation for the given paintState
             *   - the current Paint can be accelerated for this destination
             *   - multitexturing is available (since we need to modulate
             *     the alpha mask texture with the paint texture)
             *
             * In all other cases, we return null, in which case the
             * validation code will choose a more general software-based loop.
             */
      if (!OGLPaints.isValid(sg2d) || !graphicsConfig.isCapPresent(CAPS_MULTITEXTURE)) {
        return null;
      }
    }
    return super.getMaskFill(sg2d);
  }

  @Override
  public Raster getRaster(int x, int y, int w, int h) {
    throw new InternalError("not implemented yet");
  }

  @Override
  public boolean copyArea(SunGraphics2D sg2d, int x, int y, int w, int h, int dx, int dy) {
    if (sg2d.transformState < SunGraphics2D.TRANSFORM_TRANSLATESCALE
        && sg2d.compositeState < SunGraphics2D.COMP_XOR) {
      x += sg2d.transX;
      y += sg2d.transY;

      oglRenderPipe.copyArea(sg2d, x, y, w, h, dx, dy);

      return true;
    }
    return false;
  }

  @Override
  public void flush() {
    invalidate();
    OGLRenderQueue rq = OGLRenderQueue.getInstance();
    rq.lock();
    try {
      // make sure we have a current context before
      // disposing the native resources (e.g. texture object)
      OGLContext.setScratchSurface(graphicsConfig);

      RenderBuffer buf = rq.getBuffer();
      rq.ensureCapacityAndAlignment(12, 4);
      buf.putInt(FLUSH_SURFACE);
      buf.putLong(getNativeOps());

      // this call is expected to complete synchronously, so flush now
      rq.flushNow();
    } finally {
      rq.unlock();
    }
  }

  /**
   * Note: This should only be called from the QFT under the AWT lock.
   * This method is kept separate from the initSurface() method below just
   * to keep the code a bit cleaner.
   */
  void initSurfaceNow(int width, int height) {
    boolean isOpaque = getTransparency() == Transparency.OPAQUE;
    boolean success = false;

    switch (type) {
      case PBUFFER:
        success = initPbuffer(getNativeOps(),
            graphicsConfig.getNativeConfigInfo(),
            isOpaque,
            width,
            height);
        break;

      case TEXTURE:
        success = initTexture(getNativeOps(),
            isOpaque,
            isTexNonPow2Available(),
            isTexRectAvailable(),
            width,
            height);
        break;

      case FBOBJECT:
        success = initFBObject(getNativeOps(),
            isOpaque,
            isTexNonPow2Available(),
            isTexRectAvailable(),
            width,
            height);
        break;

      case FLIP_BACKBUFFER:
        success = initFlipBackbuffer(getNativeOps());
        break;

      default:
        break;
    }

    if (!success) {
      throw new OutOfMemoryError("can't create offscreen surface");
    }
  }

  /**
   * Initializes the appropriate OpenGL offscreen surface based on the value
   * of the type parameter.  If the surface creation fails for any reason,
   * an OutOfMemoryError will be thrown.
   */
  protected void initSurface(int width, int height) {
    OGLRenderQueue rq = OGLRenderQueue.getInstance();
    rq.lock();
    try {
      switch (type) {
        case TEXTURE:
        case PBUFFER:
        case FBOBJECT:
          // need to make sure the context is current before
          // creating the texture (or pbuffer, or fbobject)
          OGLContext.setScratchSurface(graphicsConfig);
          break;
        default:
          break;
      }
      rq.flushAndInvokeNow(new Runnable() {
        @Override
        public void run() {
          initSurfaceNow(width, height);
        }
      });
    } finally {
      rq.unlock();
    }
  }

  /**
   * Returns the OGLContext for the GraphicsConfig associated with this
   * surface.
   */
  @Override
  public final OGLContext getContext() {
    return graphicsConfig.getContext();
  }

  /**
   * Returns the OGLGraphicsConfig associated with this surface.
   */
  final OGLGraphicsConfig getOGLGraphicsConfig() {
    return graphicsConfig;
  }

  /**
   * Returns one of the surface type constants defined above.
   */
  public final int getType() {
    return type;
  }

  /**
   * If this surface is backed by a texture object, returns the target
   * for that texture (either GL_TEXTURE_2D or GL_TEXTURE_RECTANGLE_ARB).
   * Otherwise, this method will return zero.
   */
  public final int getTextureTarget() {
    return getTextureTarget(getNativeOps());
  }

  /**
   * If this surface is backed by a texture object, returns the texture ID
   * for that texture.
   * Otherwise, this method will return zero.
   */
  public final int getTextureID() {
    return getTextureID(getNativeOps());
  }

  /**
   * Returns true if OpenGL textures can have non-power-of-two dimensions
   * when using the basic GL_TEXTURE_2D target.
   */
  boolean isTexNonPow2Available() {
    return graphicsConfig.isCapPresent(CAPS_TEXNONPOW2);
  }

  /**
   * Returns true if OpenGL textures can have non-power-of-two dimensions
   * when using the GL_TEXTURE_RECTANGLE_ARB target (only available when the
   * GL_ARB_texture_rectangle extension is present).
   */
  boolean isTexRectAvailable() {
    return graphicsConfig.isCapPresent(CAPS_EXT_TEXRECT);
  }

  /**
   * Returns true if the surface is an on-screen window surface or
   * a FBO texture attached to an on-screen CALayer.
   * <p>
   * Needed by Mac OS X port.
   */
  boolean isOnScreen() {
    return getType() == WINDOW;
  }
}
