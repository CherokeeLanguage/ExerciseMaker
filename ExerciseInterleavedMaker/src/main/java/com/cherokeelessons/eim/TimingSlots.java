package com.cherokeelessons.eim;

import java.util.HashSet;

@SuppressWarnings("serial")
public class TimingSlots extends HashSet<Integer> {

	public boolean isUsed(int seconds_start, int length) {
		for (int ix=seconds_start; ix<seconds_start+length; ix++) {
			if (contains(ix)){
				return true;
			}
		}
		return false;
	}

	public void markUsed(int seconds_start, int length) {
		for (int ix=seconds_start; ix<seconds_start+length; ix++) {
			add(ix);
		}
	}

}
