import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;
import java.util.TreeSet;

import org.junit.*;

/** Put your OWN test cases in this file, for all classes in the assignment. */
public class MyTests {

	//TokenScanner Tests
	@Test public void testEmpty() throws IOException {
		TokenScanner ts = new TokenScanner(new StringReader(""));
		assertFalse(ts.hasNext()); 
	}

	@Test public void testSingleton() throws IOException {
		TokenScanner ts = new TokenScanner(new StringReader("one"));
		assertTrue(ts.hasNext()); 
		String word = ts.next(); 
		assertTrue(word.equals("one")); 
		assertFalse(ts.hasNext()); 
	}

	@Test public void testSingleNonword() throws IOException {
		assertFalse(TokenScanner.isWord("a23")); 
	}

	@Test public void testOtherChars() throws IOException {
		assertTrue(TokenScanner.isWord("isn't")); 
	}

	@Test public void testEndNotWord() throws IOException {
		Reader in = new StringReader("Doesn't this suck?"); 
		TokenScanner d = new TokenScanner(in);
		try {
			assertTrue("has next", d.hasNext());
			assertEquals("Doesn't", d.next());

			assertTrue("has next", d.hasNext());
			assertEquals(" ", d.next());

			assertTrue("has next", d.hasNext());
			assertEquals("this", d.next());

			assertTrue("has next", d.hasNext());
			assertEquals(" ", d.next());

			assertTrue("has next", d.hasNext());
			assertEquals("suck", d.next());

			assertTrue("has next", d.hasNext());
			assertFalse(TokenScanner.isWord("?")); 
			assertEquals("?", d.next());

			assertFalse("reached end of stream", d.hasNext());
		} finally {
			in.close();
		}
	}

	@Test public void testEndWord() throws IOException {
		Reader in = new StringReader("So what's up"); 
		TokenScanner d = new TokenScanner(in);
		try {
			assertTrue("has next", d.hasNext());
			assertEquals("So", d.next());

			assertTrue("has next", d.hasNext());
			assertEquals(" ", d.next());

			assertTrue("has next", d.hasNext());
			assertEquals("what's", d.next());

			assertTrue("has next", d.hasNext());
			assertEquals(" ", d.next());

			assertTrue("has next", d.hasNext());
			assertEquals("up", d.next());

			assertFalse("reached end of stream", d.hasNext());
		} finally {
			in.close();
		}
	}

	//Dictionary tests
	@Test(timeout=500) public void testWordInNotInDictionary() throws IOException {
		Dictionary d = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
		assertTrue("'carrot' is in file", d.isWord("carrot"));
		assertFalse("'tostitos' is not in file", d.isWord("tostitos"));
		assertFalse("empty string is not a word", d.isWord("")); 
	}

	@Test(timeout=500) public void testNumWords() throws IOException {
		Dictionary d = new Dictionary(new TokenScanner(new FileReader("smallDictionary.txt")));
		assertEquals(32, d.getNumWords()); 
	}

	@Test(timeout=500) public void testUppercase() throws IOException {
		Dictionary d = new Dictionary(new TokenScanner(new FileReader("testText.txt")));
		assertTrue("APPLE same as apple", d.isWord("APPLE"));
		assertTrue("mixed case eLePHant", d.isWord("elephant"));
		assertTrue("banana with spaces around it is true", d.isWord("banana"));
		assertEquals(5, d.getNumWords()); 
	}

	//FileCorrector tests

	private Set<String> makeSet(String[] strings) {
		Set<String> mySet = new TreeSet<String>();
		for (String s : strings) {
			mySet.add(s);
		}
		return mySet;
	}

	@Test (timeout=500)
	public void testBadSpacing() throws IOException, FileCorrector.FormatException  {
		Corrector c = FileCorrector.make("myfctests.txt");
		assertEquals("lyon -> lion", makeSet(new String[]{"lion"}), c.getCorrections("lyon"));
	}

	@Test (timeout=500)
	public void testNoCorrections() throws IOException, FileCorrector.FormatException  {
		Corrector c = FileCorrector.make("myfctests.txt");
		assertEquals("lion -> ", makeSet(new String[]{}), c.getCorrections("lion"));
	}

	@Test (timeout=500)
	public void testMultipleCorrections() throws IOException, FileCorrector.FormatException  {
		Corrector c = FileCorrector.make("myfctests.txt");
		assertEquals("flack -> flick, flock", makeSet(new String[]{"flick", "flock"}), 
				c.getCorrections("flack"));
	}

	@Test (timeout=500)
	public void testMixedCase() throws IOException, FileCorrector.FormatException  {
		Corrector c = FileCorrector.make("myfctests.txt");
		assertEquals("APLE -> Apple", makeSet(new String[]{"Apple"}), 
				c.getCorrections("APLE"));
		assertEquals("aple -> apple", makeSet(new String[]{"apple"}), 
				c.getCorrections("aple"));
		assertEquals("werd -> weird", makeSet(new String[]{"weird"}), 
				c.getCorrections("werd"));
		assertEquals("inspite -> in spite", makeSet(new String[]{"in spite"}), 
				c.getCorrections("inspite"));
	}

	//SwapCorrector tests

	@Test public void testSwapNull() throws IOException {
		try {
			new SwapCorrector(null);
			fail("Expected an IllegalArgumentException - "
					+ "cannot create SwapCorrector with null.");
		} catch (IllegalArgumentException f) {    
		}
	}

	@Test public void testWordIsCorrect() throws IOException {
		Reader reader = new FileReader("smallDictionary.txt");
		try {
			Dictionary d = new Dictionary(new TokenScanner(reader));
			SwapCorrector swap = new SwapCorrector(d);
			assertEquals("carrot", makeSet(new String[]{}), swap.getCorrections("carrot"));
		} finally {
			reader.close();
		}
	}

	@Test public void testNonsenseNoCorrections() throws IOException {
		Reader reader = new FileReader("smallDictionary.txt");
		try {
			Dictionary d = new Dictionary(new TokenScanner(reader));
			SwapCorrector swap = new SwapCorrector(d);
			assertEquals("nonsense word", makeSet(new String[]{}), 
					swap.getCorrections("alsdfjas"));
		} finally {
			reader.close();
		}
	}


	@Test public void testSwapAllCombinations() throws IOException {
		Reader reader = new FileReader("swapdictionary.txt");
		try {
			Dictionary d = new Dictionary(new TokenScanner(reader));
			SwapCorrector swap = new SwapCorrector(d);
			assertEquals("hand -> {ahnd, hnad, hadn}", 
					makeSet(new String[]{"ahnd", "hnad", "hadn"}), 
					swap.getCorrections("hand"));
		} finally {
			reader.close();
		}
	}

	@Test public void testSwapMixedCases() throws IOException {
		Reader reader = new FileReader("smallDictionary.txt");
		try {
			Dictionary d = new Dictionary(new TokenScanner(reader));
			SwapCorrector swap = new SwapCorrector(d);
			assertEquals("CyA -> {Cay}", makeSet(new String[]{"cay"}), swap.getCorrections("CyA"));
			assertEquals("oYurs -> {yours}", makeSet(new String[]{"yours"}), swap.getCorrections("oYurs"));
			assertEquals("NAY -> {any}", makeSet(new String[]{"any"}), swap.getCorrections("NAY"));
		} finally {
			reader.close();
		}
	}



}