package com.hevsoft.hashes.common;

import javax.swing.SwingUtilities;

public class Handler {
	
	public void post(Runnable runnable){
		SwingUtilities.invokeLater(runnable);
	}
}
