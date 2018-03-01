package hu.mysolution;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author bozbalint
 *
 *         It is a java 8 code.
 *
 *         It doesn't have Junit tests as Junit is not part of the standard Java
 *         :-((
 *
 *         e and é ABC order is not as expected but the collator on my
 *         environment produce it like that. I don't feel that fix this problem
 *         is part of the challenge. I maybe wrong.
 */
public class IndexFile {

	private final static String INPUT_FILE = "text.txt";
	private final static String OUTPUT_FILE = "index.txt";

	public static void main(String[] args) {

		IndexFile process = new IndexFile();
		Map<String, MyIndex> dictionary = process.countAndSort(Paths.get(INPUT_FILE));
		process.writeFile(dictionary, Paths.get(OUTPUT_FILE));
	}

	/**
	 * @param inputfile
	 *            the name of the input file
	 * @return a map with all the words and the locations of the words
	 */
	private Map<String, MyIndex> countAndSort(Path inputfile) {

		Map<String, MyIndex> dictionary = new HashMap<String, MyIndex>();
		MyCounter i = new MyCounter();
		try (Stream<String> fileLines = Files.lines(inputfile)) {
			fileLines.sequential().map(s -> s.toLowerCase()).map(s -> s.split("\\s+")).forEach(s -> {
				for (String text : s) {
					if (dictionary.containsKey(text)) {
						dictionary.get(text).addIndex(i.getValue());
					} else {
						dictionary.put(text, new MyIndex());
					}
				}
				i.increase();
			});

		} catch (IOException e) {
			System.out.println("Failed to open file: " + INPUT_FILE);
		}

		return dictionary;
	}

	/**
	 * @param dictionary
	 *            is the map of the words and its line numbers
	 * @param outputFile
	 *            the name of the output file name
	 * 
	 */
	private void writeFile(Map<String, MyIndex> dictionary, Path outputFile) {

		try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
			dictionary.keySet().stream().sorted((c1, c2) -> {
				Collator huCollator = Collator.getInstance(Locale.forLanguageTag("HU"));
				huCollator.setStrength(Collator.SECONDARY);
				return huCollator.compare(c1, c2);
			}).forEach(s -> {

				try {
					writer.write(s + " " + dictionary.get(s).getValue());
					writer.newLine();
					writer.flush();
				} catch (IOException e) {
					System.out.println("Failed to write file: " + outputFile.getFileName());
				}
			});

		} catch (IOException e) {
			System.out.println("Failed to open file: " + outputFile.getFileName());
		}
	}

	/**
	 * @author bozbalint
	 *
	 *         It collects the line number of a words. The result is a String of
	 *         comma separated numbers.
	 *
	 */
	class MyIndex {
		private StringBuffer value = new StringBuffer("1");

		public String getValue() {
			return value.toString();
		}

		public void addIndex(int i) {
			this.value.append(", " + Integer.toString(i));
		}
	}

	/**
	 * @author bozbalint
	 * 
	 *         It counts the lines. It is a running index for a stream.
	 *
	 */
	class MyCounter {
		private int value = 1;

		public int getValue() {
			return this.value;
		}

		public void increase() {
			this.value += 1;
		}
	}
}
