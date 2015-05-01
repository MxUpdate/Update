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

package org.mxupdate.update.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to export, create, delete and update JPOs within MX.
 *
 * @author The MxUpdate Team
 */
public class JPOProgram_mxJPO
    extends AbstractProgram_mxJPO<JPOProgram_mxJPO>
{
    /** String with name suffix (used also from the extract routine from Matrix). */
    private static final String NAME_SUFFIX = "_" + "mxJPO";

    /** String with name suffix (used also from the extract routine from Matrix). */
    private static final String NAME_SUFFIX_EXTENDSION = JPOProgram_mxJPO.NAME_SUFFIX + ".java";

    private static final int NAME_SUFFIX_EXTENDSION_LENGTH = JPOProgram_mxJPO.NAME_SUFFIX_EXTENDSION.length();

    /**
     * Regular expression for the package line. The package name must be
     * extracted to get the real name of the JPO used within MX.
     */
    private static final Pattern PATTERN_JPO_PACKAGE = Pattern.compile("(?<=package)[ \\t]+[A-Za-z0-9\\._]*[ \\t]*;");

    /**
     * The flag indicates that the back slashes are converted. In older MX
     * versions double back slashes was escaped. In this cases all escaped
     * double back slashes must be replaced. In newer MX versions this
     * 'feature' does not exists anymore if an MQL insert was done.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private boolean backslashUpgraded = false;

    ////////////////////////////////////////////////////////////////////////////
    // global methods start

    /**
     * {@inheritDoc}
     *
     */
    @Override()
    public SortedMap<String,File> evalMatching(final ParameterCache_mxJPO _paramCache,
                                               final Collection<File> _files,
                                               final Collection<String> _matches)
        throws UpdateException_mxJPO
    {
        final SortedMap<String,File> ret = super.evalMatching(_paramCache, _files, _matches);

        for (final File file : _files)  {
            if (file.getName().endsWith(JPOProgram_mxJPO.NAME_SUFFIX_EXTENDSION))  {
                // file identified as JPO

                final String code;
                try {
                    code = this.getCode(file).toString();
                } catch (final IOException e)  {
// TODO: exception handling!
throw new Error("could not open file " + file, e);
                }
                String mxName = file.getName().substring(0, file.getName().length() - JPOProgram_mxJPO.NAME_SUFFIX_EXTENDSION_LENGTH);

                // prefix with package name
                for (final String line : code.split("\n"))  {
                    final Matcher pckMatch = JPOProgram_mxJPO.PATTERN_JPO_PACKAGE.matcher(line);
                    if (pckMatch.find())  {
                        mxName = pckMatch.group().replace(';', ' ').trim() + "." + mxName;
                        break;
                    }
                }

                if (!ret.containsKey(mxName) && this.matchMxName(_paramCache, mxName, _matches))  {
                    ret.put(mxName, file);
                }
            }
        }
        return ret;
    }

    /**
     * Reads for given file the code and returns them.
     *
     * @param _file     file to read the code
     * @return read code of the file
     * @throws IOException if the file could not be opened or read
     */
    private StringBuilder getCode(final File _file)
        throws IOException
    {
        // read code
        final StringBuilder code = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new FileReader(_file));
        String line = reader.readLine();
        while (line != null)  {
            code.append(line).append('\n');
            line = reader.readLine();
        }
        reader.close();

        return code;
    }

    // global methods end
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Constructor used to initialize the type definition enumeration and the
     * name.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the JPO object
     */
    public JPOProgram_mxJPO(final TypeDef_mxJPO _typeDef,
                            final String _mxName)
    {
        super(Kind.JAVA, _typeDef, _mxName);
    }

    /**
     * <p>Parses all common program specific URL values. This includes:
     * <ul>
     * <li>{@link #backslashUpgraded back slash flag} to indicate the they are
     *     upgraded</li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content depending on the URL
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if ("/backslashUpgraded".equals(_url))  {
            this.backslashUpgraded = true;
            parsed = true;
        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * First writes the MXU file and then the JPO itself.
     *
     * @param _paramCache       parameter cache
     * @param _path             path to write through (if required also
     *                          including depending file path defined from the
     *                          information annotation)
     * @throws IOException      if JPO or MXU file can not be written
     * @throws MatrixException  if some MQL statement failed
     * @throws ParseException   if the XML export of the object could not
     *                          parsed (for admin objects)
     */
    @Override()
    public void export(final ParameterCache_mxJPO _paramCache,
                       final File _path)
        throws IOException, MatrixException, ParseException
    {
        try  {
            this.parse(_paramCache);

            if (!this.hasNoValuesDefined(_paramCache) || (this.getCode() == null) || this.getCode().isEmpty())  {
                final File file = new File(_path, this.getFileName());
                if (!file.getParentFile().exists())  {
                    file.getParentFile().mkdirs();
                }
                final Writer out = new FileWriter(file);
                try  {
                    this.write(_paramCache, out);
                    out.flush();
                } finally {
                    out.close();
                }
            }

            if ((this.getCode() != null) && !this.getCode().isEmpty())  {

                // prepare name of JPO to extract
                final int index = this.getName().lastIndexOf('.');
                final String fileName = new StringBuilder()
                        .append((index >= 0) ? this.getName().substring(index + 1) : this.getName())
                        .append(JPOProgram_mxJPO.NAME_SUFFIX_EXTENDSION)
                        .toString();
                // prepare path
                final StringBuilder path = new StringBuilder().append(_path);
                if (index >= 0)  {
                    path.append('/').append(this.getName().substring(0, index).replaceAll("\\.", "/"));
                }

                final File file = new File(path.toString(), fileName);
                if (!file.getParentFile().exists())  {
                    file.getParentFile().mkdirs();
                }
                final Writer out = new FileWriter(file);
                try  {
                    this.write1(_paramCache, out);
                    out.flush();
                } finally {
                    out.close();
                }
            }
        } catch (final IOException e)  {
            if (_paramCache.getValueBoolean(ValueKeys.ParamContinueOnError))  {
                _paramCache.logError(e.toString());
            } else {
                throw e;
            }
        } catch (final MatrixException e)  {
            if (_paramCache.getValueBoolean(ValueKeys.ParamContinueOnError))  {
                _paramCache.logError(e.toString());
            } else {
                throw e;
            }
        } catch (final ParseException e)  {
            if (_paramCache.getValueBoolean(ValueKeys.ParamContinueOnError))  {
                _paramCache.logError(e.toString());
            } else {
                throw e;
            }
        }
    }

    /**
     * Writes given JPO to given path for given name. The JPO code is first
     * converted, because Matrix uses keywords which must be replaced to have
     * real Java code. The conversion works like the original extract method,
     * but only converts the given JPOs and not depending JPOs.
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance to the file where the JPO code must
     *                      be written
     * @throws IOException if the source code could not be written
     * @throws MatrixException if the source code of the JPO could not be
     *                         extracted from MX
     */
    protected void write1(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        // define package name (if points within JPO name)
        final int idx = this.getName().lastIndexOf('.');
        if (idx > 0)  {
            _out.append("package ").append(this.getName().substring(0, idx)).append(";\n");
        }

        // replace class names and references to other JPOs
        final String name = this.getName() + JPOProgram_mxJPO.NAME_SUFFIX;
        final String code = this.getCode()
                                .replaceAll("\\" + "$\\{CLASSNAME\\}", name.replaceAll(".*\\.", ""))
                                .replaceAll("(?<=\\"+ "$\\{CLASS\\:[0-9a-zA-Z_.]{0,200})\\}", JPOProgram_mxJPO.NAME_SUFFIX)
                                .replaceAll("\\" + "$\\{CLASS\\:", "")
                                .trim();

        // for old MX all backslashes are doubled...
        if (!this.backslashUpgraded)  {
            _out.append(code.replaceAll("\\\\\\\\", "\\\\"));
        } else  {
            _out.append(code);
        }
    }


    /**
     * Updates this administration (business) object if the stored information
     * about the version is not the same as the file date. If an update is
     * required, the file is read and the object is updated with
     * {@link #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)}.
     *
     * @param _paramCache   parameter cache
     * @param _create       <i>true</i> if the CI object is new created (and
     *                      first update is done)
     * @param _file         file with TCL update code
     * @throws Exception if the update from the derived class failed
     */
    @Override()
    public void update(final ParameterCache_mxJPO _paramCache,
                       final boolean _create,
                       final File _file)
        throws Exception
    {
        if (_file.getName().endsWith(JPOProgram_mxJPO.NAME_SUFFIX_EXTENDSION))  {
            MqlBuilder_mxJPO.mql().cmd("escape insert program ").arg(_file.toString()).exec(_paramCache);
        } else  {
            super.update(_paramCache, _create, _file);
        }
    }

    /**
     * Compile current JPO.
     *
     * @param _paramCache   parameter cache
     * @return always <i>true</i>
     * @throws Exception if the compile of the JPO failed
     */
    @Override()
    public boolean compile(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        MqlBuilder_mxJPO.mql().cmd("escape compile prog ").arg(this.getName()).exec(_paramCache);
        return true;
    }
}
