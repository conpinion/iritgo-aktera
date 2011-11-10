/**
 * This file is part of the Iritgo/Aktera Framework.
 *
 * Copyright (C) 2005-2011 Iritgo Technologies.
 * Copyright (C) 2003-2005 BueroByte GbR.
 *
 * Iritgo licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.iritgo.aktera.util.string;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;


/**
 * Enhanced String/StringBuffer-type class for more capability than a normal
 * String or StringBuffer object.
 *
 * NOTE: This class includes some methods originally developed for the Expresso
 * project, hosted at www.jcorporate.com. See the license file for appropriate
 * credit and license information.
 */

/**
 * <p>
 * A fast string buffer implements a mutable sequence of characters.
 * </p>
 * <p>
 * SuperStrings are <b>not</b> thread-safe by themselves - be sure to
 * synchronize
 * </p>
 * <p>
 * Every string buffer has a capacity. As long as the length of the character
 * sequence contained in the string buffer does not exceed the capacity, it is
 * not necessary to allocate a new internal buffer array. If the internal
 * buffer overflows, it is automatically made larger.
 * </p>
 *
 * @see java.io.ByteArrayOutputStream
 * @see java.lang.StringBuffer
 */
public final class SuperString implements Serializable
{
	/** The value is used for character storage. */
	private char[] value;

	/** The count is the number of characters in the buffer. */
	private int count;

	/** A flag indicating whether the buffer is shared */
	private boolean shared;

	/**
	 * Constructs an empty SuperString with no characters in it and an initial
	 * capacity of 16 characters.
	 *
	 */
	public SuperString()
	{
		this(16);
	}

	/**
	 * Constructs a SuperString with no characters in it and an initial
	 * capacity specified by the <code>length</code> argument.
	 *
	 * @param length
	 *            the initial capacity.
	 * @exception NegativeArraySizeException
	 *                if the <code>length</code> argument is less than <code>0</code>.
	 */
	public SuperString(int length)
	{
		value = new char[length];
		shared = false;
	}

	/**
	 * Constructs a SuperString so that it represents the same sequence of
	 * characters as the string argument. The initial capacity of the string
	 * buffer is <code>16</code> plus the length of the string argument.
	 *
	 * @param str
	 *            the initial contents of the buffer.
	 */
	public SuperString(String originalString)
	{
		this(originalString.length() + 16);
		append(originalString);
	}

	//
	// The following two methods are needed by SuperString to efficiently
	// convert a StringBuffer into a SuperString. They are not public.
	// They shouldn't be called by anyone but String.
	final void setShared()
	{
		shared = true;
	}

	final char[] getValue()
	{
		return value;
	}

	/**
	 * Returns the length (character count) of this string buffer.
	 *
	 * @return the number of characters in this string buffer.
	 */
	public int length()
	{
		return count;
	}

	/**
	 * Returns the current capacity of the String buffer. The capacity is the
	 * amount of storage available for newly inserted characters; beyond which
	 * an allocation will occur.
	 *
	 * @return the current capacity of this string buffer.
	 */
	public int capacity()
	{
		return value.length;
	}

	/**
	 * Copies the buffer value if it is shared.
	 */
	private final void copyWhenShared()
	{
		if (shared)
		{
			char[] newValue = new char[value.length];

			System.arraycopy(value, 0, newValue, 0, count);
			value = newValue;
			shared = false;
		}
	}

