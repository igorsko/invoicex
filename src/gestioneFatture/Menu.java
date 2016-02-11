/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gestioneFatture;

import java.awt.AWTException;
import java.awt.AWTKeyStroke;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.image.BufferStrategy;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.EventListener;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javax.accessibility.AccessibleContext;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

/**
 *
 * @author mceccarelli
 */
public class Menu extends Frame {

    public org.jdesktop.swingx.JXBusyLabel lblInfoLoading2;

    public boolean equals(Object obj) {
        return main.getPadrePanel().getFrame().equals(obj);
    }

    public void transferFocusUpCycle() {
        main.getPadrePanel().getFrame().transferFocusUpCycle();
    }

    public void transferFocus() {
        main.getPadrePanel().getFrame().transferFocus();
    }

    public String toString() {
        return main.getPadrePanel().getFrame().toString();
    }

    public Dimension size() {
        return main.getPadrePanel().getFrame().size();
    }

    public void show(boolean b) {
        main.getPadrePanel().getFrame().show(b);
    }

    public void setPreferredSize(Dimension preferredSize) {
        main.getPadrePanel().getFrame().setPreferredSize(preferredSize);
    }

    public void setName(String name) {
        main.getPadrePanel().getFrame().setName(name);
    }

    public void setMaximumSize(Dimension maximumSize) {
        main.getPadrePanel().getFrame().setMaximumSize(maximumSize);
    }

    public void setLocation(Point p) {
        main.getPadrePanel().getFrame().setLocation(p);
    }

    public void setLocation(int x, int y) {
        main.getPadrePanel().getFrame().setLocation(x, y);
    }

    public void setLocale(Locale l) {
        main.getPadrePanel().getFrame().setLocale(l);
    }

    public void setIgnoreRepaint(boolean ignoreRepaint) {
        main.getPadrePanel().getFrame().setIgnoreRepaint(ignoreRepaint);
    }

    public void setForeground(Color c) {
        main.getPadrePanel().getFrame().setForeground(c);
    }

    public void setFocusable(boolean focusable) {
        main.getPadrePanel().getFrame().setFocusable(focusable);
    }

    public void setFocusTraversalKeysEnabled(boolean focusTraversalKeysEnabled) {
        main.getPadrePanel().getFrame().setFocusTraversalKeysEnabled(focusTraversalKeysEnabled);
    }

    public void setEnabled(boolean b) {
        main.getPadrePanel().getFrame().setEnabled(b);
    }

    public synchronized void setDropTarget(DropTarget dt) {
        main.getPadrePanel().getFrame().setDropTarget(dt);
    }

    public void setComponentOrientation(ComponentOrientation o) {
        main.getPadrePanel().getFrame().setComponentOrientation(o);
    }

    public void setBackground(Color c) {
        main.getPadrePanel().getFrame().setBackground(c);
    }

    public void resize(Dimension d) {
        main.getPadrePanel().getFrame().resize(d);
    }

    public void resize(int width, int height) {
        main.getPadrePanel().getFrame().resize(width, height);
    }

    public boolean requestFocusInWindow() {
        return main.getPadrePanel().getFrame().requestFocusInWindow();
    }

    public void requestFocus() {
        main.getPadrePanel().getFrame().requestFocus();
    }

    public void repaint(long tm, int x, int y, int width, int height) {
        main.getPadrePanel().getFrame().repaint(tm, x, y, width, height);
    }

    public void repaint(int x, int y, int width, int height) {
        main.getPadrePanel().getFrame().repaint(x, y, width, height);
    }

    public void repaint(long tm) {
        main.getPadrePanel().getFrame().repaint(tm);
    }

    public void repaint() {
        main.getPadrePanel().getFrame().repaint();
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        main.getPadrePanel().getFrame().removePropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        main.getPadrePanel().getFrame().removePropertyChangeListener(listener);
    }

    public synchronized void removeMouseWheelListener(MouseWheelListener l) {
        main.getPadrePanel().getFrame().removeMouseWheelListener(l);
    }

    public synchronized void removeMouseMotionListener(MouseMotionListener l) {
        main.getPadrePanel().getFrame().removeMouseMotionListener(l);
    }

    public synchronized void removeMouseListener(MouseListener l) {
        main.getPadrePanel().getFrame().removeMouseListener(l);
    }

