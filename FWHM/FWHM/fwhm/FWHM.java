package fwhm;

import java.lang.*;

import java.util.*;

/**

This class takes the data from the image as input and calls the other methods
(in classes MrqMintest and Background)

**/


public class FWHM {
	
	
	public static double a[];
	
	public static double chisq;
	
	
	public static void fwhmlsq(double y[], int npt, double gues3) {
		
		
		int MA=6,i,j,k,itst,mfit,lista[], MaxIterations=100, l=0;
		
		double SPREAD=0.001,alamda,ochisq,covar[][],alpha[][],x[], beta[];
		
		
		lista = new int[MA+1];
		
		beta = new double[MA+1];
		
		a = new double[MA+1];
		
		covar = new double[MA+1][MA+1];
		
		alpha = new double[MA+1][MA+1];
		
		x =  new double[npt+1];
		
		
		Background back = new Background();
  		
  		back.background(y,npt);
  		
  		double gues[] = {0.0, back.maximum-back.minarray[1], back.ymax, gues3, back.background, 0, 0};
  		
  		for (i=1;i<=npt;i++) x[i] = i;
  		
  		
		
		mfit=6;

		for (i=1;i<=mfit;i++) lista[i]=i;
		
		for (i=1;i<=MA;i++) a[i]=gues[i];

		MrqMintest MMT1 = new MrqMintest();
		
		MMT1.alamda_min = -1.0;
		
		MMT1.alpha = alpha;
		
		MMT1.mrqmin(x,y,a,lista,covar,npt,MA);
		
		
		for (i=1;i<=MA;i++)			/*Checking if there were any problems*/
		
			if (a[i] == 0) l++;
		
		if (l == 6) {				/*if there were problems return a value of 999999999*/
		
			a[3] = 600561205.2283273;
		
			return;
		
		} else l=0;
		
		
		ochisq = MMT1.chisq_min;
		
		alamda = MMT1.alamda_min;
		
		beta = MMT1.beta;
		
		alpha = MMT1.alpha;
		
		k=1;

		itst=0;

		
		while (k < MaxIterations) {

			k++;
			
			MrqMintest MMT2 = new MrqMintest();
		
			MMT2.alamda_min = alamda;
			
			MMT2.beta = beta;
			
			MMT2.alpha = alpha;
			
			
			MMT2.mrqmin(x,y,a,lista,covar,npt,MA);
			
			
			for (i=1;i<=MA;i++)			/*Checking if there were any problems*/
		
				if (a[i] == 0) l++;
		
			if (l == 6) {				/*if there were problems return a value of 999999999*/
		
				a[3] = 600561205.2283273;
		
				return;
		
			} else l=0;
		

			if (ochisq > MMT2.chisq_min || ochisq > MMT2.chisq_min) itst=0;

			else if (Math.abs(ochisq-MMT2.chisq_min) < 0.1) itst++;

			ochisq = MMT2.chisq_min;
			
			alamda = MMT2.alamda_min;
		
			beta = MMT2.beta;
			
			alpha = MMT2.alpha;
		}
		
		
		MrqMintest MMT3 = new MrqMintest();
		
		MMT3.alamda_min = 0.0;
   	 	
		MMT3.beta = beta;
			
		MMT3.alpha = alpha;
			
		MMT3.mrqmin(x, y, a, lista, covar,  npt, MA);
			
		chisq = MMT3.chisq_min;
		
		
		for (i=1;i<=MA;i++)			/*Checking if there were any problems*/
		
			if (a[i] == 0) l++;
		
		if (l == 6) {				/*if there were problems return a value of 999999999*/		
			
			a[3] = 600561205.2283273;
		
			return;
		
		} else l=0;
		
		return;
	}
}
