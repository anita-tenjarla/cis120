import java.util.Arrays;

/*
 * This file is adapted by UPenn CIS 120 course staff from code by
 * Richard Wicentowski and Tia Newhall (2005)
 * 
 * You need to complete this file FIRST before moving on to the 
 * rest of the project.
 */

/** 
 * A point of color. 
 *
 * Pixels are represented as three color components (Red, Green, and Blue)
 * which should all be ints in the range of 0-255. Lower values mean less
 * color; higher means more. For example, new Pixel(255,255,255) represents
 * white, new Pixel(0,0,0) is black, and new Pixel(0,255,0) represents green.
 *
 * This data structure should be immutable. Once pixels have been created
 * they cannot be modified.
 */
public class Pixel implements Comparable<Pixel> {
   
    /** Some constant colors */
    public static final Pixel BLACK = new Pixel(0,0,0);
    public static final Pixel BLUE = new Pixel(0,0,255);
    public static final Pixel RED = new Pixel(255,0,0);
    public static final Pixel GREEN = new Pixel(0,255,0);
    public static final Pixel WHITE = new Pixel(255,255,255);
   
    public int red; 
    public int green;
    public int blue; 
    
    /** creates a new color pixel with the specified color intensities
     * 
     *  If the supplied arguments are not between 0 and 255 it 
     *  clips them:  
     *     - negative components are set to 0
     *     - components > 255 are set to 255
     *  */
    Pixel(int r, int g, int b) {
    	if (r < 0) {r = 0;}; 
    	if (r > 255) {r = 255;} 
    	if (g < 0) {g = 0;}
    	if (g > 255) {g = 255;} 
    	if (b < 0) {b = 0;} 
    	if (b > 255) {b = 255;}
    	this.red = r; 
    	this.green = g; 
    	this.blue = b;
    }

    /** creates a new pixel with the component intensities specified as
     *  an array  c[0] = red  c[1] = green c[2] = blue 
     *  
     *  If c has fewer than 3 entries, the missing components are set to
     *  0.  If c has more than 3 entries, the extra entries are ignored.
     *  */ 
    Pixel(int[] c) {
    	for (int i = 0; i < 3; i++) {
    		if (c[i] < 0) { c[i] = 0; } 
    		if (c[i] > 255) { c[i] = 255; }  
        }
      this.red = c[0]; 
      this.green = c[1]; 
      this.blue = c[2];       
    }

    /** gets the red component */ 
    public int getRed() { 
    	return red;        
    }

    /** gets the green component */ 
    public int getGreen() { 
    	return green;    
    }

    /** gets the blue component */ 
    public int getBlue() { 
    	return blue;
    }

    /** get the components as an array of three integers */ 
    public int[] getComponents() {
    	int [] c = {red, green, blue};  
    	return c;        
    }

    
    /**
     * Determines how similar this pixel is to another by summing the
     * (absolute values) of the differences between corresponding
     * components of the two pixels.
     * 
     *  Hint: use Math.abs
     * 
     * @param px The other pixel with which to compare
     * @return The sum of the differences in each of the color components
     */
    public int distance(Pixel px) {
    	int sum = 0; 
    	int [] p1 = getComponents(); 
    	int [] p2 = px.getComponents(); 
    	for (int i = 0; i < 3; i++) {
    		sum += Math.abs (p1[i] - p2[i]); 
    	}
    	return sum; 
    }

    /** 
     * Returns a String representation of this pixel. The string should 
     * comma separate the rgb values and surround them with parenthesis
     *
     * Example: RED.toString()   is   "(255, 0, 0)"
     *
     * Note: This function will allow you to print Pixels in a readable format.
     * This can be very helpful while debugging, and we highly encourage you to
     * use print statements to debug this assignment.
     * 
     * @return A string representation of this pixel
     * 
     */ 
    public String toString() {
    	return "(" + red + ", " + green + ", " + blue + ")";
    }

    /** 
     * Compares the rgb values of the current Pixel with another to check if
     * they are the same (and thus whether the two Pixels equal each other)
     *
     * @param other The Pixel being compared to the current Pixel
     */
    public boolean equals(Pixel other) {
    	int [] p1 = getComponents(); 
    	int [] p2 = other.getComponents(); 
    	if (p1[0] == p2[0] && p1[1] == p2[1] && p1[2] == p2[2])
    		{return true;} 
    	return false; 
    }  
    
    
    /* ---------------- Don't modify below this line ------------------*/ 

  
    /** 
     * Checks whether this pixel has the same components as the given Object.
     * If the object is not a Pixel, then this returns false.
     */ 
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof Pixel) {  
            Pixel o = (Pixel) other;

            return equals(o); 
        }
        return false;
    }

    public int hashCode() {
        int h = 0;
        int[] components = getComponents();

        for (int k = 0; k < components.length; k++) {
            h += k*255 + components[k];
        }
        return h;
    }

    public int compareTo(Pixel o) {
        int rc = getRed() - o.getRed();
        int gc = getGreen() - o.getGreen();
        int bc = getBlue() - o.getBlue();
        
        if (rc != 0) {
            return rc;
        } else if (gc != 0) {
            return gc;
        } else {
            return bc;
        }
    }
}