    public synchronized void removeKeyListener(KeyListener l) {
        main.getPadrePanel().getFrame().removeKeyListener(l);
    }

    public synchronized void removeInputMethodListener(InputMethodListener l) {
        main.getPadrePanel().getFrame().removeInputMethodListener(l);
    }

    public void removeHierarchyListener(HierarchyListener l) {
        main.getPadrePanel().getFrame().removeHierarchyListener(l);
    }

    public void removeHierarchyBoundsListener(HierarchyBoundsListener l) {
        main.getPadrePanel().getFrame().removeHierarchyBoundsListener(l);
    }

    public synchronized void removeFocusListener(FocusListener l) {
        main.getPadrePanel().getFrame().removeFocusListener(l);
    }

    public synchronized void removeComponentListener(ComponentListener l) {
        main.getPadrePanel().getFrame().removeComponentListener(l);
    }

    public void printAll(Graphics g) {
        main.getPadrePanel().getFrame().printAll(g);
    }

    public boolean prepareImage(Image image, int width, int height, ImageObserver observer) {
        return main.getPadrePanel().getFrame().prepareImage(image, width, height, observer);
    }

    public boolean prepareImage(Image image, ImageObserver observer) {
        return main.getPadrePanel().getFrame().prepareImage(image, observer);
    }

    public void paintAll(Graphics g) {
        main.getPadrePanel().getFrame().paintAll(g);
    }

    public void nextFocus() {
        main.getPadrePanel().getFrame().nextFocus();
    }

    public void move(int x, int y) {
        main.getPadrePanel().getFrame().move(x, y);
    }

    public boolean mouseUp(Event evt, int x, int y) {
        return main.getPadrePanel().getFrame().mouseUp(evt, x, y);
    }

    public boolean mouseMove(Event evt, int x, int y) {
        return main.getPadrePanel().getFrame().mouseMove(evt, x, y);
    }

    public boolean mouseExit(Event evt, int x, int y) {
        return main.getPadrePanel().getFrame().mouseExit(evt, x, y);
    }

    public boolean mouseEnter(Event evt, int x, int y) {
        return main.getPadrePanel().getFrame().mouseEnter(evt, x, y);
    }

    public boolean mouseDrag(Event evt, int x, int y) {
        return main.getPadrePanel().getFrame().mouseDrag(evt, x, y);
    }

    public boolean mouseDown(Event evt, int x, int y) {
        return main.getPadrePanel().getFrame().mouseDown(evt, x, y);
    }

    public boolean lostFocus(Event evt, Object what) {
        return main.getPadrePanel().getFrame().lostFocus(evt, what);
    }

    public Point location() {
        return main.getPadrePanel().getFrame().location();
    }

    public void list(PrintWriter out) {
        main.getPadrePanel().getFrame().list(out);
    }

    public void list(PrintStream out) {
        main.getPadrePanel().getFrame().list(out);
    }

    public void list() {
        main.getPadrePanel().getFrame().list();
    }

    public boolean keyUp(Event evt, int key) {
        return main.getPadrePanel().getFrame().keyUp(evt, key);
    }

    public boolean keyDown(Event evt, int key) {
        return main.getPadrePanel().getFrame().keyDown(evt, key);
    }

    public boolean isVisible() {
        return main.getPadrePanel().getFrame().isVisible();
    }

    public boolean isValid() {
        return main.getPadrePanel().getFrame().isValid();
    }

    public boolean isPreferredSizeSet() {
        return main.getPadrePanel().getFrame().isPreferredSizeSet();
    }

    public boolean isOpaque() {
        return main.getPadrePanel().getFrame().isOpaque();
    }

    public boolean isMinimumSizeSet() {
        return main.getPadrePanel().getFrame().isMinimumSizeSet();
    }

    public boolean isMaximumSizeSet() {
        return main.getPadrePanel().getFrame().isMaximumSizeSet();
    }

    public boolean isLightweight() {
        return main.getPadrePanel().getFrame().isLightweight();
    }

    public boolean isForegroundSet() {
        return main.getPadrePanel().getFrame().isForegroundSet();
    }

    public boolean isFontSet() {
        return main.getPadrePanel().getFrame().isFontSet();
    }

    public boolean isFocusable() {
        return main.getPadrePanel().getFrame().isFocusable();
    }

    public boolean isFocusTraversable() {
        return main.getPadrePanel().getFrame().isFocusTraversable();
    }

