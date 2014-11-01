package fwhm;



import java.io.*;

import java.util.*;


/**

Called by MrqMin.

**/


public class Covsrttest2	{



	public void covsrt(double covar[][],int MA, int lista[]) {

	  

	  int i, j; 

	  

	  double swap;

	  



	  for (j = 1; j < MA; j++) {        /*** Zero all elements below diagonal */



	    for (i = j+1;i <= MA;i++) covar[i][j] = 0.0;}  



	  /*** Repack off-diagonals of fit into correct locations below diagonal */

	  

	  for (i = 1; i < MA ; i++) {



	    for (j = i+1; j <= MA ; j++) {



	      if (lista[j] > lista[i]) covar[lista[j]][lista[i]] = covar[i][j];



	      else covar[lista[i]][lista[j]] = covar[i][j];

		} 

	  }

	    

	  swap = covar[1][1]; /*** Temporarily store original diagonal elements..*/



  	  for (j = 1; j <= MA; j++) {    /***...in top row and zero the diagonal */



    	covar[1][j] = covar[j][j];



    	covar[j][j] = 0.0;



  	  }



	  covar[lista[1]][lista[1]] = swap;  /*** Now sort elements into proper..*/



  	  for (j = 2; j <= MA; j++)                     /***...order on diagonal */



    	covar[lista[j]][lista[j]] = covar[1][j];



  	  for (j = 2; j <= MA; j++)    /*** Fill in above diagonal with symmetry */



    	for (i = 1; i <= j-1; i++) covar[i][j] = covar[j][i];

    	

  	

  

	}

}
