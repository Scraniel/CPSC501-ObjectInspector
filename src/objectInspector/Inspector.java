package objectInspector;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import fromD2l.ClassA;
import fromD2l.ClassB;
import fromD2l.ClassC;
import fromD2l.ClassD;

public class Inspector implements ReflectiveInspector 
{
	LinkedList<Object> toInspect = new LinkedList<Object>();
	//LinkedList<Class> toInspect = new LinkedList<Class>();
	HashSet<Object> inspected = new HashSet<Object>();

	public static void main(String[] args)
	{
		try {
			(new Inspector()).inspect(new ClassA(), true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void inspect(Object obj, boolean recursive) 
	{
		System.out.println("RECURSIVE IS SET TO " + recursive + ". WILL" + (recursive ? " " : " NOT ") + "RECURSIVELY INSPECT FIELDS.");
		
		toInspect.add(obj);		 
		//toInspect.add(obj.getClass());
		
		while(!toInspect.isEmpty())
		{
			Object instance = toInspect.pop();
			Class inspecting;
			
			// Checks if the added object was a Class; if so, this
			// means it's an interface that was added, so there is 
			// no instantiation of the object. Just inspect it as is.
			if(instance.getClass().isInstance(Class.class) )
			{
				inspecting = (Class)instance;
				instance = null;
			}
			else
			{
				inspecting = instance.getClass();
			}
			//Class inspecting = toInspect.pop();
			inspected.add(inspecting);
			
			System.out.println(findClassInfo(inspecting, instance, recursive, 0));
			System.out.println(String.join("\n" + prefix(0), traverseInheritanceHierarcy(inspecting, instance, recursive, 0)));
		}
		
		
	}
	
	public ArrayList<String> findDeclaredFieldInfo(Class inspecting, Object instance, boolean recursive, int depth)
	{
		StringBuilder builder = new StringBuilder();
		Field [] fields = inspecting.getDeclaredFields();
		ArrayList<String> strings = new ArrayList<String>();
		
		for(Field field : fields)
		{
			builder.setLength(0);
			// Modifiers
			String modifier = Modifier.toString(field.getModifiers());
			builder.append(modifier);
			builder.append(' ');
			
			//Type 
			builder.append(field.getType().getCanonicalName());
			builder.append(' ');
			
			// Value; if there is no instance, don't bother checking
			builder.append(field.getName());
			if(instance != null)
			{
				field.setAccessible(true);
				builder.append(" = ");
				try {
					Object obj = field.get(instance);
					if(obj != null){
						// Dealing with pesky arrays
						if(obj.getClass().isArray())
							builder.append(getInfoFromArray(obj));

						else
							builder.append(obj);
						
						// If the field is an object, inspect it
						if(!field.getType().isPrimitive() && recursive == true)
						{
							strings.add("***********************************************************");
							strings.add("Inspecting Field of " + inspecting.getCanonicalName() +": " + field.getName());
							strings.add("***********************************************************");
							if(!inspected.contains(obj))
							{
								inspected.add(obj);
								builder.append(findClassInfo(obj.getClass(), obj, false, depth + 1));
								builder.append(prefix(depth + 1) + String.join("\n" + prefix(depth + 1), traverseInheritanceHierarcy(obj.getClass(), obj, recursive, depth + 1)));
							}
							else
							{
								builder.append("This class has already been inspected!");
							}
						}
					}
					else
						builder.append(obj);
					
					
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			strings.add(builder.toString());
		}
		
		return strings;
		
	}
	
	private String getInfoFromArray(Object obj)
	{
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		int length = Array.getLength(obj);
	    for (int i = 0; i < length; i ++) {
	        Object arrayElement = Array.get(obj, i);
	        builder.append(arrayElement);
	        if(i < length -1)
	        	builder.append(", ");
	    }
	    builder.append(']');
	    
	    return builder.toString();
	}
	
	public ArrayList<String> findDeclaredMethodInfo(Class inspecting)
	{
		StringBuilder builder = new StringBuilder();
		Method [] methods = inspecting.getDeclaredMethods();
		ArrayList<String> strings = new ArrayList<String>();
		
		for(Method method : methods)
		{
			builder.setLength(0);
			// modifiers
			String modifier = Modifier.toString(method.getModifiers());
			builder.append(modifier);
			
			// Return type
			builder.append(' ');
			builder.append(method.getReturnType().getCanonicalName());
			
			// Name
			builder.append(' ');
			builder.append(method.getName());
			
			// Arguments
			builder.append('(');
			Class [] parameters = method.getParameterTypes();
			Class parameter;
			for(int i = 0; i < parameters.length; i++)
			{
				parameter = parameters[i];
				builder.append(parameter.getCanonicalName());
				
				// Commas between every parameter but the last one
				if(i < parameters.length -1)
				{
					builder.append(", ");
				}
			}
			builder.append(')');
			
			// Exceptions
			Class [] exceptions = method.getExceptionTypes();
			if(exceptions.length > 0){
				builder.append(" throws ");
				for(Class exception : exceptions)
				{
					builder.append(exception.getCanonicalName());
					builder.append(", ");
				}
				builder.deleteCharAt(builder.length()-1);
				builder.deleteCharAt(builder.length()-1);
			}
			
			strings.add(builder.toString());
		}
		
		return strings;
	}

	public ArrayList<String> findInterfaceInfo(Class inspecting, int depth)
	{
		StringBuilder builder = new StringBuilder();
		Class [] interfaces = inspecting.getInterfaces();
		ArrayList<String> strings = new ArrayList<String>();
		
		for(Class implementing : interfaces )
		{
			
			strings.add("***********************************************************");
			strings.add("Inspecting Interface of " + inspecting.getCanonicalName() +": " + implementing.getCanonicalName());		
			strings.add("***********************************************************");
			builder.setLength(0);
			
			if(inspected.contains(implementing))
			{
				builder.append(prefix(depth + 1) + "This class was already inspected!\n");
			}
			else
			{
				inspected.add(implementing);
				builder.append(findClassInfo(implementing, null, false, depth + 1));
			}
			strings.add(builder.toString());
		}
		
		return strings;
	}
	
	public ArrayList<String> traverseInheritanceHierarcy(Class inspecting, Object instance, boolean recursive, int depth)
	{
		StringBuilder builder = new StringBuilder();
		ArrayList<String> strings = new ArrayList<String>();
		ArrayList<Class> superClasses = new ArrayList<Class>();
		Class superClass = inspecting.getSuperclass();
		while(superClass != null)
		{
			superClasses.add(superClass);
			superClass = superClass.getSuperclass();
		}
		
		for(Class currentSuper : superClasses)
		{

			strings.add("***********************************************************");
			strings.add("Inspecting Superclass of " + inspecting.getCanonicalName() + ": " + currentSuper.getCanonicalName());
			strings.add("***********************************************************");

			builder.setLength(0);
			
			if(inspected.contains(currentSuper))
			{
				builder.append(prefix(depth + 1) + "This class was already inspected!\n");
			}
			else
			{
				builder.append(findClassInfo(currentSuper, instance, recursive, depth + 1));
			}
			strings.add(builder.toString());
		}
		
		return strings;
	}
	
	public ArrayList<String> findConstructorInfo(Class inspecting)
	{
		StringBuilder builder = new StringBuilder();
		ArrayList<String> strings = new ArrayList<String>();
		Constructor [] constructors = inspecting.getConstructors();
		
		for(int i = 0; i < constructors.length; i++)
		{
			builder.setLength(0);
			builder.append(Modifier.toString(constructors[i].getModifiers()));
			builder.append(" " + constructors[i].getName() + "(");
			
			Class [] parameters = constructors[i].getParameterTypes();
			for(int j = 0; j < parameters.length; j++)
			{
				builder.append(parameters[j].getCanonicalName());
				if(j < parameters.length - 1)
					builder.append(", ");
			}
			builder.append(')');
			
			Class [] exceptions = constructors[i].getExceptionTypes();
			if(exceptions.length != 0)
			{
				builder.append(" throws ");
				for(int k = 0; k < exceptions.length; k++)
				{
					builder.append(exceptions[k].getCanonicalName());
					if(k < exceptions.length -1)
						builder.append(", ");
				}
			}
			strings.add(builder.toString());
		}
		return strings;
	}
	
	public String findClassInfo(Class inspecting, Object instance, boolean recursive, int depth)
	{
		StringBuilder builder = new StringBuilder();
		
		Class superClass = inspecting.getSuperclass();
		String superClassName = superClass == null ? null : superClass.getCanonicalName();
		
		
		builder.append("\n" + prefix(depth) + "-----------------------------------------------------------\n" + prefix(depth) + "NEW CLASS:\n" + prefix(depth) + inspecting.getCanonicalName() + '\n');
		builder.append("\n" + prefix(depth) + "Immediate Superclass Name:\n" + prefix(depth) + superClassName + '\n');
		builder.append("\n" + prefix(depth) + "Interfaces Implemented:\n" + prefix(depth) + String.join("\n" + prefix(depth), findInterfaceInfo(inspecting, depth + 1)) + '\n');
		builder.append("\n" + prefix(depth) + "Methods:\n" + prefix(depth) + String.join("\n" + prefix(depth), findDeclaredMethodInfo(inspecting)) + '\n');
		builder.append("\n" + prefix(depth) + "Constructors:\n" + prefix(depth) + String.join("\n" + prefix(depth), findConstructorInfo(inspecting)) + '\n');
		builder.append("\n" + prefix(depth) + "Fields:\n" + prefix(depth) + String.join("\n" + prefix(depth), findDeclaredFieldInfo(inspecting, instance, recursive, depth + 1)) + '\n');
		builder.append("\n" + prefix(depth) + "-----------------------------------------------------------\n");
		return builder.toString();
	}
	
	public String prefix(int depth)
	{
		if(depth < 0)
			return "";
		
		char[] array = new char[depth];
	    Arrays.fill(array, '\t');
	    return new String(array);
	}
	
}
