/*
 * Copyright (c) 1997, 2011, Oracle and/or its affiliates. All rights reserved.
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

/*
 * @author Charlton Innovations, Inc.
 */

package sun.java2d.loops;

import java.awt.AlphaComposite;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

/**
 * defines interface for primitives which can be placed into
 * the graphic component manager framework
 */
public abstract class GraphicsPrimitive {

  public static final int TRACELOG = 1;
  public static final int TRACETIMESTAMP = 2;
  public static final int TRACECOUNTS = 4;
  public static int traceflags;
  public static String tracefile;
  public static PrintStream traceout;
  static Map traceMap;
  private static int unusedPrimID = 1;

  static {
    String trace = System.getProperty("sun.java2d.trace");
    String tracefile = null;
    if (trace != null) {
      boolean verbose = false;
      int traceflags = 0;
      StringTokenizer st = new StringTokenizer(trace, ",");
      while (st.hasMoreTokens()) {
        String tok = st.nextToken();
        if ("count".equalsIgnoreCase(tok)) {
          traceflags |= TRACECOUNTS;
        } else if ("log".equalsIgnoreCase(tok)) {
          traceflags |= TRACELOG;
        } else if ("timestamp".equalsIgnoreCase(tok)) {
          traceflags |= TRACETIMESTAMP;
        } else if ("verbose".equalsIgnoreCase(tok)) {
          verbose = true;
        } else if (tok.regionMatches(true, 0, "out:", 0, 4)) {
          tracefile = tok.substring(4);
        } else {
          if (!"help".equalsIgnoreCase(tok)) {
            System.err.println("unrecognized token: " + tok);
          }
          System.err.println("usage: -Dsun.java2d.trace=" +
              "[log[,timestamp]],[count]," +
              "[out:<filename>],[help],[verbose]");
        }
      }
      if (verbose) {
        System.err.print("GraphicsPrimitive logging ");
        if ((traceflags & TRACELOG) != 0) {
          System.err.println("enabled");
          System.err.print("GraphicsPrimitive timetamps ");
          if ((traceflags & TRACETIMESTAMP) != 0) {
            System.err.println("enabled");
          } else {
            System.err.println("disabled");
          }
        } else {
          System.err.println("[and timestamps] disabled");
        }
        System.err.print("GraphicsPrimitive invocation counts ");
        if ((traceflags & TRACECOUNTS) != 0) {
          System.err.println("enabled");
        } else {
          System.err.println("disabled");
        }
        System.err.print("GraphicsPrimitive trace output to ");
        if (tracefile == null) {
          System.err.println("System.err");
        } else {
          System.err.println("file '" + tracefile + "'");
        }
      }
      GraphicsPrimitive.traceflags = traceflags;
    }
    GraphicsPrimitive.tracefile = tracefile;
  }

  /**
   * INSTANCE DATA MEMBERS DESCRIBING CHARACTERISTICS OF THIS PRIMITIVE
   **/

  // Making these be instance data members (instead of virtual methods
  // overridden by subclasses) is actually cheaper, since each class
  // is a singleton.  As instance data members with final accessors,
  // accesses can be inlined.
  private final String methodSignature;
  private final int uniqueID;
  private final SurfaceType sourceType;
  private final CompositeType compositeType;

  /**
   * METHODS TO DESCRIBE THE SURFACES PRIMITIVES
   * CAN OPERATE ON AND THE FUNCTIONALITY THEY IMPLEMENT
   **/
  private final SurfaceType destType;
  private long pNativePrim;   // Native blit loop info
  private String cachedname;

  /**
   * Create a new GraphicsPrimitive with all of the required
   * descriptive information.
   */
  protected GraphicsPrimitive(
      String methodSignature, int primTypeID, SurfaceType sourceType, CompositeType compositeType,
      SurfaceType destType) {
    this.methodSignature = methodSignature;
    this.sourceType = sourceType;
    this.compositeType = compositeType;
    this.destType = destType;

    uniqueID = sourceType == null || compositeType == null || destType == null ? primTypeID << 24
        : makeUniqueID(primTypeID, sourceType, compositeType, destType);
  }