	/**
	 * Ensures that the capacity of the buffer is at least equal to the
	 * specified minimum. If the current capacity of this string buffer is less
	 * than the argument, then a new internal buffer is allocated with greater
	 * capacity. The new capacity is the larger of:
	 * <ul>
	 * <li>The <code>minimumCapacity</code> argument.
	 * <li>Twice the old capacity, plus <code>2</code>.
	 * </ul>
	 * If the <code>minimumCapacity</code> argument is nonpositive, this
	 * method takes no action and simply returns.
	 *
	 * @param minimumCapacity
	 *            the minimum desired capacity.
	 */
	public void ensureCapacity(int minimumCapacity)
	{
		int maxCapacity = value.length;

		if (minimumCapacity > maxCapacity)
		{
			int newCapacity = (maxCapacity + 1) * 2;

			if (minimumCapacity > newCapacity)
			{
				newCapacity = minimumCapacity;
			}

			char[] newValue = new char[newCapacity];

			System.arraycopy(value, 0, newValue, 0, count);
			value = newValue;
			shared = false;
		}
	}

	/**
	 * Sets the length of this String buffer. If the <code>newLength</code>
	 * argument is less than the current length of the string buffer, the
	 * string buffer is truncated to contain exactly the number of characters
	 * given by the <code>newLength</code> argument.
	 * <p>
	 * If the <code>newLength</code> argument is greater than or equal to the
	 * current length, sufficient null characters (<code>'&#92;u0000'</code>)
	 * are appended to the string buffer so that length becomes the <code>newLength</code>
	 * argument.
	 * <p>
	 * The <code>newLength</code> argument must be greater than or equal to
	 * <code>0</code>.
	 *
	 * @param newLength
	 *            the new length of the buffer.
	 * @exception StringIndexOutOfBoundsException
	 *                if the <code>newLength</code> argument is invalid.
	 * @see java.lang.StringBuffer#length()
	 */
	public void setLength(int newLength)
	{
		if (newLength < 0)
		{
			throw new StringIndexOutOfBoundsException(newLength);
		}

		ensureCapacity(newLength);

		if (count < newLength)
		{
			copyWhenShared();

			for (; count < newLength; count++)
			{
				value[count] = '\0';
			}
		}

		count = newLength;
	}

	/**
	 * Returns the character at a specific index in this string buffer.
	 * <p>
	 * The first character of a string buffer is at index <code>0</code>,
	 * the next at index <code>1</code>, and so on, for array indexing.
	 * <p>
	 * The index argument must be greater than or equal to <code>0</code>,
	 * and less than the length of this string buffer.
	 *
	 * @param index
	 *            the index of the desired character.
	 * @return the character at the specified index of this string buffer.
	 * @exception StringIndexOutOfBoundsException
	 *                if the index is invalid.
	 * @see java.lang.StringBuffer#length()
	 */
	public char charAt(int index)
	{
		if ((index < 0) || (index >= count))
		{
			throw new StringIndexOutOfBoundsException(index);
		}

		return value[index];
	}

