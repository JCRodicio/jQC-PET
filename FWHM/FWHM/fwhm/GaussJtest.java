package fwhm;

import java.io.*;

import java.util.*;

import java.lang.Math;

import java.lang.*;

/**

This class is called by MrqMin

**/

public class GaussJtest {



	public int icol, irow;	

	

	public void gaussj(double a[][], int MA, double b[][]) {

		

		int i, j, k, l, m = 1, ll ;			/*** m = 1 as only 1 pass needed */

		int indxc[] = new int[MA+1];	

		int indxr[] = new int[MA+1];	

		int ipiv[] = new int[MA+1];		

		double big, dum, pivinv, temp;


		for (j = 1; j <= MA; j++) ipiv[j] = 0;	//setting ipiv[j] to 0

	  	for (i = 1; i <= MA; i++){ 				/*** main loop over columns to be reduced */

	  	 	big = 0.0;

	    	for (j = 1; j <= MA; j++)	 		/***outer loop of search for pivot element*/

				if (ipiv[j] != 1)				//already been set to 0 above, so always true

	      	  		for (k = 1; k <= MA; k++) {

	            		if (ipiv[k] == 0) {			//already been set to 0 above, so always true

	          	  			if (Math.abs(a[j][k]) >=  big) {	

	                			big = Math.abs(a[j][k]);

								irow = j;

								icol = k;

	             	 		}

	          	  		} else if (ipiv[k] > 1) {
	
	          				/*System.out.println("Run-time error \nGAUSSJ: Singular Matrix-1");*/

	          				for (j = 1; j <= MA; j++) {
	          					
	          					for (k = 1; k <= MA; k++) a[j][k] = 0;
	          					
	          					b[j][1]= 0;
	          				}
	          				
	          				return;
	          				
	          				//System.exit(0);	
	          			} 

					}

					

	   		ipiv[icol]++;					

				
			if (irow != icol) {

	        	for (l = 1; l <= MA; l++)  {temp = a[irow][l]; a[irow][l] = a[icol][l]; a[icol][l] = temp;}

				for (l = 1; l <= m; l++)  {temp = b[irow][l]; b[irow][l] = b[icol][l]; b[icol][l] = temp;}

	        }

	   		indxr[i] = irow;  		 /*** Now ready to divide the pivot row by the */

			indxc[i] = icol;  		 /*** pivot element, located at irow and icol. */

	        	
			if (a[icol][icol] == 0.0) { 

	   			/*System.out.println("Run-time error \nGAUSSJ: Singular Matrix-2"); */

	   			for (j = 1; j <= MA; j++) {
	          		
	          		for (k = 1; k <= MA; k++) a[j][k] = 0;
	          		
	          		b[j][1]= 0;
	          	
	          	}
	        	
	        	return;
	   			//System.exit(0);

	        }

	        	

	        pivinv = 1.0 / a[icol][icol];

			

	        a[icol][icol] = 1.0;



	        for (l = 1; l <= MA; l++) {a[icol][l] *= pivinv;}



	        for (l = 1; l <= m; l++) {b[icol][l] *= pivinv;}
			
			for (ll = 1; ll <= MA; ll++) 				/*** Next, reduce the rows except...*/

				if (ll != icol)  {          			/*** ...for the pivot of course.    */


					dum = a[ll][icol];

					a[ll][icol] = 0.0;

					for (l = 1; l <= MA; l++) a[ll][l] -= a[icol][l]*dum;

					for (l = 1; l <= m; l++) b[ll][l] -= b[icol][l]*dum;

				}
		}

		for (l = MA; l >= 1; l--) {

	  		if (indxr[l] != indxc[l]) 

			for (k = 1; k <= MA; k++) {

			temp = a[k][indxr[l]]; a[k][indxr[l]]= a[k][indxc[l]]; a[k][indxc[l]] = temp;	
			
			}
		}
	}
}