  /**
   * Create a new GraphicsPrimitive for native invocation
   * with all of the required descriptive information.
   */
  protected GraphicsPrimitive(
      long pNativePrim, String methodSignature, int primTypeID, SurfaceType sourceType,
      CompositeType compositeType, SurfaceType destType) {
    this.pNativePrim = pNativePrim;
    this.methodSignature = methodSignature;
    this.sourceType = sourceType;
    this.compositeType = compositeType;
    this.destType = destType;

    uniqueID = sourceType == null || compositeType == null || destType == null ? primTypeID << 24
        : makeUniqueID(primTypeID, sourceType, compositeType, destType);
  }

  public static final synchronized int makePrimTypeID() {
    if (unusedPrimID > 255) {
      throw new InternalError("primitive id overflow");
    }
    int result = unusedPrimID;
    unusedPrimID++;
    return result;
  }

  public static final synchronized int makeUniqueID(
      int primTypeID, SurfaceType src, CompositeType cmp, SurfaceType dst) {
    return primTypeID << 24 |
        dst.getUniqueID() << 16 |
        cmp.getUniqueID() << 8 |
        src.getUniqueID();
  }

  static PrintStream getTraceOutputFile() {
    if (traceout == null) {
      if (tracefile != null) {
        FileOutputStream o
            = AccessController.doPrivileged(new PrivilegedAction<FileOutputStream>() {
          @Override
          public FileOutputStream run() {
            try {
              return new FileOutputStream(tracefile);
            } catch (FileNotFoundException e) {
              return null;
            }
          }
        });
        traceout = o != null ? new PrintStream(o) : System.err;
      } else {
        traceout = System.err;
      }
    }
    return traceout;
  }

  public static synchronized void tracePrimitive(Object prim) {
    if ((traceflags & TRACECOUNTS) != 0) {
      if (traceMap == null) {
        traceMap = new HashMap();
        setShutdownHook();
      }
      Object o = traceMap.get(prim);
      if (o == null) {
        o = new int[1];
        traceMap.put(prim, o);
      }
      ((int[]) o)[0]++;
    }
    if ((traceflags & TRACELOG) != 0) {
      PrintStream ps = getTraceOutputFile();
      if ((traceflags & TRACETIMESTAMP) != 0) {
        ps.print(System.currentTimeMillis() + ": ");
      }
      ps.println(prim);
    }
  }

  protected static Blit createConverter(SurfaceType srctype, SurfaceType dsttype) {
    if (srctype.equals(dsttype)) {
      return null;
    }
    Blit cv = Blit.getFromCache(srctype, CompositeType.SrcNoEa, dsttype);
    if (cv == null) {
      throw new InternalError("Cannot construct converter for " +
          srctype + "=>" + dsttype);
    }
    return cv;
  }

  protected static SurfaceData convertFrom(
      Blit ob, SurfaceData srcData, int srcX, int srcY, int w, int h, SurfaceData dstData) {
    return convertFrom(ob, srcData, srcX, srcY, w, h, dstData, BufferedImage.TYPE_INT_ARGB);
  }

  protected static SurfaceData convertFrom(
      Blit ob, SurfaceData srcData, int srcX, int srcY, int w, int h, SurfaceData dstData,
      int type) {
    if (dstData != null) {
      Rectangle r = dstData.getBounds();
      if (w > r.width || h > r.height) {
        dstData = null;
      }
    }
    if (dstData == null) {
      BufferedImage dstBI = new BufferedImage(w, h, type);
      dstData = BufImgSurfaceData.createData(dstBI);
    }
    ob.Blit(srcData, dstData, AlphaComposite.Src, null, srcX, srcY, 0, 0, w, h);
    return dstData;
  }

  protected static void convertTo(
      Blit ob, SurfaceData srcImg, SurfaceData dstImg, Region clip, int dstX, int dstY, int w,
      int h) {
    if (ob != null) {
      ob.Blit(srcImg, dstImg, AlphaComposite.Src, clip, 0, 0, dstX, dstY, w, h);
    }
  }

