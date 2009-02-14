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
import java.io.Writer;
import java.util.Set;
import java.util.TreeSet;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 * The class is used to export, create, delete and update JPOs within MX.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class JPO_mxJPO
        extends AbstractProgram_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -4933461290880123088L;

    /**
     * The variable is used to evaluate if MX doubles backslashes (done from
     * old MX versions). The name of the variable and the variable definition
     * must not be changed or formatted in another way. Otherwise the test for
     * doubled backslashes fails....
     *
     * @see #write(ParameterCache_mxJPO, Writer)
     */
    @SuppressWarnings("unused")
    private static final String JPO_REPLACE_TEST = "\\";

    /**
     * String with name suffix (used also from the extract routine from
     * Matrix).
     *
     * @see #export(Context, File, String)
     */
    private static final String NAME_SUFFIX = "_" + "mxJPO";

    /**
     * Defines the parameter to cache the information if the backslashes within
     * MX are doubled (old MX versions) or not (new MX versions).
     *
     * @see #write(ParameterCache_mxJPO, Writer)
     */
    private static final String PARAM_BACKSLASHDOUBLED = "JPOBackslashDoubled";

    /**
     * Defines the parameter to define the string where the TCL update code
     * starts.
     *
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String PARAM_MARKSTART = "JPOTclUpdateMarkStart";

    /**
     * Defines the parameter to define the string where the TCL update code
     * ends.
     *
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String PARAM_MARKEND = "JPOTclUpdateMarkEnd";

    /**
     * Defines the parameter to define if embedded TCL update code within JPOs
     * must be executed.
     *
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String PARAM_NEEDED = "JPOTclUpdateNeeded";

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     */
    public JPO_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        super(_typeDef);
    }

    /**
     * Searches for all programs which are JPOs and returns this list.
     *
     * @param _paramCache   parameter cache
     * @return set of all JPO program names
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
        for (final String name : execMql(_paramCache.getContext(), cmd).split("\n"))  {
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
     * Returns the file name for this JPO.
     *
     * @return file name of this administration (business) object
     */
    @Override
    protected String getFileName()
    {
        return new StringBuilder()
                .append(this.getName().replaceAll("\\.", "/"))
                .append(this.getTypeDef().getFileSuffix())
                .toString();
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
    @Override
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Writer _out)
            throws IOException, MatrixException
    {
        // define package name (if points within JPO name)
        final int idx = this.getName().lastIndexOf('.');
        if (idx > 0)  {
            _out.append("package ")
                .append(this.getName().substring(0, idx))
                .append(";\n");
        }

        // old MX style or new? (means backslashes are doubled...)
        final Boolean backslashDoubledVal = _paramCache.getValueBoolean(PARAM_BACKSLASHDOUBLED);
        final boolean backslashDoubled;
        if (backslashDoubledVal == null)  {
            final String code = execMql(_paramCache.getContext(),
                                        "print prog org.mxupdate.update.program.JPO select code dump");
            final int start = code.indexOf("JPO_REPLACE_TEST");
            final int end = code.indexOf('\n', start);
            backslashDoubled = code.substring(start + 20, end - 2).length() == 4;
            _paramCache.defineValueBoolean(PARAM_BACKSLASHDOUBLED, backslashDoubled);
        } else  {
            backslashDoubled = backslashDoubledVal;
        }

        // replace class names and references to other JPOs
        final String name = this.getName() + NAME_SUFFIX;
        final StringBuilder cmd = new StringBuilder()
                .append("print program \"").append(this.getName()).append("\" select code dump");
        final String code = execMql(_paramCache.getContext(), cmd)
                                .replaceAll("\\" + "$\\{CLASSNAME\\}", name.replaceAll(".*\\.", ""))
                                .replaceAll("(?<=\\"+ "$\\{CLASS\\:[0-9a-zA-Z_.]{0,200})\\}", NAME_SUFFIX)
                                .replaceAll("\\" + "$\\{CLASS\\:", "")
                                .trim();

        // for old MX all backslashes are doubled...
        if (backslashDoubled)  {
            _out.append(code.replaceAll("\\\\\\\\", "\\\\"));
        } else  {
            _out.append(code);
        }
    }

    /**
     * Creates given JPO object from given type with given name.
     *
     * @param _context          context for this request
     * @param _file             file for which the administration object must
     *                          be created (not used)
     * @param _name             name of administration object to create
     * @throws Exception if create of JPO failed
     */
    @Override
    public void create(final Context _context,
                       final File _file,
                       final String _name)
            throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("add ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(_name).append("\" java");
        execMql(_context, cmd);
    }

    /**
     * The JPO is updated by following steps if the modified date of the file
     * is not the same as the the version property. Following steps are done:
     * <ul>
     * <li>reset the execute user</li>
     * <li>remove all properties</li>
     * <li>append TCL code of JPO</li>
     * <li>define new version</li>
     * <li>insert JPO code</li>
     * </ul>
     *
     * @param _paramCache       parameter cache
     * @param _name             name of the administration (business) object
     * @param _file             reference to the file to update
     * @param _newVersion       new version which must be set within the update
     *                          (or <code>null</code> if the version must not
     *                          be set).
     * @throws Exception if update of the JPO failed
     */
    @Override
    public void update(final ParameterCache_mxJPO _paramCache,
                       final String _name,
                       final File _file,
                       final String _newVersion)
            throws Exception
    {
        this.parse(_paramCache, _name);

        // get parameters
        final String markStart = _paramCache.getValueString(PARAM_MARKSTART).trim();
        final String markEnd = _paramCache.getValueString(PARAM_MARKEND).trim();
        final boolean exec = _paramCache.getValueBoolean(PARAM_NEEDED);

        final StringBuilder cmd = new StringBuilder();

        // update JPO code
        cmd.append("insert prog \"").append(_file.getPath()).append("\";\n");

        // append TCL code of JPO
        final StringBuilder jpoCode = this.getCode(_file);
        final int start = jpoCode.indexOf(markStart);
        final int end = jpoCode.indexOf(markEnd);
        if ((start >= 0) && (end > 0))  {
            final String tclCode = jpoCode.substring(start + markStart.length(), end).trim();
            if (!"".equals(tclCode))  {
                if (exec)  {
                    _paramCache.logTrace("    - TCL update code is executed");
                    cmd.append("tcl;\neval {\n")
                       .append(tclCode)
                       .append("\n}\nexit;\n");
                } else  {
                    _paramCache.logError("    - Warning! Existing TCL update code is not executed!");
                }
            }
        }

        // and update
        this.update(_paramCache, cmd, _newVersion, _file);
    }
}
