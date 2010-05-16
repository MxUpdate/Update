/*
 * Copyright 2008-2010 The MxUpdate Team
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

package org.mxupdate.test.data.userinterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;

/**
 * Used to define a menu, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class MenuData
    extends AbstractCommandData<MenuData>
{
    /**
     * List of all children commands / menus.
     *
     * @see #addChild(AbstractCommandData)
     */
    private final List<AbstractCommandData<?>> children = new ArrayList<AbstractCommandData<?>>();

    /**
     * Constructor to initialize this menu.
     *
     * @param _test     related test implementation (where this menu is
     *                  defined)
     * @param _name     name of the menu
     */
    public MenuData(final AbstractTest _test,
                    final String _name)
    {
        super(_test, AbstractTest.CI.UI_MENU, _name);
    }

    /**
     * Adds to this menu instance a child command / menu.
     *
     * @param _child    child to append
     * @return this menu instance
     * @see #children
     */
    public MenuData addChild(final AbstractCommandData<?> _child)
    {
        this.children.add(_child);
        return this;
    }

    /**
     * Prepares the configuration item update file depending on the
     * configuration of this menu.
     *
     * @return code for the configuration item update file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder();
        this.append4CIFileHeader(cmd);
        cmd.append("mql escape mod menu \"${NAME}\"");

        // append hidden flag
        if (this.isHidden() != null)  {
            cmd.append(' ');
            if (!this.isHidden())  {
                cmd.append('!');
            }
            cmd.append("hidden");
        }

        for (final AbstractCommandData<?> child : this.children)  {
            cmd.append(" add ").append(child.getCI().getMxType())
               .append(" \"").append(AbstractTest.convertTcl(child.getName())).append('\"');
        }
        this.append4CIFileValues(cmd);

        return cmd.toString();
    }

    /**
     * Creates a this menu with all values and settings.
     *
     * @return this menu instance
     * @throws MatrixException if create failed
     */
    @Override()
    public MenuData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder()
                    .append("escape add menu \"" + AbstractTest.convertMql(this.getName()) + "\"");

            // append hidden flag
            if (this.isHidden() != null)  {
                cmd.append(' ');
                if (!this.isHidden())  {
                    cmd.append('!');
                }
                cmd.append("hidden");
            }

            this.append4Create(cmd);
            cmd.append(';');

            // append all child command / menus
            if (!this.children.isEmpty())  {
                cmd.append("escape mod menu \"" + AbstractTest.convertMql(this.getName()) + "\"");
                for (final AbstractCommandData<?> child : this.children)  {
                    cmd.append(" add ").append(child.getCI().getMxType())
                       .append(" \"").append(AbstractTest.convertMql(child.getName())).append('\"');
                }
                cmd.append(';');
            }

            cmd.append(";\n")
               .append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to menu \"").append(AbstractTest.convertMql(this.getName())).append("\"");

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * Creates all child commands / menus.
     *
     * @see #children
     */
    @Override()
    public MenuData createDependings()
        throws MatrixException
    {
        super.createDependings();

        for (final AbstractCommandData<?> child : this.children)  {
            child.create();
        }
        return this;
    }

    /**
     * Evaluates all 'adds' in the configuration item file (e.g. add user, add
     * setting, ...).
     *
     * @param _needAdds     set with add strings used to append the adds for
     *                      {@link #children}
     * @see #children
     */
    @Override()
    protected void evalAdds4CheckExport(final Set<String> _needAdds)
    {
        super.evalAdds4CheckExport(_needAdds);
        for (final AbstractCommandData<?> child : this.children)  {
            _needAdds.add(child.getCI().getMxType() + " \"" + AbstractTest.convertTcl(child.getName()) + "\"");
        }
    }
}
