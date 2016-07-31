/**
 * 
 */
package com.cherokeelessons.eim;

import java.awt.EventQueue;

/**
 * @author mjoyner
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
