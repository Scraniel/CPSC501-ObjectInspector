package objectInspector;

import java.lang.reflect.Field;

public class Inspector implements ReflectiveInspector 
{

	public static void main(String[] args)
	{
		(new Inspector()).inspect(new String(), false);
	}
	
	@Override
	public void inspect(Object obj, boolean recursive) 
	{
		Class toInspect = obj.getClass();
		Class superClass = toInspect.getSuperclass();
		
		System.out.println("Class name:\n\t" + toInspect.getName());
		System.out.println("Superclass Name:\n\t" + findSuperclassInfo(superClass));
		System.out.print("Interfaces Implemented:\n" + findInterfaceInfo(toInspect));
		
		
		System.out.println("Fields:\n" + findDeclaredFieldInfo(toInspect));
	}
	
	public String findDeclaredFieldInfo(Class toInspect)
	{
		StringBuilder builder = new StringBuilder();
		Field [] fields = toInspect.getDeclaredFields();
		
		for(Field field : fields)
		{
			builder.append('\t');
			builder.append(field.getName());
			builder.append('\n');
		}
		
		return builder.toString();
		
	}
	
	// Right now actually takes in the superclass as a parameter, consider
	// changing to accept the subclass and getting the info that way. May 
	// not need this class, depending on how the rest of the design goes.
	//
	// Potential solution: Just have an 'getClassInfo' method and call it
	// when necessary for the class itself, it's interfaces, superclasses, etc.
	public String findSuperclassInfo(Class toInspect)
	{
		return toInspect.getName();
	}

	public String findInterfaceInfo(Class toInspect)
	{
		StringBuilder builder = new StringBuilder();
		Class [] interfaces = toInspect.getInterfaces();
		
		for(Class implementing : interfaces )
		{
			builder.append('\t');
			builder.append(implementing.getName());
			builder.append('\n');
		}
		
		return builder.toString();
	}
	
}
