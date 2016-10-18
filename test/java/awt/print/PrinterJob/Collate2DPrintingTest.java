/*
 * Copyright (c) 2009, 2013, Oracle and/or its affiliates. All rights reserved.
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
  @test
 * @bug 6362683 8012381
 * @summary Collation should work.
 * @run main/manual Collate2DPrintingTest
 */

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrintService;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.InputStream;
import java.io.Reader;
import sun.awt.PrintServiceLookup;

public class Collate2DPrintingTest
    extends Frame implements Doc, Printable, ActionListener {

  private static final long serialVersionUID = -7717564129069545724L;
  final Button print2D = new Button("2D Print");
        final Button printMerlin = new Button("PrintService");
        final PrinterJob pj = PrinterJob.getPrinterJob();
        PrintService defService;
        final HashPrintRequestAttributeSet prSet = new HashPrintRequestAttributeSet();

    public Collate2DPrintingTest() {

        Panel butPanel = new Panel();
        butPanel.add(print2D);
        butPanel.add(printMerlin);
      print2D.addActionListener(this);
      printMerlin.addActionListener(this);
      addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing (WindowEvent e) {
              dispose();
            }
        });
      add(BorderLayout.SOUTH, butPanel);

      defService = PrintServiceLookup.lookupDefaultPrintService();
        PrintService[] pservice;
        if (defService == null) {
            pservice = PrintServiceLookup.lookupPrintServices(null, null);
            if (pservice.length == 0) {
                throw new RuntimeException("No printer found.  TEST ABORTED");
            }
          defService = pservice[0];
        }
      prSet.add(SheetCollate.COLLATED);
      prSet.add(new Copies(2));
      pj.setPrintable(this);
      setSize(300, 200);
      setVisible(true);
    }


    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex)
          throws PrinterException {
        g.drawString("Page: " + pageIndex, 100, 100);
      return pageIndex == 2 ? Printable.NO_SUCH_PAGE : Printable.PAGE_EXISTS;
    }

    @Override
    public void actionPerformed (ActionEvent ae) {
        try {
            if (ae.getSource() == print2D) {
                if (pj.printDialog(prSet)) {
                  pj.print(prSet);
                }
            } else {
                DocPrintJob pj = defService.createPrintJob();
                pj.print(this, prSet);
            }
            System.out.println ("DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DocAttributeSet getAttributes() {
        return null;
    }

    public DocFlavor getDocFlavor() {
      return DocFlavor.SERVICE_FORMATTED.PRINTABLE;
    }

    public Object getPrintData() {
        return this;
    }

    public Reader getReaderForText() {
        return null;
    }

    public InputStream getStreamForBytes() {
        return null;
    }

  public static void main( String[] args) {

  String[] instructions =
        {
         "You must have a printer available to perform this test",
         "The print result should be collated."
       };
      Sysout.createDialog( );
      Sysout.printInstructions( instructions );

     new Collate2DPrintingTest();
  }
}


final class Sysout {
   private static TestDialog dialog;

   public static void createDialogWithInstructions( String[] instructions )
    {
      dialog = new TestDialog( new Frame(), "Instructions" );
      dialog.printInstructions( instructions );
      dialog.setVisible(true);
      println( "Any messages for the tester will display here." );
    }

   public static void createDialog( )
    {
      dialog = new TestDialog( new Frame(), "Instructions" );
      String[] defInstr = { "Instructions will appear here. ", "" } ;
      dialog.printInstructions( defInstr );
      dialog.setVisible(true);
      println( "Any messages for the tester will display here." );
    }


   public static void printInstructions( String[] instructions )
    {
      dialog.printInstructions( instructions );
    }


   public static void println( String messageIn )
    {
      dialog.displayMessage( messageIn );
    }

}// Sysout  class

/**
  This is part of the standard test machinery.  It provides a place for the
   test instructions to be displayed, and a place for interactive messages
   to the user to be displayed.
  To have the test instructions displayed, see Sysout.
  To have a message to the user be displayed, see Sysout.
  Do not call anything in this dialog directly.
  */
class TestDialog extends Dialog {

  private static final long serialVersionUID = -175121528743417031L;
  final TextArea instructionsText;
   final TextArea messageText;
   final int maxStringLength = 80;

   //DO NOT call this directly, go through Sysout
   public TestDialog( Frame frame, String name )
    {
      super( frame, name );
      int scrollBoth = TextArea.SCROLLBARS_BOTH;
      instructionsText = new TextArea( "", 15, maxStringLength, scrollBoth );
      add(BorderLayout.NORTH, instructionsText);

      messageText = new TextArea( "", 5, maxStringLength, scrollBoth );
      add(BorderLayout.CENTER, messageText);

      pack();

      setVisible(true);
    }// TestDialog()

   //DO NOT call this directly, go through Sysout
   public void printInstructions( String[] instructions )
    {
      //Clear out any current instructions
      instructionsText.setText( "" );

      //Go down array of instruction strings

      String printStr, remainingStr;
      for (String instruction : instructions) {
        //chop up each into pieces maxSringLength long
        remainingStr = instruction;
        while (!remainingStr.isEmpty()) {
          //if longer than max then chop off first max chars to print
          if (remainingStr.length() >= maxStringLength) {
            //Try to chop on a word boundary
            int posOfSpace = remainingStr.
                lastIndexOf(' ', maxStringLength - 1);

            if (posOfSpace <= 0) {
              posOfSpace = maxStringLength - 1;
            }

            printStr = remainingStr.substring(0, posOfSpace + 1);
            remainingStr = remainingStr.substring(posOfSpace + 1);
          }
          //else just print
          else {
            printStr = remainingStr;
            remainingStr = "";
          }

          instructionsText.append(printStr + "\n");
        }// while
      }// for

    }//printInstructions()

   //DO NOT call this directly, go through Sysout
   public void displayMessage( String messageIn )
    {
      messageText.append( messageIn + "\n" );
    }

 }// TestDialog  class
