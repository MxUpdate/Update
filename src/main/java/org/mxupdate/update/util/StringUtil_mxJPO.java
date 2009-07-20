/*
 * Copyright 2008-2009 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.update.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.Stack;

/**
 *
 * @author Tim Moxter
 * @version $Id$
 */
public final class StringUtil_mxJPO
{
    /**
     * String of the key within the parameter cache for the file date format
     * parameter.
     *
     * @see #formatFileDate(ParameterCache_mxJPO, Date)
     * @see #parseFileDate(ParameterCache_mxJPO, String)
     */
    private static final String PARAM_FILEDATEFORMAT = "FileDateFormat";

    /**
     * String of the key within the parameter cache for the installed date
     * format parameter.
     *
     * @see #formatInstalledDate(ParameterCache_mxJPO, Date)
     * @see #parseInstalledDate(ParameterCache_mxJPO, String)
     */
    private static final String PARAM_INSTALLEDDATEFORMAT = "InstalledDateFormat";

    /**
     * Holding the GMT0 time zone used to convert file and installed dates.
     *
     * @see #formatFileDate(ParameterCache_mxJPO, Date)
     * @see #formatInstalledDate(ParameterCache_mxJPO, Date)
     * @see #parseFileDate(ParameterCache_mxJPO, String)
     * @see #parseInstalledDate(ParameterCache_mxJPO, String)
     */
    private static final SimpleTimeZone TIMEZONE = new SimpleTimeZone(0, "GMT");

    /**
     * The constructor is defined so that no instance of the string utility
     * could be created.
     */
    private StringUtil_mxJPO()
    {
    }

    /**
     * Converts given string by escaping all special characters for TCL.
     *
     * @param _text     character stream to convert
     * @return converted string
     */
    public static String convertTcl(final CharSequence _text)
    {
        return (_text != null)
               ? _text.toString().replaceAll("\\\\", "\\\\\\\\")
                                 .replaceAll("\\\"", "\\\\\"")
                                 .replaceAll("\\" + "$", "\\\\\\" + "$")
                                 .replaceAll("\\{", "\\\\{")
                                 .replaceAll("\\}", "\\\\}")
                                 .replaceAll("\\[", "\\\\[")
                                 .replaceAll("\\]", "\\\\]")
               : "";
    }

    /**
     * Converts given string by escaping the &quot; so that in escape mode on
     * string could be handled with &quot; and '.
     *
     * @param _text     character stream to convert
     * @return converted string
     */
    public static String convertMql(final CharSequence _text)
    {
        return (_text != null)
               ? _text.toString().replaceAll("\\\\", "\\\\\\\\")
                                 .replaceAll("\\\"", "\\\\\"")
               : "";
    }

    /**
     * <p>Decodes given MX name to a name which could be used within a file
     * system. This must be done because some characters could not handled
     * correctly from the file system.</p>
     * <p>Following characters are not converted:
     * <ul>
     * <li>number</li>
     * <li>alphabetic character (lower and upper case)</li>
     * <li>left or right parenthesis</li>
     * <li>plus '+' or minus '-'</li>
     * <li>comma ',' or point '.'</li>
     * <li>space</li>
     * <li>equals sign '='</li>
     * <li>underscore '_'</li>
     * </ul>
     * </p>
     * <p>All other characters are converted with the algorithm:
     * <ul>
     * <li>the &quot;at symbol&quot; '@' will be converted to double at symbol
     *     '@@'</li>
     * <li>if a character is in the range of 0 and 254 (ASCII character), then
     *     the character is converted to a two characters long hexa-decimal
     *     code with '@' as prefix (e.g. double quotes '=' is converted to
     *     '@22')</li>
     * <li>characters greater than 254 are converted to a four characters
     *     long hexa-decimal code with '@u' as prefix (e.g. the euro sign
     *     '&euro;' is converted to '@u20AC')</li>
     * </ul>
     * </p>
     *
     * @param _name     MX name to convert
     * @return converted file name
     * @see #convertFromFileName(String)
     */
    public static String convertToFileName(final String _name)
    {
        final char[] charName = _name.toCharArray();
        final StringBuilder fileName = new StringBuilder();
        for (int idx = 0; idx < charName.length; idx++)  {
            final char ch = charName[idx];
            if (ch == '@')  {
                fileName.append("@@");
            } else if ((ch < '(' || ch > ')')
                    && (ch < '+' || ch > '.')
                    && (ch < '0' || ch > '9')
                    && (ch < 'A' || ch > 'Z')
                    && (ch < 'a' || ch > 'z')
                    && (ch != ' ') && (ch != '=') && (ch != '_'))  {

                final String hex = String.valueOf(Integer.toHexString(ch));
                fileName.append('@');
                switch (hex.length())  {
                    case 1:
                        fileName.append('0').append(hex);
                        break;
                    case 3:
                        fileName.append("u0").append(hex);
                        break;
                    case 4:
                        fileName.append('u').append(hex);
                        break;
                    default:
                        fileName.append(hex);
                        break;
                }
            } else  {
                fileName.append(ch);
            }
        }
        return fileName.toString();
    }

