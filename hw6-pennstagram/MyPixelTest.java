import org.junit.Test;
import static org.junit.Assert.*;


/** 
 *  Use this file to test your implementation of Pixel.
 * 
 *  We will manually grade this file to give you feedback
 *  about the completeness of your test cases.
 */

public class MyPixelTest {

	@Test
    public void createPixelTest() {
        Pixel p = new Pixel(1, 256, 3);
        
        assertEquals(1, p.getRed());
        assertEquals(255, p.getGreen());
        assertEquals(3, p.getBlue());
    }
    
	@Test
    public void getComponentsTest() {
    	Pixel p = new Pixel(1, 256, 3);
        int [] c = {1, 255, 3}; 
        assertArrayEquals(c, p.getComponents()); 
    }
    
	@Test
    public void distanceTest() {
    	Pixel o = new Pixel(3, 250, 12); 
        Pixel q = new Pixel(5, 200, 15);
    	assertEquals(55, o.distance(q)); 
    }
    
	@Test
    public void toStringTest() {
    	Pixel p = new Pixel(1, 256, 3);
    	assertEquals("(1, 255, 3)", p.toString()); 
    }
    
	@Test
    public void equalsTests() {
    	Pixel p = new Pixel(1, 256, 3);
        Pixel o = new Pixel(3, 250, 12); 
        Pixel q = new Pixel(5, 200, 15);
        Pixel r = new Pixel(3, 250, 12); 
        
    	assertFalse(p.equals(q)); 
        assertTrue(o.equals(r)); 
    }   

}
