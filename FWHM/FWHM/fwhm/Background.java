package fwhm;

import java.lang.*;

import java.util.*;

/**

Finds the vertical offset for the Gaussian (ie zero point). Must be offset to account for
background noise in the images.

Finds the peak value in the star. 

**/

public class Background {


	public static double maximum,minimum, ymaxs[],ymax,background, minarray[], yback[];
	
	
	public void background(double y[], int npt) {
		
		int	i, j, k, l, m=0;
		
		double sum=0.0;

		minarray = new double[21];
		
		yback = new double[npt+1];
		
		ymaxs = new double[npt+1];
		
		for (i=1;i<=npt;i++) yback[i] = y[i];
		
		maximum=0.0;
		
		//Finding maximum value in array
		
		for (i=1;i<=npt;i++) {
		
			if (yback[i] > maximum && yback[i] !=65535.0) maximum = yback[i];		
		}
		
		/*What to do if there is more than one value equal to the maximum*/
		for (i=1;i<=npt;i++) {
		
			if (yback[i] == maximum) {
				
				m++;
				
				ymaxs[m] = i;
			}
		}
		
		if (m==1) ymax = ymaxs[m];
		
		if (m>1) {
			
			ymax = (int)Math.rint((ymaxs[m]-ymaxs[1])/2);
		}
		

		//Loop to find 10 lowest values
		for (j=1;j<=20;j++) {
		
			minimum=65535.0;
			
			// Finding minimum value
			for  (i=1;i<=npt;i++) {
			
				if (yback[i] < minimum && yback[i] != 0.0) minimum = yback[i];
			}
			
			
			/* Setting this minimum value to a high one so it doesn't show in the search for next lowest value */
			l=1; 
			
			k=0;
			
			while (l <= npt && k < 1) {
			
				if (yback[l] == minimum) {
				
					yback[l] = 65535.0;
					
					k++;
					
				} else k = 0 ;
				
				l++;
			}
			
			//Storing minimum found as part of array
			minarray[j] = minimum;
		}
		
		//Averaging 20 lowest values to find background
		
		for (i=1;i<=20;i++) sum += minarray[i];
		
		background = sum / 20.0;
			
	}
}