    /**
     * <p>Encodes the given file name back to internal used names. This must
     * be done because some characters could not handled correctly from the
     * file system.</p>
     * <p>The same characters described in {@link #convertToFileName(String)}
     * are converted back.</p>
     *
     * @param _fileName     name from file to convert
     * @return converted name extracted from a file name
     * @throws UpdateException_mxJPO if the configuration item name could not
     *                               be extracted from the file name
     *                               <code>_fileName</code> because the encoded
     *                               special characters are not in correct
     *                               format
     * @see #convertToFileName(String)
     */
    public static String convertFromFileName(final String _fileName)
        throws UpdateException_mxJPO
    {
        final char[] charFileName = _fileName.toCharArray();
        final StringBuilder name = new StringBuilder();
        for (int idx = 0; idx < charFileName.length; idx++)  {
            final char ch = charFileName[idx];
            if (ch == '@')  {
                switch (StringUtil_mxJPO.convertFromFileNameGetChar(charFileName, ++idx))  {
                    case '@':
                        name.append('@');
                        break;
                    case 'u':
                        final char[] hex4 = new char[4];
                        hex4[0] = StringUtil_mxJPO.convertFromFileNameGetChar(charFileName, ++idx);
                        hex4[1] = StringUtil_mxJPO.convertFromFileNameGetChar(charFileName, ++idx);
                        hex4[3] = StringUtil_mxJPO.convertFromFileNameGetChar(charFileName, ++idx);
                        hex4[4] = StringUtil_mxJPO.convertFromFileNameGetChar(charFileName, ++idx);
                        name.append((char) ((int) Integer.valueOf(new String(hex4), 16)));
                        break;
                    default:
                        final char[] hex2 = new char[2];
                        hex2[0] = StringUtil_mxJPO.convertFromFileNameGetChar(charFileName, idx);
                        hex2[1] = StringUtil_mxJPO.convertFromFileNameGetChar(charFileName, ++idx);
                        name.append((char) ((int) Integer.valueOf(new String(hex2), 16)));
                        break;
                }
            } else  {
                name.append(ch);
            }
        }

        return name.toString();
    }

    /**
     *
     * @param _charFileName
     * @param _idx
     * @return
     */
    private static char convertFromFileNameGetChar(final char[] _charFileName,
                                                   final int _idx)
        throws UpdateException_mxJPO
    {
        if (_idx >= _charFileName.length)  {
            throw new UpdateException_mxJPO(UpdateException_mxJPO.Error.UTIL_STRINGUTIL_CONVERT_FROM_FILENAME);
        }
        return _charFileName[_idx];
    }

    /**
     * A list of string is joined to one string. Between two string the given
     * separator is set. If quotes parameter is defined each element of the
     * list is surrounded with quotes. Each element is converted to TCL code.
     *
     * @param _separator    separator between two list items
     * @param _quotes       surround the elements of the string with quotes
     * @param _list         list of strings
     * @param _emptyString  string which is written if the list is empty (or
     *                      <code>null</code> if no string for empty list is
     *                      written)
     * @return joined string of the list items
     * @see #convertMql(CharSequence)
     */
    public static String joinTcl(final char _separator,
                                 final boolean _quotes,
                                 final Collection<String> _list,
                                 final String _emptyString)
    {
        final StringBuilder ret = new StringBuilder();

        boolean first = true;
        if (_list.isEmpty())  {
            if (_emptyString != null)  {
                ret.append(_emptyString);
            }
        } else  {
            for (final String elem : _list)  {
                if (!first)  {
                    ret.append(_separator);
                } else  {
                    first = false;
                }
                if (_quotes)  {
                    ret.append('\"');
                }
                ret.append(StringUtil_mxJPO.convertTcl(elem));
                if (_quotes)  {
                    ret.append('\"');
                }
            }
        }
        return ret.toString();
    }

