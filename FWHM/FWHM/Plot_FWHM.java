/**
Plugs into the program ImageJ and displays a plot of pixel coordinate
versus count value. Displays the best fit Gaussian on this plot.

Written by Marjorie Gonzalez and Jennifer West, 1999 - 2001
Adapted from code written by R. Bud Fairly using methods from Numerical Recipes.
Winnipeg, Manitoba, Canada
(umwestjl@cc.umanitoba.ca)
**/

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import ij.gui.*;
import ij.*;
import ij.process.*;
import ij.util.*;
import ij.plugin.PlugIn;
import fwhm.Background;
import fwhm.ComputeFWHM;


public class Plot_FWHM implements PlugIn {

	
	public PlotWindow plotwin;
	public PlotWindow plotwin2;
	public Background back;
	public Background back2;
	public ComputeFWHM cf;
	
	int i;
	public int width;
	public int height;
	
	public double rowdata[];
	public double columndata[];
	public double a1[];
	public double a2[];
	
	public void run(String args) {
	
		//WindowManager wm = new WindowManager();
		ImagePlus imp = WindowManager.getCurrentImage();
		if(imp == null) return;
		
		IJ.showStatus("Finding FWHM...");
		
		cf = new ComputeFWHM();
		cf.findFWHM(imp);
			
		this.width = cf.width;
		this.height = cf.height;
		this.rowdata = cf.rowdata;
		this.columndata = cf.columndata;
		this.a1 = cf.a1;
		this.a2 = cf.a2;
		
		float[] xValues = new float[width]; 
		float[] rowValues = new float[width];
		float[] rowFit = new float[width];
		double[] arg = new double[width];
		double[] exp = new double[width];
		float[] xVal = new float[width];
		float[] yValues = new float[height];
		float[] colValues = new float[height];
		float[] colFit = new float[height];	
		double[] arg2 = new double[height];
		double[] exp2 = new double[height];
		float[] yVal = new float[height];
		
		plotFWHM(columndata, height, a2, yVal, colFit, yValues, colValues, arg2, exp2, "Column Plot");
		plotFWHM(rowdata, width, a1, xVal, rowFit, xValues, rowValues, arg, exp, "Row Plot");
		
		IJ.showStatus("");
		
	}	
	
	public void plotFWHM(double[] columndata, int height, double a[], float yVal[], float colFit[], float yValues[], 
								float colValues[], double arg2[], double exp2[], String label) {
		/*Plotting fit*/
		back2 = new Background();				/*Adding fit as a line*/
		back2.background(columndata,height-1);
		
		double backgd[] = new double[height];
		
		for (i=0; i<height; i++)
	    	yVal[i] = (float)i;
	    for (i=0; i<height; i++)
	    	backgd[i] = a[4]+a[5]*yVal[i]+a[6]*yVal[i]*yVal[i];
	    for (i = 0; i<height; i++)
			arg2[i] = ((yVal[i] - a[2]) / a[3]);
		for (i = 0; i<height; i++)
			exp2[i] = Math.exp(-arg2[i] * arg2[i]);
		for (i = 0; i<height; i++)
			colFit[i] = (float)Math.rint(a[1] * exp2[i] + backgd[1]);
		plotwin2 = new PlotWindow(label, "Pixel", "Counts", yVal, colFit);
		
		for (i=0; i<height; i++)				/*Plotting data as dots*/
	    	yValues[i] = (float)(i);
	    for (i=0; i<height; i++)
	    	colValues[i] = (float)columndata[i];
		
		if (back2.maximum > a[1]) {
			if (back2.minarray[1] < backgd[0])
				plotwin2.setLimits(0,height-1,(float)(back2.minarray[1]-(back2.maximum*.025)),(float)(back2.maximum*1.05));
			else  plotwin2.setLimits(0,height-1,(float)(backgd[0]-(back2.maximum*.025)),(float)(back2.maximum*1.05));
		} else {
			if (back2.minarray[1] < backgd[0])
				plotwin2.setLimits(0,height-1,(float)(back2.minarray[1]-(a[1]*0.025)),(float)(a[1]*1.05));
			else  plotwin2.setLimits(0,height-1,(float)(backgd[0]-(a[1]*0.025)),(float)(a[1]*1.05));
		}
		
		
		plotwin2.addPoints(yValues, colValues,1);
		String result = "FWHM = " + d2s(a[3]*1.66510922);
		plotwin2.addLabel(.1, .1, result);
		plotwin2.draw();
	}
	
	String d2s(double n) {
		if (Math.round(n)==n)
			return(IJ.d2s(n,0));
		else
			return(IJ.d2s(n));
	}
}
