/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
 */

package org.mxupdate.test.data.userinterface;

import java.util.ArrayList;
import java.util.List;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.ExportParser.Line;
import org.testng.Assert;

/**
 * Used to define a menu, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class MenuData
    extends AbstractCommandData<MenuData>
{
    /** List of all children commands / menus. */
    private final List<AbstractCommandData<?>> children = new ArrayList<AbstractCommandData<?>>();
    /** Tree menu. */
    private Boolean treeMenu;

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
     */
    public MenuData addChild(final AbstractCommandData<?> _child)
    {
        this.children.add(_child);
        return this;
    }

    /**
     * Defines that this menu is a {@link #treeMenu tree menu}.
     *
     * @return this menu instance
     */
    public MenuData setTreeMenu(final Boolean _treeMenu)
    {
        this.treeMenu = _treeMenu;
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
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate menu \"${NAME}\" {\n");
        if (this.treeMenu != null)  {
            strg.append("    ").append(this.treeMenu ? "" : "!").append("treemenu\n");
        }
        this.getFlags().append4Update("    ", strg);
        this.getValues().append4Update("    ", strg);
        this.getSettings().appendUpdate("    ", strg, "\n");
        this.getProperties().append4Update("    ", strg);
        for (final String ciLine : this.getCILines())  {
            strg.append("    ").append(ciLine).append('\n');
        }
        for (final AbstractCommandData<?> child : this.children)  {
            strg.append("    ").append(child.getCI().getMxType()).append(" \"").append(AbstractTest.convertUpdate(child.getName())).append("\"\n");
        }
        strg.append("}");

        return strg.toString();
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
               .append(" to menu \"").append(AbstractTest.convertMql(this.getName())).append("\";");

            if ((this.treeMenu != null) && this.treeMenu)  {
                cmd.append("escape mod menu Tree add menu \"").append(AbstractTest.convertMql(this.getName())).append("\";");
            }

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
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        // check symbolic name
        Assert.assertEquals(
                _exportParser.getSymbolicName(),
                this.getSymbolicName(),
                "check symbolic name");

        if ((this.treeMenu != null) && this.treeMenu)  {
            this.checkValueExists(_exportParser, "treemenu", "treemenu", true);
        } else  {
            this.checkNotExistingSingleValue(_exportParser, "treemenu", "treemenu");
        }

        this.getFlags().check4Export(_exportParser, "");
        this.getValues().check4Export(_exportParser, "");
        this.getSettings().checkExport(_exportParser.getLines("/mxUpdate/setting/@value"));
        this.getProperties().checkExport(_exportParser.getLines("/mxUpdate/property/@value"));

        // fetch child from export file
        final List<String> childDefs = new ArrayList<String>();
        for (final Line line : _exportParser.getRootLines().get(0).getChildren())  {
            if ("menu".equals(line.getTag()) || "command".equals(line.getTag()))  {
                childDefs.add(line.getTag() + " " + line.getValue());
            }
        }
        // fetch child from this definition
        final List<String> thisDefs = new ArrayList<String>();
        for (final AbstractCommandData<?> child : this.children)  {
            thisDefs.add(child.getCI().getMxType() + " \"" + AbstractTest.convertUpdate(child.getName()) + "\"");
        }
        // and compare
        Assert.assertEquals(childDefs, thisDefs, "check child of menu");
    }
}
