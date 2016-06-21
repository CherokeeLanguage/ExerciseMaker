package com.cherokeelessons.eim;

import com.cherokeelessons.eim.ChallengeResponsePair.ResponseLayout;

public class Pragma {
	private int depth;
	private int maxsets;
	private ResponseLayout layout;
	private boolean includeRerversed;
	private boolean onlyRerversed;
	private float maxSetSize;
	private boolean sort;
	private boolean random;
	private String sep;

	public Pragma() {
		this.depth = 5;
		this.maxsets = 0;
		this.layout = ResponseLayout.SingleLine;
		this.includeRerversed = false;
		this.onlyRerversed = false;
		this.maxSetSize = 5;
		this.sort = true;
		this.random = false;
		this.sep = ":";
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getMaxsets() {
		return maxsets;
	}

	public void setMaxsets(int maxsets) {
		this.maxsets = maxsets;
	}

	public ResponseLayout getLayout() {
		return layout;
	}

	public void setLayout(ResponseLayout layout) {
		this.layout = layout;
	}

	public boolean isIncludeRerversed() {
		return includeRerversed;
	}

	public void setIncludeRerversed(boolean includeRerversed) {
		this.includeRerversed = includeRerversed;
	}

	public boolean isOnlyRerversed() {
		return onlyRerversed;
	}

	public void setOnlyRerversed(boolean onlyRerversed) {
		this.onlyRerversed = onlyRerversed;
	}

	public float getMaxSetSize() {
		return maxSetSize;
	}

	public void setMaxSetSize(float maxSetSize) {
		this.maxSetSize = maxSetSize;
	}

	public boolean isSort() {
		return sort;
	}

	public void setSort(boolean sort) {
		this.sort = sort;
	}

	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}

	public String getSep() {
		return sep;
	}

	public void setSep(String sep) {
		this.sep = sep;
	}
}