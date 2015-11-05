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

package org.mxupdate.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;


/**
 * Utility methods for the handling of files.
 *
 * @author The MxUpdate Team
 */
public final class FileUtils_mxJPO
{
    /**
     * The constructor is defined to avoid external initialization.
     */
    private FileUtils_mxJPO()
    {
    }

    /**
     * Returns the sub path between the given {@code _ciPath} and the file name
     * defined in the complete {@code _filePath}.
     *
     * @param _filePath     file path
     * @param _ciPath       defined CI sub path
     * @return sub path after the {@code _ciPath}; {@code null} if not found
     */
    public static String extraceSubPath(final String _filePath,
                                        final String _ciPath)
    {
        final String ret;

        if (_filePath == null || _ciPath == null)  {
            ret = null;
        } else  {
            // hint: to test in reality for paths, the ci path must contain path separator!
            final String ciPath = new StringBuilder().append('/').append(_ciPath).append('/').toString();

            final int idx = _filePath.lastIndexOf(ciPath);
            if (idx < 0)  {
                ret = null;
            } else  {
                final int idxStart = idx + ciPath.length();
                final int idxFile  = _filePath.lastIndexOf('/');
                if (idxFile < idxStart)  {
                    ret = null;
                } else  {
                    ret = _filePath.substring(idxStart, idxFile);
                }
            }
        }
        return ret;
    }

    /**
     * Calculates the file name for type definition {@code _typeDef} with name
     * {@code _mxName}. The file name is a concatenation of the defined file
     * prefix within the type definition, the name of the MX object and the
     * file suffix within the type definition. All special characters are
     * converted automatically from
     * {@link FileUtils_mxJPO#encodeFileName(String)}.
     *
     * @param _typeDef      type definition
     * @param _mxName       name of ci object
     * @return file name of this administration (business) object
     */
    public static String calcCIFileName(final TypeDef_mxJPO _typeDef,
                                        final String _mxName)
    {
        final StringBuilder ret = new StringBuilder();
        if (_typeDef.getFilePrefix() != null)  {
            ret.append(_typeDef.getFilePrefix());
        }
        ret.append(_mxName);
        if (_typeDef.getFileSuffix() != null)  {
            ret.append(_typeDef.getFileSuffix());
        }
        return FileUtils_mxJPO.encodeFileName(ret.toString());
    }

