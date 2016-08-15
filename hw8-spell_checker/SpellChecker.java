import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * A SpellChecker uses a Dictionary, a Corrector, and I/O to interactively
 * spell check an input stream.
 * It writes the corrected output to the specified output stream.
 * <p>
 * Note:
 * <ul>
 * <li> The provided partial implementation includes some I/O methods useful
 * for getting user input from a Scanner.
 * <li> All user prompts and messages should be output on System.out
 * </ul>
 * <p>
 * The SpellChecker object is used by SpellCheckerRunner; see the provided
 * code there.
 * @see SpellCheckerRunner
 */
public class SpellChecker {
	private Corrector corr;
	private Dictionary dict;

	/**
	 * Constructs a SpellChecker
	 * 
	 * @param c a Corrector
	 * @param d a Dictionary
	 */
	public SpellChecker(Corrector c, Dictionary d) {
		corr = c;
		dict = d;
	}

	/**
	 * Returns the next integer from the given scanner in the range [min, max].
	 * Will re-prompt the user until a valid integer is provided.
	 *
	 * @param min
	 * @param max
	 * @param sc
	 */
	private int getNextInt(int min, int max, Scanner sc) {
		while (true) {
			try {
				int choice = Integer.parseInt(sc.next());
				if (choice >= min && choice <= max) {
					return choice;
				}
			} catch (NumberFormatException ex) {
				// Was not a number. Ignore and prompt again.
			}
			System.out.println("Invalid input. Please try again!");
		}
	}

	/**
	 * Returns the next string input from the Scanner.
	 *
	 * @param sc
	 */
	private String getNextString(Scanner sc) {
		return sc.next();
	}



	/**
	 * checkDocument interactively spell checks a given document.  
	 * Internally, it should use a TokenScanner to parse the document.  
	 * Word tokens that are not in the dictionary should be
	 * corrected; non-word tokens and words that are in the dictionary should 
	 * be output verbatim.
	 * <p>
	 * You may assume all of the inputs to this method are non-null.
	 *
	 * @param in the source document to spell check
	 * @param input an InputStream from which user input is obtained
	 * @param out the target document on which the corrected output is written
	 * @throws IOException if error while reading
	 */
	public void checkDocument(Reader in, InputStream input, Writer out) throws IOException {

		TokenScanner ts = new TokenScanner(in);
		Scanner scanner = new Scanner(input);
		String word = null;

		while (ts.hasNext()){
			word = ts.next();

			if (dict.isWord(word) || (!TokenScanner.isWord(word))) out.write(word);
			else{
				List<String> suggestions = new LinkedList<String>(corr.getCorrections(word));
				Collections.sort(suggestions);

				System.out.println("The word \"" + word + "\" is not in the dictionary."
						+ " Please enter the \n" + "number corresponding with the "
						+ "appropriate action: "); 
				System.out.println("\n0: Ignore and continue");
				System.out.println("\n1: Replace with another word"); 

				for (int i = 0; i < suggestions.size(); i++){
					System.out.println(i + 2 + ": Replace with \"" + suggestions.get(i)
					+ "\"");
				}
				int chosen = getNextInt(0, suggestions.size() + 1, scanner);
				if (chosen == 0){
					out.write(word);
				} else if (chosen == 1){
					out.write(getNextString(scanner));
				} else{
					out.write(suggestions.get(chosen - 2));
				}
			}
		}
	}
}

