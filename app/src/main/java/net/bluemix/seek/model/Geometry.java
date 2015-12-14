package net.bluemix.seek.model;

import java.util.Arrays;

public class Geometry {
	
	double[] cordinates;

	public double[] getCordinates() {
		return cordinates;
	}

	public void setCordinates(double[] cordinates) {
		this.cordinates = cordinates;
	}

	@Override
	public String toString() {
		return "Geometry [cordinates=" + Arrays.toString(cordinates) + "]";
	}

}
