if(roiManager("count")!=0){
	roiManager("reset");
}
run("Clear Results");
run("Set Measurements...", "area mean min centroid redirect=None decimal=3");

requires("1.32f");
  title = getTitle;
  width = getWidth;
  height = getHeight;
  depth = nSlices;
  getPixelSize(unit, pw, ph, pd);
  path = getDirectory("image");
  getThreshold(t1, t2); 

//print(path);
//print(unit, pw, ph, pd);
run("Set Scale...", "distance=" +1/pw+" known=1 pixel=1 unit=mm global");

//meter el corte donde esta el centro de las esferas
var nSlice;
nSlice=getNumber("num. corte de las esferas: ", nSlice);
setSlice(nSlice);

//busco el centro de los zeolitos
run("Copy");
newImage("Select Threshold", "16-bit Black", width, height, 1);
selectWindow("Select Threshold");
run("Paste");

setAutoThreshold("Default dark");
run("Convert to Mask");
//	run("Threshold...");
	//waitForUser("...", "Pulsa OK cuando se haya establecido el umbral");

//busco las particulas
run("Analyze Particles...", "size=0-Infinity circularity=0.00-1.00 show=Nothing clear record add");
close();
selectWindow(title);

//hay que obtener el centro de las particulas
Xc_tmp = newArray(3);
Yc_tmp = newArray(3);
Xc = newArray(3);
Yc = newArray(3);
Area_tmp = newArray(3);
diametro = newArray(3);
for(i=0; i<3; i++)
{
	Xc_tmp[i] = getResult("X",i);
	Yc_tmp[i] = getResult("Y",i);
	//Area_tmp[i] = getResult("Area",i);
}

//Hay que ordenar las esferas en el orden correcto
//	#1 -> 0,1
//	#2 -> 0,10
//	#3 -> 10,0

if (Xc_tmp[1] < Xc_tmp[0]-50) {
        Xc[2]=Xc_tmp[1];
        Xc[0]=Xc_tmp[2];
        Xc[1]=Xc_tmp[0];
        Yc[2]=Yc_tmp[1];
        Yc[1]=Yc_tmp[2];
        Yc[0]=Yc_tmp[0];
} else {
 	Xc[2]=Xc_tmp[2];
        Xc[0]=Xc_tmp[1];
        Xc[1]=Xc_tmp[0];
        Yc[2]=Yc_tmp[2];
        Yc[0]=Yc_tmp[1];
        Yc[1]=Yc_tmp[0];
}

ROIsize= 40; //en mm
nSliceMin = nSlice - 6;
nSliceMax = nSlice + 6;
var nSlice_tmp;
var Xpoint;
var Ypoint;
sResult = "Result.txt";
//para cada particula:
//	- hago una ROI alrededor suya
//	- hago vistas ortogonales
//	- corro FWHM en cada vista

roiManager("reset");
for(i=0; i<3; i++)
{
	makeRectangle((Xc[i]-ROIsize/2)/pw,(Yc[i]-ROIsize/2)/ph,ROIsize/pw,ROIsize/ph);
	roiManager("add");
}

for(i=0; i<3; i++)
{
	roiManager("Select",i);
	run("Make Substack...", "  slices="+nSliceMin+"-"+nSliceMax+"");
	selectWindow("Substack ("+nSliceMin+"-"+nSliceMax+")");
	run("Orthogonal Views");
	waitForUser("...", "Pulsa OK cuando se haya establecido la posición del máximo");
	//necesito conocer el title de las vistas ortogonales
	//XY --> Substack ("+nSliceMin+"-"+nSliceMax+")
	//YZ --> YZ+" "+Xpoint/2
	//XZ --> XZ+" "+Ypoint/2
	nSlice_tmp = getSliceNumber();
	run("Find Maxima...", "noise=10 output=[Point Selection]");
	run("Measure");
	Xpoint = getResult("X",i);
	Ypoint = getResult("Y",i);
	//print(Xpoint, Ypoint);
	selectWindow("Substack ("+nSliceMin+"-"+nSliceMax+")");
	run("Plot FWHM");
	selectWindow("Column Plot");
	close();
	selectWindow("Row Plot");
	close();
	selectWindow("YZ "+Xpoint/2+"");
	run("Plot FWHM");
	selectWindow("Column Plot");
	close();
	selectWindow("Row Plot");
	close();
	selectWindow("XZ "+Ypoint/2+"");
	run("Plot FWHM");
	selectWindow("Column Plot");
	close();
	selectWindow("Row Plot");
	close();

	selectWindow("Substack ("+nSliceMin+"-"+nSliceMax+")");
	close();
	selectWindow("YZ "+Xpoint/2+"");
	close();
	selectWindow("XZ "+Ypoint/2+"");
	close();
	selectWindow(title);
}

//leo la tabla de resultados y extraigo cada una de las medidas
//01x_1, 010x_1, 100x_1		01x_2, 010x_2, 100x_2
//01y_1, 010y_1, 100y_1		01y_2, 010y_2, 100y_2
//01z_1, 010z_1, 100z_1		01z_2, 010z_2, 100z_2