    public boolean isFocusOwner() {
        return main.getPadrePanel().getFrame().isFocusOwner();
    }

    public boolean isEnabled() {
        return main.getPadrePanel().getFrame().isEnabled();
    }

    public boolean isDoubleBuffered() {
        return main.getPadrePanel().getFrame().isDoubleBuffered();
    }

    public boolean isDisplayable() {
        return main.getPadrePanel().getFrame().isDisplayable();
    }

    public boolean isCursorSet() {
        return main.getPadrePanel().getFrame().isCursorSet();
    }

    public boolean isBackgroundSet() {
        return main.getPadrePanel().getFrame().isBackgroundSet();
    }

    public boolean inside(int x, int y) {
        return main.getPadrePanel().getFrame().inside(x, y);
    }

    public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
        return main.getPadrePanel().getFrame().imageUpdate(img, infoflags, x, y, w, h);
    }

    public boolean hasFocus() {
        return main.getPadrePanel().getFrame().hasFocus();
    }

    public boolean handleEvent(Event evt) {
        return main.getPadrePanel().getFrame().handleEvent(evt);
    }

    public boolean gotFocus(Event evt, Object what) {
        return main.getPadrePanel().getFrame().gotFocus(evt, what);
    }

    public int getY() {
        return main.getPadrePanel().getFrame().getY();
    }

    public int getX() {
        return main.getPadrePanel().getFrame().getX();
    }

    public int getWidth() {
        return main.getPadrePanel().getFrame().getWidth();
    }

    public Dimension getSize(Dimension rv) {
        return main.getPadrePanel().getFrame().getSize(rv);
    }

    public Dimension getSize() {
        return main.getPadrePanel().getFrame().getSize();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return main.getPadrePanel().getFrame().getPropertyChangeListeners(propertyName);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return main.getPadrePanel().getFrame().getPropertyChangeListeners();
    }

    public ComponentPeer getPeer() {
        return main.getPadrePanel().getFrame().getPeer();
    }

    public Container getParent() {
        return main.getPadrePanel().getFrame().getParent();
    }

    public String getName() {
        return main.getPadrePanel().getFrame().getName();
    }

    public synchronized MouseWheelListener[] getMouseWheelListeners() {
        return main.getPadrePanel().getFrame().getMouseWheelListeners();
    }

    public Point getMousePosition() throws HeadlessException {
        return main.getPadrePanel().getFrame().getMousePosition();
    }

    public synchronized MouseMotionListener[] getMouseMotionListeners() {
        return main.getPadrePanel().getFrame().getMouseMotionListeners();
    }

    public synchronized MouseListener[] getMouseListeners() {
        return main.getPadrePanel().getFrame().getMouseListeners();
    }

    public Point getLocationOnScreen() {
        return main.getPadrePanel().getFrame().getLocationOnScreen();
    }

    public Point getLocation(Point rv) {
        return main.getPadrePanel().getFrame().getLocation(rv);
    }

    public Point getLocation() {
        return main.getPadrePanel().getFrame().getLocation();
    }

    public synchronized KeyListener[] getKeyListeners() {
        return main.getPadrePanel().getFrame().getKeyListeners();
    }

    public InputMethodRequests getInputMethodRequests() {
        return main.getPadrePanel().getFrame().getInputMethodRequests();
    }

    public synchronized InputMethodListener[] getInputMethodListeners() {
        return main.getPadrePanel().getFrame().getInputMethodListeners();
    }

    public boolean getIgnoreRepaint() {
        return main.getPadrePanel().getFrame().getIgnoreRepaint();
    }

    public synchronized HierarchyListener[] getHierarchyListeners() {
        return main.getPadrePanel().getFrame().getHierarchyListeners();
    }

    public synchronized HierarchyBoundsListener[] getHierarchyBoundsListeners() {
        return main.getPadrePanel().getFrame().getHierarchyBoundsListeners();
    }

    public int getHeight() {
        return main.getPadrePanel().getFrame().getHeight();
    }

    public Graphics getGraphics() {
        return main.getPadrePanel().getFrame().getGraphics();
    }

    public Color getForeground() {
        return main.getPadrePanel().getFrame().getForeground();
    }

    public FontMetrics getFontMetrics(Font font) {
        return main.getPadrePanel().getFrame().getFontMetrics(font);
    }

    public Font getFont() {
        return main.getPadrePanel().getFrame().getFont();
    }

    public boolean getFocusTraversalKeysEnabled() {
        return main.getPadrePanel().getFrame().getFocusTraversalKeysEnabled();
    }

    public synchronized FocusListener[] getFocusListeners() {
        return main.getPadrePanel().getFrame().getFocusListeners();
    }

    public synchronized DropTarget getDropTarget() {
        return main.getPadrePanel().getFrame().getDropTarget();
    }

    public Cursor getCursor() {
        return main.getPadrePanel().getFrame().getCursor();
    }

    public ComponentOrientation getComponentOrientation() {
        return main.getPadrePanel().getFrame().getComponentOrientation();
    }

    public synchronized ComponentListener[] getComponentListeners() {
        return main.getPadrePanel().getFrame().getComponentListeners();
    }

    public ColorModel getColorModel() {
        return main.getPadrePanel().getFrame().getColorModel();
    }

    public Rectangle getBounds(Rectangle rv) {
        return main.getPadrePanel().getFrame().getBounds(rv);
    }

    public Rectangle getBounds() {
        return main.getPadrePanel().getFrame().getBounds();
    }

    public Color getBackground() {
        return main.getPadrePanel().getFrame().getBackground();
    }

    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        main.getPadrePanel().getFrame().firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        main.getPadrePanel().getFrame().firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        main.getPadrePanel().getFrame().firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        main.getPadrePanel().getFrame().firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
        main.getPadrePanel().getFrame().firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        main.getPadrePanel().getFrame().firePropertyChange(propertyName, oldValue, newValue);
    }

    public void enableInputMethods(boolean enable) {
        main.getPadrePanel().getFrame().enableInputMethods(enable);
    }

    public void enable(boolean b) {
        main.getPadrePanel().getFrame().enable(b);
    }

    public void enable() {
        main.getPadrePanel().getFrame().enable();
    }

    public void disable() {
        main.getPadrePanel().getFrame().disable();
    }

    public VolatileImage createVolatileImage(int width, int height, ImageCapabilities caps) throws AWTException {
        return main.getPadrePanel().getFrame().createVolatileImage(width, height, caps);
    }

    public VolatileImage createVolatileImage(int width, int height) {
        return main.getPadrePanel().getFrame().createVolatileImage(width, height);
    }

    public Image createImage(int width, int height) {
        return main.getPadrePanel().getFrame().createImage(width, height);
    }

    public Image createImage(ImageProducer producer) {
        return main.getPadrePanel().getFrame().createImage(producer);
    }

    public boolean contains(Point p) {
        return main.getPadrePanel().getFrame().contains(p);
    }

    public boolean contains(int x, int y) {
        return main.getPadrePanel().getFrame().contains(x, y);
    }

    public int checkImage(Image image, int width, int height, ImageObserver observer) {
        return main.getPadrePanel().getFrame().checkImage(image, width, height, observer);
    }

    public int checkImage(Image image, ImageObserver observer) {
        return main.getPadrePanel().getFrame().checkImage(image, observer);
    }

    public Rectangle bounds() {
        return main.getPadrePanel().getFrame().bounds();
    }

    public synchronized void addMouseWheelListener(MouseWheelListener l) {
        main.getPadrePanel().getFrame().addMouseWheelListener(l);
    }

    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        main.getPadrePanel().getFrame().addMouseMotionListener(l);
    }

    public synchronized void addMouseListener(MouseListener l) {
        main.getPadrePanel().getFrame().addMouseListener(l);
    }

    public synchronized void addKeyListener(KeyListener l) {
        main.getPadrePanel().getFrame().addKeyListener(l);
    }

    public synchronized void addInputMethodListener(InputMethodListener l) {
        main.getPadrePanel().getFrame().addInputMethodListener(l);
    }

    public void addHierarchyListener(HierarchyListener l) {
        main.getPadrePanel().getFrame().addHierarchyListener(l);
    }

    public void addHierarchyBoundsListener(HierarchyBoundsListener l) {
        main.getPadrePanel().getFrame().addHierarchyBoundsListener(l);
    }

    public synchronized void addFocusListener(FocusListener l) {
        main.getPadrePanel().getFrame().addFocusListener(l);
    }

    public synchronized void addComponentListener(ComponentListener l) {
        main.getPadrePanel().getFrame().addComponentListener(l);
    }

    public synchronized void add(PopupMenu popup) {
        main.getPadrePanel().getFrame().add(popup);
    }

    public boolean action(Event evt, Object what) {
        return main.getPadrePanel().getFrame().action(evt, what);
    }

    public void validate() {
        main.getPadrePanel().getFrame().validate();
    }

    public void update(Graphics g) {
        main.getPadrePanel().getFrame().update(g);
    }

    public void transferFocusDownCycle() {
        main.getPadrePanel().getFrame().transferFocusDownCycle();
    }

    public void transferFocusBackward() {
        main.getPadrePanel().getFrame().transferFocusBackward();
    }

    public void setLayout(LayoutManager mgr) {
//        main.getPadrePanel().getFrame().setLayout(mgr);
    }

    public void setFont(Font f) {
        main.getPadrePanel().getFrame().setFont(f);
    }

    public void setFocusTraversalPolicy(FocusTraversalPolicy policy) {
        main.getPadrePanel().getFrame().setFocusTraversalPolicy(policy);
    }

    public void setFocusTraversalKeys(int id, Set<? extends AWTKeyStroke> keystrokes) {
        main.getPadrePanel().getFrame().setFocusTraversalKeys(id, keystrokes);
    }

    public synchronized void removeContainerListener(ContainerListener l) {
        main.getPadrePanel().getFrame().removeContainerListener(l);
    }

    public void removeAll() {
        main.getPadrePanel().getFrame().removeAll();
    }

    public void remove(Component comp) {
        main.getPadrePanel().getFrame().remove(comp);
    }

    public void remove(int index) {
        main.getPadrePanel().getFrame().remove(index);
    }

    public void printComponents(Graphics g) {
        main.getPadrePanel().getFrame().printComponents(g);
    }

    public void print(Graphics g) {
        main.getPadrePanel().getFrame().print(g);
    }

    public Dimension preferredSize() {
        return main.getPadrePanel().getFrame().preferredSize();
    }

    public void paintComponents(Graphics g) {
        main.getPadrePanel().getFrame().paintComponents(g);
    }

    public Dimension minimumSize() {
        return main.getPadrePanel().getFrame().minimumSize();
    }

    public Component locate(int x, int y) {
        return main.getPadrePanel().getFrame().locate(x, y);
    }

    public void list(PrintWriter out, int indent) {
        main.getPadrePanel().getFrame().list(out, indent);
    }

    public void list(PrintStream out, int indent) {
        main.getPadrePanel().getFrame().list(out, indent);
    }

    public void layout() {
        main.getPadrePanel().getFrame().layout();
    }

    public boolean isFocusTraversalPolicySet() {
        return main.getPadrePanel().getFrame().isFocusTraversalPolicySet();
    }

    public boolean isFocusCycleRoot(Container container) {
        return main.getPadrePanel().getFrame().isFocusCycleRoot(container);
    }

    public boolean isAncestorOf(Component c) {
        return main.getPadrePanel().getFrame().isAncestorOf(c);
    }

    public void invalidate() {
        main.getPadrePanel().getFrame().invalidate();
    }

    public Insets insets() {
        return main.getPadrePanel().getFrame().insets();
    }

    public Dimension getPreferredSize() {
        return main.getPadrePanel().getFrame().getPreferredSize();
    }

    public Point getMousePosition(boolean allowChildren) throws HeadlessException {
        return main.getPadrePanel().getFrame().getMousePosition(allowChildren);
    }

    public Dimension getMinimumSize() {
        return main.getPadrePanel().getFrame().getMinimumSize();
    }

    public Dimension getMaximumSize() {
        return main.getPadrePanel().getFrame().getMaximumSize();
    }

    public LayoutManager getLayout() {
        return main.getPadrePanel().getFrame().getLayout();
    }

    public Insets getInsets() {
        return main.getPadrePanel().getFrame().getInsets();
    }

    public FocusTraversalPolicy getFocusTraversalPolicy() {
        return main.getPadrePanel().getFrame().getFocusTraversalPolicy();
    }

    public synchronized ContainerListener[] getContainerListeners() {
        return main.getPadrePanel().getFrame().getContainerListeners();
    }

    public Component[] getComponents() {
        return main.getPadrePanel().getFrame().getComponents();
    }

    public int getComponentCount() {
        return main.getPadrePanel().getFrame().getComponentCount();
    }

    public Component getComponentAt(Point p) {
        return main.getPadrePanel().getFrame().getComponentAt(p);
    }

    public Component getComponentAt(int x, int y) {
        return main.getPadrePanel().getFrame().getComponentAt(x, y);
    }

    public Component getComponent(int n) {
        return main.getPadrePanel().getFrame().getComponent(n);
    }

    public float getAlignmentY() {
        return main.getPadrePanel().getFrame().getAlignmentY();
    }

    public float getAlignmentX() {
        return main.getPadrePanel().getFrame().getAlignmentX();
    }

    public Component findComponentAt(Point p) {
        return main.getPadrePanel().getFrame().findComponentAt(p);
    }

    public Component findComponentAt(int x, int y) {
        return main.getPadrePanel().getFrame().findComponentAt(x, y);
    }

    public void doLayout() {
        main.getPadrePanel().getFrame().doLayout();
    }

    public void deliverEvent(Event e) {
        main.getPadrePanel().getFrame().deliverEvent(e);
    }

    public int countComponents() {
        return main.getPadrePanel().getFrame().countComponents();
    }

    public boolean areFocusTraversalKeysSet(int id) {
        return main.getPadrePanel().getFrame().areFocusTraversalKeysSet(id);
    }

    public void applyComponentOrientation(ComponentOrientation o) {
        main.getPadrePanel().getFrame().applyComponentOrientation(o);
    }

    public synchronized void addContainerListener(ContainerListener l) {
        main.getPadrePanel().getFrame().addContainerListener(l);
    }

    public void add(Component comp, Object constraints, int index) {
        main.getPadrePanel().getFrame().add(comp, constraints, index);
    }

    public void add(Component comp, Object constraints) {
        main.getPadrePanel().getFrame().add(comp, constraints);
    }

    public Component add(Component comp, int index) {
        return main.getPadrePanel().getFrame().add(comp, index);
    }

    public Component add(String name, Component comp) {
        return main.getPadrePanel().getFrame().add(name, comp);
    }

    public Component add(Component comp) {
        return main.getPadrePanel().getFrame().add(comp);
    }

    public void toFront() {
        main.getPadrePanel().getFrame().toFront();
    }

    public void toBack() {
        main.getPadrePanel().getFrame().toBack();
    }

    public void show() {
        main.getPadrePanel().getFrame().show();
    }

    public void setVisible(boolean b) {
        main.getPadrePanel().getFrame().setVisible(b);
    }

    public void setSize(int width, int height) {
        main.getPadrePanel().getFrame().setSize(width, height);
    }

    public void setSize(Dimension d) {
        main.getPadrePanel().getFrame().setSize(d);
    }

    public void setMinimumSize(Dimension minimumSize) {
        main.getPadrePanel().getFrame().setMinimumSize(minimumSize);
    }

    public void setLocationRelativeTo(Component c) {
        main.getPadrePanel().getFrame().setLocationRelativeTo(c);
    }

    public void setLocationByPlatform(boolean locationByPlatform) {
        main.getPadrePanel().getFrame().setLocationByPlatform(locationByPlatform);
    }

    public void setFocusableWindowState(boolean focusableWindowState) {
        main.getPadrePanel().getFrame().setFocusableWindowState(focusableWindowState);
    }

    public void setCursor(Cursor cursor) {
        main.getPadrePanel().getFrame().setCursor(cursor);
    }

    public void setBounds(Rectangle r) {
        main.getPadrePanel().getFrame().setBounds(r);
    }

    public void setBounds(int x, int y, int width, int height) {
        main.getPadrePanel().getFrame().setBounds(x, y, width, height);
    }

    public void reshape(int x, int y, int width, int height) {
        main.getPadrePanel().getFrame().reshape(x, y, width, height);
    }

    public synchronized void removeWindowStateListener(WindowStateListener l) {
        main.getPadrePanel().getFrame().removeWindowStateListener(l);
    }

    public synchronized void removeWindowListener(WindowListener l) {
        main.getPadrePanel().getFrame().removeWindowListener(l);
    }

    public synchronized void removeWindowFocusListener(WindowFocusListener l) {
        main.getPadrePanel().getFrame().removeWindowFocusListener(l);
    }

    public boolean postEvent(Event e) {
        return main.getPadrePanel().getFrame().postEvent(e);
    }

    public void paint(Graphics g) {
        main.getPadrePanel().getFrame().paint(g);
    }

    public void pack() {
        main.getPadrePanel().getFrame().pack();
    }

    public boolean isShowing() {
        return main.getPadrePanel().getFrame().isShowing();
    }

    public boolean isLocationByPlatform() {
        return main.getPadrePanel().getFrame().isLocationByPlatform();
    }

    public boolean isFocused() {
        return main.getPadrePanel().getFrame().isFocused();
    }

    public boolean isActive() {
        return main.getPadrePanel().getFrame().isActive();
    }

    public void hide() {
        main.getPadrePanel().getFrame().hide();
    }

    public synchronized WindowStateListener[] getWindowStateListeners() {
        return main.getPadrePanel().getFrame().getWindowStateListeners();
    }

    public synchronized WindowListener[] getWindowListeners() {
        return main.getPadrePanel().getFrame().getWindowListeners();
    }

    public synchronized WindowFocusListener[] getWindowFocusListeners() {
        return main.getPadrePanel().getFrame().getWindowFocusListeners();
    }

    public Toolkit getToolkit() {
        try {
            return main.getPadrePanel().getFrame().getToolkit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ritorno default toolkit");
            return Toolkit.getDefaultToolkit();
        }
    }

    public Window getOwner() {
        return main.getPadrePanel().getFrame().getOwner();
    }

    public Window[] getOwnedWindows() {
        return main.getPadrePanel().getFrame().getOwnedWindows();
    }

    public Component getMostRecentFocusOwner() {
        return main.getPadrePanel().getFrame().getMostRecentFocusOwner();
    }

    public Locale getLocale() {
        return main.getPadrePanel().getFrame().getLocale();
    }

    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return main.getPadrePanel().getFrame().getListeners(listenerType);
    }

    public InputContext getInputContext() {
        return main.getPadrePanel().getFrame().getInputContext();
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return main.getPadrePanel().getFrame().getGraphicsConfiguration();
    }

    public boolean getFocusableWindowState() {
        return main.getPadrePanel().getFrame().getFocusableWindowState();
    }

    public Set<AWTKeyStroke> getFocusTraversalKeys(int id) {
        return main.getPadrePanel().getFrame().getFocusTraversalKeys(id);
    }

    public Component getFocusOwner() {
        return main.getPadrePanel().getFrame().getFocusOwner();
    }

    public BufferStrategy getBufferStrategy() {
        return main.getPadrePanel().getFrame().getBufferStrategy();
    }

    public void dispose() {
        main.getPadrePanel().getFrame().dispose();
    }

    public void createBufferStrategy(int numBuffers, BufferCapabilities caps) throws AWTException {
        main.getPadrePanel().getFrame().createBufferStrategy(numBuffers, caps);
    }

    public void createBufferStrategy(int numBuffers) {
        main.getPadrePanel().getFrame().createBufferStrategy(numBuffers);
    }

    public void applyResourceBundle(String rbName) {
        main.getPadrePanel().getFrame().applyResourceBundle(rbName);
    }

    public void applyResourceBundle(ResourceBundle rb) {
        main.getPadrePanel().getFrame().applyResourceBundle(rb);
    }

    public synchronized void addWindowStateListener(WindowStateListener l) {
        main.getPadrePanel().getFrame().addWindowStateListener(l);
    }

    public synchronized void addWindowListener(WindowListener l) {
        main.getPadrePanel().getFrame().addWindowListener(l);
    }

    public synchronized void addWindowFocusListener(WindowFocusListener l) {
        main.getPadrePanel().getFrame().addWindowFocusListener(l);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        main.getPadrePanel().getFrame().addPropertyChangeListener(propertyName, listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        main.getPadrePanel().getFrame().addPropertyChangeListener(listener);
    }

    public void setUndecorated(boolean undecorated) {
        main.getPadrePanel().getFrame().setUndecorated(undecorated);
    }

    public void setTitle(String title) {
        main.getPadrePanel().getFrame().setTitle(title);
    }

    public synchronized void setState(int state) {
        main.getPadrePanel().getFrame().setState(state);
    }

    public void setResizable(boolean resizable) {
        main.getPadrePanel().getFrame().setResizable(resizable);
    }

    public void setMenuBar(MenuBar mb) {
        main.getPadrePanel().getFrame().setMenuBar(mb);
    }

    public synchronized void setMaximizedBounds(Rectangle bounds) {
        main.getPadrePanel().getFrame().setMaximizedBounds(bounds);
    }

    public void setIconImage(Image image) {
        main.getPadrePanel().getFrame().setIconImage(image);
    }

    public synchronized void setExtendedState(int state) {
        main.getPadrePanel().getFrame().setExtendedState(state);
    }

    public void setCursor(int cursorType) {
        main.getPadrePanel().getFrame().setCursor(cursorType);
    }

    public void removeNotify() {
        main.getPadrePanel().getFrame().removeNotify();
    }

    public void remove(MenuComponent m) {
        main.getPadrePanel().getFrame().remove(m);
    }

    public boolean isUndecorated() {
        return main.getPadrePanel().getFrame().isUndecorated();
    }

    public boolean isResizable() {
        return main.getPadrePanel().getFrame().isResizable();
    }

    public String getTitle() {
        return main.getPadrePanel().getFrame().getTitle();
    }

    public synchronized int getState() {
        return main.getPadrePanel().getFrame().getState();
    }

    public MenuBar getMenuBar() {
        return main.getPadrePanel().getFrame().getMenuBar();
    }

    public Rectangle getMaximizedBounds() {
        return main.getPadrePanel().getFrame().getMaximizedBounds();
    }

    public Image getIconImage() {
        return main.getPadrePanel().getFrame().getIconImage();
    }

    public static Frame[] getFrames() {
        return main.getPadrePanel().getFrame().getFrames();
    }

    public synchronized int getExtendedState() {
        return main.getPadrePanel().getFrame().getExtendedState();
    }

    public int getCursorType() {
        return main.getPadrePanel().getFrame().getCursorType();
    }

    public AccessibleContext getAccessibleContext() {
        return main.getPadrePanel().getFrame().getAccessibleContext();
    }

    public void addNotify() {
        main.getPadrePanel().getFrame().addNotify();
    }

//---------------------------------------------------------------------------------
    public Menu() {
        lblInfoLoading2 = main.getPadrePanel().lblInfoLoading2;
        menBar = main.getPadrePanel().menBar;
        panBarr = main.getPadrePanel().panBarr;

        desktop = main.getPadrePanel().desktop;
        menAchievo = main.getPadrePanel().menAchievo;
        menAchievoOre = main.getPadrePanel().menAchievoOre;
        menAnagrafiche = main.getPadrePanel().menAnagrafiche;
        menUtilAggi = main.getPadrePanel().menUtilAggi;
        menUtilBackupOnline = main.getPadrePanel().menUtilBackupOnline;
        menUtilRestoreOnline = main.getPadrePanel().menUtilRestoreOnline;
    }

    //classe per retrocompatibilità
//        MenuPanel m = (MenuPanel) main.getPadrePanel();
//        m.openFrame(frm, 720, (int) m.getDesktopPane().getVisibleRect().getHeight() - 80);
    public void openFrame(JInternalFrame frame, int larg, int alte) {
        main.getPadrePanel().openFrame(frame, larg, alte);
    }

    public void openFrame(JInternalFrame frame, int larg, int alte, int top, int left) {
        main.getPadrePanel().openFrame(frame, larg, alte, top, left);
    }

    public JDesktopPane getDesktopPane() {
        return main.getPadrePanel().getDesktopPane();
    }

    public void aggiornaTitle() {
        main.getPadreFrame().aggiornaTitle();
    }

    public void apridatiazienda() {
        main.getPadrePanel().apridatiazienda();
    }

    public void closeFrame(JInternalFrame frame) {
        //metodo fittizio per retrocompatibilità
    }

    static public Frame getCurrenWindow() {
        return main.getPadre();
    }

    public void exitForm(java.awt.event.WindowEvent evt) {
        main.getPadrePanel().exitForm(evt);
    }
    public JMenuBar menBar;
    public JPanel panBarr;
    public javax.swing.JDesktopPane desktop;
    public javax.swing.JMenu menAchievo;
    public javax.swing.JMenuItem menAchievoOre;
    public javax.swing.JMenu menAnagrafiche;
    public javax.swing.JMenuItem menUtilAggi;
    public javax.swing.JMenuItem menUtilBackupOnline;
    public javax.swing.JMenuItem menUtilRestoreOnline;
}
