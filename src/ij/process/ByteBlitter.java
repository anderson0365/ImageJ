package ij.process;
import java.awt.*;

/** This class does bit blitting of byte images. */
public class ByteBlitter implements Blitter {

	private ByteProcessor ip;
	private int width, height;
	private byte[] pixels;
	private int transparent = 255;
	
	/** Constructs a ByteBlitter from a ByteProcessor. */
	public ByteBlitter(ByteProcessor ip) {
		this.ip = ip;
		width = ip.getWidth();
		height = ip.getHeight();
		pixels = (byte[])ip.getPixels();
	}

	public void setTransparentColor(Color c) {
		transparent = ip.getBestIndex(c);
	}
	
	/** Copies the byte image in 'ip' to (x,y) using the specified mode. */
	public void copyBits(ImageProcessor ip, int xloc, int yloc, int mode) {
		ColorBit cBit = new ColorBit(this, ip, xloc, yloc);
		int upTo = cBit.r1.y+cBit.r1.height;
		for (int y=cBit.r1.y; y<upTo; y++) {
			cBit.srcIndex = (y-yloc)*cBit.srcWidth + (cBit.r1.x-xloc);
			cBit.dstIndex = y * width + cBit.r1.x;
			switch (mode) {
				case COPY:
					cBit.copy();
					break;
				case COPY_INVERTED:
					cBit.copyInverted();
					break;
				case COPY_TRANSPARENT:
					cBit.copyTransparent();
					break;
				case COPY_ZERO_TRANSPARENT:
					cBit.copyZeroTransparent();
					break;
				case ADD:
					cBit.add();
					break;
				case AVERAGE:
					cBit.average();
					break;
				case SUBTRACT:
					cBit.subtract();
					break;
				case DIFFERENCE:
					cBit.difference();
					break;
				case MULTIPLY:
					cBit.multiply();
					break;
				case DIVIDE:
					cBit.divide();
					break;
				case AND:
					cBit.and();
					break;
				case OR:
					cBit.or();
					break;
				case XOR:
					cBit.xor();
					break;
				case MIN:
					cBit.min();
					break;
				case MAX:
					cBit.max();
					break;
			}
		}
	}
	
	private class ColorBit {
		
		public Rectangle r1, r2;
		private int srcIndex, dstIndex;
		private byte[] srcPixels;
		private int src, dst;
		public int srcWidth;
		public int srcHeight;
		public ColorBit(ByteBlitter base, ImageProcessor ip, int xloc, int yloc) {	
			srcWidth= ip.getWidth();
			srcHeight = ip.getHeight();
			r1 = new Rectangle(srcWidth, srcHeight);
			r1.setLocation(xloc, yloc);
			r2 = new Rectangle(width, height);
			if (!r1.intersects(r2))
				return;
			if (ip instanceof ColorProcessor) {
				int[] pixels32 = (int[])ip.getPixels();
				int size = ip.getWidth()*ip.getHeight();
				srcPixels = new byte[size];
				if (base.ip.isInvertedLut())
					for (int i=0; i<size; i++)
						srcPixels[i] = (byte)(255-pixels32[i]&255);
				else 
					for (int i=0; i<size; i++)
						srcPixels[i] = (byte)(pixels32[i]&255);
			} else {
				ip = ip.convertToByte(true);
				srcPixels = (byte [])ip.getPixels();
			}
			r1 = r1.intersection(r2);
		}
		
		public void copy() {
			for (int i=r1.width; --i>=0;)
				pixels[dstIndex++] = srcPixels[srcIndex++];
		}
		public void copyInverted() {
			for (int i=r1.width; --i>=0;)
				pixels[dstIndex++] = (byte)(255-srcPixels[srcIndex++]&255);
		}
		public void copyTransparent() {
			for (int i=r1.width; --i>=0;) {
				src = srcPixels[srcIndex++]&255;
				if (src==transparent)
					dst = pixels[dstIndex];
				else
					dst = src;
				pixels[dstIndex++] = (byte)dst;
			}	
		}

		public void copyZeroTransparent() {
			for (int i=r1.width; --i>=0;) {
				src = srcPixels[srcIndex++]&255;
				if (src==0)
					dst = pixels[dstIndex];
				else
					dst = src;
				pixels[dstIndex++] = (byte)dst;
			}	
		}

		public void add() {
			for (int i=r1.width; --i>=0;) {
				dst = (srcPixels[srcIndex++]&255)+(pixels[dstIndex]&255);
				if (dst>255) dst = 255;
				pixels[dstIndex++] = (byte)dst;
			}
		}


		public void average() {
			for (int i=r1.width; --i>=0;) {
				dst = ((srcPixels[srcIndex++]&255)+(pixels[dstIndex]&255))/2;
				pixels[dstIndex++] = (byte)dst;
			}

		}

		public void subtract() {
			for (int i=r1.width; --i>=0;) {
				dst = (pixels[dstIndex]&255)-(srcPixels[srcIndex++]&255);
				if (dst<0) dst = 0;
				pixels[dstIndex++] = (byte)dst;
			}	
		}
		
		public void difference() {
			for (int i=r1.width; --i>=0;) {
				dst = (pixels[dstIndex]&255)-(srcPixels[srcIndex++]&255);
				if (dst<0) dst = -dst;
				pixels[dstIndex++] = (byte)dst;
			}
		}

		public void multiply() {
			for (int i=r1.width; --i>=0;) {
				dst = (srcPixels[srcIndex++]&255)*(pixels[dstIndex]&255);
				if (dst>255) dst = 255;
				pixels[dstIndex++] = (byte)dst;
			}
		}
		public void divide() {
			for (int i=r1.width; --i>=0;) {
				src = srcPixels[srcIndex++]&255;
				if (src==0)
					dst = 255;
				else
					dst = (pixels[dstIndex]&255)/src;
				pixels[dstIndex++] = (byte)dst;
			}	
		}
		public void and() {
			for (int i=r1.width; --i>=0;) {
				dst = srcPixels[srcIndex++]&pixels[dstIndex];
				pixels[dstIndex++] = (byte)dst;
			}	
		}
		
		public void or() {
			for (int i=r1.width; --i>=0;) {
				dst = srcPixels[srcIndex++]|pixels[dstIndex];
				pixels[dstIndex++] = (byte)dst;
			}
		}
		
		public void xor() {
			for (int i=r1.width; --i>=0;) {
				dst = srcPixels[srcIndex++]^pixels[dstIndex];
				pixels[dstIndex++] = (byte)dst;
			}
		}
		public void min() {
			for (int i=r1.width; --i>=0;) {
				src = srcPixels[srcIndex++]&255;
				dst = pixels[dstIndex]&255;
				if (src<dst) dst = src;
				pixels[dstIndex++] = (byte)dst;
			}
		}
		public void max() {
			for (int i=r1.width; --i>=0;) {
				src = srcPixels[srcIndex++]&255;
				dst = pixels[dstIndex]&255;
				if (src>dst) dst = src;
				pixels[dstIndex++] = (byte)dst;
			}
		}
	}
}
