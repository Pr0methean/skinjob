/*
 * Copyright (c) 2000, 2012, Oracle and/or its affiliates. All rights reserved.
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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import sun.awt.datatransfer.DataTransferer;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.*;
import java.awt.im.InputMethodHighlight;
import java.awt.im.InputMethodRequests;
import java.awt.image.*;
import java.awt.datatransfer.Clipboard;
import java.awt.peer.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Properties;

public class HeadlessToolkit extends Toolkit
    implements ComponentFactory, KeyboardFocusManagerPeerProvider {

    private static final KeyboardFocusManagerPeer kfmPeer = new KeyboardFocusManagerPeer() {
        public void setCurrentFocusedWindow(Window win) {}
        public Window getCurrentFocusedWindow() { return null; }
        public void setCurrentFocusOwner(Component comp) {}
        public Component getCurrentFocusOwner() { return null; }
        public void clearGlobalFocusOwner(Window activeWindow) {}
    };

    private Toolkit tk;
    private ComponentFactory componentFactory;

    public HeadlessToolkit(Toolkit tk) {
        this.tk = tk;
        if (tk instanceof ComponentFactory) {
            componentFactory = (ComponentFactory)tk;
        }
    }

    public Toolkit getUnderlyingToolkit() {
        return tk;
    }

    /*
     * Component peer objects.
     */

    /* Lightweight implementation of Canvas and Panel */

    public CanvasPeer createCanvas(Canvas target) {
        return (CanvasPeer)createComponent(target);
    }

    public PanelPeer createPanel(Panel target) {
        return (PanelPeer)createComponent(target);
    }

    /*
     * Component peer objects - unsupported.
     */

    public WindowPeer createWindow(Window target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public FramePeer createFrame(Frame target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public DialogPeer createDialog(Dialog target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public ButtonPeer createButton(Button target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public TextFieldPeer createTextField(TextField target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public ChoicePeer createChoice(Choice target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public LabelPeer createLabel(Label target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public ListPeer createList(List target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public CheckboxPeer createCheckbox(Checkbox target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public ScrollbarPeer createScrollbar(Scrollbar target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public ScrollPanePeer createScrollPane(ScrollPane target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public TextAreaPeer createTextArea(TextArea target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public FileDialogPeer createFileDialog(FileDialog target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public MenuBarPeer createMenuBar(MenuBar target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public MenuPeer createMenu(Menu target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public PopupMenuPeer createPopupMenu(PopupMenu target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public MenuItemPeer createMenuItem(MenuItem target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem target)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public DragSourceContextPeer createDragSourceContextPeer(
        DragGestureEvent dge)
        throws InvalidDnDOperationException {
        throw new InvalidDnDOperationException("Headless environment");
    }

    public RobotPeer createRobot(Robot target, GraphicsDevice screen)
        throws AWTException, HeadlessException {
        throw new HeadlessException();
    }

    public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() {
        // See 6833019.
        return kfmPeer;
    }

    public TrayIconPeer createTrayIcon(TrayIcon target)
      throws HeadlessException {
        throw new HeadlessException();
    }

    public SystemTrayPeer createSystemTray(SystemTray target)
      throws HeadlessException {
        throw new HeadlessException();
    }

    public boolean isTraySupported() {
        return false;
    }

    public GlobalCursorManager getGlobalCursorManager()
        throws HeadlessException {
        throw new HeadlessException();
    }

    /*
     * Headless toolkit - unsupported.
     */
    protected void loadSystemColors(int[] systemColors)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public ColorModel getColorModel()
        throws HeadlessException {
        throw new HeadlessException();
    }

    public int getScreenResolution()
        throws HeadlessException {
        throw new HeadlessException();
    }

    public Map mapInputMethodHighlight(InputMethodHighlight highlight)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public int getMenuShortcutKeyMask()
        throws HeadlessException {
        throw new HeadlessException();
    }

    public boolean getLockingKeyState(int keyCode)
        throws UnsupportedOperationException {
        throw new HeadlessException();
    }

    public void setLockingKeyState(int keyCode, boolean on)
        throws UnsupportedOperationException {
        throw new HeadlessException();
    }

    public Cursor createCustomCursor(Image cursor, Point hotSpot, String name)
        throws IndexOutOfBoundsException, HeadlessException {
        throw new HeadlessException();
    }

    public Dimension getBestCursorSize(int preferredWidth, int preferredHeight)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public int getMaximumCursorColors()
        throws HeadlessException {
        throw new HeadlessException();
    }

    public <T extends DragGestureRecognizer> T
        createDragGestureRecognizer(Class<T> abstractRecognizerClass,
                                    DragSource ds, Component c,
                                    int srcActions, DragGestureListener dgl)
    {
        return null;
    }

    public int getScreenHeight()
        throws HeadlessException {
        throw new HeadlessException();
    }

    public int getScreenWidth()
        throws HeadlessException {
        throw new HeadlessException();
    }

    public Dimension getScreenSize()
        throws HeadlessException {
        throw new HeadlessException();
    }

    public Insets getScreenInsets(GraphicsConfiguration gc)
        throws HeadlessException {
        throw new HeadlessException();
    }

    public void setDynamicLayout(boolean dynamic)
        throws HeadlessException {
        throw new HeadlessException();
    }

    protected boolean isDynamicLayoutSet()
        throws HeadlessException {
        throw new HeadlessException();
    }

    public boolean isDynamicLayoutActive()
        throws HeadlessException {
        throw new HeadlessException();
    }

    public Clipboard getSystemClipboard()
        throws HeadlessException {
        throw new HeadlessException();
    }

    /*
     * Printing
     */
    public PrintJob getPrintJob(Frame frame, String jobtitle,
        JobAttributes jobAttributes,
        PageAttributes pageAttributes) {
        if (frame != null) {
            // Should never happen
            throw new HeadlessException();
        }
        throw new NullPointerException("frame must not be null");
    }

    public PrintJob getPrintJob(Frame frame, String doctitle, Properties props)
    {
        if (frame != null) {
            // Should never happen
            throw new HeadlessException();
        }
        throw new NullPointerException("frame must not be null");
    }

    /*
     * Headless toolkit - supported.
     */

    public void sync() {
        // Do nothing
    }

    public void beep() {
        // Send alert character
        System.out.write(0x07);
    }

    /*
     * Event Queue
     */
    public EventQueue getSystemEventQueueImpl() {
        return SunToolkit.getSystemEventQueueImplPP();
    }

    /*
     * Images.
     */
    public int checkImage(Image img, int w, int h, ImageObserver o) {
        return tk.checkImage(img, w, h, o);
    }

    public boolean prepareImage(
        Image img, int w, int h, ImageObserver o) {
        return tk.prepareImage(img, w, h, o);
    }

    public Image getImage(String filename) {
        return tk.getImage(filename);
    }

    public Image getImage(URL url) {
        return tk.getImage(url);
    }

    public Image createImage(String filename) {
        return tk.createImage(filename);
    }

    public Image createImage(URL url) {
        return tk.createImage(url);
    }

    public Image createImage(byte[] data, int offset, int length) {
        return tk.createImage(data, offset, length);
    }

    public Image createImage(ImageProducer producer) {
        return tk.createImage(producer);
    }

    public Image createImage(byte[] imagedata) {
        return tk.createImage(imagedata);
    }


    /*
     * Fonts
     */
    @SuppressWarnings("deprecation")
    public FontPeer getFontPeer(String name, int style) {
        if (componentFactory != null) {
            return componentFactory.getFontPeer(name, style);
        }
        return null;
    }

    @Override
    public DataTransferer getDataTransferer() {
        return null;
    }

    @SuppressWarnings("deprecation")
    public FontMetrics getFontMetrics(Font font) {
        return tk.getFontMetrics(font);
    }

    @SuppressWarnings("deprecation")
    public String[] getFontList() {
        return tk.getFontList();
    }

    /*
     * Desktop properties
     */

    public void addPropertyChangeListener(String name,
        PropertyChangeListener pcl) {
        tk.addPropertyChangeListener(name, pcl);
    }

    public void removePropertyChangeListener(String name,
        PropertyChangeListener pcl) {
        tk.removePropertyChangeListener(name, pcl);
    }

    /*
     * Modality
     */
    public boolean isModalityTypeSupported(Dialog.ModalityType modalityType) {
        return false;
    }

    public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType exclusionType) {
        return false;
    }

    /*
     * Always on top
     */
    public boolean isAlwaysOnTopSupported() {
        return false;
    }

    /*
     * AWT Event listeners
     */

    public void addAWTEventListener(AWTEventListener listener,
        long eventMask) {
        tk.addAWTEventListener(listener, eventMask);
    }

    public void removeAWTEventListener(AWTEventListener listener) {
        tk.removeAWTEventListener(listener);
    }

    public AWTEventListener[] getAWTEventListeners() {
        return tk.getAWTEventListeners();
    }

    public AWTEventListener[] getAWTEventListeners(long eventMask) {
        return tk.getAWTEventListeners(eventMask);
    }

    public boolean isDesktopSupported() {
        return false;
    }

    public DesktopPeer createDesktopPeer(Desktop target)
    throws HeadlessException{
        throw new HeadlessException();
    }

    public boolean areExtraMouseButtonsEnabled() throws HeadlessException{
        throw new HeadlessException();
    }

    protected Context getAndroidContext() {
        return SkinJobUtil.getAndroidApplicationContext();
    }

    protected void launchIntent(File file, String action) throws IOException {
        Intent intentToOpen = new Intent(action);
        Uri fileUri = Uri.fromFile(file);
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            String mime = URLConnection.guessContentTypeFromStream();
            if (mime == null) mime = URLConnection.guessContentTypeFromName(file.getName());
            intentToOpen.setDataAndType(fileUri, mime);
            androidContext.startActivity(intentToOpen);
        } finally {
            fileInputStream.close();
        }
    }

    private static class ButtonPeerImpl implements ButtonPeer {
        public ButtonPeerImpl(Button target) {

        }

        @Override
        public void setLabel(String label) {

        }

        @Override
        public boolean isObscured() {
            return false;
        }

        @Override
        public boolean canDetermineObscurity() {
            return false;
        }

        @Override
        public void setVisible(boolean v) {

        }

        @Override
        public void setEnabled(boolean e) {

        }

        @Override
        public void paint(Graphics g) {

        }

        @Override
        public void print(Graphics g) {

        }

        @Override
        public void setBounds(int x, int y, int width, int height, int op) {

        }

        @Override
        public void handleEvent(AWTEvent e) {

        }

        @Override
        public void coalescePaintEvent(PaintEvent e) {

        }

        @Override
        public Point getLocationOnScreen() {
            return null;
        }

        @Override
        public Dimension getPreferredSize() {
            return null;
        }

        @Override
        public Dimension getMinimumSize() {
            return null;
        }

        @Override
        public ColorModel getColorModel() {
            return null;
        }

        @Override
        public Graphics getGraphics() {
            return null;
        }

        @Override
        public FontMetrics getFontMetrics(Font font) {
            return null;
        }

        @Override
        public void dispose() {

        }

        @Override
        public void setForeground(Color c) {

        }

        @Override
        public void setBackground(Color c) {

        }

        @Override
        public void setFont(Font f) {

        }

        @Override
        public void updateCursorImmediately() {

        }

        @Override
        public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
            return false;
        }

        @Override
        public boolean isFocusable() {
            return false;
        }

        @Override
        public Image createImage(ImageProducer producer) {
            return null;
        }

        @Override
        public Image createImage(int width, int height) {
            return null;
        }

        @Override
        public VolatileImage createVolatileImage(int width, int height) {
            return null;
        }

        @Override
        public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
            return false;
        }

        @Override
        public int checkImage(Image img, int w, int h, ImageObserver o) {
            return 0;
        }

        @Override
        public GraphicsConfiguration getGraphicsConfiguration() {
            return null;
        }

        @Override
        public boolean handlesWheelScrolling() {
            return false;
        }

        @Override
        public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {

        }

        @Override
        public Image getBackBuffer() {
            return null;
        }

        @Override
        public void flip(int x1, int y1, int x2, int y2, BufferCapabilities.FlipContents flipAction) {

        }

        @Override
        public void destroyBuffers() {

        }

        @Override
        public void reparent(ContainerPeer newContainer) {

        }

        @Override
        public boolean isReparentSupported() {
            return false;
        }

        @Override
        public void layout() {

        }

        @Override
        public void applyShape(Region shape) {

        }

        @Override
        public void setZOrder(ComponentPeer above) {

        }

        @Override
        public boolean updateGraphicsData(GraphicsConfiguration gc) {
            return false;
        }
    }

    private static class TextFieldPeerImpl implements TextFieldPeer {
        public TextFieldPeerImpl(TextField target) {

        }

        @Override
        public void setEchoChar(char echoChar) {

        }

        @Override
        public Dimension getPreferredSize(int columns) {
            return null;
        }

        @Override
        public Dimension getMinimumSize(int columns) {
            return null;
        }

        @Override
        public void setEditable(boolean editable) {

        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public void setText(String text) {

        }

        @Override
        public int getSelectionStart() {
            return 0;
        }

        @Override
        public int getSelectionEnd() {
            return 0;
        }

        @Override
        public void select(int selStart, int selEnd) {

        }

        @Override
        public void setCaretPosition(int pos) {

        }

        @Override
        public int getCaretPosition() {
            return 0;
        }

        @Override
        public InputMethodRequests getInputMethodRequests() {
            return null;
        }

        @Override
        public boolean isObscured() {
            return false;
        }

        @Override
        public boolean canDetermineObscurity() {
            return false;
        }

        @Override
        public void setVisible(boolean v) {

        }

        @Override
        public void setEnabled(boolean e) {

        }

        @Override
        public void paint(Graphics g) {

        }

        @Override
        public void print(Graphics g) {

        }

        @Override
        public void setBounds(int x, int y, int width, int height, int op) {

        }

        @Override
        public void handleEvent(AWTEvent e) {

        }

        @Override
        public void coalescePaintEvent(PaintEvent e) {

        }

        @Override
        public Point getLocationOnScreen() {
            return null;
        }

        @Override
        public Dimension getPreferredSize() {
            return null;
        }

        @Override
        public Dimension getMinimumSize() {
            return null;
        }

        @Override
        public ColorModel getColorModel() {
            return null;
        }

        @Override
        public Graphics getGraphics() {
            return null;
        }

        @Override
        public FontMetrics getFontMetrics(Font font) {
            return null;
        }

        @Override
        public void dispose() {

        }

        @Override
        public void setForeground(Color c) {

        }

        @Override
        public void setBackground(Color c) {

        }

        @Override
        public void setFont(Font f) {

        }

        @Override
        public void updateCursorImmediately() {

        }

        @Override
        public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
            return false;
        }

        @Override
        public boolean isFocusable() {
            return false;
        }

        @Override
        public Image createImage(ImageProducer producer) {
            return null;
        }

        @Override
        public Image createImage(int width, int height) {
            return null;
        }

        @Override
        public VolatileImage createVolatileImage(int width, int height) {
            return null;
        }

        @Override
        public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
            return false;
        }

        @Override
        public int checkImage(Image img, int w, int h, ImageObserver o) {
            return 0;
        }

        @Override
        public GraphicsConfiguration getGraphicsConfiguration() {
            return null;
        }

        @Override
        public boolean handlesWheelScrolling() {
            return false;
        }

        @Override
        public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {

        }

        @Override
        public Image getBackBuffer() {
            return null;
        }

        @Override
        public void flip(int x1, int y1, int x2, int y2, BufferCapabilities.FlipContents flipAction) {

        }

        @Override
        public void destroyBuffers() {

        }

        @Override
        public void reparent(ContainerPeer newContainer) {

        }

        @Override
        public boolean isReparentSupported() {
            return false;
        }

        @Override
        public void layout() {

        }

        @Override
        public void applyShape(Region shape) {

        }

        @Override
        public void setZOrder(ComponentPeer above) {

        }

        @Override
        public boolean updateGraphicsData(GraphicsConfiguration gc) {
            return false;
        }
    }

    private static class LabelPeerImpl implements LabelPeer {
        @Override
        public void setText(String label) {

        }

        @Override
        public void setAlignment(int alignment) {

        }

        @Override
        public boolean isObscured() {
            return false;
        }

        @Override
        public boolean canDetermineObscurity() {
            return false;
        }

        @Override
        public void setVisible(boolean v) {

        }

        @Override
        public void setEnabled(boolean e) {

        }

        @Override
        public void paint(Graphics g) {

        }

        @Override
        public void print(Graphics g) {

        }

        @Override
        public void setBounds(int x, int y, int width, int height, int op) {

        }

        @Override
        public void handleEvent(AWTEvent e) {

        }

        @Override
        public void coalescePaintEvent(PaintEvent e) {

        }

        @Override
        public Point getLocationOnScreen() {
            return null;
        }

        @Override
        public Dimension getPreferredSize() {
            return null;
        }

        @Override
        public Dimension getMinimumSize() {
            return null;
        }

        @Override
        public ColorModel getColorModel() {
            return null;
        }

        @Override
        public Graphics getGraphics() {
            return null;
        }

        @Override
        public FontMetrics getFontMetrics(Font font) {
            return null;
        }

        @Override
        public void dispose() {

        }

        @Override
        public void setForeground(Color c) {

        }

        @Override
        public void setBackground(Color c) {

        }

        @Override
        public void setFont(Font f) {

        }

        @Override
        public void updateCursorImmediately() {

        }

        @Override
        public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
            return false;
        }

        @Override
        public boolean isFocusable() {
            return false;
        }

        @Override
        public Image createImage(ImageProducer producer) {
            return null;
        }

        @Override
        public Image createImage(int width, int height) {
            return null;
        }

        @Override
        public VolatileImage createVolatileImage(int width, int height) {
            return null;
        }

        @Override
        public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
            return false;
        }

        @Override
        public int checkImage(Image img, int w, int h, ImageObserver o) {
            return 0;
        }

        @Override
        public GraphicsConfiguration getGraphicsConfiguration() {
            return null;
        }

        @Override
        public boolean handlesWheelScrolling() {
            return false;
        }

        @Override
        public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {

        }

        @Override
        public Image getBackBuffer() {
            return null;
        }

        @Override
        public void flip(int x1, int y1, int x2, int y2, BufferCapabilities.FlipContents flipAction) {

        }

        @Override
        public void destroyBuffers() {

        }

        @Override
        public void reparent(ContainerPeer newContainer) {

        }

        @Override
        public boolean isReparentSupported() {
            return false;
        }

        @Override
        public void layout() {

        }

        @Override
        public void applyShape(Region shape) {

        }

        @Override
        public void setZOrder(ComponentPeer above) {

        }

        @Override
        public boolean updateGraphicsData(GraphicsConfiguration gc) {
            return false;
        }
    }

    private static class ListPeerImpl implements ListPeer {
        public ListPeerImpl(List target) {

        }

        @Override
        public int[] getSelectedIndexes() {
            return new int[0];
        }

        @Override
        public void add(String item, int index) {

        }

        @Override
        public void delItems(int start, int end) {

        }

        @Override
        public void removeAll() {

        }

        @Override
        public void select(int index) {

        }

        @Override
        public void deselect(int index) {

        }

        @Override
        public void makeVisible(int index) {

        }

        @Override
        public void setMultipleMode(boolean m) {

        }

        @Override
        public Dimension getPreferredSize(int rows) {
            return null;
        }

        @Override
        public Dimension getMinimumSize(int rows) {
            return null;
        }

        @Override
        public boolean isObscured() {
            return false;
        }

        @Override
        public boolean canDetermineObscurity() {
            return false;
        }

        @Override
        public void setVisible(boolean v) {

        }

        @Override
        public void setEnabled(boolean e) {

        }

        @Override
        public void paint(Graphics g) {

        }

        @Override
        public void print(Graphics g) {

        }

        @Override
        public void setBounds(int x, int y, int width, int height, int op) {

        }

        @Override
        public void handleEvent(AWTEvent e) {

        }

        @Override
        public void coalescePaintEvent(PaintEvent e) {

        }

        @Override
        public Point getLocationOnScreen() {
            return null;
        }

        @Override
        public Dimension getPreferredSize() {
            return null;
        }

        @Override
        public Dimension getMinimumSize() {
            return null;
        }

        @Override
        public ColorModel getColorModel() {
            return null;
        }

        @Override
        public Graphics getGraphics() {
            return null;
        }

        @Override
        public FontMetrics getFontMetrics(Font font) {
            return null;
        }

        @Override
        public void dispose() {

        }

        @Override
        public void setForeground(Color c) {

        }

        @Override
        public void setBackground(Color c) {

        }

        @Override
        public void setFont(Font f) {

        }

        @Override
        public void updateCursorImmediately() {

        }

        @Override
        public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
            return false;
        }

        @Override
        public boolean isFocusable() {
            return false;
        }

        @Override
        public Image createImage(ImageProducer producer) {
            return null;
        }

        @Override
        public Image createImage(int width, int height) {
            return null;
        }

        @Override
        public VolatileImage createVolatileImage(int width, int height) {
            return null;
        }

        @Override
        public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
            return false;
        }

        @Override
        public int checkImage(Image img, int w, int h, ImageObserver o) {
            return 0;
        }

        @Override
        public GraphicsConfiguration getGraphicsConfiguration() {
            return null;
        }

        @Override
        public boolean handlesWheelScrolling() {
            return false;
        }

        @Override
        public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {

        }

        @Override
        public Image getBackBuffer() {
            return null;
        }

        @Override
        public void flip(int x1, int y1, int x2, int y2, BufferCapabilities.FlipContents flipAction) {

        }

        @Override
        public void destroyBuffers() {

        }

        @Override
        public void reparent(ContainerPeer newContainer) {

        }

        @Override
        public boolean isReparentSupported() {
            return false;
        }

        @Override
        public void layout() {

        }

        @Override
        public void applyShape(Region shape) {

        }

        @Override
        public void setZOrder(ComponentPeer above) {

        }

        @Override
        public boolean updateGraphicsData(GraphicsConfiguration gc) {
            return false;
        }
    }

    private class DesktopPeerImpl implements DesktopPeer {

        @Override
        public boolean isSupported(Desktop.Action action) {
            return action != Desktop.Action.PRINT;
        }

        @Override
        public void open(File file) throws IOException {
            launchIntent(file, Intent.ACTION_VIEW);
        }

        @Override
        public void edit(File file) throws IOException {
            launchIntent(file, Intent.ACTION_EDIT);
        }

        @Override
        public void print(File file) throws IOException {
            throw new
                UnsupportedOperationException("TODO: Uncomment once support.v4 JAR is installed");
            /**
            if (ContextCompat.checkSelfPermission(androidContext,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(androidContext,
                        Manifest.permission.READ_CONTACTS)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(thisActivity,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
            launchIntent(file, Intent.ACTION_PR);
             */
        }

        @Override
        public void mail(URI mailtoURL) throws IOException {
            Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
            mailIntent.setData(Uri.parse(mailtoURL.toString()));
            androidContext.startActivity(mailIntent);
        }

        @Override
        public void browse(URI uri) throws IOException {
            Intent browseIntent = new Intent(Intent.ACTION_VIEW);
            browseIntent.setData(Uri.parse(uri.toString()));
            androidContext.startActivity(browseIntent);
        }
    }
}
