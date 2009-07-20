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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;

/**
 * The class is used to define a menu, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Menu
    extends AbstractCommand<Menu>
{
    /**
     * List of all children commands / menus.
     *
     * @see #addChild(AbstractCommand)
     */
    private final List<AbstractCommand<?>> children = new ArrayList<AbstractCommand<?>>();

    /**
     * Constructor to initialize this menu.
     *
     * @param _test     related test implementation (where this menu is
     *                  defined)
     * @param _name     name of the menu
     */
    public Menu(final AbstractTest _test,
                final String _name)
    {
        super(_test, AbstractTest.CI.MENU, _name);
    }

    /**
     * Adds to this menu instance a child command / menu.
     *
     * @param _child    child to append
     * @return this menu instance
     * @see #children
     */
    public Menu addChild(final AbstractCommand<?> _child)
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
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape mod menu \"${NAME}\"");
        for (final AbstractCommand<?> child : this.children)  {
            cmd.append(" add ").append(child.getCI().getMxType())
               .append(" \"").append(this.getTest().convertTcl(child.getName())).append('\"');
        }
        this.append4CIFileValues(cmd);
        this.append4CIFileSettings(cmd);

        return cmd.toString();
    }

    /**
     * Creates a this menu with all values and settings.
     *
     * @return this menu instance
     * @throws MatrixException if create failed
     */
    @Override
    public Menu create()
        throws MatrixException
    {
        this.createChildren();

        final StringBuilder cmd = new StringBuilder()
                .append("escape add menu \"" + this.getTest().convertMql(this.getName()) + "\"");
        this.append4CreateValues(cmd);
        this.append4CreateSettings(cmd);
        cmd.append(';');

        // append all child command / menus
        if (!this.children.isEmpty())  {
            cmd.append("escape mod menu \"" + this.getTest().convertMql(this.getName()) + "\"");
            for (final AbstractCommand<?> child : this.children)  {
                cmd.append(" add ").append(child.getCI().getMxType())
                   .append(" \"").append(this.getTest().convertMql(child.getName())).append('\"');
            }
            cmd.append(';');
        }

        this.getTest().mql(cmd);

        return this;
    }

    /**
     * Creates all child commands / menus.
     *
     * @return this menu instance
     * @throws MatrixException if child commands / menus could not be created
     * @see #children
     */
    public Menu createChildren()
        throws MatrixException
    {
        for (final AbstractCommand<?> child : this.children)  {
            child.create();
        }
        return this;
    }

    /**
     * Evaluates all 'adds' in the configuration item file (e.g. add user, add
     * setting, ...).
     *
     * @param _needAdds     set with add strings used to append the adds for
     *                      {@link #users}
     * @see #users
     */
    @Override
    protected void evalAdds4CheckExport(final Set<String> _needAdds)
    {
        super.evalAdds4CheckExport(_needAdds);
        for (final AbstractCommand<?> child : this.children)  {
            _needAdds.add(child.getCI().getMxType() + " \"" + this.getTest().convertTcl(child.getName()) + "\"");
        }
    }
}
