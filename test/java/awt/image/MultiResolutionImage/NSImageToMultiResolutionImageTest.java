/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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

import java.awt.Image;
import java.awt.Toolkit;
import sun.awt.OSInfo;
import sun.awt.OSInfo.OSType;
import sun.awt.image.MultiResolutionImage;
/*
 * @test
 * @bug 8033534 8035069
 * @summary [macosx] Get MultiResolution image from native system
 * @author Alexander Scherbatiy
 * @run main NSImageToMultiResolutionImageTest
 */

public final class NSImageToMultiResolutionImageTest {

  private NSImageToMultiResolutionImageTest() {
  }

  public static void main(String[] args) throws Exception {

        if (OSInfo.getOSType() != OSType.MACOSX) {
            return;
        }

        String icon = "NSImage://NSApplicationIcon";
        Image image = Toolkit.getDefaultToolkit().getImage(icon);

        if (!(image instanceof MultiResolutionImage)) {
            throw new RuntimeException("Icon does not have resolution variants!");
        }

        MultiResolutionImage multiResolutionImage = (MultiResolutionImage) image;

        int width = 0;
        int height = 0;

        for (Image resolutionVariant : multiResolutionImage.getResolutionVariants()) {
            int rvWidth = resolutionVariant.getWidth(null);
            int rvHeight = resolutionVariant.getHeight(null);
            if (rvWidth < width || rvHeight < height) {
                throw new RuntimeException("Resolution variants are not sorted!");
            }
            width = rvWidth;
            height = rvHeight;
        }
    }
}
