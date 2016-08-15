import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;



import org.junit.Test;


public class SpellCheckerTest {

  /** spellCheckFiles Runs the spell checker on some test input.  See
   * the description of the inputs below.
   *
   * @param fdict The filename of the dictionary
   * @param dictSize If the int dictSize is -1, it is ignored.
   *    Otherwise we check the size of the dictionary after creating it,
   *    to make sure it was parsed correctly and the tests will work.
   * @param fcorr The filename of the corrections to use, or null
        if the swap corrector should be used.
   * @param fdoc The filename of the document to check
   * @param fout The filename where the output should be written
   * @param finput The filename where the user input should be read from
   */
  public static void spellCheckFiles(String fdict, int dictSize, String fcorr,
                               String fdoc, String fout, String finput) 
		throws IOException, FileCorrector.FormatException
  {
    Dictionary dict = Dictionary.make(fdict);
    
    Corrector corr = null;
    if (fcorr == null) {
      corr = new SwapCorrector(dict);
    } else {
      corr = FileCorrector.make(fcorr);
    }

    // Make sure the dictionary has expected number of entries. Otherwise, when
    // spellchecking, it might miss words and cause the simulated user input
    // to fall out of sync.
    if(dictSize >= 0) 
      assertEquals("Dictionary size = " + dictSize, dictSize,
                   dict.getNumWords());
   
    FileInputStream input = new FileInputStream(finput);
    Reader in = new BufferedReader(new FileReader(fdoc));
    Writer out = new BufferedWriter(new FileWriter(fout));

    SpellChecker sc = new SpellChecker(corr,dict);
    
    sc.checkDocument(in, input, out);
    in.close();
    input.close();
    out.flush();
    out.close();
  }


  
  @Test(timeout=500) public void testCheckFoxGood() throws IOException, FileCorrector.FormatException {
    spellCheckFiles("theFoxDictionary.txt",7,"theFoxMisspellings.txt",
                    "theFox.txt","foxout.txt","theFox_goodinput.txt");
    compareDocs("foxout.txt", "theFox_expected_output.txt");
  }

  
  @Test(timeout=500) public void testCheckMeanInput() throws IOException, FileCorrector.FormatException {
    spellCheckFiles("theFoxDictionary.txt",7,"theFoxMisspellings.txt",
                    "theFox.txt","foxout.txt","theFox_meaninput.txt");
    compareDocs("foxout.txt", "theFox_expected_output.txt");
  }

  
  @Test(timeout=500) public void testCheckGettysburgSwap() throws IOException, FileCorrector.FormatException {
    // Use the SwapCorrector instead!
    spellCheckFiles("dictionary.txt",60822,null,
                    "Gettysburg.txt","Gettysburg-out.txt",
                    "Gettysburg_input.txt");
    compareDocs("Gettysburg-out.txt", "Gettysburg_expected_output.txt");
  }

  

  /**
   * This is a helper method that compares two documents.
   */
  public static void compareDocs(String out, String expected) 
    throws IOException, FileNotFoundException 
  {
    BufferedReader f1 = new BufferedReader(new FileReader(out));
    BufferedReader f2 = new BufferedReader(new FileReader(expected));

    try{
      String line1 = f1.readLine();
      String line2 = f2.readLine();
      while(line1 != null && line2 != null){
        assertEquals("Output file did not match expected output.", line2, line1);
        line1 = f1.readLine();
        line2 = f2.readLine();
      }
      if(line1 != null) {
        fail("Expected end of file, but found extra lines in the output.");
      } else if(line2 != null) {
        fail("Expected more lines, but found end of file in the output. ");
      }
    }
    finally{
      f1.close();
      f2.close();
      
    }
  }

  // Do NOT put your test cases here. Add them to MyTest.java
}
