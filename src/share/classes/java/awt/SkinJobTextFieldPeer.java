package java.awt;

import java.awt.event.PaintEvent;
import java.awt.im.InputMethodRequests;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.TextFieldPeer;

import sun.awt.CausedFocusEvent;

/**
 * Created by cryoc on 2016-10-09.
 */
class SkinJobTextFieldPeer implements TextFieldPeer {
    public SkinJobTextFieldPeer(TextField target) {

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
