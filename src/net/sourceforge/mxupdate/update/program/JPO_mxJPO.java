/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update.program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.xml.sax.SAXException;

import matrix.db.Context;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.match;

/**
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO(title = "",
                                                     filePrefix = "",
                                                     fileSuffix = "_" + "mxJPO.java",
                                                     filePath = "jpo",
                                                     description = "jpo")
public class JPO_mxJPO
        extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO
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
            final String[] nameArr = name.split("\t");
            if ("TRUE".equals(nameArr[1]))  {
                for (final String match : _matches)  {
                    if (match(nameArr[0], match))  {
                        ret.add(nameArr[0]);
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

        final String name = _name + NAME_SUFFIX;
        final String code = execMql(_context, cmd)
                               .replaceAll("\\$\\{CLASSNAME\\}", name.replaceAll(".*\\.", ""))
                               .replaceAll("\\\\\\\\", "\\\\")
                               .replaceAll("(?<=\\$\\{CLASS\\:[0-9a-zA-Z_.]{0,200})\\}", NAME_SUFFIX)
                               .replaceAll("\\$\\{CLASS\\:", "")
                               .trim();

        final File file = new File(_path, name.replaceAll("\\.", "/") + ".java");
        if (!file.getParentFile().exists())  {
            file.getParentFile().mkdirs();
        }
        final Writer out = new FileWriter(file);
        out.append(code);
        out.flush();
        out.close();
    }
}
