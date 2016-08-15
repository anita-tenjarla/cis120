import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * 
 * The main entry point for the SpellChecker program.  
 * <p>
 * Can be used from the command line as:
 * <p>
 * <code>java SpellCheckerRunner &ltin&gt &ltout&gt &ltdictionary&gt &ltcorrector&gt</code>
 * <p>
 * <ul>
 * <li> &ltin&gt - the input file
 * <li> &ltout&gt - the output file
 * <li> &ltdictionary&gt - the dictionary
 * <li> &ltcorrector&gt -  one of: SWAP (for a SwapCorrector), 
 * LEV (for a Levenshtein Corrector), or a filename (for a FileCorrector)
 * </ul>
 * 
 * Note: if you choose to do the challenge problem for LEV correctors,
 * uncomment the code in <code>makeCorrector</code> to enable the LEV option.
 */
public class SpellCheckerRunner {
  /**
   * Creates the right kind of Corrector, based on the command-line switch.
   * If type is LEV, creates a Levenshtein corrector.
   * If type is SWAP, creates a SwapCorrector.
   * Else, it creates a FileCorrector using the input as the path.
   *
   * @param type
   * @param dict
   * @throws IOException
   * @throws FileCorrector.FormatException
   */
  private static Corrector makeCorrector(String type, Dictionary dict)
      throws IOException, FileCorrector.FormatException {
    if (type.equals("SWAP")) {
      return new SwapCorrector(dict);
    }

    // If you choose to do the challenge problem, you can uncomment this!
    //    if (type.equals("LEV")) {
    //      return new Levenshtein(dict);
    //    }

    return FileCorrector.make(type);
  }

  /**
   * This main program creates appropriate Reader and Writer objects 
   * for doing File IO, creates the appropriate Corrector based on the 
   * command-line arguments, and then uses a SpellChecker to 
   * interactively check the input file.  
   * 
   * @param args - array of command line arguments that specify 
   * the input and output files, the dictionary, and the type of 
   * Corrector to use 
   */
  public static void main(String[] args) {
    if (args.length != 4) {
      System.out.println("usage: java SpellCheckRunner <in> <out> <dictionary> <corrector>");
      System.out.println("<corrector> is either SWAP, LEV, or the path to a corrections file.");
      return;
    }
    try {
      Reader in = new BufferedReader(new FileReader(args[0]));
      Writer out = new BufferedWriter(new FileWriter(args[1]));
      Dictionary dict = Dictionary.make(args[2]);
      SpellChecker sp = new SpellChecker(makeCorrector(args[3], dict), dict);
      sp.checkDocument(in, System.in, out);
      in.close();
      out.flush();
      out.close();
    } catch (IOException e) {
      System.out.println("error while checking document: " + e.getMessage());
    } catch (FileCorrector.FormatException e) {
      System.out.println("format error while checking document: " + e.getMessage());
    }
  }
}
