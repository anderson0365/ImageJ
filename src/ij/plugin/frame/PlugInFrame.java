package ij.plugin.frame;
import java.awt.*;
import java.awt.event.*;
import ij.*;
import ij.plugin.*;

/**  This is a closeable window that plugins can extend. */
public class PlugInFrame extends Frame implements CommonPlugInDialogFrameInterface {

	String title;
	
	public PlugInFrame(String title) {
		super(title);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		this.title = title;
		ImageJ ij = IJ.getInstance();
		addWindowListener(this);
 		addFocusListener(this);
		if (IJ.isLinux()) setBackground(ImageJ.backgroundColor);
		if (ij!=null && !IJ.isMacOSX()) {
			Image img = ij.getIconImage();
			if (img!=null)
				try {setIconImage(img);} catch (Exception e) {}
		}
	}
	
    public void windowClosing(WindowEvent e) {
    	commonWindowClosing(e, this);
    }
    
    /** Closes this window. */
    public void close() {
    	commonClose(this);
    }

    public void windowActivated(WindowEvent e) {
		if (Prefs.setIJMenuBar) {
			this.setMenuBar(Menus.getMenuBar());
			Menus.setMenuBarCount++;
		}
		WindowManager.setWindow(this);
	}

	public void focusGained(FocusEvent e) {
		WindowManager.setWindow(this);
	}
}
