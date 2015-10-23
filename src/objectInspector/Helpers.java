package objectInspector;

import java.lang.reflect.Array;
import java.util.Arrays;

public abstract class Helpers {
	
	// Adds tabs as prefix (for formatting)
	public static String prefix(int depth)
	{
		if(depth < 0)
			return "";
		
		char[] array = new char[depth];
	    Arrays.fill(array, '\t');
	    return new String(array);
	}
	
	
	// Retrieves info from a metaArray.
	// NOTE: Assumes the object inputted IS an array
	public static String getInfoFromArray(Object obj)
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
}
