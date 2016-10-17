/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

/*
 * @test
 * @bug 8029204
 * @summary Tests GlyphVector is printed in the correct location
 * @run main/manual=yesno PrintGlyphVectorTest
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.print.*;

public class PrintGlyphVectorTest extends Component implements Printable {

  private static final long serialVersionUID = 5496726229031792455L;

  public void drawGVs(Graphics g) {

        String testString = "0123456789abcdefghijklm";
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.black);
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 30);
        FontRenderContext frc = g2d.getFontRenderContext();
        GlyphVector v = font.createGlyphVector(frc, testString);


        float x = 50f,
              y = 50f;

        g2d.drawGlyphVector(v, x, y);
        Rectangle2D r = v.getVisualBounds();
        r.setRect(r.getX()+x, r.getY()+y, r.getWidth(), r.getHeight());
        g2d.draw(r);

        Point2D p; // .Float p = new Point2D.Float();
        for (int i = 0; i < v.getNumGlyphs(); i++) {
            p = v.getGlyphPosition(i);
            p.setLocation(p.getX()+50, p.getY());
            v.setGlyphPosition(i, p);
        }

        x = 0;
        y+= 50;

        g2d.drawGlyphVector(v, x, y);
        r = v.getVisualBounds();
        r.setRect(r.getX()+x, r.getY()+y, r.getWidth(), r.getHeight());
        g2d.draw(r);



    }

     @Override
     public void paint(Graphics g) {
       g.setColor(Color.white);
       g.fillRect(0,0, getSize().width, getSize().height);
       drawGVs(g);
     }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600,200);
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) {

        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
      drawGVs(g2d);

        return Printable.PAGE_EXISTS;
    }


    public static void main(String[] arg) throws Exception {

       Frame f = new Frame();
       PrintGlyphVectorTest pvt = new PrintGlyphVectorTest();
       f.add(BorderLayout.CENTER, pvt);
       f.add(BorderLayout.SOUTH, new PrintInstructions());
       f.pack();
       f.show();


    }
}

class PrintInstructions extends Panel implements ActionListener {

   static final String INSTRUCTIONS =
       "You must have a printer installed for this test.\n" +
       "Press the PRINT button below and OK the print dialog\n" +
       "Retrieve the output and compare the printed and on-screen text\n" +
       " to confirm that in both cases the text is aligned and the boxes\n" +
       "are around the text, not offset from the text.";
  private static final long serialVersionUID = -1254827888824398571L;

  PrintInstructions() {

    setLayout(new GridLayout(2,1));
     TextArea t = new TextArea(INSTRUCTIONS, 8, 80);
    add(t);
     Button b = new Button("PRINT");
     b.setFont(new Font(OwnedWindowsSerialization.DIALOG_LABEL, Font.BOLD, 30));
     b.addActionListener(this);
    add(b);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
       PrinterJob pj = PrinterJob.getPrinterJob();
       if (pj == null ||
           pj.getPrintService() == null ||
           !pj.printDialog()) {
           return;
       }

       pj.setPrintable(new PrintGlyphVectorTest());
       try {
           pj.print();
       } catch (PrinterException ex) {
           System.err.println(ex);
       }
  }

}
