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

package org.mxupdate.update.program;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.util.MqlUtil_mxJPO;

/**
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Program_mxJPO
        extends AbstractProgram_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -3329894042318127257L;

    /**
     * Defines the parameter to define the string where the TCL update code
     * starts.
     *
     * @see #update(ParameterCache_mxJPO, File, String)
     */
    private static final String PARAM_MARKSTART = "ProgramTclUpdateMarkStart";

    /**
     * Defines the parameter to define the string where the TCL update code
     * ends.
     *
     * @see #update(ParameterCache_mxJPO, File, String)
     */
    private static final String PARAM_MARKEND = "ProgramTclUpdateMarkEnd";

    /**
     * Defines the parameter to define if embedded TCL update code within
     * programs must be executed.
     *
     * @see #update(ParameterCache_mxJPO, File, String)
     */
    private static final String PARAM_NEEDED = "ProgramTclUpdateNeeded";

    /**
     *
     *
     * @see #update(ParameterCache_mxJPO, File, String)
     */
    private static final String PARAM_EXTENSION = "ProgramTclUpdateExtension";

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the program object
     */
    public Program_mxJPO(final TypeDef_mxJPO _typeDef,
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
    @Override
    public Set<String> getMxNames(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        final StringBuilder cmd = new StringBuilder()
                .append("list program * select name isjavaprogram dump \"\t\"");
        final Set<String> ret = new TreeSet<String>();
        for (final String name : MqlUtil_mxJPO.execMql(_paramCache.getContext(), cmd).split("\n"))  {
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
     * Writes the code from the program to given writer instance.
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws MatrixException  if the print of the code of the program failed
     * @throws IOException      if the source code could not be written to the
     *                          writer instance
     */
    @Override
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
            throws IOException, MatrixException
    {
        final StringBuilder cmd = new StringBuilder()
                .append("print program \"").append(this.getName()).append("\" select code dump");
        _out.append(MqlUtil_mxJPO.execMql(_paramCache.getContext(), cmd));
    }

    /**
     * Creates given program object from given type with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if the program could not be created
     */
    @Override
    public void create(final ParameterCache_mxJPO _paramCache)
            throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("add ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append('\"');
        MqlUtil_mxJPO.execMql(_paramCache.getContext(), cmd);
    }

    /**
     * The program is updated if the modified date of the file is not the same
     * as the the version property. Embedded TCL update code is searched
     * depending on the file extension and the TCL update needed parameter
     * {@link #PARAM_NEEDED}. If for a file extension a line prefix is defined,
     * the line prefix is removed from the TCL update code (and also the mark
     * texts defined with {@link #PARAM_MARKSTART} and {@link #PARAM_MARKEND}
     * depends on the line prefix).
     *
     * @param _paramCache       parameter cache
     * @param _file             reference to the file to update
     * @param _newVersion       new version which must be set within the update
     *                          (or <code>null</code> if the version must not
     *                          be set).
     * @throws Exception if update of the program failed
     * @see #PARAM_EXTENSION
     * @see #PARAM_MARKEND
     * @see #PARAM_MARKSTART
     * @see #PARAM_NEEDED
     */
    @Override
    public void update(final ParameterCache_mxJPO _paramCache,
                       final File _file,
                       final String _newVersion)
            throws Exception
    {
        this.parse(_paramCache);

        // get parameters
        final String markStartStr = _paramCache.getValueString(Program_mxJPO.PARAM_MARKSTART).trim();
        final String markEndStr = _paramCache.getValueString(Program_mxJPO.PARAM_MARKEND).trim();
        final boolean exec = _paramCache.getValueBoolean(Program_mxJPO.PARAM_NEEDED);
        final Map<String,String> extensions = _paramCache.<String>getValueMap(Program_mxJPO.PARAM_EXTENSION);

        // append to marker the line prefixes
        final String fileExtension = _file.getName().substring(_file.getName().lastIndexOf('.'));
        final String linePrefix = extensions.get(fileExtension);
        final int linePrefixLength = (linePrefix != null) ? linePrefix.length() : -1;
        final String markStart;
        final String markEnd;
        if (linePrefixLength > 0)  {
            final StringBuilder markStartBld = new StringBuilder();
            for (final String line : markStartStr.split("\n"))  {
                markStartBld.append(linePrefix).append(line).append('\n');
            }
            markStart = markStartBld.toString();

            final StringBuilder markEndBld = new StringBuilder();
            for (final String line : markEndStr.split("\n"))  {
                markEndBld.append(linePrefix).append(line).append('\n');
            }
            markEnd = markEndBld.toString();
        } else  {
            markStart = markStartStr;
            markEnd = markEndStr;
        }

        // update code
        final StringBuilder cmd = new StringBuilder()
                .append("mod prog \"").append(this.getName())
                        .append("\" file \"").append(_file.getPath()).append("\";\n");

        // append TCL code of file
        final StringBuilder prgCode = this.getCode(_file);
        final int start = prgCode.indexOf(markStart);
        final int end = prgCode.indexOf(markEnd);
        if ((start >= 0) && (end > 0))  {
            final String tclCode = prgCode.substring(start + markStart.length(), end).trim();
            if (!"".equals(tclCode))  {
                // TCL code must be executed only if allowed
                // and line prefix is defined
                if (exec && (linePrefixLength >= 0))  {
                    _paramCache.logTrace("    - TCL update code is executed");
                    cmd.append("tcl;\neval {\n");
                    // remove line prefixes from TCL code (if defined)
                    if (linePrefixLength > 0)  {
                        for (final String line : tclCode.split("\n"))  {
                            cmd.append(line.substring(linePrefixLength)).append('\n');
                        }
                    } else  {
                        cmd.append(tclCode);
                    }
                    cmd.append("\n}\nexit;\n");
                } else  {
                    _paramCache.logError("    - Warning! Existing TCL update code is not executed!");
                }
            }
        }

        // and update
        this.update(_paramCache, cmd, _newVersion, _file);
    }
}
