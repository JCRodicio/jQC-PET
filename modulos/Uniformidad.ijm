if(roiManager("count")!=0){
	roiManager("reset");
}
run("Clear Results");

requires("1.32f");
  title = getTitle;
  width = getWidth;
  height = getHeight;
  depth = nSlices;
  getPixelSize(unit, pw, ph, pd);
  path = getDirectory("image");
  getThreshold(t1, t2); 

run("Set Measurements...", "area mean min centroid redirect=None decimal=3");


setSlice(22);
run("Copy");

newImage("Untitled", "16-bit Black", width, height, 1);
selectWindow("Untitled");
run("Paste");
run("Invert LUT");

setAutoThreshold("Default");

run("Convert to Mask");
run("Analyze Particles...", "size=1500-2500 circularity=0.00-1.00 show=Nothing clear record add");

  for (i=0; i<nResults-1; i++)
  {
      x = getResult('XStart', i);
      y = getResult('YStart', i);
      doWand(x,y);
      roiManager("add");
  }

//hay que obtener el centro de la ROI Xc, Yc
//a partir de Xc, Yc se dibujarán todas las ROIs

Xc = getResult("X",0);
Yc = getResult("Y",0);
radio = sqrt(getResult("Area",0)/3.14);

//cierro la imagen de analisis
close();
roiManager("reset");

k=0;
setSlice(1);

x=Xc-radio;
y=Yc-radio;
   for (j=0; j<15; j++)
   {
      for (i=0; i<15; i++)
      {
         m=sqrt((x-Xc)*(x-Xc)+(y-Yc)*(y-Yc));
         if(i<15)
	 {
            if (m<radio-4)
	    {  
	       makeRectangle(x-2, y-2, 4, 4);             
	       run("Add to Manager");
               k=k+1;
            }
            x=x+4;
         }
      }
      x=Xc-radio;
      y=y+4;
   }
for (h=1; h<=45; h++)
{
	setSlice(h);
	roiManager("Measure");
}

//hacemos estadística de datos
//print(nResults/nSlices);
//me pongo en el corte i=1 y analizo nRois
//Busco el VMP-max, VMP-min y el promedio de los VMP

nROI = nResults / nSlices;

//cada slice tiene asignado un VMPmax, VMPmin y VMPmean
VMPmax = newArray(nSlices);
VMPmin = newArray(nSlices);
VMPmean = newArray(nSlices);
SD = newArray(nSlices);
CV = newArray(nSlices);
NUmax = newArray(nSlices);
NUmin = newArray(nSlices);
Slice = newArray(nSlices);

//tengo que hacer un array temporal con los VMP de cada ROI del slice "i"
Tmp = newArray(nROI);

for(i=0;i< nSlices; i++)
{
	//setSlice(i);
	Array.fill(Tmp,0);
	for(j=0;j<= nROI - 1; j++)
	{
		Tmp[j]=getResult("Mean",nROI*i+j);
	}

	Array.getStatistics(Tmp,VMPmin[i],VMPmax[i],VMPmean[i],SD[i]);
	//print(i+1, VMPmin[i], VMPmax[i], VMPmean[i], SD[i]);

	CV[i] = 100*SD[i]/VMPmean[i];
	NUmax[i] = 100*(VMPmax[i]-VMPmean[i])/VMPmean[i];
	NUmin[i] = -100*(VMPmean[i]-VMPmin[i])/VMPmean[i];
	Slice[i] = i+1;
}

Array.getStatistics(NUmax,Mmin,Mmax);
Array.getStatistics(NUmin,mmin,mmax);
Array.getStatistics(Slice,Smin,Smax);

Plot.create("No Uniformidad", "slice", "NU(i)");
Plot.setLimits(Smin-1,Smax+1,mmin,Mmax);
Plot.setColor("red");
Plot.add("circles", Slice, NUmax);
Plot.setColor("blue");
Plot.add("circles", Slice, NUmin);
Plot.show;

Array.getStatistics(CV,CVmin,CVmax);
Plot.create("CV-slices", "Slice", "CV(i)");
Plot.setLimits(Smin-1,Smax+1,0,CVmax);
Plot.setColor("black");
Plot.add("circles", Slice, CV);
Plot.show;


