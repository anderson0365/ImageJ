package ij.process;

import java.util.*;
import java.awt.*;
import java.awt.image.*;


/** This is an extended ColorProcessor that supports signed 32-bit int images. */
public class IntProcessor extends ColorProcessor implements CommonIntShortProcessInterface  {
	private byte[] pixels8;

	/**Creates a blank IntProcessor with the specified dimensions. */
	public IntProcessor(int width, int height) {
		this(width, height, new int[width*height]);
	}

	/**Creates an IntProcessor from a pixel array. */
	public IntProcessor(int width, int height, int[] pixels) {
		super(width, height, pixels);
		makeDefaultColorModel();
	}

	/** Create an 8-bit AWT image by scaling pixels in the range min-max to 0-255. */
	@Override
	public Image createImage() {
		return commonCreateImage(this);
	}
	
	Image createBufferedImage() {
		return commonCreateBufferedImage(this);
	}
	
	public void findMinAndMax() {
		int size = width*height;
		int value;
		int min = pixels[0];
		int max = pixels[0];
		for (int i=1; i<size; i++) {
			value = pixels[i];
			if (value<min)
				min = value;
			else if (value>max)
				max = value;
		}
		this.min = min;
		this.max = max;
		minMaxSet = true;
	}
	
	public int getPixels(int index) {
		return pixels[index];
	}
	
	public byte getPixels8(int index) {
		return pixels8[index];
	}
	
	public byte[] getPixels8() {
		return pixels8;
	}
	
	// creates 8-bit image by linearly scaling from float to 8-bits
	public byte[] create8BitImage(boolean thresholding) {
		int size = width*height;
		if (pixels8==null)
			pixels8 = new byte[size];
		double value;
		int ivalue;
		double min2 = getMin();
		double max2 = getMax();
		double scale = 255.0/(max2-min2);
		int maxValue = thresholding?254:255;
		for (int i=0; i<size; i++) {
			value = pixels[i]-min2;
			if (value<0.0) value=0.0;
			ivalue = (int)(value*scale+0.5);
			if (ivalue>maxValue) ivalue = maxValue;
			pixels8[i] = (byte)ivalue;
		}
		return pixels8;
	}

	@Override
	byte[] create8BitImage() {
		return create8BitImage(false);
	}

	/** Returns this image as an 8-bit BufferedImage . */
	public BufferedImage getBufferedImage() {
		return convertToByte(true).getBufferedImage();
	}
	
	@Override
	public void setColorModel(ColorModel cm) {
		if (cm!=null && !(cm instanceof IndexColorModel))
			throw new IllegalArgumentException("IndexColorModel required");
		if (cm!=null && cm instanceof LUT)
			cm = ((LUT)cm).getColorModel();
		this.cm = cm;
		baseCM = null;
		rLUT1 = rLUT2 = null;
		inversionTested = false;
		minThreshold = NO_THRESHOLD;
	}
	
	@Override
	public float getPixelValue(int x, int y) {
		if (x>=0 && x<width && y>=0 && y<height)
			return (float)pixels[y*width+x];
		else 
			return Float.NaN;
	}

	/** Returns the number of channels (1). */
	@Override
	public int getNChannels() {
		return 1;
	}
	

	@Override
	public void resetMinAndMax() {
		findMinAndMax();
		resetThreshold();
	}
	
	@Override
	public void setMinAndMax(double minimum, double maximum, int channels) {
		min = (int)minimum;
		max = (int)maximum;
		minMaxSet = true;
		resetThreshold();
	}

}


