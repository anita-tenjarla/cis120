import java.util.TreeSet;
import java.util.Set;

/**
 * Skeleton implementation for classes capable of providing spelling corrections.
 * <p>
 * The matchCase method is code that is shared among all Correctors.
 * <p>
 * Concrete subclasses of this abstract class should implement the getCorrections
 * method (which will usually call matchCase).
 */
public abstract class Corrector {

  /**
   * Returns a new set that contains the same elements as the input set,
   * except the case (all lowercase, or uppercase first letter) matches that of
   * the input string.
   *
   * @param incorrectWord the word whose case should be matched
   * @param corrections the set whose capitalization should be fixed
   * @return set of corrections with capitalization appropriately modified
   */
  public Set<String> matchCase(String incorrectWord, Set<String> corrections) {
    if (incorrectWord == null || corrections == null) {
      throw new IllegalArgumentException("null input given");
    }
    Set<String> revisedSet = new TreeSet<String>();
    boolean capitalizeFirst = Character.isUpperCase(incorrectWord.charAt(0));
    for (String s : corrections) {
      if (capitalizeFirst) {
        String ucfirst =
            s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        revisedSet.add(ucfirst);
      } else {
        revisedSet.add(s.toLowerCase());
      }
    }
    return revisedSet;
  }

  /**
   * Returns a set of proposed corrections for an incorrectly spelled word. The
   * corrections should match the case of the input; the matchCase method is
   * helpful here.
   * <p>
   * Any input that is *not* a valid word (i.e. only composed of letters and/or apostrophes) 
   * should throw an IllegalArgumentException. 
   *
   * @param wrong the misspelled word
   * @return a (potentially empty) set of proposed corrections
   * @throws IllegalArgumentException if the input is not a valid word 
   * (i.e. only composed of letters and/or apostrophes) 
   */
  public abstract Set<String> getCorrections(String wrong);
}
