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
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.match;

/**
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.Path_mxJPO("jpo")
@net.sourceforge.mxupdate.update.util.TagName_mxJPO("jpo")
public class JPO_mxJPO
        extends net.sourceforge.mxupdate.update.MatrixObject_mxJPO
{

    @Override
    protected String getExportMQL(String _name)
    {
        return null;
    }

    @Override
    public String getFileName()
    {
        return null;
    }

    @Override
    public Set<String> getMatchingNames(final Context _context,
                                        final Collection<String> _matches)
            throws MatrixException
    {
        final MQLCommand mql = new MQLCommand();
        final StringBuilder cmd = new StringBuilder()
                .append("list program * select name isjavaprogram dump \"\t\"");
        mql.executeCommand(_context, cmd.toString().trim());
        final Set<String> ret = new TreeSet<String>();
        for (final String name : mql.getResult().split("\n"))  {
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

    @Override
    public void export(final Context _context,
                       final File _path,
                       final String _name)
            throws MatrixException, SAXException, IOException
    {
        final MQLCommand mql = new MQLCommand();
        final StringBuilder cmd = new StringBuilder()
                .append("print program \"").append(_name).append("\" select code dump");
        mql.executeCommand(_context, cmd.toString().trim());

final String name = _name + "_"+"mxJPO";
final String code = mql.getResult()
                       .replaceAll("\\$\\{CLASSNAME\\}", name.replaceAll(".*\\.", ""))
                       .replaceAll("\\\\\\\\", "\\\\")
                       .replaceAll("(?<=\\$\\{CLASS\\:[0-9a-zA-Z_.]{0,200})\\}", "_" + "mxJPO")
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

    @Override
    protected void write(final Writer _out)
            throws IOException
    {

    }

    @Override
    protected void prepare(Context _context) throws MatrixException
    {
    }

}
