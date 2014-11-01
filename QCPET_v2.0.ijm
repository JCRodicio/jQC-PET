requires("1.42n");


var sCmds = newMenu("QCPET Menu Tool",
newArray("Calidad de Imagen", "Uniformidad Tomografica", "Resolucion Espacial", "-", "...Acerca de QCPET..."));
macro "QCPET Menu Tool - T0710Q T8710C T0f10P T8f10T" {
cmd = getArgument();

//path = getDirectory("macros")+"QC_PET"+File.separator+"modulos";
path = getDirectory("current");

//----------------------------------------------------------------------------------------------------------------------------

if (cmd=="Calidad de Imagen") 
{
//runMacro(path+File.separator+"IQ_PET.ijm");
runMacro("QC_PET\\modulos\\IQ_PET.ijm");
	exit
}

//----------------------------------------------------------------------------------------------------------------------------

if (cmd=="Uniformidad Tomografica") 
{
runMacro("QC_PET\\modulos\\Uniformidad.ijm");
	exit
}

//----------------------------------------------------------------------------------------------------------------------------

if (cmd=="Resolucion Espacial") 
{
runMacro("QC_PET\\modulos\\resolucion_PET.ijm");
	exit
}

//----------------------------------------------------------------------------------------------------------------------------

if (cmd=="...Acerca de QCPET...") 
{
path = getDirectory("current");
open(path + File.separator + "AcercaQCPET.bmp");
	exit
}


if(cmd!="-") {
run(cmd);
}
}