	/**
	 * Characters are copied from this string buffer into the destination
	 * character array <code>dst</code>. The first character to be copied is
	 * at index <code>srcBegin</code>; the last character to be copied is at
	 * index <code>srcEnd-1.</code> The total number of characters to be
	 * copied is <code>srcEnd-srcBegin</code>. The characters are copied
	 * into the subarray of <code>dst</code> starting at index <code>dstBegin</code>
	 * and ending at index:
	 * <p>
	 * <blockquote>
	 *
	 * <pre>
	 *  dstbegin + (srcEnd-srcBegin) - 1
	 * </pre>
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 * </blockquote>
	 *
	 * @param srcBegin
	 *            start copying at this offset in the string buffer.
	 * @param srcEnd
	 *            stop copying at this offset in the string buffer.
	 * @param dst
	 *            the array to copy the data into.
	 * @param dstBegin
	 *            offset into <code>dst</code>.
	 * @exception StringIndexOutOfBoundsException
	 *                if there is an invalid index into the buffer.
	 */
	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin)
	{
		if ((srcBegin < 0) || (srcBegin >= count))
		{
			throw new StringIndexOutOfBoundsException(srcBegin);
		}

		if ((srcEnd < 0) || (srcEnd > count))
		{
			throw new StringIndexOutOfBoundsException(srcEnd);
		}

		if (srcBegin < srcEnd)
		{
			System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
		}
	}

	/**
	 * The character at the specified index of this string buffer is set to
	 * <code>ch</code>.
	 * <p>
	 * The offset argument must be greater than or equal to <code>0</code>,
	 * and less than the length of this string buffer.
	 *
	 * @param index
	 *            the index of the character to modify.
	 * @param ch
	 *            the new character.
	 * @exception StringIndexOutOfBoundsException
	 *                if the index is invalid.
	 * @see java.lang.StringBuffer#length()
	 */
	public void setCharAt(int index, char ch)
	{
		if ((index < 0) || (index >= count))
		{
			throw new StringIndexOutOfBoundsException(index);
		}

		copyWhenShared();
		value[index] = ch;
	}

	/**
	 * Appends the string representation of the <code>Object</code> argument
	 * to this string buffer.
	 * <p>
	 * The argument is converted to a string as if by the method <code>String.valueOf</code>,
	 * and the characters of that string are then appended to this string
	 * buffer.
	 *
	 * @param obj
	 *            an <code>Object</code>.
	 * @return this string buffer.
	 * @see java.lang.String#valueOf(java.lang.Object)
	 * @see java.lang.StringBuffer#append(java.lang.String)
	 */
	public SuperString append(Object obj)
	{
		return append(String.valueOf(obj));
	}

	/**
	 * Appends the string to this string buffer.
	 * <p>
	 * The characters of the <code>String</code> argument are appended, in
	 * order, to the contents of this string buffer, increasing the length of
	 * this string buffer by the length of the argument.
	 *
	 * @param str
	 *            a string.
	 * @return this string buffer.
	 */
	public SuperString append(String str)
	{
		String tmp = str;

		if (str == null)
		{
			tmp = String.valueOf(str);
		}

		int len = tmp.length();

		ensureCapacity(count + len);
		copyWhenShared();
		tmp.getChars(0, len, value, count);
		count += len;

		return this;
	}

	/**
	 * Appends the string representation of the <code>char</code> array
	 * argument to this string buffer.
	 * <p>
	 * The characters of the array argument are appended, in order, to the
	 * contents of this string buffer. The length of this string buffer
	 * increases by the length of the argument.
	 *
	 * @param str
	 *            the characters to be appended.
	 * @return this string buffer.
	 */
	public SuperString append(char[] str)
	{
		int len = str.length;

		ensureCapacity(count + len);
		copyWhenShared();
		System.arraycopy(str, 0, value, count, len);
		count += len;

		return this;
	}

	/**
	 * Appends the string representation of a subarray of the <code>char</code>
	 * array argument to this string buffer.
	 * <p>
	 * Characters of the character array <code>str</code>, starting at index
	 * <code>offset</code>, are appended, in order, to the contents of this
	 * string buffer. The length of this string buffer increases by the value
	 * of <code>len</code>.
	 *
	 * @param str
	 *            the characters to be appended.
	 * @param offset
	 *            the index of the first character to append.
	 * @param len
	 *            the number of characters to append.
	 * @return this string buffer.
	 */
	public SuperString append(char[] str, int offset, int len)
	{
		ensureCapacity(count + len);
		copyWhenShared();
		System.arraycopy(str, offset, value, count, len);
		count += len;

		return this;
	}

	/**
	 * Appends one SuperString to another so they can be merged with no
	 * unnecessary allocations
	 *
	 * @return this string buffer
	 */
	public SuperString append(SuperString str)
	{
		int len = str.length();

		ensureCapacity(count + str.count);
		copyWhenShared();
		System.arraycopy(str.getValue(), 0, value, count, len);
		count += len;

		return this;
	}

	/**
	 * Appends the string representation of the <code>boolean</code> argument
	 * to the string buffer.
	 * <p>
	 * The argument is converted to a string as if by the method <code>String.valueOf</code>,
	 * and the characters of that string are then appended to this string
	 * buffer.
	 *
	 * @param b
	 *            a <code>boolean</code>.
	 * @return this string buffer.
	 * @see java.lang.String#valueOf(boolean)
	 * @see java.lang.StringBuffer#append(java.lang.String)
	 */
	public SuperString append(boolean b)
	{
		return append(String.valueOf(b));
	}

	/**
	 * Appends the string representation of the <code>char</code> argument to
	 * this string buffer.
	 * <p>
	 * The argument is appended to the contents of this string buffer. The
	 * length of this string buffer increases by <code>1</code>.
	 *
	 * @param ch
	 *            a <code>char</code>.
	 * @return this string buffer.
	 */
	public SuperString append(char c)
	{
		ensureCapacity(count + 1);
		copyWhenShared();
		value[count++] = c;

		return this;
	}

	/**
	 * Appends the string representation of the <code>int</code> argument to
	 * this string buffer.
	 * <p>
	 * The argument is converted to a string as if by the method <code>String.valueOf</code>,
	 * and the characters of that string are then appended to this string
	 * buffer.
	 *
	 * @param i
	 *            an <code>int</code>.
	 * @return this string buffer.
	 * @see java.lang.String#valueOf(int)
	 * @see java.lang.StringBuffer#append(java.lang.String)
	 */
	public SuperString append(int i)
	{
		return append(String.valueOf(i));
	}

	/**
	 * Appends the string representation of the <code>long</code> argument to
	 * this string buffer.
	 * <p>
	 * The argument is converted to a string as if by the method <code>String.valueOf</code>,
	 * and the characters of that string are then appended to this string
	 * buffer.
	 *
	 * @param l
	 *            a <code>long</code>.
	 * @return this string buffer.
	 * @see java.lang.String#valueOf(long)
	 * @see java.lang.StringBuffer#append(java.lang.String)
	 */
	public SuperString append(long l)
	{
		return append(String.valueOf(l));
	}

	/**
	 * Appends the string representation of the <code>float</code> argument
	 * to this string buffer.
	 * <p>
	 * The argument is converted to a string as if by the method <code>String.valueOf</code>,
	 * and the characters of that string are then appended to this string
	 * buffer.
	 *
	 * @param f
	 *            a <code>float</code>.
	 * @return this string buffer.
	 * @see java.lang.String#valueOf(float)
	 * @see java.lang.StringBuffer#append(java.lang.String)
	 */
	public SuperString append(float f)
	{
		return append(String.valueOf(f));
	}

	/**
	 * Appends the string representation of the <code>double</code> argument
	 * to this string buffer.
	 * <p>
	 * The argument is converted to a string as if by the method <code>String.valueOf</code>,
	 * and the characters of that string are then appended to this string
	 * buffer.
	 *
	 * @param d
	 *            a <code>double</code>.
	 * @return this string buffer.
	 * @see java.lang.String#valueOf(double)
	 * @see java.lang.StringBuffer#append(java.lang.String)
	 */
	public SuperString append(double d)
	{
		return append(String.valueOf(d));
	}

	/**
	 * The character sequence contained in this string buffer is replaced by
	 * the reverse of the sequence.
	 *
	 * @return this string buffer.
	 */
	public SuperString reverse()
	{
		copyWhenShared();

		int n = count - 1;

		for (int j = (n - 1) >> 1; j >= 0; --j)
		{
			char temp = value[j];

			value[j] = value[n - j];
			value[n - j] = temp;
		}

		return this;
	}

	/**
	 * Converts to a string representing the data in this string buffer. A new
	 * <code>String</code> object is allocated and initialized to contain the
	 * character sequence currently represented by this string buffer. This
	 * <code>String</code> is then returned. Subsequent changes to the string
	 * buffer do not affect the contents of the <code>String</code>.
	 *
	 * @return a string representation of the string buffer.
	 */
	public String toString()
	{
		return new String(value, 0, count);
	}

	public static boolean toBoolean(String theString)
	{
		boolean returnValue = false;

		if (theString != null)
		{
			String tmp = theString.trim();

			if (tmp.equalsIgnoreCase("y") || tmp.equalsIgnoreCase("yes") || tmp.equalsIgnoreCase("true")
							|| tmp.equalsIgnoreCase("1"))
			{
				returnValue = true;
			}
		}

		return returnValue;
	}

	public void assertNotBlank()
	{
		assertNotBlank("Null not allowed here");
	}

	public void assertNotBlank(String theMessage)
	{
		if (toString() == null)
		{
			throw new IllegalArgumentException("Null argument not allowed. " + theMessage);
		}

		if (toString().trim().equals(""))
		{
			throw new IllegalArgumentException("Blank argument not allowed. " + theMessage);
		}
	}

	/**
	 * Make sure any arbitrary string is not null. This allows us to use the
	 * assertNotBlank methods on ordinary strings
	 *
	 * @param theString
	 *            Any string, possibly null
	 * @param theMessage
	 */
	public static void assertNotBlank(String theString, String theMessage)
	{
		if (theString == null)
		{
			throw new IllegalArgumentException(theMessage);
		}

		if (theString.trim().equals(""))
		{
			throw new IllegalArgumentException(theMessage);
		}
	} /* assertNotBlank(String, String) */

	/**
	 * Make sure a string is not null.
	 *
	 * @param theString
	 *            Any string, possibly null
	 * @return An empty string if the original was null, else the original
	 */
	public static String notNull(String theString)
	{
		String returnValue = null;

		if (theString == null)
		{
			returnValue = "";
		}
		else
		{
			returnValue = theString;
		}

		return returnValue;
	} /* notNull(String) */

	public static void assertBoolean(String theString, String theMessage)
	{
		assertNotBlank(theString, theMessage);

		if (! (theString.equalsIgnoreCase("yes") || theString.equalsIgnoreCase("true")
						|| theString.equalsIgnoreCase("no") || theString.equalsIgnoreCase("false")
						|| theString.equalsIgnoreCase("y") || theString.equalsIgnoreCase("n")))
		{
			throw new IllegalArgumentException(theMessage);
		}
	}

	/**
	 * Same thing but using a serializable string as the parameter instead
	 *
	 */
	public static String notNull(SuperString theString)
	{
		String returnValue = null;

		if (theString == null)
		{
			returnValue = "";
		}
		else
		{
			returnValue = theString.toString();
		}

		return returnValue;
	}

	/**
	 * This method is useful for creating lists that use letters instead of
	 * numbers, such as a, b, c, d...instead of 1, 2, 3, 4. Valid numbers are
	 * from 1 to 26, corresponding to the 26 letters of the alphabet. By
	 * default, the letter is returned as a lowercase, but if the boolean
	 * upperCaseFlag is true, the letter will be returned as an uppercase.
	 * Creation date: (5/11/00 12:52:23 PM) @author: Adam Rossi
	 *
	 * @return java.lang.String
	 * @param number
	 * @param upperCaseFlag
	 * @throws Exception
	 */
	public static String numberToLetter(int number, boolean upperCaseFlag) throws Exception
	{
		//add nine to bring the numbers into the right range (in java, a= 10,
		// z = 35)
		if (number < 1 || number > 26)
		{
			throw new Exception("The number is out of the proper range (1 to " + "26) to be converted to a letter.");
		}

		int modnumber = number + 9;
		char thechar = Character.forDigit(modnumber, 36);

		if (upperCaseFlag)
		{
			thechar = Character.toUpperCase(thechar);
		}

		return "" + thechar;
	} /* numberToLetter(int, boolean) */

	/**
	 * replace substrings within string.
	 *
	 * @param s
	 * @param sub
	 * @param with
	 * @return
	 */
	public static String replace(String s, String sub, String with)
	{
		int c = 0;
		int i = s.indexOf(sub, c);

		if (i == - 1)
		{
			return s;
		}

		SuperString buf = new SuperString(s.length() + with.length());

		do
		{
			buf.append(s.substring(c, i));
			buf.append(with);
			c = i + sub.length();
		} while ((i = s.indexOf(sub, c)) != - 1);

		if (c < s.length())
		{
			buf.append(s.substring(c, s.length()));
		}

		return buf.toString();
	} /* replace(String, String, String) */

	/**
	 * Test to see if a String is a number. By number, we mean an integer. Use
	 * isRealNumber if you want to consider floating point values as a number
	 * as well.
	 */
	public static boolean isNumber(String stringToTest)
	{
		boolean isNumber = true;

		try
		{
			Integer.parseInt(stringToTest);
		}
		catch (NumberFormatException ex)
		{
			isNumber = false;
		}

		return isNumber;
	}

	/**
	 * Test to see if a String is a real number. This means that both decimal
	 * and integer values are recognized.
	 */
	public static boolean isRealNumber(String stringToTest)
	{
		boolean isNumber = true;

		try
		{
			Double.parseDouble(stringToTest);

			//This accepts integers and floating point
		}
		catch (NumberFormatException ex)
		{
			isNumber = false;
		}

		return isNumber;
	}

	/**
	 * replace substrings within string.
	 *
	 * @param s
	 * @param sub
	 * @param with
	 * @return
	 */
	public String replace(String sub, String with)
	{
		int c = 0;
		String s = this.toString();
		int i = s.indexOf(sub, c);

		if (i == - 1)
		{
			return s;
		}

		SuperString buf = new SuperString(s.length() + with.length());

		do
		{
			buf.append(s.substring(c, i));
			buf.append(with);
			c = i + sub.length();
		} while ((i = s.indexOf(sub, c)) != - 1);

		if (c < s.length())
		{
			buf.append(s.substring(c, s.length()));
		}

		return buf.toString();
	} /* replace(String, String) */

	/**
	 * Formats the string to an XML/XHTML escaped character.
	 *
	 * @param s
	 *            the String to format
	 * @return The escaped formatted String.
	 * @see org.apache.xml.serialize.BaseMarkupSerializer for example of
	 *      original code.
	 */
	public static String xmlEscape(String s)
	{
		int length = s.length();
		SuperString fsb = new SuperString(length);

		for (int i = 0; i < length; i++)
		{
			fsb = printEscaped(s.charAt(i), fsb);
		}

		return fsb.toString();
	}

	/**
	 * Formats a particular character to something workable in xml Helper to
	 * xmlEscape()
	 *
	 * @param ch
	 *            the character to print.
	 * @param fsb
	 *            The SuperString to add this to.
	 */
	protected static SuperString printEscaped(char ch, SuperString fsb)
	{
		String charRef;

		// If there is a suitable entity reference for this
		// character, print it. The list of available entity
		// references is almost but not identical between
		// XML and HTML.
		charRef = getEntityRef(ch);

		if (charRef != null)
		{
			fsb.append('&');
			fsb.append(charRef);
			fsb.append(';');

			//ch<0xFF == isPrintable()
		}
		else if ((ch >= ' ' && ch < 0xFF && ch != 0xF7) || ch == '\n' || ch == '\r' || ch == '\t')
		{
			// If the character is not printable, print as character reference.
			// Non printables are below ASCII space but not tab or line
			// terminator, ASCII delete, or above a certain Unicode threshold.
			if (ch < 0x10000)
			{
				fsb.append(ch);
			}
			else
			{
				fsb.append((char) (((ch - 0x10000) >> 10) + 0xd800));
				fsb.append((char) (((ch - 0x10000) & 0x3ff) + 0xdc00));
			}
		}
		else
		{
			fsb.append("&#x");
			fsb.append(Integer.toHexString(ch));
			fsb.append(';');
		}

		return fsb;
	}

	/**
	 * Helper to xmlEscape()
	 */
	protected static String getEntityRef(int ch)
	{
		String returnValue = null;

		// Encode special XML characters into the equivalent character
		// references.
		// These five are defined by default for all XML documents.
		switch (ch)
		{
			case '<':
				returnValue = "lt";

				break;

			case '>':
				returnValue = "gt";

				break;

			case '"':
				returnValue = "quot";

				break;

			case '\'':
				returnValue = "apos";

				break;

			case '&':
				returnValue = "amp";

				break;

			default:

				//--- quikdraw: There should be a default. What should it be?
				break;
		}

		return returnValue;
	}

	public static String noNewLine(String fieldValue)
	{
		String returnValue = fieldValue;

		if (returnValue.indexOf("\n") != 0)
		{
			returnValue = SuperString.replace(returnValue, "\n", "");
		}

		if (returnValue.indexOf("\r") != 0)
		{
			returnValue = SuperString.replace(returnValue, "\r", "");
		}

		return returnValue;
	} /* noNewLine(String) */

	/**
	 * Utility method to return a string with all single quotes replaced with a
	 * pair of single quotes, and all double quotes also replaced with a pair
	 * of single quotes
	 *
	 * @param oldString
	 *            The original string
	 * @return The string modified as above
	 */
	public static String noQuotes(String oldString)
	{
		String newString = SuperString.replace(oldString, "'", "''");
		String newString2 = SuperString.replace(newString, "\"", "''");

		return newString2;
	} /* noQuotes(String) */

	/**
	 * Return the value of this SuperString as a java.util.Date. When
	 * necessary, assume that the month is before the days, e.g. mm/dd. If you
	 * know that's not true, use toDate(false)
	 *
	 * @return The current string converted to a date, if possible.
	 */
	public Date toDate()
	{
		return toDate(true);
	}

	/**
	 * Parse the current String contents as a date, if possible.
	 * Will parse just about any date/time format, with any non-character
	 * separator.
	 * @param monthFirst If you know the month comes first. False means "best guess", but will
	 * still assume month first if the values are reasonable for this.
	 * @return A date, corresponding to the current string value.
	 */
	public Date toDate(boolean monthFirst)
	{
		String monthPart = null;
		String dayPart = null;
		String yearPart = null;
		String hoursPart = "0";
		String minutesPart = "0";
		String secondsPart = "0";
		String millisPart = "0";
		List dateParts = new ArrayList();
		List timeParts = new ArrayList();
		boolean monthAsString = false;

		StringBuffer currentString = new StringBuffer();

		for (int i = 0; i < length(); i++)
		{
			char c = charAt(i);

			if (isSeperator(c))
			{
				if (dateParts.size() < 3)
				{
					dateParts.add(currentString.toString());
				}
				else
				{
					timeParts.add(currentString.toString());
				}

				currentString = new StringBuffer();
			}
			else
			{
				currentString = currentString.append(c);
			}
		}

		if ((currentString != null) && (! currentString.toString().trim().equals("")))
		{
			if (dateParts.size() < 3)
			{
				dateParts.add(currentString.toString());
			}
			else
			{
				timeParts.add(currentString.toString());
			}
		}

		int[] partsAsInts =
		{
						- 1, - 1, - 1
		};
		int partCount = 0;
		String thisPart = null;

		for (Iterator i = dateParts.iterator(); i.hasNext();)
		{
			partCount++;
			thisPart = (String) i.next();

			if ((thisPart != null) && (! thisPart.trim().equals("")))
			{
				if (Character.isDigit(thisPart.charAt(0)))
				{
					/* handle as a number */
					int partAsInt = new Integer(thisPart).intValue();

					partsAsInts[partCount - 1] = partAsInt;
				}
				else
				{
					/* must be the month */
					monthPart = thisPart;
					monthAsString = true;
				}
			}
		}

		/* extract a month */
		if (monthPart == null)
		{
			int monthIndex = getMonth(partsAsInts, monthFirst);

			monthPart = "" + partsAsInts[monthIndex];
			/* mark this part as "used up" */
			partsAsInts[monthIndex] = - 1;
		}

		/* extract a day portion */
		for (int j = 0; j < 3; j++)
		{
			if ((partsAsInts[j] < 32) && (partsAsInts[j] > 0))
			{
				dayPart = "" + partsAsInts[j];
				partsAsInts[j] = - 1;

				break;
			}
		}

		/* extract a year */
		for (int k = 0; k < 3; k++)
		{
			if (partsAsInts[k] > 0)
			{
				yearPart = "" + partsAsInts[k];

				if (yearPart.length() == 1)
				{
					yearPart = "0" + yearPart;
				}

				partsAsInts[k] = - 1;

				break;
			}
		}

		String amPm = null;

		if (timeParts.size() > 0)
		{
			hoursPart = (String) timeParts.get(0);
		}

		if (timeParts.size() > 1)
		{
			amPm = isAmOrPm((String) timeParts.get(1));

			if (amPm == null)
			{
				minutesPart = (String) timeParts.get(1);
			}
		}

		if (timeParts.size() > 2)
		{
			amPm = isAmOrPm((String) timeParts.get(2));

			if (amPm == null)
			{
				secondsPart = (String) timeParts.get(2);
			}
		}

		if (timeParts.size() > 3)
		{
			amPm = isAmOrPm((String) timeParts.get(3));

			if (amPm == null)
			{
				millisPart = (String) timeParts.get(3);
			}
		}

		if (timeParts.size() > 4)
		{
			amPm = isAmOrPm((String) timeParts.get(4));
		}

		if ((hoursPart != null) && (! hoursPart.trim().equals("")))
		{
			int hoursAsInt = new Integer(hoursPart).intValue();

			if (hoursAsInt < 12)
			{
				if (amPm != null)
				{
					if (amPm.equals("pm"))
					{
						hoursPart = "" + (hoursAsInt + 12);
					}
				}
			}
		}

		if (yearPart == null)
		{
			Calendar c = new GregorianCalendar();

			yearPart = "" + c.get(Calendar.YEAR);
		}

		StringBuffer format = new StringBuffer();

		if (monthAsString)
		{
			format.append("MMMM");
		}
		else
		{
			format.append("MM");
		}

		if (yearPart.length() < 4)
		{
			format.append("/dd/yy HH:mm:ss.S");
		}
		else
		{
			format.append("/dd/yyyy HH:mm:ss.S");
		}

		DateFormat formatter = new SimpleDateFormat(format.toString());

		try
		{
			return formatter.parse(monthPart + "/" + dayPart + "/" + yearPart + " " + hoursPart + ":" + minutesPart
							+ ":" + secondsPart + "." + millisPart);
		}
		catch (ParseException pe)
		{
			throw new IllegalArgumentException("Unable to parse date from '" + toString() + "':" + pe.getMessage());
		}
	}

	private String isAmOrPm(String s)
	{
		if (s.equalsIgnoreCase("am"))
		{
			return "am";
		}

		if (s.equalsIgnoreCase("pm"))
		{
			return "pm";
		}

		return null;
	}

	private int getMonth(int[] partsAsInts, boolean monthFirst)
	{
		if ((partsAsInts[0] <= 12) || (partsAsInts[1] <= 12))
		{
			if (monthFirst)
			{
				if ((partsAsInts[0] > 0) && (partsAsInts[0] <= 12))
				{
					return 0;
				}
			}
		}

		if ((partsAsInts[1] <= 12) && (partsAsInts[1] > 0))
		{
			return 1;
		}

		if ((partsAsInts[2] <= 12) && (partsAsInts[2] > 0))
		{
			return 2;
		}

		throw new IllegalArgumentException("No valid month in '" + toString() + "'");
	}

	private boolean isSeperator(char c)
	{
		return (! Character.isLetterOrDigit(c));
	}
} /* SuperString */
