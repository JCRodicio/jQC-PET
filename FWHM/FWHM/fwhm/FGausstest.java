package fwhm;


import java.lang.*;

import java.util.*;

/** 

Defines a Gaussian Function. From Numerical Recipes.

**/


public class FGausstest {



	public static double y, dyda[];

	
	public void fgauss_lsq(double x, double a[], int MA) {

		double fac, ex, arg;

		dyda = new double[MA+1];

		
		arg = (x - a[2]) / a[3];

		ex = Math.exp(-arg * arg);

		fac = a[1] * ex * 2.0 * arg;

		y = a[1] * ex + a[4] + a[5] * x + a[6] * x * x; 

		dyda[1] = ex;

		dyda[2] = fac / a[3];

		dyda[3] = fac * arg / a[3];

		dyda[4] = 1;

		dyda[5] = x;

		dyda[6] = x * x ;

	}

}
