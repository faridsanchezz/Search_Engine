package model;

import java.io.Serializable;
import java.util.*;

public class Word {
	private String text;
	private Set<WordOccurrence> occurrences = new HashSet<>();

	public Word(String text, WordOccurrence occurrence) {
		this.text = text;
		this.occurrences.add(occurrence);
	}

	public Word(String text, Set<WordOccurrence> occurrences) {
		this.text = text;
		this.occurrences.addAll(occurrences);
	}

	public String getText() {
		return text;
	}

	public Set<WordOccurrence> getOccurrences() {
		return occurrences;
	}

	public void addOccurrence(WordOccurrence newOccurrence) {
		occurrences.add(newOccurrence);
	}
	public void addOccurrence(Set<WordOccurrence> newOccurrences) {
		occurrences.addAll(newOccurrences);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Word: " + text + "\nOccurrences:\n");
		for (WordOccurrence occurrence : occurrences) {
			sb.append(occurrence.toString()).append("\n");
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Word word = (Word) obj;
		return Objects.equals(text, word.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(text);
	}

	public static class WordOccurrence implements Serializable {
		private final String bookID;
		private final Set<Integer> lines = new HashSet<>();

		public WordOccurrence(String bookID, Integer line) {
			this.bookID = bookID;
			this.lines.add(line);
		}

		public WordOccurrence(String bookID, Set<Integer> lines) {
			this.bookID = bookID;
			this.lines.addAll(lines);
		}

		public String getBookID() {
			return bookID;
		}

		public Set<Integer> getLineOccurrences() {
			return this.lines;
		}

		public void addLineOccurrence(Integer line) {
			this.lines.add(line);
		}
		public void addLineOccurrence(Set<Integer> lines) {
			this.lines.addAll(lines);
		}

		@Override
		public String toString() {
			return "Book ID: " + bookID + "\n" +
					"Lines: " + lines + "\n" +
					"Frequency: " + lines.size() + "\n";
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || getClass() != obj.getClass()) return false;
			WordOccurrence that = (WordOccurrence) obj;
			return Objects.equals(bookID, that.bookID);
		}

		@Override
		public int hashCode() {
			return Objects.hash(bookID);
		}
	}
}
