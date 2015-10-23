package cpsc501A2eclipse;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import fromD2l.*;
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
	
	// More of an integration test because it utilizes all the other methods.
	@Test
	public void testInspect() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindDeclaredFieldInfo() {
		ArrayList<String> expected = new ArrayList<String>();
		
		ClassA instanceA = new ClassA();
		ArrayList<String> result = inspector.findDeclaredFieldInfo(ClassA.class, instanceA, false);
		String[] initializer = {"private int val = 3","private double val2 = 0.2", "private boolean val3 = true"};
		initializeArrayList(expected, initializer);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
		ClassB instanceB = null;
		try {
			 instanceB = new ClassB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		result = inspector.findDeclaredFieldInfo(ClassB.class, instanceB, false);
		String[] initializer2 = {"private fromD2l.ClassA val = ClassA", "private fromD2l.ClassA val2 = ClassA", "private fromD2l.ClassA val3 = null"};
		initializeArrayList(expected, initializer2);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
		// Abstract class, can't be instantiated
		/* ASK ABOUT HOW TO TEST ABSTRACT CLASS
		ClassC instanceC = instanceB;
		result = inspector.findDeclaredFieldInfo(ClassC.class, null);
		System.out.println(result);
		String[] initializer3 = {"private fromD2l.ClassA val2 = ClassA","private fromD2l.ClassA val3 = null", "private fromD2l.ClassA val4 = null"};
		initializeArrayList(expected, initializer3);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		*/
		
		ClassD instanceD = new ClassD();
		result = inspector.findDeclaredFieldInfo(ClassD.class, instanceD, false);
		String[] initializer4 = {"private fromD2l.ClassA val = ClassA", "private static fromD2l.ClassA val2 = null", "private int val3 = 34", "private fromD2l.ClassA[] vallarray = [null, null, null, null, null, null, null, null, null, null]"};
		initializeArrayList(expected, initializer4);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
	}

	@Test
	public void testFindDeclaredMethodInfo() {
		ArrayList<String> expected = new ArrayList<String>();
		
		ArrayList<String> result = inspector.findDeclaredMethodInfo(ClassA.class);
		String[] initializer = {"public void run()","public java.lang.String toString()", "public int getVal()", "public void setVal(int) throws java.lang.Exception", "private void printSomething()"};
		initializeArrayList(expected, initializer);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
		result = inspector.findDeclaredMethodInfo(ClassB.class);
		String[] initializer2 = {"public void run()","public java.lang.String toString()", "public void func3(int)"};
		initializeArrayList(expected, initializer2);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
		result = inspector.findDeclaredMethodInfo(ClassC.class);
		String[] initializer3 = {"public void run()","public java.lang.String toString()", 
				"public void func0(int, boolean) throws java.lang.Exception", "public void func1(int, double, boolean, java.lang.String) throws java.lang.Exception", 
				"public int func2(java.lang.String) throws java.lang.Exception, java.lang.ArithmeticException, java.lang.IllegalMonitorStateException", 
				"public abstract void func3(int)"};
		initializeArrayList(expected, initializer3);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
		result = inspector.findDeclaredMethodInfo(ClassD.class);
		String[] initializer4 = {"public java.lang.String toString()", "public int getVal3()"};
		initializeArrayList(expected, initializer4);
		assertTrue(result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
	}
	
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
		ArrayList<String> result = inspector.findInterfaceInfo(ClassA.class);
		String[] initializer = {"java.io.Serializable","java.lang.Runnable"};
		initializeArrayList(expected, initializer);
		assertTrue(result.toString() + "\n" + expected.toString(), result.containsAll(expected));
		assertTrue(result.size() == expected.size());
		
		result = inspector.findInterfaceInfo(ClassB.class);
		expectedString = "java.lang.Runnable";
		assertTrue(result.contains(expectedString));
		assertTrue(result.size() == 1);
		
		result = inspector.findInterfaceInfo(ClassC.class);
		expectedString = "fromD2l.InterfaceA";
		assertTrue(result.contains(expectedString));
		assertTrue(result.size() == 1);
		
		result = inspector.findInterfaceInfo(ClassD.class);
		expected = new ArrayList<String>(); // Because there are no interfaces, it will be an empty arraylist
		assertEquals(expected, result);	
		
	}

	@Test
	public void testFindClassInfo() {
		fail("Not yet implemented");
	}

}
