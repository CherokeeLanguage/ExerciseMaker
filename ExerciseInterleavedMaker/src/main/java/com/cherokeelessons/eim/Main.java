/**
 * 
 */
package com.cherokeelessons.eim;

import java.awt.EventQueue;
import java.io.IOException;

import com.cherokeelessons.gui.MainWindow;

/**
 * @author mjoyner
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		MainWindow.Config config=new MainWindow.Config() {
			@Override
			public String getApptitle() {
				return "Exercise Interleaved Maker";
			}
			
			@Override
			public Runnable getApp(String... args) throws Exception {
				return new App(args);
			}
		};
		EventQueue.invokeLater(new MainWindow(config, args));
	}

}
