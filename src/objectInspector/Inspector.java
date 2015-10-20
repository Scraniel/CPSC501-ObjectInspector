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
		
		System.out.println("Class name:\n" + toInspect.getName());
		System.out.println("Superclass Name:\n" + superClass.getName());
		
		System.out.println("Fields:\n" + findDeclaredFields(toInspect));
	}
	
	public String findDeclaredFields(Class toInspect)
	{
		StringBuilder builder = new StringBuilder();
		Field [] fields = toInspect.getDeclaredFields();
		
		for(Field field : fields)
		{
			builder.append(field.getName());
			builder.append('\n');
		}
		
		return builder.toString();
		
	}

}
