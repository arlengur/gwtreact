/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

/**
 * Russian transliteration that implements ISO 9 System B (GOST 7.79-2000)
 * 
 * @author sviyazov.a
 * 
 */
public class RussianTransliterator {

	private static final String[] translitTable = new String[65536];
	static {
		translitTable['А'] = "A";
		translitTable['Б'] = "B";
		translitTable['В'] = "V";
		translitTable['Г'] = "G";
		translitTable['Д'] = "D";
		translitTable['Е'] = "E";
		translitTable['Ё'] = "YO";
		translitTable['Ж'] = "ZH";
		translitTable['З'] = "Z";
		translitTable['И'] = "I";
		translitTable['Й'] = "J";
		translitTable['К'] = "K";
		translitTable['Л'] = "L";
		translitTable['М'] = "M";
		translitTable['Н'] = "N";
		translitTable['О'] = "O";
		translitTable['П'] = "P";
		translitTable['Р'] = "R";
		translitTable['С'] = "S";
		translitTable['Т'] = "T";
		translitTable['У'] = "U";
		translitTable['Ф'] = "F";
		translitTable['Х'] = "KH";
		translitTable['Ц'] = "C";
		translitTable['Ч'] = "CH";
		translitTable['Ш'] = "SH";
		translitTable['Щ'] = "SHH";
		translitTable['Ъ'] = "''";
		translitTable['Ы'] = "Y";
		translitTable['Ь'] = "'";
		translitTable['Э'] = "E'";
		translitTable['Ю'] = "YU";
		translitTable['Я'] = "YA";

		for (int i = 0; i < translitTable.length; i++) {
			final char idx = (char) i;
			final char lower = new String(new char[]{idx}).toLowerCase()
					.charAt(0);
			if (translitTable[i] != null) {
				translitTable[lower] = translitTable[i].toLowerCase();
			}
		}
	}

	/**
	 * @param text
	 * @return new string with all Russian letters transliterated using ISO 9
	 *         System B.
	 */
	public static String transliterate(final String text) {
		final char charArray[] = text.toCharArray();
		final StringBuffer sb = new StringBuffer(text.length());
		for (int i = 0; i < charArray.length; i++) {
			final String replace = translitTable[charArray[i]];
			if (replace == null) {
				sb.append(charArray[i]);
			} else {
				sb.append(replace);
			}
		}
		return sb.toString();
	}
}
