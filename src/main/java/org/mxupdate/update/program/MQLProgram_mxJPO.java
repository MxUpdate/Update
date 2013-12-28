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
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export, create, delete and update MQL programs within
 * MX.
 *
 * @author The MxUpdate Team
 */
public class MQLProgram_mxJPO
    extends AbstractProgram_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for MQL programs.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        MQLProgram_mxJPO.IGNORED_URLS.add("/mqlProgram");
    }

    /**
     * Defines the parameter to define the string where the TCL update code
     * starts.
     *
     * @see #write(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_MARKSTART = "ProgramTclUpdateMarkStart";

    /**
     * Defines the parameter to define the string where the TCL update code
     * ends.
     *
     * @see #write(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_MARKEND = "ProgramTclUpdateMarkEnd";

    /**
     * Defines the parameter to define if embedded TCL update code within
     * programs must be executed.
     *
     * @see #write(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_NEEDED = "ProgramTclUpdateNeeded";

    /**
     * Defines the parameter to define if the embedded TCL update code within
     * programs must be executed.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_REMOVE = "ProgramTclUpdateRemoveInCode";

    /**
     * Defines the parameter which maps from the file extension to the used
     * line prefix.
     *
     * @see #getLinePrefix(ParameterCache_mxJPO, String)
     */
    private static final String PARAM_EXTENSION = "ProgramTclUpdateExtension";

    /**
     * Constructor used to initialize the type definition enumeration and the
     * name.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the program object
     */
    public MQLProgram_mxJPO(final TypeDef_mxJPO _typeDef,
                            final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Searches for all programs which are not JPOs and returns this list.
     *
     * @param _paramCache   parameter cache
     * @return set of all program names (which are not JPOs)
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
                if (!"TRUE".equals(nameArr[1]))  {
                    ret.add(nameArr[0]);
                }
            }
        }
        return ret;
    }

    /**
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
        if (MQLProgram_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Writes the code from the MQL program to given writer instance.
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws MatrixException  if the print of the code of the program failed
     * @throws IOException      if the source code could not be written to the
     *                          writer instance
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException, MatrixException
    {
        final String markStartStr = _paramCache.getValueString(MQLProgram_mxJPO.PARAM_MARKSTART).trim();
        final String markEndStr = _paramCache.getValueString(MQLProgram_mxJPO.PARAM_MARKEND).trim();

        // get line prefix
        final String linePrefix = this.getLinePrefix(_paramCache, this.getName());

        // append to marker the line prefixes
        final String markStart = this.makeLinePrefix(linePrefix, markStartStr);
        final String markEnd = this.makeLinePrefix(linePrefix, markEndStr);

        this.writeUpdateCode(_paramCache, _out, markStart, markEnd, linePrefix);

        // append original code (without old TCL update code)
        final int start = this.getCode().indexOf(markStart);
        final int end = this.getCode().indexOf(markEnd);
        if ((start >= 0) && (end > 0))  {
            _out.append(this.getCode().substring(0, start).trim())
                .append(this.getCode().substring(end + markEnd.length()).trim());
        } else  {
            _out.append(this.getCode());
        }
    }

    /**
     * Depending on the extension of given <code>_fileName</code> the defined
     * line prefix defined with parameter {@link #PARAM_EXTENSION} is returned.
     *
     * @param _paramCache   parameter cache
     * @param _fileName     name of the file with the extension
     * @return related of the extension the found extension
     * @see #PARAM_EXTENSION
     */
    protected String getLinePrefix(final ParameterCache_mxJPO _paramCache,
                                   final String _fileName)
    {
        final Map<String,String> extensions = _paramCache.<String>getValueMap(MQLProgram_mxJPO.PARAM_EXTENSION);
        final int fileExtIdx = _fileName.lastIndexOf('.');
        final String fileExtension = (fileExtIdx >= 0) ? _fileName.substring(fileExtIdx) : null;
        return extensions.get(fileExtension);
    }

    /**
     * Creates given program object from given type with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if the program could not be created
     */
    @Override()
    public void create(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("escape add ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"');
        MqlUtil_mxJPO.execMql(_paramCache, cmd);
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to update this MQL program. Following
     * steps are done within update:
     * <ul>
     * <li>An existing execute user is removed.</li>
     * <li>Execution of the JPO is immediate.</li>
     * <li>The description is set to an empty string.</li>
     * <li>The MQL program is set to not hidden.</li>
     * <li>The MQL program does not need the context of a business object.</li>
     * <li>The MQL program is not downloadable.</li>
     * <li>The input / output of the MQL program is not piped.</li>
     * <li>The MQL program is not pooled (used only for TCL programs).</li>
     * <li>The program is updated with the content of <code>_sourceFile</code>.
     *     </li>
     * <li>If the file name includes a '@' the file content is copied to a new
     *     file and updated the new file (because of a bug in MX that a MQL
     *     program could not be updated with @ in file names).</li>
     * <li>If the code includes embedded TCL update code, the code is written
     *     in a new file if parameter {@link #PARAM_REMOVE} is
     *     <i>true</i>.</li>
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
     * @see #PARAM_EXTENSION
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
        // get parameters
        final String markStartStr = _paramCache.getValueString(MQLProgram_mxJPO.PARAM_MARKSTART).trim();
        final String markEndStr = _paramCache.getValueString(MQLProgram_mxJPO.PARAM_MARKEND).trim();
        final boolean exec = _paramCache.getValueBoolean(MQLProgram_mxJPO.PARAM_NEEDED);

        // get line prefix and length of line prefix
        final String linePrefix = this.getLinePrefix(_paramCache, _sourceFile.getName());

        // append to marker the line prefixes
        final String markStart = this.makeLinePrefix(linePrefix, markStartStr);
        final String markEnd = this.makeLinePrefix(linePrefix, markEndStr);

        File tempFile = null;
        try  {
            final StringBuilder preMQLCode = new StringBuilder();
            final StringBuilder preTCLCode = new StringBuilder();

            // append TCL code of file
            final String code = this.extractTclUpdateCode(_paramCache,
                                                          preTCLCode,
                                                          exec,
                                                          this.getCode(_sourceFile),
                                                          markStart,
                                                          markEnd,
                                                          linePrefix);

            // write new code
            if (((code != null) && _paramCache.getValueBoolean(MQLProgram_mxJPO.PARAM_REMOVE))
                    || _sourceFile.getPath().contains("@"))  {

                tempFile = File.createTempFile("MXUPDATE", "TMP");

                FileWriter out = null;
                try  {
                    out = new FileWriter(tempFile);
                    if ((code != null) && _paramCache.getValueBoolean(MQLProgram_mxJPO.PARAM_REMOVE))  {
                        out.write(code.trim());
                    } else  {
                        out.write(this.getCode().trim());
                    }
                } finally  {
                    if (out != null)  {
                        out.close();
                    }
                }
            }

            // update code; reset execute user
            preMQLCode.append("escape mod prog \"").append(StringUtil_mxJPO.convertMql(this.getName()))
                      .append("\" execute user \"\" execute immediate !needsbusinessobject !downloadable !pipe !pooled description \"\" !hidden file \"");
            if (tempFile != null)  {
                preMQLCode.append(StringUtil_mxJPO.convertMql(tempFile.getPath()));
            } else  {
                preMQLCode.append(StringUtil_mxJPO.convertMql(_sourceFile.getPath()));
            }
            preMQLCode.append("\";\n");

            // append already existing pre MQL code
            preMQLCode.append(";\n")
                      .append(_preMQLCode);

            // append procedure to order fields of the form
            preTCLCode.append('\n')
                      .append(_preTCLCode);

            // and update
            super.update(_paramCache, preMQLCode, _postMQLCode, preTCLCode, _tclVariables, null);
        } finally  {
            if (tempFile != null)  {
                tempFile.delete();
            }
        }
    }
}