    /**
     * Formats given date with time (normally from the last modification time
     * of a file) to the related string representation in MX. The convert is
     * done in time zone {@link #TIMEZONE} so that always the same time zone
     * is used (e.g. summer and winter time...).
     *
     * @param _paramCache   parameter cache
     * @param _date         last modification date of a file to convert to a
     *                      string
     * @return string representation of the date in time zone {@link #TIMEZONE}
     * @see #PARAM_FILEDATEFORMAT
     */
    public static String formatFileDate(final ParameterCache_mxJPO _paramCache,
                                        final Date _date)
    {
        final DateFormat fileFormat
                = new SimpleDateFormat(_paramCache.getValueString(StringUtil_mxJPO.PARAM_FILEDATEFORMAT));
        fileFormat.setTimeZone(StringUtil_mxJPO.TIMEZONE);
        return fileFormat.format(_date);
    }

    /**
     * Parses given string with date and time in time zone {@link #TIMEZONE}
     * and returns related date.
     *
     * @param _paramCache   parameter cache
     * @param _date         string with the last modified date in time zone
     *                      {@link #TIMEZONE}
     * @return parsed date instance
     * @throws ParseException if date string could not be parsed
     * @see #PARAM_FILEDATEFORMAT
     */
    public static Date parseFileDate(final ParameterCache_mxJPO _paramCache,
                                     final String _date)
            throws ParseException
    {
        final DateFormat fileFormat
                = new SimpleDateFormat(_paramCache.getValueString(StringUtil_mxJPO.PARAM_FILEDATEFORMAT));
        fileFormat.setTimeZone(StringUtil_mxJPO.TIMEZONE);
        return fileFormat.parse(_date);
    }

    /**
     * Formats given installation date to the related string representation in
     * MX. The convert is done in time zone {@link #TIMEZONE} so that always
     * the same time zone is used (e.g. summer and winter time...).
      *
     * @param _paramCache   parameter cache
     * @param _date         installed date to convert to a string
     * @return string representation of the date in time zone {@link #TIMEZONE}
     * @see #PARAM_INSTALLEDDATEFORMAT
     */
    public static String formatInstalledDate(final ParameterCache_mxJPO _paramCache,
                                             final Date _date)
    {
        final DateFormat fileFormat
                = new SimpleDateFormat(_paramCache.getValueString(StringUtil_mxJPO.PARAM_INSTALLEDDATEFORMAT));
        fileFormat.setTimeZone(StringUtil_mxJPO.TIMEZONE);
        return fileFormat.format(_date);
    }

    /**
     * Parses given string with date in time zone {@link #TIMEZONE} and returns
     * related date.
     *
     * @param _paramCache   parameter cache
     * @param _date         string with the installed date in time zone
     *                      {@link #TIMEZONE}
     * @return parsed date instance
     * @throws ParseException if date string could not be parsed
     * @see #PARAM_INSTALLEDDATEFORMAT
     */
    public static Date parseInstalledDate(final ParameterCache_mxJPO _paramCache,
                                          final String _date)
            throws ParseException
    {
        final DateFormat fileFormat
                = new SimpleDateFormat(_paramCache.getValueString(StringUtil_mxJPO.PARAM_INSTALLEDDATEFORMAT));
        fileFormat.setTimeZone(StringUtil_mxJPO.TIMEZONE);
        return fileFormat.parse(_date);
    }

    /**
     * A list of string is joined to one string. Between two string the given
     * separator is set. If quotes parameter is defined each element of the
     * list is surrounded with quotes. Each element is converted to MQL code.
     *
     * @param _separator    separator between two list items
     * @param _quotes       surround the elements of the string with quotes
     * @param _list         list of strings
     * @param _emptyString  string which is written if the list is empty (or
     *                      <code>null</code> if no string for empty list is
     *                      written)
     * @return joined string of the list items
     * @see #convertMql(CharSequence)
     */
    public static String joinMql(final char _separator,
                                 final boolean _quotes,
                                 final Collection<String> _list,
                                 final String _emptyString)
    {
        final StringBuilder ret = new StringBuilder();

        boolean first = true;
        if (_list.isEmpty())  {
            if (_emptyString != null)  {
                ret.append(_emptyString);
            }
        } else  {
            for (final String elem : _list)  {
                if (!first)  {
                    ret.append(_separator);
                } else  {
                    first = false;
                }
                if (_quotes)  {
                    ret.append('\"');
                }
                ret.append(StringUtil_mxJPO.convertMql(elem));
                if (_quotes)  {
                    ret.append('\"');
                }
            }
        }
        return ret.toString();
    }

