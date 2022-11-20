package ij.process;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

public interface CommonIntShortProcessInterface {
	
	default Image commonCreateImage(ImageProcessor img) {
		if (!img.minMaxSet)
			findMinAndMax();
		boolean firstTime = getPixels8()== null;
		boolean thresholding = img.minThreshold!=ImageProcessor.NO_THRESHOLD && img.lutUpdateMode<ImageProcessor.NO_LUT_UPDATE;
		//ij.IJ.log("createImage: "+firstTime+"  "+lutAnimation+"  "+thresholding);
		if (firstTime || !img.lutAnimation)
			create8BitImage(thresholding&&img.lutUpdateMode==ImageProcessor.RED_LUT);
		if (img.cm==null)
			img.makeDefaultColorModel();
		if (thresholding) {
			int t1 = (int)img.minThreshold;
			int t2 = (int)img.maxThreshold;
			int size = img.width*img.height;
			int value;
			if (img.lutUpdateMode==ImageProcessor.BLACK_AND_WHITE_LUT) {
				for (int i=0; i<size; i++) {
					value = (getPixels(i)&0xffff);
					if (value>=t1 && value<=t2)
						getPixels8()[i] = (byte)255;
					else
						getPixels8()[i] = (byte)0;
				}
			} else { // threshold red
				for (int i=0; i<size; i++) {
					value = (getPixels(i)&0xffff);
					if (value>=t1 && value<=t2)
						getPixels8()[i] = (byte)255;
				}
			}
		}
		return commonCreateBufferedImage(img);
	}
	
	default Image commonCreateBufferedImage(ImageProcessor img) {
		if (img.raster==null) {
			SampleModel sm = img.getIndexSampleModel();
			DataBuffer db = new DataBufferByte(getPixels8(), img.width*img.height, 0);
			img.raster = Raster.createWritableRaster(sm, db, null);
		}
		if (img.image==null || img.cm!=img.cm2) {
			if (img.cm==null) img.cm = img.getDefaultColorModel();
			img.image = new BufferedImage(img.cm, img.raster, false, null);
			img.cm2 = img.cm;
		}
		img.lutAnimation = false;
		return img.image;
	}
	
	public void findMinAndMax();
	
	public int getPixels(int index);
	
	public byte getPixels8(int index);
	
	public byte[] getPixels8();
	
	public byte[] create8BitImage(boolean thresholding);
}
