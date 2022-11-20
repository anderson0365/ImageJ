package ij.plugin.frame;

import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import ij.WindowManager;
import ij.plugin.PlugIn;

public interface CommonPlugInDialogFrameInterface extends PlugIn, WindowListener, FocusListener {
	default void run(String arg) {}
	default void windowOpened(WindowEvent e) {}
	default void windowClosed(WindowEvent e) {}
	default void windowIconified(WindowEvent e) {}
	default void windowDeiconified(WindowEvent e) {}
	default void windowDeactivated(WindowEvent e) {}
	default void focusLost(FocusEvent e) {}
	
	default void commonWindowClosing(WindowEvent e, Window w) {
    	if (e.getSource()==this) {
    		commonClose(w);
    		if (Recorder.record)
    			Recorder.record("run", "Close");
    	}
    }
	
    /** Closes this window. */
    default void commonClose(Window w) {
		//setVisible(false);
		w.dispose();
		WindowManager.removeWindow(w);
    }
}
