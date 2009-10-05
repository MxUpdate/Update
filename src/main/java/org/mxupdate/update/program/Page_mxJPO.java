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
        extends AbstractProgram_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -2142094727046919689L;

    /**
     * Constructor used to initialize the type definition enumeration.
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
     * Writes the code from the page to given writer instance.
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
                .append("escape print page \"")
                .append(StringUtil_mxJPO.convertMql(this.getName()))
                .append("\" select content dump");
        _out.append(MqlUtil_mxJPO.execMql(_paramCache, cmd));
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
     * The page is updated if the modified date of the file is not the same
     * as the the version property.
     *
     * @param _paramCache       parameter cache
     * @param _file             reference to the file to update
     * @param _newVersion       new version which must be set within the update
     *                          (or <code>null</code> if the version must not
     *                          be set).
     * @throws Exception if update of the page object failed
     */
    @Override()
    public void update(final ParameterCache_mxJPO _paramCache,
                       final File _file,
                       final String _newVersion)
            throws Exception
    {
        this.parse(_paramCache);

        final StringBuilder cmd = new StringBuilder();

        // append MQL statements to reset properties
        this.appendResetProperties(_paramCache, cmd);

        // pages could not be updated if there are add's in the file name
        // (because special characters are used for the page name)
        if (_file.getPath().indexOf('@') >= 0)  {
            final StringBuilder prgCode = this.getCode(_file);
            final File tempFile = File.createTempFile("MxUpdatePage", ".page");
            try  {
                final FileWriter writer = new FileWriter(tempFile);
                try  {
                    writer.write(prgCode.toString());
                } finally  {
                    writer.close();
                }
                // update code
                cmd.append("escape mod page \"").append(StringUtil_mxJPO.convertMql(this.getName()))
                   .append("\" file \"")
                   .append(StringUtil_mxJPO.convertMql(tempFile.getPath()))
                   .append("\";\n");

                // append standard properties
                this.appendProperties(_paramCache, cmd, _newVersion, _file);

                // and execute alls
                MqlUtil_mxJPO.execMql(_paramCache, cmd);
            } finally  {
                tempFile.delete();
            }
        } else  {
            // update code
            cmd.append("escape mod page \"").append(StringUtil_mxJPO.convertMql(this.getName()))
               .append("\" file \"")
               .append(StringUtil_mxJPO.convertMql(_file.getPath()))
               .append("\";\n");

            // append standard properties
            this.appendProperties(_paramCache, cmd, _newVersion, _file);

            // and execute alls
            MqlUtil_mxJPO.execMql(_paramCache, cmd);
        }
    }
}
