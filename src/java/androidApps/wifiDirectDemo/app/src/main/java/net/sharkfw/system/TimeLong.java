package net.sharkfw.system;

/**
 * fail safe number parsing 
 * 
 */
public class TimeLong {
    public final static String onlyFirstNumbersRegex = "^[^0-9]*([0-9]+)[^0-9]*.*$";
    
    /**
     * @param value	 string representation of unix time
     * @return  unix time or -1 in case of error 
     */
	public static long parse(String value) {
		try {
			return Long.parseLong(value.replaceAll(onlyFirstNumbersRegex, "$1"));
		} catch(Exception e) {			
		}	
		return -1;
	}
}
