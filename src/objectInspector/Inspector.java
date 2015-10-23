package objectInspector;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import fromD2l.ClassD;

public class Inspector implements ReflectiveInspector 
{
	LinkedList<Object> toInspect = new LinkedList<Object>();
	//LinkedList<Class> toInspect = new LinkedList<Class>();
	HashSet<Object> inspected = new HashSet<Object>();

	public static void main(String[] args)
	{
		(new Inspector()).inspect(new ClassD(), true);
	}
	
	@Override
	public void inspect(Object obj, boolean recursive) 
	{
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
			
			System.out.println(findClassInfo(inspecting, instance, recursive));		
		}
		
		
	}
	
	public ArrayList<String> findDeclaredFieldInfo(Class inspecting, Object instance, boolean recursive)
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
							if(!inspected.contains(obj))
							{
								toInspect.add(obj);
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

	public ArrayList<String> findInterfaceInfo(Class inspecting)
	{
		StringBuilder builder = new StringBuilder();
		Class [] interfaces = inspecting.getInterfaces();
		ArrayList<String> strings = new ArrayList<String>();
		
		for(Class implementing : interfaces )
		{
			builder.setLength(0);
			builder.append("***********************************************************\nInspecting Interface of " + inspecting.getCanonicalName() +": ");		
			builder.append(implementing.getCanonicalName());
			builder.append("\n***********************************************************\n");
			if(inspected.contains(implementing))
			{
				builder.append("This class was already inspected!\n");
			}
			else
			{
				builder.append(findClassInfo(implementing, null, false));
			}
			strings.add(builder.toString());
		}
		
		return strings;
	}
	
	public String findClassInfo(Class inspecting, Object instance, boolean recursive)
	{
		StringBuilder builder = new StringBuilder();
		
		Class superClass = inspecting.getSuperclass();
		String superClassName = superClass == null ? null : superClass.getCanonicalName();
		
		
		builder.append("\n-----------------------------------------------------------\nNEW CLASS:\n\t" + inspecting.getCanonicalName());
		builder.append("\nSuperclass Name:\n\t" + superClassName);
		builder.append("\nInterfaces Implemented:\n" + String.join("\n", findInterfaceInfo(inspecting)));
		builder.append("\nMethods:" + format(findDeclaredMethodInfo(inspecting)));
		builder.append("\nFields:" + format(findDeclaredFieldInfo(inspecting, instance, recursive)));
		builder.append("\n-----------------------------------------------------------\n");
		return builder.toString();
	}
	
	// Formats a list of strings into a nicely tabbed string.
	public String format(ArrayList<String> list)
	{
		return "\n\t" + String.join("\n\t", list);
	}
	
}
