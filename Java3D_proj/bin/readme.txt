
*********************************************************************************************
*                                                                                           *
*********************************************************************************************
1/15/2012

1. several obj file are removed since they don't have texture vertex coordinates. When use 
auto generated texture coordinates, there are some wired spots on the display. There are some 
files of obj format can be downloaded free. If the objects in the data directory in not suited
for the testing, feel free to find one. Is facegen software can save obj file? if so, you may 
use face object to the testing.

2. If a new object file is adding, for example the object file is abc.obj, then you need to edit 
abc.obj and  put "#" to the front of all lines starts with either "matlib" or "usemtl" since 
we would like to use random dot texture.

3. When you would like to add a new object, just put the file in data directory and add its 
name to TestDialog.java around line 19, for example add line "abc.obj". 

4. type "java Mytest" to do the display. When key "d" or "D" is pushed, the images will 
auto generated in images directory.  compile is the same as before.

*********************************************************************************************
*                                                                                           *
*********************************************************************************************
1. installation java 
------------------------------------------------------
 click on download/jdk-7u2-windows-i586 to install java lanauage.
(You may change the default installation location of c:\program files\java\jdk1.7.0_02 to
c:\java\jdk1.7.0_02)

2. installation of java3d
-----------------------------------------------------
doule click on java3d-1_3_1-windows-i586-directx-rt.exe
change location to the same as you specified in 1.

3.set environment variable
------------------------------------------------------ 
if you installed in c:/java/jdk1.7.0_02, then put c:/java/jdk1.7.0_02/bin in your path enviroment variable
(click start button(first button in window), right mouse over computer, click on advanced tabe, then you will see the environment variable. adding the above to the path )

4. compile
----------------------------------------------------
in sphere directory of all the java source code, type "javac -d myclass *java"

5. execute
----------------------------------------------------
down to myclass directory from 4, type "java DisplayMain"
key stoke of the following funtion:
m:   dialog popup to let you chose object, dot texture etc.
a:   start animation
s:   stop animation







