/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
 */

package org.mxupdate.update.util;

/**
 * Utility for string handling.
 *
 * @author The MxUpdate Team
 */
public final class UpdateUtils_mxJPO
{
    /**
     * Private constructor to avoid initialize of this class.
     */
    private UpdateUtils_mxJPO()
    {
    }

    /**
     * <p>Encodes all special characters defined in given {@code _text}.</p>
     * <p>Following character are not converted:
     * <ul>
     * <li>all standard 7-bit-ASCII characters from 32 till 127 (space till
     *     tilde '~') except backslash (used for encoding), apostrophe</li>
     * <li>new lines</li>
     * </ul>
     * </p>
     * <p>All other characters are converted with the algorithm:
     * <ul>
     * <li>&quot;backslash&quot; '\' is converted to double backslash
     *     '\\' (because used for encoding)</li>
     * <li>tabulator is converted to '\t' (for better reading)</li>
     * <li>all other characters are encoded to a four characters long
     *      hexa-decimal code with '\&#x0075;' as prefix (final e.g. the euro
     *      sign '&euro;' is converted to '\u20ac')</li>
     * </ul>
     * </p>
     *
     * @param _name     MX name to convert
     * @return converted file name
     * @see #convertFromFileName(String)
     */
    public static String encodeText(final CharSequence _text)
    {
        final StringBuilder ret = new StringBuilder();
        if (_text != null)  {
            int brace = 0;
            for (final char ch : _text.toString().toCharArray()) {
                switch (ch)  {
                    case '\\':
                        ret.append("\\\\");
                        break;
                    case '\n':
                        ret.append(ch);
                        break;
                    case '\t':
                        ret.append("\\t");
                        break;
                    case '\"':
                        ret.append("\\\"");
                        break;
                    case '{':
                        brace++;
                        ret.append('{');
                        break;
                    case '}':
                        if (brace > 0)  {
                            brace--;
                        } else  {
                            ret.append('\\');
                        }
                        ret.append('}');
                        break;
                    default:
                        if ((ch < 32) || (ch > 127))  {
                            final String hex = String.valueOf(Integer.toHexString(ch));
                            ret.append("\\u");
                            switch (hex.length())  {
                                case 1:
                                    ret.append('0');
                                case 2:
                                    ret.append('0');
                                case 3:
                                    ret.append('0');
                                case 4:
                                    ret.append(hex);
                                default:
                                    break;
                            }
                        } else  {
                            ret.append(ch);
                        }
                }
            }

            // if start braces are not correct => fix them with backslashes
            if (brace > 0)  {
                for (int idx = ret.length() - 1; (idx >=0) && (brace > 0); idx--)  {
                    if (ret.charAt(idx) == '{')  {
                        brace--;
                        ret.insert(idx, '\\');
                    }
                }
            }
        }
        return ret.toString();
    }

    /**
     * <p>Decodes the given {@code _text} back to read text. The same characters
     * described in {@link #encodeText(String)} are converted back.</p>
     *
     * @param _text     text to convert
     * @return decoded text
     * @throws IllegalArgumentException if the text item name could not be
     *                  decoded, because the special characters are not in
     *                  correct format
     * @see #decodeGetChar(char[], int)
     */
    public static String decodeText(final CharSequence _text)
        throws IllegalArgumentException
    {
        final StringBuilder ret = new StringBuilder();
        if (_text != null)  {
            final char[] charFileName = _text.toString().toCharArray();
            for (int idx = 0; idx < charFileName.length; idx++)  {
                final char ch1 = charFileName[idx];
                if (ch1 == '\\')  {
                    final char ch2 = UpdateUtils_mxJPO.decodeGetChar(charFileName, ++idx);
                    switch (ch2)  {
                        case 'n':
                            ret.append('\n');
                            break;
                        case 't':
                            ret.append('\t');
                            break;
                        case 'u':
                            final char[] hex4 = new char[4];
                            hex4[0] = UpdateUtils_mxJPO.decodeGetChar(charFileName, ++idx);
                            hex4[1] = UpdateUtils_mxJPO.decodeGetChar(charFileName, ++idx);
                            hex4[2] = UpdateUtils_mxJPO.decodeGetChar(charFileName, ++idx);
                            hex4[3] = UpdateUtils_mxJPO.decodeGetChar(charFileName, ++idx);
                            ret.append((char) ((int) Integer.valueOf(new String(hex4), 16)));
                            break;
                        default:
                            ret.append(ch2);
                            break;
                    }
                } else  {
                    ret.append(ch1);
                }
            }
        }
        return ret.toString();
    }

    /**
     * Returns the character on the position {@code _idx} within character
     * array {@code _text}. If the position does not exists, an
     * exception will be thrown.
     *
     * @param _text     character array
     * @param _idx      index in the character array {@code _text}
     * @return the character on index <code>_idx</code> in character array
     *         <code>_charFile</code>
     * @throws IllegalArgumentException if the number in {@code _idx} is
     *                  greater then the length of the character array
     *                 {@code _text} (meaning array out of bound exception)
     */
    private static char decodeGetChar(final char[] _text,
                                      final int _idx)
        throws IllegalArgumentException
    {
        if (_idx >= _text.length)  {
            throw new IllegalArgumentException();
        }
        return _text[_idx];
    }
}
