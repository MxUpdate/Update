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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to export, create, delete and update JPOs within MX.
 *
 * @author The MxUpdate Team
 */
public class JPOProgram_mxJPO
    extends AbstractProgram_mxJPO<JPOProgram_mxJPO>
{
    /**
     * Set of all ignored URLs from the XML definition for JPO programs.
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        JPOProgram_mxJPO.IGNORED_URLS.add("/javaProgram");
    }

    /**
     * String with name suffix (used also from the extract routine from
     * Matrix).
     *
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private static final String NAME_SUFFIX = "_" + "mxJPO";

    /**
     * Defines the parameter to define the string where the TCL update code
     * starts.
     *
     * @see #write(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_MARKSTART = "JPOTclUpdateMarkStart";

    /**
     * Defines the parameter to define the string where the TCL update code
     * ends.
     *
     * @see #write(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_MARKEND = "JPOTclUpdateMarkEnd";

    /**
     * Defines the parameter to define if embedded TCL update code within JPOs
     * must be executed.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_NEEDED = "JPOTclUpdateNeeded";

    /**
     * Regular expression for the package line. The package name must be
     * extracted to get the real name of the JPO used within MX.
     *
     * @see #extractMxName(ParameterCache_mxJPO, File)
     */
    private static final Pattern PATTERN_PACKAGE = Pattern.compile("(?<=package)[ \\t]+[A-Za-z0-9\\._]*[ \\t]*;");

    /**
     * Used line prefix for the TCL update code.
     *
     * @see #write(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String LINE_PREFIX = "//";

    /**
     * The flag indicates that the back slashes are converted. In older MX
     * versions double back slashes was escaped. In this cases all escaped
     * double back slashes must be replaced. In newer MX versions this
     * 'feature' does not exists anymore if an MQL insert was done.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private boolean backslashUpgraded = false;

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
        super(_typeDef, _mxName);
    }

    /**
     * Searches for all programs which are JPOs and returns this list.
     *
     * @param _paramCache   parameter cache
     * @return set of all JPO program names
     * @throws MatrixException if the &quot;<code>list program</code>&quot;
     *                         failed which is used to evaluate the JPO names
     */
    @Override()
    public Set<String> getMxNames(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        final StringBuilder cmd = new StringBuilder()
                .append("list program * select name isjavaprogram dump \"\t\"");
        final Set<String> ret = new TreeSet<String>();
        for (final String name : MqlUtil_mxJPO.execMql(_paramCache, cmd).split("\n"))  {
            if (!"".equals(name))  {
                final String[] nameArr = name.split("\t");
                if ("TRUE".equals(nameArr[1]))  {
                    ret.add(nameArr[0]);
                }
            }
        }
        return ret;
    }

    /**
     * Returns the file name for this JPO. The original method is overwritten
     * because the name of a JPO could include points ('.') in a name which
     * defines a package of a JPO (and is not included in the file name
     * itself).
     *
     * @return file name of this administration (business) object (without
     *         package names)
     */
    @Override()
    public String getFileName()
    {
        final int index = this.getName().lastIndexOf('.');
        return new StringBuilder()
                .append((index >= 0)
                        ? this.getName().substring(index + 1)
                        : this.getName())
                .append(this.getTypeDef().getFileSuffix())
                .toString();
    }

    /**
     * Returns the path where the file is located of this JPO including the sub
     * path of the JPO package. A JPO has a package if the JPO name includes
     * points ('.').
     *
     * @return sub path including package path
     * @see #getTypeDef()
     */
    @Override()
    public String getPath()
    {
        final StringBuilder ret = new StringBuilder().append(super.getPath());
        final int index = this.getName().lastIndexOf('.');
        if (index >= 0)  {
            ret.append('/').append(this.getName().substring(0, index).replaceAll("\\.", "/"));
        }
        return ret.toString();
    }

    /**
     * If a file is a JPO (checked by calling the extraxtMxName method from
     * super class), the package is extracted from file and returned together
     * with the extracted MxName from the file.
     *
     * @param _paramCache   parameter cache
     * @param _file         file for which the MX name is searched
     * @return MX name or <code>null</code> if the file is not an update file
     *         for current type definition
     * @throws UpdateException_mxJPO if the JPO name could not be extracted
     *                               from the file name
     * @see #PATTERN_PACKAGE
     * @todo idea: maybe performance improvement by opening file itself and
     *       read only till class, interface or enum is defined....
     */
    @Override()
    public String extractMxName(final ParameterCache_mxJPO _paramCache,
                                final File _file)
        throws UpdateException_mxJPO
    {
        String mxName = super.extractMxName(_paramCache, _file);

        if (mxName != null)  {
            final String code;
            try {
                code = this.getCode(_file).toString();
            } catch (final IOException e)  {
                throw new Error("could not open file " + _file, e);
            }
            for (final String line : code.split("\n"))  {
                final Matcher pckMatch = JPOProgram_mxJPO.PATTERN_PACKAGE.matcher(line);
                if (pckMatch.find())  {
                    mxName = pckMatch.group().replace(';', ' ').trim() + "." + mxName;
                    break;
                }
            }
        }
        return mxName;
    }

    /**
     * Dummy implementation because not used.
     */
    @Override()
    public void parseUpdate(final String _code)
    {
        throw new Error("not supported");
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
     * @see #IGNORED_URLS
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (JPOProgram_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/backslashUpgraded".equals(_url))  {
            this.backslashUpgraded = true;
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
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
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException, MatrixException
    {
        final String markStart = this.makeLinePrefix(JPOProgram_mxJPO.LINE_PREFIX, _paramCache.getValueString(JPOProgram_mxJPO.PARAM_MARKSTART).trim());
        final String markEnd = this.makeLinePrefix(JPOProgram_mxJPO.LINE_PREFIX, _paramCache.getValueString(JPOProgram_mxJPO.PARAM_MARKEND).trim());

        this.writeUpdateCode(_paramCache, _out, markStart, markEnd, JPOProgram_mxJPO.LINE_PREFIX);

        // define package name (if points within JPO name)
        final int idx = this.getName().lastIndexOf('.');
        if (idx > 0)  {
            _out.append("package ")
                .append(this.getName().substring(0, idx))
                .append(";\n");
        }

        // get original code (without old TCL update code)
        final int start = this.getCode().indexOf(markStart);
        final int end = this.getCode().indexOf(markEnd);
        final String origCode;
        if ((start >= 0) && (end > start))  {
            origCode = new StringBuilder()
                    .append(this.getCode().substring(0, start).trim())
                    .append(this.getCode().substring(end + markEnd.length()).trim())
                    .toString();
        } else  {
            origCode = this.getCode();
        }

        // replace class names and references to other JPOs
        final String name = this.getName() + JPOProgram_mxJPO.NAME_SUFFIX;
        final String code = origCode
                                .replaceAll("\\" + "$\\{CLASSNAME\\}", name.replaceAll(".*\\.", ""))
                                .replaceAll("(?<=\\"+ "$\\{CLASS\\:[0-9a-zA-Z_.]{0,200})\\}",
                                            JPOProgram_mxJPO.NAME_SUFFIX)
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
     * Creates given JPO object from given type with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if create of JPO failed
     */
    @Override()
    public void create(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("add ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append("\" java");
        MqlUtil_mxJPO.execMql(_paramCache, cmd);
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to update this JPO program. Following
     * steps are done within update:
     * <ul>
     * <li>An existing execute user is removed.</li>
     * <li>Execution of the JPO is immediate.</li>
     * <li>The description is set to an empty string.</li>
     * <li>The JPO program is set to not hidden.</li>
     * <li>The JPO program does not need the context of a business object.</li>
     * <li>The MQL program is not downloadable.</li>
     * <li>The input / output of the MQL program is not piped.</li>
     * <li>The MQL program is not pooled (used only for TCL programs).</li>
     * <li>The JPO is updated with the content of <code>_sourceFile</code>.
     *     </li>
     * <li>Embedded TCL update code is searched depending on the file extension
     *     and the TCL update needed parameter {@link #PARAM_NEEDED}. If for a
     *     file extension a line prefix is defined, the line prefix is removed
     *     from the TCL update code (and also the mark texts defined with
     *     {@link #PARAM_MARKSTART} and {@link #PARAM_MARKEND} depends on the
     *     line prefix).</li>
     * </ul>
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     * @see #PARAM_MARKEND
     * @see #PARAM_MARKSTART
     * @see #PARAM_NEEDED
     */
    @Override()
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
        throws Exception
    {
        final StringBuilder preMQLCode = new StringBuilder();
        final StringBuilder preTCLCode = new StringBuilder();

        // get parameters
        final String markStart = _paramCache.getValueString(JPOProgram_mxJPO.PARAM_MARKSTART).trim();
        final String markEnd = _paramCache.getValueString(JPOProgram_mxJPO.PARAM_MARKEND).trim();
        final String markStartWithPrefix = this.makeLinePrefix(JPOProgram_mxJPO.LINE_PREFIX, markStart);
        final String markEndWithPrefix = this.makeLinePrefix(JPOProgram_mxJPO.LINE_PREFIX, markEnd);
        final boolean exec = _paramCache.getValueBoolean(JPOProgram_mxJPO.PARAM_NEEDED);

        // update JPO code; reset execute user + description + hidden flag
        preMQLCode.append("escape mod prog \"").append(StringUtil_mxJPO.convertMql(this.getName()))
                  .append("\" execute user \"\" execute immediate !needsbusinessobject !downloadable !pipe !pooled description \"\" !hidden;\n")
                  .append("insert prog \"").append(_sourceFile.getPath()).append("\";\n");

        // append TCL code of file (first without line prefix, then with)
        final StringBuilder jpoCode = this.getCode(_sourceFile);
        this.extractTclUpdateCode(_paramCache,
                                  preTCLCode,
                                  exec,
                                  jpoCode,
                                  markStart,
                                  markEnd,
                                  null);
        this.extractTclUpdateCode(_paramCache,
                                  preTCLCode,
                                  exec,
                                  jpoCode,
                                  markStartWithPrefix,
                                  markEndWithPrefix,
                                  JPOProgram_mxJPO.LINE_PREFIX);

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        // append procedure to order fields of the form
        preTCLCode.append('\n')
                  .append(_preTCLCode);

        // and update
        super.update(_paramCache, preMQLCode, _postMQLCode, preTCLCode, _tclVariables, null);
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
        MqlUtil_mxJPO.execMql(_paramCache.getContext(), new StringBuilder()
                              .append("escape compile prog \"")
                              .append(StringUtil_mxJPO.convertMql(this.getName()))
                              .append('\"'),
                              false);
        return true;
    }


    /**
     * Dummy implementation because not used.
     */
    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final JPOProgram_mxJPO _current)
    {
        throw new Error("not supported");
    }
}
