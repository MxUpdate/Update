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
import java.io.Writer;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO.AdminPropertyDef;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.xml.sax.SAXException;

import static org.mxupdate.update.util.StringUtil_mxJPO.match;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 * @author tmoxter
 * @version $Id$
 */
public class JPO_mxJPO
        extends AbstractObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -4933461290880123088L;

    /**
     * String with name suffix (used also from the extract routine from
     * Matrix).
     *
     * @see #export(Context, File, String)
     */
    private final static String NAME_SUFFIX = "_" + "mxJPO";

    /**
     * Defines the separator where the TCL update code starts.
     */
    private final static String SEPARATOR_START
            = "################################################################################\n"
            + "# START NEEDED MQL UPDATE FOR THIS JPO PROGRAM                                 #\n"
            + "################################################################################";

    /**
     * Defines the separator where the TCL update code ends.
     */
    private final static String SEPARATOR_END
            = "################################################################################\n"
            + "# END NEEDED MQL UPDATE FOR THIS JPO PROGRAM                                   #\n"
            + "################################################################################";

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
     * Evaluates for given collection of string which JPOs are matching
     * returns them as set.
     *
     * @param _context          context for this request
     * @param _matches          collection of strings which must match
     */
    @Override
    public Set<String> getMatchingNames(final Context _context,
                                        final Collection<String> _matches)
            throws MatrixException
    {
        final StringBuilder cmd = new StringBuilder()
                .append("list program * select name isjavaprogram dump \"\t\"");
        final Set<String> ret = new TreeSet<String>();
        for (final String name : execMql(_context, cmd).split("\n"))  {
            if (!"".equals(name))  {
                final String[] nameArr = name.split("\t");
                if ("TRUE".equals(nameArr[1]))  {
                    for (final String match : _matches)  {
                        if (match(nameArr[0], match))  {
                            ret.add(nameArr[0]);
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Exports given JPO to given path for given name. The JPO code is first
     * converted, because Matrix uses keywords which must be replaced to have
     * real Java code. The conversion works like the original extract method,
     * but only converts the given JPOs and not depending JPOs.
     *
     * @param _context          context for this request
     * @param _path             export path
     * @param _name             name of JPO to export
     */
    @Override
    public void export(final Context _context,
                       final File _path,
                       final String _name)
            throws MatrixException, SAXException, IOException
    {
        final StringBuilder cmd = new StringBuilder()
                .append("print program \"").append(_name).append("\" select code dump");

        // replace class names and references to other JPOs
        final String name = _name + NAME_SUFFIX;
        final String code = execMql(_context, cmd)
                               .replaceAll("\\" + "$\\{CLASSNAME\\}", name.replaceAll(".*\\.", ""))
                               .replaceAll("\\\\\\\\", "\\\\")
                               .replaceAll("(?<=\\"+ "$\\{CLASS\\:[0-9a-zA-Z_.]{0,200})\\}", NAME_SUFFIX)
                               .replaceAll("\\" + "$\\{CLASS\\:", "")
                               .trim();

        // extract package name
        final int idx = _name.lastIndexOf('.');
        final String pck;
        if (idx > 0)  {
            pck = "package " + _name.substring(0, idx) + ";\n";
        } else  {
            pck = "";
        }

        final File file = new File(_path, name.replaceAll("\\.", "/") + ".java");
        if (!file.getParentFile().exists())  {
            file.getParentFile().mkdirs();
        }
        final Writer out = new FileWriter(file);
        out.append(pck)
           .append(code);
        out.flush();
        out.close();
    }

    /**
     * Deletes administration object from given type with given name.
     *
     * @param _context      context for this request
     * @param _name         name of object to delete
     * @throws Exception if delete failed
     */
    @Override
    public void delete(final Context _context,
                       final String _name)
            throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("delete ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(_name).append("\" ")
                .append(this.getTypeDef().getMxAdminSuffix());
        execMql(_context, cmd);
    }

    /**
     * Creates given JPO object from given type with given name.
     *
     * @param _context          context for this request
     * @param _file             file for which the administration object must
     *                          be created (not used)
     * @param _name             name of administration object to create
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
     * is not the same as the the version property:
     * <ul>
     * <li>reset the execute user</li>
     * <li>remove all properties</li>
     * <li>append TCL code of JPO</li>
     * <li>define new version</li>
     * <li>insert JPO code</li>
     * </ul>
     *
     * @param _context          context for this request
     * @param _name             name of the administration (business) object
     * @param _file             reference to the file to update
     * @param _newVersion       new version which must be set within the update
     *                          (or <code>null</code> if the version must not
     *                          be set).
     * @throws Exception if update of the JPO failed
     */
    @Override
    public void update(final Context _context,
                       final String _name,
                       final File _file,
                       final String _newVersion)
            throws Exception
    {
        // append statement to reset execute user
        final StringBuilder cmd = new StringBuilder()
                .append("mod prog \"").append(_name)
                .append("\" execute user \"\";\n");

        // append MQL statements to reset properties
        final String prpStr = execMql(_context,
                                      new StringBuilder().append("print program \"").append(_name)
                                           .append("\" select property.name property.to dump ' @@@@@@'"));
        final String[] prpArr = prpStr.toString().split("(@@@@@@)");
        final int length = (prpArr.length + 1) / 2;
        for (int idxName = 0, idxTo = length; idxName < length; idxName++, idxTo++)  {
            final String name = prpArr[idxName].trim();
            if (AdminPropertyDef.getEnumByPropName(name) == null)  {
// TODO: if to is defined, the remove must be specified the to ....
                final String to = (idxTo < length) ? prpArr[idxTo].trim() : "";
                cmd.append("mod prog \"").append(_name)
                   .append("\" remove property \"").append(name).append("\";\n");
            }
        }

        // not equal => update JPO code and version
        cmd.append("insert prog \"").append(_file.getPath()).append("\";\n");

        // append TCL code of JPO
        final StringBuilder jpoCode = this.getCode(_file);
        final int start = jpoCode.indexOf(SEPARATOR_START);
        final int end = jpoCode.indexOf(SEPARATOR_END);
        if ((start >= 0) && (end > 0))  {
            final String tclCode = jpoCode.substring(start + SEPARATOR_START.length(), end).trim();
            if (!"".equals(tclCode))  {
                cmd.append("tcl;\neval {\n")
                   .append(tclCode)
                   .append("\n}\nexit;\n");
            }
        }

        // define new version
        if (_newVersion != null)  {
            cmd.append("mod prog \"").append(_name)
                            .append("\" add property version value \"").append(_newVersion).append("\";");
        }

        // execute MQL statement
        execMql(_context, cmd);
    }
}
