package de.tblsoft.solr.util;

import java.util.BitSet;

/**
 * Invalid XML characters.
 */
public class XMLCharacters {

	/**
	 * Version for XML 1.1.
	 */
	static final String XML11 = "1.1";

	/**
	 * The invalid characters in XML 1.0.
	 */
	static final BitSet XML10_INVALID;
	static {
		// See http://en.wikipedia.org/wiki/XML#Valid_characters

		BitSet invalid = new BitSet();
		for (char c = 0; c < '\u0020'; ++c) {
			if (c != '\u0009' && c != '\n' && c != '\r') {
				invalid.set(c);
			}
		}
		for (char c = '\uD800'; c < '\uE000'; ++c) {
			invalid.set(c);
		}
		invalid.set('\uFFFE');
		invalid.set('\uFFFF');

		XML10_INVALID = invalid;
	}

	/**
	 * The invalid characters in XML 1.1.
	 */
	static final BitSet XML11_INVALID;
	static {
		// See http://en.wikipedia.org/wiki/XML#Valid_characters

		BitSet invalid = new BitSet();
		invalid.set('\u0000');
		// \u0001 to \u001F are valid in XML 1.1
		for (char c = '\uD800'; c < '\uE000'; ++c) {
			invalid.set(c);
		}
		invalid.set('\uFFFE');
		invalid.set('\uFFFF');

		XML11_INVALID = invalid;
	}

}
