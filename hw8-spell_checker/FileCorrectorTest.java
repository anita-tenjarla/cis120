import static org.junit.Assert.*;

import java.util.TreeSet;
import java.util.Set;
import java.io.*;



import org.junit.Test;

public class FileCorrectorTest {

  /**
   * This is a helper method that helps you make a set for comparison. To use:
   *
   * Set<String> blah = makeSet(new String[]{"firstString", "secondString", "nthString"});
   * assertEquals("!!!", blah, someFunction.call());
   * 
   * WARNING: make sure you don't accidently create a set containing a single string.
   *  i.e. this is a common source of bugs: 
   *     makeSet(new String[]{"firstString, secondString"})
   */
  private Set<String> makeSet(String[] strings) {
    Set<String> mySet = new TreeSet<String>();
    for (String s : strings) {
      mySet.add(s);
    }
    return mySet;
  }

  
  
  @Test public void testFileCorrectorNullReader() throws IOException, FileCorrector.FormatException {
    // Here's how to test expecting an exception...
    try {
      new FileCorrector(null);
      fail("Expected an IllegalArgumentException - cannot create FileCorrector with null.");
    } catch (IllegalArgumentException f) {    
      //Do nothing. It's supposed to throw an exception
    }
  }

  
  @Test public void testGetCorrection() throws IOException, FileCorrector.FormatException  {
    Corrector c = FileCorrector.make("smallMisspellings.txt");
    assertEquals("lyon -> lion", makeSet(new String[]{"lion"}), c.getCorrections("lyon"));
    TreeSet<String> set2 = new TreeSet<String>();
    assertEquals("TIGGER -> {Trigger,Tiger}", makeSet(new String[]{"Trigger","Tiger"}), c.getCorrections("TIGGER"));
  }


  
  @Test public void testInvalidFormat() throws IOException, FileCorrector.FormatException  {
	 try {
		  Corrector c = new FileCorrector(new StringReader("no comma in this puppy"));
	     fail("This is a bad format");
	 } catch (FileCorrector.FormatException e) {
	    // do nothing
	 }
  }

  // Do NOT add your own tests here. Put your tests in MyTest.java
}