    /**
     * The method is original implemented by the project on
     * <a href="http://commons.apache.org/io">Apache Commons IO</a> and copied
     * from method <code>wildcardMatch</code> within class
     * <a href="http://commons.apache.org/io/xref/org/apache/commons/io/FilenameUtils.html">
     * org.apache.commons.io.FilenameUtils</a>.
     *
     * @param _filename         file name to check
     * @param _wildcardMatcher  wildcard matcher string
     * @return <i>true</i> if <code>_filename</code> matches
     *         <code>_wildcardMatcher</code>
     */
    public static boolean match(final String _filename,
                                final String _wildcardMatcher)
    {
        final boolean ret;

        if ((_filename == null) && (_wildcardMatcher == null))  {
            ret = true;
        } else if (_filename == null || _wildcardMatcher == null) {
            ret = false;
        } else  {
            boolean tmpRet = false;

    //                 if (caseSensitivity == null) {
    //                     caseSensitivity = IOCase.SENSITIVE;
    //                 }
    //                 filename = caseSensitivity.convertCase(filename);
    //                 wildcardMatcher = caseSensitivity.convertCase(wildcardMatcher);
            final String[] wcs = splitOnTokens(_wildcardMatcher);
            boolean anyChars = false;
            int textIdx = 0;
            int wcsIdx = 0;
            final Stack<int[]> backtrack = new Stack<int[]>();

            // loop around a backtrack stack, to handle complex * matching
            do {
                if (backtrack.size() > 0) {
                    final int[] array = backtrack.pop();
                    wcsIdx = array[0];
                    textIdx = array[1];
                    anyChars = true;
                }

                // loop whilst tokens and text left to process
                while (wcsIdx < wcs.length) {

                    if (wcs[wcsIdx].equals("?")) {
                        // ? so move to next text char
                        textIdx++;
                        anyChars = false;

                    } else if (wcs[wcsIdx].equals("*")) {
                        // set any chars status
                        anyChars = true;
                        if (wcsIdx == wcs.length - 1) {
                            textIdx = _filename.length();
                        }

                    } else {
                        // matching text token
                        if (anyChars) {
                            // any chars then try to locate text token
                            textIdx = _filename.indexOf(wcs[wcsIdx], textIdx);
                            if (textIdx == -1) {
                                // token not found
                                break;
                            }
                            final int repeat = _filename.indexOf(wcs[wcsIdx], textIdx + 1);
                            if (repeat >= 0) {
                                backtrack.push(new int[] {wcsIdx, repeat});
                            }
                        } else {
                            // matching from current position
                            if (!_filename.startsWith(wcs[wcsIdx], textIdx)) {
                                // couldnt match token
                                break;
                            }
                        }

                        // matched text token, move text index to end of matched token
                        textIdx += wcs[wcsIdx].length();
                        anyChars = false;
                    }

                    wcsIdx++;
                }

                // full match
                if ((wcsIdx == wcs.length) && (textIdx == _filename.length()))  {
                    tmpRet = true;
                    break;
                }

            } while (backtrack.size() > 0);

            ret = tmpRet;
        }
        return ret;
    }

    /**
     * The method is original implemented by the project on
     * <a href="http://commons.apache.org/io">Apache Commons IO</a> and copied
     * from method <code>splitOnTokens</code> within class
     * <a href="http://commons.apache.org/io/xref/org/apache/commons/io/FilenameUtils.html">
     * org.apache.commons.io.FilenameUtils</a>.
     * Splits a string into a number of tokens.
     *
     * @param _text  the text to split
     * @return the tokens, never null
     */
    public static String[] splitOnTokens(final String _text)
    {
        final String[] ret;

         if ((_text.indexOf("?") == -1) && (_text.indexOf("*") == -1))  {
             ret = new String[] { _text };
         } else  {
             final char[] array = _text.toCharArray();
             final ArrayList<String> list = new ArrayList<String>();
             final StringBuilder buffer = new StringBuilder();
             for (int i = 0; i < array.length; i++) {
                 if ((array[i] == '?') || (array[i] == '*')) {
                     if (buffer.length() != 0) {
                         list.add(buffer.toString());
                         buffer.setLength(0);
                     }
                     if (array[i] == '?') {
                         list.add("?");
                     } else if (list.isEmpty() || ((i > 0) && !list.get(list.size() - 1).equals("*"))) {
                         list.add("*");
                     }
                 } else  {
                     buffer.append(array[i]);
                 }
             }
             if (buffer.length() != 0)  {
                 list.add(buffer.toString());
             }
             ret = list.toArray(new String[list.size()]);
         }
         return ret;
     }

}
