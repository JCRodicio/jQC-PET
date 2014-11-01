if(roiManager("count")!=0){
	roiManager("reset");
}
run("Clear Results");
run("Set Measurements...", "area mean min centroid redirect=None decimal=3");

requires("1.32f");
  title = getTitle;
  width_i = getWidth;
  height_i = getHeight;
  depth = nSlices;
  getPixelSize(unit, pw_i, ph_i, pd);
  //print(width_i, height_i, depth);
  //print(unit, pw_i, ph_i, pd);
  path = getDirectory("image");
  getThreshold(t1, t2); 

run("Size...", "width=" +width_i*pw_i*2+" height=" +height_i*ph_i*2+" depth=" +depth+" constrain average interpolation=None");
width = getWidth;
height = getHeight;
getPixelSize(unit, pw, ph, pd);

//print(width, height, depth);
//print(unit, pw, ph, pd);
run("Set Scale...", "distance=" +1/pw+" known=1 pixel=1 unit=mm global");

ratio=getNumber("Relacion de Actividades (4 u 8): ", 0);
//meter el corte donde esta el centro de las esferas
var nSlice;
nSlice=getString("num. corte de las esferas: ", nSlice);
setSlice(nSlice);



//busco el centro de las esferas frias
run("Copy");
newImage("SelectCOLD", "16-bit Black", width, height, 1);
selectWindow("SelectCOLD");
run("Paste");
var AutoThres="si/no";
AutoThres=getString("Hacer umbrales automaticos? Esferas FRIAS!!", AutoThres);
if(AutoThres=="si"){
	setAutoThreshold("Default dark");
} else {
	run("Threshold...");
	waitForUser("...", "Pulsa OK cuando se haya establecido el umbral");
}
//busco la particula que identifique el inserto de pulmón.
//el centro del inserto de pulmón será mi origen de coordenadas para dibujar las ROIs de fondo
//tb tengo que buscar la partícula que identifique a cada una de las esferas frias
run("Analyze Particles...", "size=10-2500 circularity=0.00-1.00 show=Nothing clear record add");
close();
selectWindow(title);

//busco el centro de las esferas calientes
run("Copy");
newImage("SelectHOT", "16-bit Black", width, height, 1);
selectWindow("SelectHOT");
run("Paste");
var AutoThres="si/no";
AutoThres=getString("Hacer umbrales automaticos? Esferas CALIENTES!!", AutoThres);
if(AutoThres=="si"){
	setAutoThreshold("Default dark");
} else {
	run("Threshold...");
	waitForUser("...", "Pulsa OK cuando se haya establecido el umbral");
}
run("Analyze Particles...", "size=5-2500 circularity=0.00-1.00 show=Nothing record add");
close();
selectWindow(title);

//hay que obtener el centro del inserto central y de 6 esferas
Xc_tmp = newArray(7);
Yc_tmp = newArray(7);
Xc = newArray(7);
Yc = newArray(7);
Area_tmp = newArray(7);
diametro = newArray(7);
for(i=0; i<7; i++)
{
	Xc_tmp[i] = getResult("X",i);
	Yc_tmp[i] = getResult("Y",i);
	Area_tmp[i] = getResult("Area",i);
}

//for(i=0; i<7; i++)
//{
//	print(Area_tmp[i], Xc_tmp[i],Yc_tmp[i]);
//}

//Hay que ordenar las esferas en el orden correcto
//primero el inserto central --> Xc[1],Yc[1]
//luego las esferas de más diametro a menos
//algoritmo para las tres esferas frias:
if (Area_tmp[0] > Area_tmp[1]) {
        Xc[0]=Xc_tmp[0];
        Xc[1]=Xc_tmp[1];
        Xc[2]=Xc_tmp[2];
        Yc[0]=Yc_tmp[0];
        Yc[1]=Yc_tmp[1];
        Yc[2]=Yc_tmp[2];
           
} else {
 	Xc[0]=Xc_tmp[1];
        Xc[1]=Xc_tmp[2];
        Xc[2]=Xc_tmp[0];
        Yc[0]=Yc_tmp[1];
        Yc[1]=Yc_tmp[2];
        Yc[2]=Yc_tmp[0];
}
        
