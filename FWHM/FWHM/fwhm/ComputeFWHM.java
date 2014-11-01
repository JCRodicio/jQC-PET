package fwhm;

import ij.*;
import ij.process.*;


/**

Finds the full width at half maximum for a star given an image.
This class plugs into a program which can open a standard FITS (flexible
image transport system) image. This program provides the Image Processor class.

Also computes the center of mass for the star. This value will be used as the
star position for our matching routine.

**/


public class ComputeFWHM {

	
	public static String centMass;
	public static String result;
	public int width;
	public int height;
	public double rowdata[];
	public double columndata[];
	public double a1[];
	public double a2[];
	public int xCOM, yCOM;
	public FWHM fwhm;
	public FWHM fwhm2;
	public double maximum;

	

	public double findFWHM(ImagePlus imp) {

	
		ImageProcessor ip = imp.getProcessor(); //Creating ImageProcessor object
//		Utilities util = new Utilities();
		
		width = ip.getWidth();
		height = ip.getHeight();

		int rw = width, rh = height;
		double pw = 1.0, ph = 1.0;
		double Vfwhm=0, Hfwhm=0, ratio;
		int    i, j, k, x, y, ymax=0, xmax=0, l;
		
		rowdata = new double[width];
		columndata = new double[height];
		a1 = new double[7];
		a2 = new double[7];
		
		findCOM(ip);
		centMass = xCOM + "\t" + yCOM + "\t";
		//IJ.write("xCOM = " + xCOM + " yCOM = " + yCOM);
		for (x=0; x<width; x++) rowdata[x] = (double)ip.getPixel(x, yCOM);
		for (y=0; y<height; y++) columndata[y] = (double)ip.getPixel(xCOM, y);
		
		
		/***Finding the fwhm of the image*/
		/*For the row*/
		double gues3r=2;
		int npt = width-1;
		fwhm = new FWHM();
		fwhm.fwhmlsq(rowdata, npt, gues3r);
		for (x=1; x<7; x++) a1[x] = fwhm.a[x];
		k=0; l=0;
		while (k<1 && l <= 50) {
			if (a1[3] > 1000) {
				a1[3] = 600561205.2283273;
				k = 1;
			} else k=0;
			if (a1[3] < -1000) {
				a1[3] = -600561205.2283273;
				k = 1;
			} else k=0;
			if ( a1[3]-gues3r > 0.5) {
				if (a1[3] == 600561205.2283273) k=1;
				else {gues3r+=0.5;
					fwhm.fwhmlsq(rowdata, npt, gues3r);
					for (x=1; x<7; x++) a1[x] = fwhm.a[x];
					k=0;}
			}
			else if ( a1[3]-gues3r < -0.5 && gues3r != 0.0) {
				if (a1[3] == 600561205.2283273) k=1;
				else {gues3r-=0.5;
					fwhm.fwhmlsq(rowdata, npt, gues3r);
					for (x=1; x<7; x++) a1[x] = fwhm.a[x];
					k=0;}
			} else k=1;
			l++;
		}
		
		if (l == 50) a1[3] = 600561205.2283273;
		if (fwhm.a[3] < 0) a1[3] = -600561205.2283273;
		
		Hfwhm = a1[3]*1.66510922;
		
		/*For the column*/
		double gues3c=2;
		int npt2 = height-1;
		fwhm2 = new FWHM();
		fwhm2.fwhmlsq(columndata, npt2,gues3c);
		for (x=1; x<7; x++) a2[x] = fwhm2.a[x];
		k=0; l=0;
		while (k<1 && l <= 50) {
			if (a2[3] > 1000) {
				a2[3] = 600561205.2283273;
				k = 1;
			} else k=0;
			if (a2[3] < -1000) {
				a2[3] = -600561205.2283273;
				k = 1;
			} else k=0;
			if ( a2[3]-gues3c > 0.5) {
				if (a2[3] == 600561205.2283273) k=1;
				else {
					gues3c+=0.5;
					fwhm2.fwhmlsq(columndata, npt2, gues3c);
					for (x=1; x<7; x++) a2[x] =fwhm2.a[x];
					k=0;}
			}
			else if ( a2[3]-gues3c < -0.5 && gues3c != 0.0) {
				if (a2[3] == 600561205.2283273) k=1;
				else {
					gues3c-=0.5;
					fwhm2.fwhmlsq(columndata, npt2, gues3c);
					for (x=1; x<7; x++) a2[x] = fwhm2.a[x];
					k=0;}
			} else k=1;
			l++;
		}
		
		if (l == 50) a2[3] = 600561205.2283273;
		if (fwhm2.a[3] < 0) a2[3] = -600561205.2283273;
		
		Vfwhm = a2[3]*1.66510922;
		
		result = Vfwhm + "\t" + Hfwhm + "\t";
		
		ratio = Vfwhm/Hfwhm;
		String vertical = d2s(Vfwhm);
		String horizontal = d2s(Hfwhm);
		String max = d2s(maximum);
		String vhratio = d2s(ratio);
/*		if(writeToCam) {
			util.appendLine("Max = " + max + " at (" +xCOM +", " +yCOM +")");
			util.write("   FWHM: V = " + vertical + "  H = " + horizontal + "  V/H = " + vhratio);
		}
		else {
*/			IJ.write("Max = " + max + " at (" +xCOM +", " +yCOM +")");
			IJ.write("   FWHM: V = " + vertical + "  H = " + horizontal + "  V/H = " + vhratio);
		
		
		return Double.valueOf(vertical).doubleValue();
		
	}

		
		
	public void findCOM(ImageProcessor ip) {
	
		/*Finding maximum value in array and its position*/
		int    i, j, k, x, y, ymax=0, xmax=0, l;
		double pw = 1.0, ph = 1.0;
		
		int width = ip.getWidth();
		int height = ip.getHeight();
		int rw = width, rh = height;

		for ( y=0 ; y<height; y++) {
			for ( x=0 ; x<width; x++)
				if (ip.getPixel(x,y) > maximum && ip.getPixel(x,y) != 65535) {
					maximum = ip.getPixel(x,y);
					ymax = y;
					xmax = x;
				}
			}

		/***Loop to find centrer of mass on a 20x20 region about the maximum */
		//int[] mask = ip.getMask();
		int[] mask = null;
		int  mi, my;
		double v, count=0.0, xsum=0.0, ysum=0.0;

		for (y=ymax-10,my=0; y<ymax+10; y++,my++) {
			mi = my*rw;
			for (x=xmax-10; x<xmax+10; x++) {
				if (mask==null || mask[mi++]==ip.BLACK) {
					v = ip.getPixel(x,y);
					count += v;
					xsum += x*v;
					ysum += y*v;
				}
			}
		}

		double xCenterOfMass = (xsum/count+0.5)*pw;
		double yCenterOfMass = (ysum/count+0.5)*ph;

		xCOM = (int)Math.rint(xCenterOfMass);
		yCOM = (int)Math.rint(yCenterOfMass);
	}

	public String d2s(double n) {
		if (Math.round(n)==n)
			return(IJ.d2s(n,0));
		else
			return(IJ.d2s(n));
	}

}
