/*==========================================================================
File: Asst2TestDriver.java
Purpose: Driver program that loads the objects inspector and runs the
         tests. Verification of tests is done through the inspection
         of the out from the object inspector loaded at run time.

Location: University of Calgary, Alberta, Canada
Created By: Jordan Kidney
Created on:  Oct 23, 2005
Last Updated: Oct 23, 2005
========================================================================*/
package fromD2l;

import java.lang.reflect.*;

public class Asst2TestDriver
{
    
    //-------------------------------------------------------------------
    public Asst2TestDriver(String ObjInspectorName, boolean recursive)
	throws Exception
    {
	this.recursive=recursive;
	setObjectInspectorInfo(ObjInspectorName);
    }
    //--------------------------------------------------------------------
    public void setObjectInspectorInfo(String ObjectInspectorName)
	throws Exception
    {
	Class objInspectClass=null;
	try
	    {
		objInspectClass = Class.forName(ObjectInspectorName);
		ObjInspector = objInspectClass.newInstance();
	    }
	catch(Exception e) 
	    {
		throw new Exception("Unable create instance of your object inspector");
	    }

	// get reference to inspect method
	try
	    {
		Class[] param = { Object.class, boolean.class };
		inspectionMethod = objInspectClass.getDeclaredMethod("inspect",param);
	    }
	catch(Exception e) 
	    {
		throw new Exception("Unable to find required method: public void inspect(Object obj,boolean recursive)");
	    }
    }
    //--------------------------------------------------------------------
    public void runTest(Object testObj) throws Exception
    {
	try
	    {
		System.out.println("======================================================");
		System.out.println("Running Test: " + testObj);
		Object[] param = { testObj, new Boolean(recursive) };
		inspectionMethod.invoke(ObjInspector, param); 
		System.out.println("======================================================");
	    }
	catch(Exception e)
	    {
		
		e.printStackTrace();
		throw new Exception("unable to compleatly run test");
	    
	    }
    }
    //------- Fields -----------------------------------------------------
    private Object ObjInspector = null;
    private Method inspectionMethod =null;
    private boolean recursive=false;
    //====================== MAIN =======================================
    public static void main(String[] args)
    {
	boolean rec=true;

	if(args.length >= 1)
	    {
		if(args.length > 1) rec = Boolean.parseBoolean(args[1]); // Bug fixed

		try
		    {
			System.out.println("Loading object inspector: " + args[0]);
			Asst2TestDriver driver = new Asst2TestDriver(args[0],rec);
			driver.runTest( new ClassA() );
			driver.runTest( new ClassA(12) );
			driver.runTest( new ClassB() );
			driver.runTest( new ClassD(32) );
			driver.runTest( new ClassD() );
			driver.runTest( new ClassB[12] );
			driver.runTest( new ClassB[12][12] );	
			driver.runTest( "Test String" );
		    }
		catch(Exception e)
		    {
			
			System.out.println("ERROR: " + e.getMessage());
			System.out.println("Exiting test driver");
		    }
	    }
	else
	    System.out.println("usage: java Asst2TestDriver <object inspector name> [false]\n\nFirst argument is the name of the object inspector class to load, this class should be in the current directory. the second argument is optional, if set to false the recursive introspection will not be run for the tests.\n\n");

    }
}
