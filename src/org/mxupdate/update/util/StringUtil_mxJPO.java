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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
public final class StringUtil_mxJPO
{
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
     * @param _text string to convert
     * @return converted string
     */
    public static String convertTcl(final String _text)
    {
        return (_text != null)
               ? _text.replaceAll("\\\\", "\\\\\\\\")
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
     * @param _text string to convert
     * @return converted string
     */
    public static String convertMql(final String _text)
    {
        return (_text != null)
               ? _text.replaceAll("\\\\", "\\\\\\\\")
                      .replaceAll("\\\"", "\\\\\"")
               : "";
    }

    /**
     * Converts given MX name to a name which could be used within a file
     * system. This must be done because some characters could not handled
     * correctly from the file system.
     * <ul>
     * <li>a backslash is converted to &quot;@5C&quot;</li>
     * <li>a slash is converted to &quot;@2F&quot;</li>
     * <li>% is converted to &quot;@25&quot;</li>
     * <li>@ is converted to &quot;@@&quot;</li>
     * </ul>
     *
     * @param _name     MX name to convert
     * @return converted file name
     */
    public static String convertToFileName(final String _name)
    {
        return _name.replaceAll("@", "@@")
                    .replaceAll("%", "@25")
                    .replaceAll("/", "@2F")
                    .replaceAll("\\\\", "@5C");
    }

    /**
     * Converts the given file name back to internal used names. This must be
     * done because some characters could not handled correctly from the file
     * system.
     * <ul>
     * <li>&quot;@5C&quot; is converted to a backslash</li>
     * <li>&quot;@2F&quot; is converted to a slash</li>
     * <li>&quot;@25&quot; is converted to %</li>
     * <li>&quot;@@&quot; is converted to @</li>
     * </ul>
     *
     * @param _fileName     name from file to convert
     * @return converted name extracted from a file name
     */
    public static String convertFromFileName(final String _fileName)
    {
        return _fileName.replaceAll("@5C", "\\\\")
                        .replaceAll("@2F", "/")
                        .replaceAll("@25", "%")
                        .replaceAll("@@", "@");
    }

    /**
     * A list of string is joined to one string. Between two string the given
     * separator is set.
     *
     * @param _separator    separator between two list items
     * @param _list         list of strings
     * @param _emptyString  string which is written if the list is empty (or
     *                      <code>null</code> if no string for empty list is
     *                      written)
     * @return joined string of the list items
     */
    public static String join(final char _separator,
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
            for (final String access : _list)  {
                if (!first)  {
                    ret.append(_separator);
                } else  {
                    first = false;
                }
                ret.append(access);
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
     * @param _filename
     * @param _wildcardMatcher
     * @return
     */
    public static boolean match(final String _filename,
                                final String _wildcardMatcher)
    {

                     if (_filename == null && _wildcardMatcher == null) {
                         return true;
                     }
                     if (_filename == null || _wildcardMatcher == null) {
                         return false;
                     }
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
                         if (wcsIdx == wcs.length && textIdx == _filename.length()) {
                             return true;
                         }

                     } while (backtrack.size() > 0);

                     return false;
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
