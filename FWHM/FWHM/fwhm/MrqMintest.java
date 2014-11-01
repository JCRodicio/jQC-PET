package fwhm;

/**

This is basically a straight translation from Numerical Recipes in C into Java.
Some modifications have been made.

The class calls MrqCof repeatedly in order to minimize chisq.

**/

public class MrqMintest {

	
	public static double da[], atry[], oneda[][], beta[],alpha[][], ochisq_min;

	public static double chisq_min, alamda_min;

	
	public void mrqmin(double x[], double y[], double a[], int lista[], double covar[][], int npt, int MA) {

		
		int k, kk, j, ihit, l=0;

		if ( alamda_min < 0.0) {

			kk = MA+2;

    		oneda = new double[MA+1][2];

			atry = new double[MA+1];

			da = new double[MA+1];

			beta = new double[MA+1];

	
			for (j = 1; j <= MA; j++) {         /*** Does lista contain a proper */

				ihit = 0;                    		/*** permutation of the coefficients? */


				for (k = 1; k <= MA; k++) if (lista[k] == j) ihit++;

				if (ihit == 0) {lista[kk] = j;	kk++;}	

				else if (ihit > 1) {

      				/*System.out.println("Run-time error: Bad LISTA permutation in MRQMIN-1");*/

      				for (k = 1; k <= MA; k++) a[k] = 0;
      				
      				return;
      				
      				//System.exit(0);

      			}
			}

    		
			if (kk != MA+2) {
	
      			/*System.out.println("Run-time error: Bad LISTA permutation in MRQMIN-1");*/

				for (k = 1; k <= MA; k++) a[k] = 0;
      				
      			return;
      			
      			//System.exit(0);

    		}


			alamda_min = 0.001;

			MrqCoftest MCT = new MrqCoftest();

    		MCT.mrqcof(x, y, a, lista, alpha, beta, npt, MA);

    		chisq_min = MCT.chisq_cof;

			ochisq_min = chisq_min;

    	}  		

		for (j = 1; j <= MA; j++) {

			for (k = 1; k <= MA; k++) covar[j][k] = alpha[j][k];

			covar[j][j] = alpha[j][j] * (1.0 + alamda_min);

			oneda[j][1] = beta[j];

    	}

		 		

  		GaussJtest GJT = new GaussJtest();

  		GJT.gaussj(covar, MA, oneda); /*** Matrix solution */



		for (j = 1; j <= MA; j++) {				/*Check if GaussJ had any problems*/
		
			if (oneda[j][1] == 0) l++;
		
			for (k = 1; k <= MA; k++)
			
				if (covar[j][k] == 0) 
				
					l++;
		}
		
		if (l == 42) {							/*If GaussJ had problems, set a[] to 0*/
			
			for (k = 1; k <= MA; k++) a[k] = 0;
			
			return;
			
		} else l=0;
					
					

  		for (j = 1; j <= MA; j++) {

  			da[j] = oneda[j][1];

		}


  		if (alamda_min == 0.0) {  /*** Once converged, evaluate covariance matrix */

			Covsrttest2 CST = new Covsrttest2();

    		CST.covsrt(covar, MA, lista);                          /*** with alamda = 0 */

			return;

		}

  		

  		for (j = 1; j <= MA; j++) atry[j] = a[j];

		for (j = 1; j <= MA; j++) atry[lista[j]] = a[lista[j]]+da[j];	/*** Did the trial succeed? */

  		MrqCoftest MCT3 = new MrqCoftest();

		MCT3.mrqcof(x, y, atry, lista, covar, da, npt, MA);

		chisq_min = MCT3.chisq_cof;

		if (chisq_min < ochisq_min) {

			alamda_min *= 0.1;

			ochisq_min = chisq_min;

			for (j = 1; j <= MA; j++) {

				for (k = 1; k <= MA; k++) alpha[j][k] = covar[j][k];

				beta[j] = da[j];

				a[lista[j]] = atry[lista[j]];

      		}

    	} else {								/*if not, then increase alamda and do it again*/

			alamda_min *= 10.0;

			chisq_min = ochisq_min;
 		}
 		
 		return;
	}
}
