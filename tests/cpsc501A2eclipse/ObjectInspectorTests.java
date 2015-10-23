package cpsc501A2eclipse;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import fromD2l.*;
import objectInspector.Helpers;
import objectInspector.Inspector;

public class ObjectInspectorTests {
	ClassA firstClass;
	ClassB secondClass;
	ClassD thirdClass;
	Inspector inspector = new Inspector();
	
	@Before
	public void setUp()
	{
		firstClass = new ClassA();
		try 
		{
			secondClass = new ClassB();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		thirdClass = new ClassD();
		
	}

	@Test
	public void testTraverseInheritanceHierarcy()
	{
		// Single Superclass
		ArrayList<String> result = inspector.traverseInheritanceHierarcy(ClassA.class, firstClass, false, 0);
		String expected = "\n\t-----------------------------------------------------------\n\tNEW CLASS:\n\tjava.lang."
				+ "Object\n\n\tImmediate Superclass Name:\n\tnull\n\n\tInterfaces Implemented:\n\t\n\n\tMethods:\n\tpr"
				+ "otected void finalize() throws java.lang.Throwable\n\tpublic final void wait() throws java.lang.Int"
				+ "erruptedException\n\tpublic final void wait(long, int) throws java.lang.InterruptedException\n\tpub"
				+ "lic final native void wait(long) throws java.lang.InterruptedException\n\tpublic boolean equals(jav"
				+ "a.lang.Object)\n\tpublic java.lang.String toString()\n\tpublic native int hashCode()\n\tpublic fina"
				+ "l native java.lang.Class getClass()\n\tprotected native java.lang.Object clone() throws java.lang.C"
				+ "loneNotSupportedException\n\tpublic final native void notify()\n\tpublic final native void notifyAl"
				+ "l()\n\tprivate static native void registerNatives()\n\n\tConstructors:\n\tpublic java.lang.Object()"
				+ "\n\n\tFields:\n\t\n\n\t-----------------------------------------------------------\n";
		assertTrue(result.contains(expected));
		
		
		// No superclass hierarchy  
		result = inspector.traverseInheritanceHierarcy(Object.class, new Object(), false, 0);
		ArrayList<String> expectedList = new ArrayList<String>();
		assertEquals(expectedList, result);
	}
	@Test
	public void testFindConstructorInfo()
	{
		
		// Regular, typical case
		ArrayList<String> result = inspector.findConstructorInfo(ClassA.class);
		String [] initializer = {"public fromD2l.ClassA()", "public fromD2l.ClassA(int)"};
		ArrayList<String> expectedList = new ArrayList<String>();
		initializeArrayList(expectedList, initializer);
		assertTrue(result.containsAll(expectedList));
		
		// Constructor throws exception
		result = inspector.findConstructorInfo(ClassB.class);
		String [] initializer2 = {"public fromD2l.ClassB() throws java.lang.Exception"};
		expectedList = new ArrayList<String>();
		initializeArrayList(expectedList, initializer2);
		assertTrue(result.containsAll(expectedList));
		
		// Two more typical cases
		result = inspector.findConstructorInfo(ClassC.class);
		String [] initializer3 = {"public fromD2l.ClassC()", "public fromD2l.ClassC(int, int)"};
		expectedList = new ArrayList<String>();
		initializeArrayList(expectedList, initializer3);
		assertTrue(result.containsAll(expectedList));
		
		result = inspector.findConstructorInfo(ClassD.class);
		String [] initializer4 = {"public fromD2l.ClassD()", "public fromD2l.ClassD(int)"};
		expectedList = new ArrayList<String>();
		initializeArrayList(expectedList, initializer4);
		assertTrue(result.containsAll(expectedList));
	}
	
	@Test
	public void testPrefix()
	{
		// fail case
		String result = Helpers.prefix(-1);
		assertEquals(result, "");
		
		// edge case
		result = Helpers.prefix(0);
		assertEquals(result, "");
		
		// typical case
		result = Helpers.prefix(5);
		assertEquals(result, "\t\t\t\t\t");
	}
	
	@Test
	public void testFindDeclaredFieldInfo() {
		ArrayList<String> expected = new ArrayList<String>();
		
		// All primitive fields
		ClassA instanceA = new ClassA();
		ArrayList<String> result = inspector.findDeclaredFieldInfo(ClassA.class, instanceA, false, 0);
		String[] initializer = {"private int val = 3","private double val2 = 0.2", "private boolean val3 = true"};
		initializeArrayList(expected, initializer);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
		// Object fields, one null
		ClassB instanceB = null;
		try {
			 instanceB = new ClassB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		result = inspector.findDeclaredFieldInfo(ClassB.class, instanceB, false, 0);
		String[] initializer2 = {"private fromD2l.ClassA val = ClassA", "private fromD2l.ClassA val2 = ClassA", "private fromD2l.ClassA val3 = null"};
		initializeArrayList(expected, initializer2);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
		// Object fields, including Array
		ClassD instanceD = new ClassD();
		result = inspector.findDeclaredFieldInfo(ClassD.class, instanceD, false, 0);
		String[] initializer4 = {"private fromD2l.ClassA val = ClassA", "private static fromD2l.ClassA val2 = null", "private int val3 = 34", "private fromD2l.ClassA[] vallarray = [null, null, null, null, null, null, null, null, null, null]"};
		initializeArrayList(expected, initializer4);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
	}

	@Test
	public void testFindDeclaredMethodInfo() {
		ArrayList<String> expected = new ArrayList<String>();
		
		// Few methods, one with exception
		ArrayList<String> result = inspector.findDeclaredMethodInfo(ClassA.class);
		String[] initializer = {"public void run()","public java.lang.String toString()", "public int getVal()", "public void setVal(int) throws java.lang.Exception", "private void printSomething()"};
		initializeArrayList(expected, initializer);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
		// Few methods, typical case
		result = inspector.findDeclaredMethodInfo(ClassB.class);
		String[] initializer2 = {"public void run()","public java.lang.String toString()", "public void func3(int)"};
		initializeArrayList(expected, initializer2);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
		// Many methods, exceptions, multiple parameters, abstract class
		result = inspector.findDeclaredMethodInfo(ClassC.class);
		String[] initializer3 = {"public void run()","public java.lang.String toString()", 
				"public void func0(int, boolean) throws java.lang.Exception", "public void func1(int, double, boolean, java.lang.String) throws java.lang.Exception", 
				"public int func2(java.lang.String) throws java.lang.Exception, java.lang.ArithmeticException, java.lang.IllegalMonitorStateException", 
				"public abstract void func3(int)"};
		initializeArrayList(expected, initializer3);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
		// Regular old typical case
		result = inspector.findDeclaredMethodInfo(ClassD.class);
		String[] initializer4 = {"public java.lang.String toString()", "public int getVal3()"};
		initializeArrayList(expected, initializer4);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
	}
	
	// Helper to initialize an arraylist using a string array
	private void initializeArrayList(ArrayList<String> list, String[] initializer)
	{
		list.clear();
		
		for(String string : initializer)
		{
			list.add(string);
		}
	}

	@Test
	public void testFindInterfaceInfo() {
		ArrayList<String> expected = new ArrayList<String>();
		String expectedString;
		
		// Run for each tester class
		
		// 2 interfaces, no hierarchy
		ArrayList<String> result = inspector.findInterfaceInfo(ClassA.class, 0);
		String[] initializer = {"\n\t-----------------------------------------------------------\n\t"
				+ "NEW CLASS:\n\tjava.io.Serializable\n\n\tImmediate Superclass Name:\n\tnull\n\n\t"
				+ "Interfaces Implemented:\n\t\n\n\tMethods:\n\t\n\n\tConstructors:\n\t\n\n\tFields:"
				+ "\n\t\n\n\t-----------------------------------------------------------\n", 
				"\n\t-----------------------------------------------------------\n\tNEW CLASS:\n\t"
				+ "java.lang.Runnable\n\n\tImmediate Superclass Name:\n\tnull\n\n\tInterfaces "
				+ "Implemented:\n\t\n\n\tMethods:\n\tpublic abstract void run()\n\n\tConstructors:"
				+ "\n\t\n\n\tFields:\n\t\n\n\t-----------------------------------------------------------\n"};
		initializeArrayList(expected, initializer);
		assertTrue(result.toString() + "\n" + expected.toString(), result.containsAll(expected));
		
		
		// 1 Interface, no hierarchy 
		inspector = new Inspector();
		result = inspector.findInterfaceInfo(ClassB.class, 0);
		expectedString = "\n\t-----------------------------------------------------------\n\tNEW CLASS:"
				+ "\n\tjava.lang.Runnable\n\n\tImmediate Superclass Name:\n\tnull\n\n\tInterfaces "
				+ "Implemented:\n\t\n\n\tMethods:\n\tpublic abstract void run()\n\n\tConstructors:\n\t\n"
				+ "\n\tFields:\n\t\n\n\t-----------------------------------------------------------\n";
		assertTrue(result.contains(expectedString));
		
		
		// No interfaces
		result = inspector.findInterfaceInfo(ClassD.class, 0);
		expected = new ArrayList<String>(); // Because there are no interfaces, it will be an empty arraylist
		assertEquals(expected, result);	
		
	}

}