    /**
     * Reads for given file the content and returns them.
     *
     * @param _file     file used to read
     * @return read content of the file
     * @throws UpdateException_mxJPO if the file could not be opened or read
     */
    public static String readFileToString(final File _file)
        throws UpdateException_mxJPO
    {
        // read code
        final StringBuilder code = new StringBuilder();
        try  {
            BufferedReader reader = null;
            try  {
                reader = new BufferedReader(new FileReader(_file));
            } catch (final FileNotFoundException e)  {
                throw new UpdateException_mxJPO(ErrorKey.UTIL_FILEUTILS_READ_FILE_NOT_EXISTS, _file);
            }
            String line = reader.readLine();
            while (line != null)  {
                code.append(line).append('\n');
                line = reader.readLine();
            }
            if (reader != null)  {
                reader.close();
            }
        } catch (final IOException e)  {
            throw new UpdateException_mxJPO(ErrorKey.UTIL_FILEUTILS_READ_FILE_UNEXPECTED, _file, e.getMessage());
        }

        return code.toString();
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
     * @see #decodeFileName(String)
     */
    public static String encodeFileName(final String _name)
    {
        final StringBuilder ret = new StringBuilder();
        if (_name != null)  {
            for (final char ch : _name.toCharArray()) {
                if (ch == '@')  {
                    ret.append("@@");
                } else if (((ch < '(') || (ch > ')'))
                        && ((ch < '+') || (ch > '.'))
                        && ((ch < '0') || (ch > '9'))
                        && ((ch < 'A') || (ch > 'Z'))
                        && ((ch < 'a') || (ch > 'z'))
                        && (ch != ' ') && (ch != '=') && (ch != '_'))  {

                    final String hex = String.valueOf(Integer.toHexString(ch));
                    ret.append('@');
                    switch (hex.length())  {
                        case 1:
                            ret.append('0').append(hex);
                            break;
                        case 3:
                            ret.append("u0").append(hex);
                            break;
                        case 4:
                            ret.append('u').append(hex);
                            break;
                        default:
                            ret.append(hex);
                            break;
                    }
                } else  {
                    ret.append(ch);
                }
            }
        }
        return ret.toString();
    }

    /**
     * <p>Encodes the given file name back to internal used names. This must
     * be done because some characters could not handled correctly from the
     * file system.</p>
     * <p>The same characters described in {@link FileUtils_mxJPO#decodeFileName(String)}
     * are converted back.</p>
     *
     * @param _fileName     name from file to convert
     * @return converted name extracted from a file name
     * @throws UpdateException_mxJPO if the configuration item name could not
     *                               be extracted from the file name
     *                               <code>_fileName</code> because the encoded
     *                               special characters are not in correct
     *                               format
     * @see FileUtils_mxJPO#decodeFileName(String)
     */
     public static String decodeFileName(final String _fileName)
        throws UpdateException_mxJPO
    {
        final StringBuilder ret = new StringBuilder();
        if (_fileName != null)  {
            final char[] charFileName = _fileName.toCharArray();
            for (int idx = 0; idx < charFileName.length; idx++)  {
                final char ch = charFileName[idx];
                if (ch == '@')  {
                    switch (FileUtils_mxJPO.decodeFileNameGetChar(charFileName, ++idx))  {
                        case '@':
                            ret.append('@');
                            break;
                        case 'u':
                            final char[] hex4 = new char[4];
                            hex4[0] = FileUtils_mxJPO.decodeFileNameGetChar(charFileName, ++idx);
                            hex4[1] = FileUtils_mxJPO.decodeFileNameGetChar(charFileName, ++idx);
                            hex4[2] = FileUtils_mxJPO.decodeFileNameGetChar(charFileName, ++idx);
                            hex4[3] = FileUtils_mxJPO.decodeFileNameGetChar(charFileName, ++idx);
                            ret.append((char) ((int) Integer.valueOf(new String(hex4), 16)));
                            break;
                        default:
                            final char[] hex2 = new char[2];
                            hex2[0] = FileUtils_mxJPO.decodeFileNameGetChar(charFileName, idx);
                            hex2[1] = FileUtils_mxJPO.decodeFileNameGetChar(charFileName, ++idx);
                            ret.append((char) ((int) Integer.valueOf(new String(hex2), 16)));
                            break;
                    }
                } else  {
                    ret.append(ch);
                }
            }
        }
        return ret.toString();
    }

    /**
     * Returns the character on the position <code>_idx</code> within character
     * array <code>_charFileName</code>. If the position does not exists, an
     * exception will be thrown.
     *
     * @param _charFileName     character array
     * @param _idx              index in the character array
     *                          <code>_charFileName</code>
     * @return the character on index <code>_idx</code> in character array
     *         <code>_charFile</code>
     * @throws UpdateException_mxJPO if the number in <code>_idx</code> is
     *                               greater then the length of the character
     *                               array <code>_charFileName</code> (meaning
     *                               array out of bound exception)
     */
    private static char decodeFileNameGetChar(final char[] _charFileName,
                                                   final int _idx)
        throws UpdateException_mxJPO
    {
        if (_idx >= _charFileName.length)  {
            throw new UpdateException_mxJPO(ErrorKey.UTIL_FILEUTILS_DECODE_FILENAME);
        }
        return _charFileName[_idx];
    }

    /**
     * Calculates the file.
     *
     * @param _ciFile   ci file
     * @param _file     file
     * @return calculated file file
     */
    public static File calcFile(final File _ciFile,
                                final String _file)
    {
        final File ret;
        if (StringUtils_mxJPO.isEmpty(_file))  {
            ret = null;
        } else  {
            // code via file
            final String tmpFile;
            // absolute path?
            if (_file.startsWith("/"))  {
                tmpFile = _file;
            } else  {
                tmpFile = _ciFile.getParent() + "/" + _file;
            }
            ret = new File(tmpFile);
        }
        return ret;
    }
}
