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

package org.mxupdate.test.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.testng.Assert;

/**
 * The class is used to define a command, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Command
    extends AbstractData<Command>
{
    /**
     * All settings of this command.
     *
     * @see #settings
     */
    private final Map<String,String> settings = new HashMap<String,String>();

    /**
     * Constructor to initalize this command.
     *
     * @param _test     related test implementation (where this command is
     *                  defined)
     * @param _name     name of the command
     */
    public Command(final AbstractTest _test,
                   final String _name)
    {
        super(_test, AbstractTest.CI.COMMAND, _name);
    }

    /**
     * Defines a new setting for this command.
     *
     * @param _key      key of the setting
     * @param _value    value of the setting
     * @return this command instance
     * @see #settings
     */
    public Command setSetting(final String _key,
                              final String _value)
    {
        this.settings.put(_key, _value);
        return this;
    }

    /**
     * Creates a this command with all values and settings.
     *
     * @return this command instance
     * @throws MatrixException if create failed
     */
    public Command create()
        throws MatrixException
    {
        final StringBuilder cmd = new StringBuilder()
                .append("escape add command \"" + this.getTest().convertMql(this.getName()) + "\"");
        for (final Map.Entry<String,String> entry : this.getValues().entrySet())  {
            cmd.append(' ').append(entry.getKey()).append(" \"")
               .append(this.getTest().convertMql(entry.getValue()))
               .append('\"');
        }
        for (final Map.Entry<String,String> entry : this.settings.entrySet())  {
            cmd.append(" setting \"").append(this.getTest().convertMql(entry.getKey())).append("\" \"")
               .append(this.getTest().convertMql(entry.getValue()))
               .append('\"');
        }

        this.getTest().mql(cmd);

        return this;
    }

    /**
     * Checks the export of a command if all values are correct defined.
     *
     * @param _exportParser     parsed export
     */
    public void checkExport(final ExportParser _exportParser)
    {
        Assert.assertEquals(_exportParser.getName(),
                            this.getName(),
                            "check name");
        Assert.assertEquals(_exportParser.getSymbolicName(),
                            this.getSymbolicName(),
                            "check symbolic name");
        Assert.assertEquals(_exportParser.getLines("/mql/label/@value").size(),
                            1,
                            "minimum and maximum one label is defined");
        Assert.assertEquals(_exportParser.getLines("/mql/label/@value").get(0),
                            "\"" + this.getTest().convertTcl(this.getValue("label")) + "\"",
                            "label is correct defined");
        Assert.assertEquals(_exportParser.getLines("/mql/description/@value").size(),
                            1,
                            "minimum and maximum one description is defined");
        Assert.assertEquals(_exportParser.getLines("/mql/href/@value").size(),
                            1,
                            "minimum and maximum one href is defined");
        Assert.assertEquals(_exportParser.getLines("/mql/href/@value").get(0),
                            "\"" + this.getTest().convertTcl(this.getValue("href")) + "\"",
                            "href is correct defined");
        final Set<String> needAdds = new HashSet<String>();
        for (final Map.Entry<String,String> entry : this.settings.entrySet())
        {
            needAdds.add("setting \"" + this.getTest().convertTcl(entry.getKey())
                    + "\" \"" + this.getTest().convertTcl(entry.getValue()) +  "\"");
        }
        final List<String> foundAdds = _exportParser.getLines("/mql/add/@value");
        Assert.assertEquals(foundAdds.size(), needAdds.size(), "all adds defined");
        for (final String foundAdd : foundAdds)  {
            Assert.assertTrue(needAdds.contains(foundAdd), "add '" + foundAdd + "' defined");
        }
    }
}
