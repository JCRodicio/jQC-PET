package fwhm;


/**

This class performs a fit to a Gaussian from given data by calling fgauss. 
This class is a straight translation from Numerical Recipes in C.

**/

public class MrqCoftest{


	public static double chisq_cof, dyda[],ymod;

		
	public void mrqcof(double x[], double y[], double a[], int lista[], double alpha[][], double beta[], int npt, int MA) {   		   

		int k, j, i;
		double wt, sig2i, dy, SIGMA = 0.1; 

	    for (j = 1; j <= MA; j++) {
			for (k = 1; k <= j; k++) alpha[j][k] = 0.0;
			beta[j] = 0.0;
		}

  		
		chisq_cof = 0.0;

  		for (i = 1; i <= npt; i++) {

  			FGausstest FGT = new FGausstest();

  			FGT.fgauss_lsq(x[i], a, MA);

			sig2i = 1.0 / (SIGMA * SIGMA);

			ymod = FGT.y;

			
			dyda = FGT.dyda;		/*added so that dyda in mrqcof is the same as the one in in fgauss*/

    		dy = y[i] - ymod;		


			for (j = 1; j <= MA; j++) {

				wt = FGT.dyda[lista[j]] * sig2i;

				for (k = 1; k <= j; k++) alpha[j][k] +=  wt * FGT.dyda[lista[k]];

				beta[j] += dy * wt;
			}

			chisq_cof += dy * dy * sig2i;	

   		}

  		for (j = 2; j <= MA; j++)

			for (k = 1; k <= j-1; k++) alpha[k][j] = alpha[j][k];
	} 		
}