//algoritmo para las esferas calientes:
if (Area_tmp[3] > Area_tmp[6]) {
	Xc[3]=Xc_tmp[3];
        Xc[4]=Xc_tmp[4];
        Xc[5]=Xc_tmp[5];
        Xc[6]=Xc_tmp[6];
        Yc[3]=Yc_tmp[3];
        Yc[4]=Yc_tmp[4];
        Yc[5]=Yc_tmp[5];
        Yc[6]=Yc_tmp[6];
           
} else {
     	Xc[3]=Xc_tmp[6];
        Xc[4]=Xc_tmp[5];
        Xc[5]=Xc_tmp[3];
        Xc[6]=Xc_tmp[4];
        Yc[3]=Yc_tmp[6];
        Yc[4]=Yc_tmp[5];
        Yc[5]=Yc_tmp[3];
        Yc[6]=Yc_tmp[4];
}

diametro[0]= 30; //en mm
diametro[1]= 37;
diametro[2]= 28;
diametro[3]= 22;
diametro[4]= 17;
diametro[5]= 13;
diametro[6]= 10;

//a partir de las coordenadas centrales dibujo ROIs circulares del radio adecuado
//para dibujar una ROI circular:
//makeOval(x, y, width, height)
//Creates an elliptical selection, where (x,y) define the upper left corner of the bounding rectangle of the ellipse.
//coordinates are in pixels
roiManager("reset");
for(i=0; i<7; i++)
{
	makeOval((Xc[i]-diametro[i]/2)/pw,(Yc[i]-diametro[i]/2)/ph,diametro[i]/pw,diametro[i]/ph);
	roiManager("add");
}

//mido las esferas frias y calientes
//y limpio el RoiManager
roiManager("Measure"); 
roiManager("reset");

//busco 12 centros sobre los que dibujar las esferas del fondo
//las dibujo a partir de las coordenadas del inserto de pulmon
//coordenadas en mm
Xc_fondo = newArray(12);
Yc_fondo = newArray(12);

Xc_fondo[0]=0;
Xc_fondo[1]=70;
Xc_fondo[2]=-70;
Xc_fondo[3]=0;
Xc_fondo[4]=-105;
Xc_fondo[5]=-80;
Xc_fondo[6]=-40;
Xc_fondo[7]=100;
Xc_fondo[8]=100;
Xc_fondo[9]=90;
Xc_fondo[10]=55;
Xc_fondo[11]=-100;

Yc_fondo[0]=-75;
Yc_fondo[1]=-50;
Yc_fondo[2]=-50;
Yc_fondo[3]=80;
Yc_fondo[4]=30;
Yc_fondo[5]=70;
Yc_fondo[6]=85;
Yc_fondo[7]=-20;
Yc_fondo[8]=25;
Yc_fondo[9]=65;
Yc_fondo[10]=85;
Yc_fondo[11]=-10;



for(i=1; i<7; i++) //hago los 6 tamaños de ROI
{
	for(k=-2;k<=2;k++)//en cinco cortes -2cm,-1cm,nSlice,+1cm,+2cm
	{
		setSlice(nSlice+k*10/pd);
		for(j=0; j<12; j++) //hago las 12 posiciones de ROI_fondo y las añado al RoiManager
		{
			makeOval((Xc[0]+Xc_fondo[j]-diametro[i]/2)/pw,(Yc[0]+Yc_fondo[j]-diametro[i]/2)/ph,diametro[i]/pw,diametro[i]/ph);
			//print((Xc[0]+Xc_fondo[j]-diametro[i]/2)/pw,(Yc[0]+Yc_fondo[j]-diametro[i]/2)/ph,diametro[i]/pw,diametro[i]/ph);
			roiManager("add");
		}
	}
	roiManager("Measure");
	//waitForUser("...", "checkpoint: medida del fondo tamaño i. RoiManager lleno con 60 ROIs. Realizadas 60 medidas en 12*(5 cortes)");
	roiManager("reset");
}


//ya tenemos todas las ROIs. Hay que hacer estadisticas
//Contraste frio = (1- VMP_fria_j / avg_VMP_fondo de tamaño j) * 100
//Contraste caliente = (VMP_caliente_j / avg_VMP_fondo de tamaño j - 1) * 100 / (ratio-1)
//Variabilidad del fondo = SD_j *100 / avg_VMP_j
//aqui guardo el promedio de los VMP del fondo de cada tamaño (6 tamaños diferentes) --> avgVMP_fondo[j]
Tmp = newArray(60);
maxVMP_fondo = newArray(6);
minVMP_fondo = newArray(6);
avgVMP_fondo = newArray(6);
SD_fondo = newArray(6);
VMP_esfera = newArray(6);
Contraste = newArray(6);
Variabilidad = newArray(6);
//ratio = 8;

