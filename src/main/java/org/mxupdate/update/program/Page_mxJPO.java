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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export, create, delete and update pages within MX.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Page_mxJPO
    extends AbstractCode_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -2142094727046919689L;

    /**
     * Constructor used to initialize the type definition enumeration and the
     * name.
     *
     * @param _typeDef      defines the related type definition enumeration
     * @param _mxName       MX name of the page object
     */
    public Page_mxJPO(final TypeDef_mxJPO _typeDef,
                      final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Searches for all pages and returns this list.
     *
     * @param _paramCache   parameter cache
     * @return set of all pages
     * @throws MatrixException if the &quot;<code>list page</code>&quot;
     *                         failed which is used to evaluate the JPO names
     */
    @Override()
    public Set<String> getMxNames(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        final StringBuilder cmd = new StringBuilder().append("list page *");
        final Set<String> ret = new TreeSet<String>();
        for (final String name : MqlUtil_mxJPO.execMql(_paramCache, cmd).split("\n"))  {
            if (!"".equals(name))  {
                ret.add(name);
            }
        }
        return ret;
    }

    /**
     * Pages could not handle symbolic names. Therefore the original method
     * must be overwritten so that nothing happens.
     *
     * @param _paramCache   parameter cache
     */
    @Override()
    protected void readSymbolicNames(final ParameterCache_mxJPO _paramCache)
    {
    }

    /**
     * At the end of the update of the page the append statements to define
     * symbolic names are not allowed because pages could not handle symbolic
     * names.
     *
     * @param _paramCache   parameter cache - ignored
     * @param _symbName     symbolic name which must be set - ignored
     * @param _mqlCode      string builder where the MQL command must be
     *                      appended - ignored
     */
    @Override()
    protected void appendSymbolicNameRegistration(final ParameterCache_mxJPO _paramCache,
                                                  final String _symbName,
                                                  final StringBuilder _mqlCode)
    {
    }

    /**
     * Writes the code from the page to given writer instance.
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
        _out.append(this.getCode());
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to update this page program. Following
     * steps are done within update:
     * <ul>
     * <li>The program is updated with the content of <code>_sourceFile</code>.
     *     </li>
     * <li>If the file name includes a '@' the file content is copied to a new
     *     file and updated the new file (because of a bug in MX that a MQL
     *     program could not be updated with @ in file names).</li>
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

        // pages could not be updated if there are add's in the file name
        // (because special characters are used for the page name)
        if (_sourceFile.getPath().indexOf('@') >= 0)  {
            final StringBuilder prgCode = this.getCode(_sourceFile);
            final File tempFile = File.createTempFile("MxUpdatePage", ".page");
            try  {
                final FileWriter writer = new FileWriter(tempFile);
                try  {
                    writer.write(prgCode.toString());
                } finally  {
                    writer.close();
                }
                // update code
                preMQLCode.append("escape mod page \"").append(StringUtil_mxJPO.convertMql(this.getName()))
                          .append("\" file \"")
                          .append(StringUtil_mxJPO.convertMql(tempFile.getPath()))
                          .append("\";\n");

                // append already existing pre MQL code
                preMQLCode.append(";\n")
                          .append(_preMQLCode);

                // and update
                super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, null);
            } finally  {
                tempFile.delete();
            }
        } else  {
            // update code
            preMQLCode.append("escape mod page \"").append(StringUtil_mxJPO.convertMql(this.getName()))
                      .append("\" file \"")
                      .append(StringUtil_mxJPO.convertMql(_sourceFile.getPath()))
                      .append("\";\n");

            // append already existing pre MQL code
            preMQLCode.append(";\n")
                      .append(_preMQLCode);

            // and update
            super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, null);
        }
    }
}