  protected static GraphicsPrimitive getGeneralOp(int primID, CompositeType comptype) {
    return GraphicsPrimitiveMgr.locatePrim(primID,
        SurfaceType.IntArgb,
        comptype,
        SurfaceType.IntArgb);
  }

  public static String simplename(Field[] fields, Object o) {
    for (Field f : fields) {
      try {
        if (o == f.get(null)) {
          return f.getName();
        }
      } catch (Exception e) {
      }
    }
    return "\"" + o + "\"";
  }

  public static String simplename(SurfaceType st) {
    return simplename(SurfaceType.class.getDeclaredFields(), st);
  }

  public static String simplename(CompositeType ct) {
    return simplename(CompositeType.class.getDeclaredFields(), ct);
  }

  public static void setShutdownHook() {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      @Override
      public Void run() {
        TraceReporter t = new TraceReporter();
        t.setContextClassLoader(null);
        Runtime.getRuntime().addShutdownHook(t);
        return null;
      }
    });
  }

  /**
   * Gets instance ID of this graphics primitive.
   * <p>
   * Instance ID is comprised of four distinct ids (ORed together)
   * that uniquely identify each instance of a GraphicsPrimitive
   * object. The four ids making up instance ID are:
   * 1. primitive id - identifier shared by all primitives of the
   * same type (eg. all Blits have the same primitive id)
   * 2. sourcetype id - identifies source surface type
   * 3. desttype id - identifies destination surface type
   * 4. compositetype id - identifies composite used
   *
   * @return instance ID
   */
  public final int getUniqueID() {
    return uniqueID;
  }

  /**
   */
  public final String getSignature() {
    return methodSignature;
  }

  /**
   * Gets unique id for this GraphicsPrimitive type.
   * <p>
   * This id is used to identify the TYPE of primitive (Blit vs. BlitBg)
   * as opposed to INSTANCE of primitive.
   *
   * @return primitive ID
   */
  public final int getPrimTypeID() {
    return uniqueID >>> 24;
  }

  /**
   */
  public final long getNativePrim() {
    return pNativePrim;
  }

  /**
   */
  public final SurfaceType getSourceType() {
    return sourceType;
  }

  /**
   */
  public final CompositeType getCompositeType() {
    return compositeType;
  }

  /**
   */
  public final SurfaceType getDestType() {
    return destType;
  }

  /**
   * Return true if this primitive can be used for the given signature
   * surfaces, and composite.
   *
   * @param signature The signature of the given operation.  Must be
   *                  == (not just .equals) the signature string given by the
   *                  abstract class that declares the operation.
   * @param srctype   The surface type for the source of the operation
   * @param comptype  The composite type for the operation
   * @param dsttype   The surface type for the destination of the operation
   */
  public final boolean satisfies(
      String signature, SurfaceType srctype, CompositeType comptype, SurfaceType dsttype) {
    if (signature != methodSignature) {
      return false;
    }
    while (true) {
      if (srctype == null) {
        return false;
      }
      if (srctype.equals(sourceType)) {
        break;
      }
      srctype = srctype.getSuperType();
    }
    while (true) {
      if (comptype == null) {
        return false;
      }
      if (comptype.equals(compositeType)) {
        break;
      }
      comptype = comptype.getSuperType();
    }
    while (true) {
      if (dsttype == null) {
        return false;
      }
      if (dsttype.equals(destType)) {
        break;
      }
      dsttype = dsttype.getSuperType();
    }
    return true;
  }

  //
  // A version of satisfies used for regression testing
  //
  final boolean satisfiesSameAs(GraphicsPrimitive other) {
    return methodSignature == other.methodSignature &&
        sourceType.equals(other.sourceType) &&
        compositeType.equals(other.compositeType) &&
        destType.equals(other.destType);
  }

  public abstract GraphicsPrimitive makePrimitive(
      SurfaceType srctype, CompositeType comptype, SurfaceType dsttype);

  public abstract GraphicsPrimitive traceWrap();

  protected void setupGeneralBinaryOp(GeneralBinaryOp gbo) {
    int primID = gbo.getPrimTypeID();
    String methodSignature = gbo.getSignature();
    SurfaceType srctype = gbo.getSourceType();
    CompositeType comptype = gbo.getCompositeType();
    SurfaceType dsttype = gbo.getDestType();
    Blit convertsrc, convertdst, convertres;
    GraphicsPrimitive performop;

    convertsrc = createConverter(srctype, SurfaceType.IntArgb);
    performop = GraphicsPrimitiveMgr.locatePrim(primID, SurfaceType.IntArgb, comptype, dsttype);
    if (performop != null) {
      convertdst = null;
      convertres = null;
    } else {
      performop = getGeneralOp(primID, comptype);
      if (performop == null) {
        throw new InternalError("Cannot construct general op for " +
            methodSignature + " " + comptype);
      }
      convertdst = createConverter(dsttype, SurfaceType.IntArgb);
      convertres = createConverter(SurfaceType.IntArgb, dsttype);
    }

    gbo.setPrimitives(convertsrc, convertdst, performop, convertres);
  }

  protected void setupGeneralUnaryOp(GeneralUnaryOp guo) {
    int primID = guo.getPrimTypeID();
    String methodSignature = guo.getSignature();
    CompositeType comptype = guo.getCompositeType();
    SurfaceType dsttype = guo.getDestType();

    Blit convertdst = createConverter(dsttype, SurfaceType.IntArgb);
    GraphicsPrimitive performop = getGeneralOp(primID, comptype);
    Blit convertres = createConverter(SurfaceType.IntArgb, dsttype);
    if (convertdst == null || performop == null || convertres == null) {
      throw new InternalError("Cannot construct binary op for " +
          comptype + " " + dsttype);
    }

    guo.setPrimitives(convertdst, performop, convertres);
  }

  public String toString() {
    if (cachedname == null) {
      String sig = methodSignature;
      int index = sig.indexOf('(');
      if (index >= 0) {
        sig = sig.substring(0, index);
      }
      cachedname = getClass().getName() + "::" +
          sig + "(" +
          simplename(sourceType) + ", " +
          simplename(compositeType) + ", " +
          simplename(destType) + ")";
    }
    return cachedname;
  }

  protected interface GeneralBinaryOp {
    /**
     * This method allows the setupGeneralBinaryOp method to set
     * the converters into the General version of the Primitive.
     */
    void setPrimitives(
        Blit srcconverter, Blit dstconverter, GraphicsPrimitive genericop, Blit resconverter);

    /**
     * These 4 methods are implemented automatically for any
     * GraphicsPrimitive.  They are used by setupGeneralBinaryOp
     * to retrieve the information needed to find the right
     * converter primitives.
     */
    SurfaceType getSourceType();

    CompositeType getCompositeType();

    SurfaceType getDestType();

    String getSignature();

    int getPrimTypeID();
  }

  protected interface GeneralUnaryOp {
    /**
     * This method allows the setupGeneralUnaryOp method to set
     * the converters into the General version of the Primitive.
     */
    void setPrimitives(Blit dstconverter, GraphicsPrimitive genericop, Blit resconverter);

    /**
     * These 3 methods are implemented automatically for any
     * GraphicsPrimitive.  They are used by setupGeneralUnaryOp
     * to retrieve the information needed to find the right
     * converter primitives.
     */
    CompositeType getCompositeType();

    SurfaceType getDestType();

    String getSignature();

    int getPrimTypeID();
  }

  public static class TraceReporter extends Thread {

    @Override
    public void run() {
      PrintStream ps = getTraceOutputFile();
      Iterator iterator = traceMap.entrySet().iterator();
      long total = 0;
      int numprims = 0;
      while (iterator.hasNext()) {
        Entry me = (Entry) iterator.next();
        Object prim = me.getKey();
        int[] count = (int[]) me.getValue();
        if (count[0] == 1) {
          ps.print("1 call to ");
        } else {
          ps.print(count[0] + " calls to ");
        }
        ps.println(prim);
        numprims++;
        total += count[0];
      }
      if (numprims == 0) {
        ps.println("No graphics primitives executed");
      } else if (numprims > 1) {
        ps.println(total + " total calls to " +
            numprims + " different primitives");
      }
    }
  }
}