//print("nSize, backPos, Tmp[backPos]");
for(nSize=0; nSize<6; nSize++)
{
	VMP_esfera[nSize]=getResult("Mean",nSize+1); //VMP de las esferas
	for(backPos=0; backPos<60; backPos++)
	{
		Tmp[backPos]=getResult("Mean",60*nSize + backPos + 7);
		//print(nSize, backPos, Tmp[backPos]);
	}
	Array.getStatistics(Tmp,minVMP_fondo[nSize],maxVMP_fondo[nSize],avgVMP_fondo[nSize],SD_fondo[nSize]);
	if(nSize<2)
	{
		Contraste[nSize]=(1-VMP_esfera[nSize]/avgVMP_fondo[nSize])*100;
	}else{
		Contraste[nSize]=(VMP_esfera[nSize] / avgVMP_fondo[nSize] - 1) * 100 / (ratio-1);
	}
	Variabilidad[nSize]=SD_fondo[nSize]*100/avgVMP_fondo[nSize];
	//print(nSize,minVMP_fondo[nSize],maxVMP_fondo[nSize],avgVMP_fondo[nSize],SD_fondo[nSize]);
}


//escribo los resultados en una tabla
run("Clear Results");
//i=nResults;
for(j=1;j<7;j++)
{
	i = nResults;
	setResult("#Esfera",i,j+1);
	setResult("Diametro(mm)",i,diametro[j]);
	setResult("Contraste(%)",i,Contraste[j-1]);
	setResult("Variabilidad(%)",i,Variabilidad[j-1]);
	
}
updateResults();


waitForUser("...", "Guarda los resultados. Voy a empezar con la variabilidad del pulmon");
if(roiManager("count")!=0){
	roiManager("reset");
}
//-------------------------------------------------------------------------------------------------------------
//A partir de aqui calculo la variabilidad del inserto de pulmon
//en cada corte hay que 
//dibujar 1 ROI de 3cm de diametro centrada en el inserto
//dibujar 12 ROIs de 3cm de diametro en las posiciones del fondo

//recorro los cortes para el pulmon
for(i=1; i<=nSlices; i++)
{
	setSlice(i);
	//i=0 es la posición del inserto de pulmon
	makeOval((Xc[0]-diametro[0]/2)/pw,(Yc[0]-diametro[0]/2)/ph,diametro[0]/pw,diametro[0]/ph);
	roiManager("add");
	
}

//recorro los cortes para el fondo
for(i=1; i<=nSlices; i++)
{
	setSlice(i);
	for(j=0; j<12; j++) //hago las 12 posiciones de ROI_fondo y las añado al RoiManager
	{
		makeOval((Xc[0]+Xc_fondo[j]-diametro[0]/2)/pw,(Yc[0]+Yc_fondo[j]-diametro[0]/2)/ph,diametro[0]/pw,diametro[0]/ph);
		roiManager("add");
	}
}
roiManager("Measure");
//waitForUser("...", "CheckPoint: 1170 ROIs, 90 en el centro y 12*90 en el fondo");
//cada slice tiene asignado un VMP_lung, avgVMP_fondo, VarLung
VMP_lung = newArray(nSlices);
avgVMP_fondo = newArray(nSlices);
VMPmin = newArray(nSlices);
VMPmax = newArray(nSlices);
SD = newArray(nSlices);
VarLung = newArray(nSlices);
VarMax = newArray(nSlices);
VarMin = newArray(nSlices);
Slice = newArray(nSlices);


//tengo que hacer un array temporal con los VMP de cada ROI del fondo del slice "i"
Tmp = newArray(12);
for(i=0;i< nSlices; i++)
{
	VMP_lung[i]=getResult("Mean",i);
	Array.fill(Tmp,0);
	for(j=0;j<= 11; j++)
	{
		Tmp[j]=getResult("Mean",12*i+j+nSlices);
	}
	Array.getStatistics(Tmp,VMPmin[i],VMPmax[i],avgVMP_fondo[i],SD[i]);
	//print(i+1, VMPmin[i], VMPmax[i], VMPmean[i], SD[i]);

	if(avgVMP_fondo[i]!=0)
	{
		VarLung[i] = 100*VMP_lung[i]/avgVMP_fondo[i];
	} else {
		VarLung[i] = 0;
	}
	Slice[i] = (i+1)*pd/10; //pongo los cortes en cm
}

Array.getStatistics(VarLung,Mmin,Mmax);
Array.getStatistics(Slice,Smin,Smax);

Plot.create("error residual de pulmón (%)", "Slice (cm)", "%");
Plot.setLimits(Smin-1,Smax+1,Mmin,Mmax+5);
Plot.setColor("black");
Plot.add("circles", Slice, VarLung);
Plot.show;

