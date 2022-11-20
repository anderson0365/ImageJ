package ij.plugin.frame;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import ij.plugin.PlugIn;

public abstract class CommonPluginDialogFrameMethods implements PlugIn, WindowListener, FocusListener {
	public void run(String arg) {}
    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
	public void focusLost(FocusEvent e) {}
}
