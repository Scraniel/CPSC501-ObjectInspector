package objectInspector;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.LinkedList;

public class Inspector implements ReflectiveInspector 
{
	LinkedList<Object> toInspect = new LinkedList<Object>();
	//LinkedList<Class> toInspect = new LinkedList<Class>();
	HashSet<String> inspected = new HashSet<String>();

	public static void main(String[] args)
	{
		(new Inspector()).inspect(new String("Hello"), false);
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
			inspected.add(inspecting.getCanonicalName());
			
			System.out.println(findClassInfo(inspecting, instance, recursive));		
		}
		
		
	}
	
	public String findDeclaredFieldInfo(Class inspecting, Object instance)
	{
		StringBuilder builder = new StringBuilder();
		Field [] fields = inspecting.getDeclaredFields();
		
		for(Field field : fields)
		{
			builder.append('\t');
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
					builder.append(field.get(instance));
					
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				builder.append('\n');
			}
		}
		
		return builder.toString();
		
	}
	
	public String findDeclaredMethodInfo(Class inspecting)
	{
		StringBuilder builder = new StringBuilder();
		Method [] methods = inspecting.getDeclaredMethods();
		
		for(Method method : methods)
		{
			builder.append('\n');
			builder.append('\t');
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
					builder.append(' ');
				}
			}
		}
		
		return builder.toString();
	}

	public String findInterfaceInfo(Class inspecting)
	{
		StringBuilder builder = new StringBuilder();
		Class [] interfaces = inspecting.getInterfaces();
		
		
		for(Class implementing : interfaces )
		{
			toInspect.add(implementing);
			builder.append('\t');
			builder.append(implementing.getName());
			builder.append('\n');
		}
		
		return builder.toString();
	}
	
	public String findClassInfo(Class inspecting, Object instance, boolean recursive)
	{
		StringBuilder builder = new StringBuilder();
		
		Class superClass = inspecting.getSuperclass();
		String superClassName = superClass == null ? null : superClass.getCanonicalName();
		
		
		builder.append("\n-----------------------------------------------------------\nNEW CLASS:\n\t" + inspecting.getCanonicalName());
		builder.append("\nSuperclass Name:\n\t" + superClassName);
		builder.append("\nInterfaces Implemented:\n" + findInterfaceInfo(inspecting));
		builder.append("\nMethods:" + findDeclaredMethodInfo(inspecting));
		builder.append("\nFields:\n" + findDeclaredFieldInfo(inspecting, instance));
		builder.append("\n-----------------------------------------------------------\n");
		return builder.toString();
	}
	
}
