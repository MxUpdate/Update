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

package org.mxupdate.test.data.user.workspace;

import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.user.AbstractUserData;
import org.testng.Assert;

/**
 * The class is used to define all view objects related to users used to
 * create / update and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <USER> class of the related user for which this tool set is defined
 */
public class ViewData<USER extends AbstractUserData<?>>
    extends AbstractWorkspaceObjectData<ViewData<USER>,USER>
{
    /**
     * Within export the description must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(3);
    static  {
    }

    /**
     * All assigned cues of this view.
     */
    private final Set<CueData<USER>> cuesActive = new HashSet<CueData<USER>>();

    /**
     * All assigned cues of this view.
     */
    private final Set<CueData<USER>> cuesNotActive = new HashSet<CueData<USER>>();

    /**
     * All assigned filters of this view.
     */
    private final Set<FilterData<USER>> filtersActive = new HashSet<FilterData<USER>>();

    /**
     * All assigned filters of this view.
     */
    private final Set<FilterData<USER>> filtersNotActive = new HashSet<FilterData<USER>>();

    /**
     * All assigned tables of this view.
     */
    private final Set<TableData<USER>> tablesActive = new HashSet<TableData<USER>>();

    /**
     * All assigned tables of this view.
     */
    private final Set<TableData<USER>> tablesNotActive = new HashSet<TableData<USER>>();

    /**
     * All assigned tips of this view.
     */
    private final Set<TipData<USER>> tipsActive = new HashSet<TipData<USER>>();

    /**
     * All assigned tips of this view.
     */
    private final Set<TipData<USER>> tipsNotActive = new HashSet<TipData<USER>>();

    /**
     * All assigned tool sets of this view.
     */
    private final Set<ToolSetData<USER>> toolSetsActive = new HashSet<ToolSetData<USER>>();

    /**
     * All assigned tool sets of this view.
     */
    private final Set<ToolSetData<USER>> toolSetsNotActive = new HashSet<ToolSetData<USER>>();

    /**
     * Default constructor.
     *
     * @param _test     related test case
     * @param _user     user for which this view is defined
     * @param _name     name of the view
     */
    public ViewData(final AbstractTest _test,
                     final USER _user,
                     final String _name)
    {
        super(_test, "view", _user, _name, ViewData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Assigns a new active <code>_cue</code> to all {@link #cuesActive} of
     * this view.
     *
     * @param _cue      cue to append
     * @return this view data instance
     * @see #cuesActive
     */
    public ViewData<USER> addActive(final CueData<USER> _cue)
    {
        this.cuesActive.add(_cue);
        return this;
    }

    /**
     * Assigns a new <code>_cue</code> to all
     * {@link #cuesNotActive not active cue} of this view.
     *
     * @param _cue      cue to append
     * @return this view data instance
     * @see #cuesNotActive
     */
    public ViewData<USER> addNotActive(final CueData<USER> _cue)
    {
        this.cuesNotActive.add(_cue);
        return this;
    }

    /**
     * Assigns a new <code>_filter</code> to all
     * {@link #filtersActive active filters} of this view.
     *
     * @param _filter       filter to append
     * @return this view data instance
     * @see #filtersActive
     */
    public ViewData<USER> addActive(final FilterData<USER> _filter)
    {
        this.filtersActive.add(_filter);
        return this;
    }

    /**
     * Assigns a new <code>_filter</code> to all
     * {@link #filtersNotActive not active filters} of this view.
     *
     * @param _filter       filter to append
     * @return this view data instance
     * @see #filtersNotActive
     */
    public ViewData<USER> addNotActive(final FilterData<USER> _filter)
    {
        this.filtersNotActive.add(_filter);
        return this;
    }

    /**
     * Assigns a new <code>_table</code> to all
     * {@link #tablesActive active tables} of this view.
     *
     * @param _table    table to append
     * @return this view data instance
     * @see #tablesActive
     */
    public ViewData<USER> addActive(final TableData<USER> _table)
    {
        this.tablesActive.add(_table);
        return this;
    }

    /**
     * Assigns a new <code>_table</code> to all
     * {@link #tablesNotActive not active tables} of this view.
     *
     * @param _table    table to append
     * @return this view data instance
     * @see #tablesActive
     */
    public ViewData<USER> addNotActive(final TableData<USER> _table)
    {
        this.tablesNotActive.add(_table);
        return this;
    }

    /**
     * Assigns a new <code>_tip</code> to all {@link #tipsActive active tips}
     * of this view.
     *
     * @param _tip      tip to append
     * @return this view data instance
     * @see #tipsActive
     */
    public ViewData<USER> addActive(final TipData<USER> _tip)
    {
        this.tipsActive.add(_tip);
        return this;
    }

    /**
     * Assigns a new <code>_tip</code> to all
     * {@link #tipsNotActive not active tips} of this view.
     *
     * @param _tip      tip to append
     * @return this view data instance
     * @see #tipsNotActive
     */
    public ViewData<USER> addNotActive(final TipData<USER> _tip)
    {
        this.tipsNotActive.add(_tip);
        return this;
    }

    /**
     * Assigns a new active <code>_toolSet</code> to all
     * {@link #toolSetsActive active tool sets} of this view.
     *
     * @param _toolSet      tool set to append
     * @return this view data instance
     * @see #toolSetsActive
     */
    public ViewData<USER> addActive(final ToolSetData<USER> _toolSet)
    {
        this.toolSetsActive.add(_toolSet);
        return this;
    }

    /**
     * Assigns a new not active <code>_toolSet</code> to all
     * {@link #toolSetsNotActive not active tool sets} of this view.
     *
     * @param _toolSet      tool set to append
     * @return this view data instance
     * @see #toolSetsNotActive
     */
    public ViewData<USER> addNotActive(final ToolSetData<USER> _toolSet)
    {
        this.toolSetsNotActive.add(_toolSet);
        return this;
    }

    /**
     *
     * @param _cmd  string builder with the TCL commands of the configuration
     *              item file
     */
    @Override()
    protected void append4CIFileValues(final StringBuilder _cmd)
    {
        super.append4CIFileValues(_cmd);
        // cues
        for (final CueData<USER> data : this.cuesActive)  {
            _cmd.append(" \\\n    active cue \"").append(AbstractTest.convertTcl(data.getName())).append("\"");
        }
        for (final CueData<USER> data : this.cuesNotActive)  {
            _cmd.append(" \\\n    inactive cue \"").append(AbstractTest.convertTcl(data.getName())).append("\"");
        }
        // filters
        for (final FilterData<USER> data : this.filtersActive)  {
            _cmd.append(" \\\n    active filter \"").append(AbstractTest.convertTcl(data.getName())).append("\"");
        }
        for (final FilterData<USER> data : this.filtersNotActive)  {
            _cmd.append(" \\\n    inactive filter \"").append(AbstractTest.convertTcl(data.getName())).append("\"");
        }
        // tables
        for (final TableData<USER> data : this.tablesActive)  {
            _cmd.append(" \\\n    active table \"").append(AbstractTest.convertTcl(data.getName())).append("\"");
        }
        for (final TableData<USER> data : this.tablesNotActive)  {
            _cmd.append(" \\\n    inactive table \"").append(AbstractTest.convertTcl(data.getName())).append("\"");
        }
        // tips
        for (final TipData<USER> data : this.tipsActive)  {
            _cmd.append(" \\\n    active tip \"").append(AbstractTest.convertTcl(data.getName())).append("\"");
        }
        for (final TipData<USER> data : this.tipsNotActive)  {
            _cmd.append(" \\\n    inactive tip \"").append(AbstractTest.convertTcl(data.getName())).append("\"");
        }
        // tool sets
        for (final ToolSetData<USER> data : this.toolSetsActive)  {
            _cmd.append(" \\\n    active toolset \"").append(AbstractTest.convertTcl(data.getName())).append("\"");
        }
        for (final ToolSetData<USER> data : this.toolSetsNotActive)  {
            _cmd.append(" \\\n    inactive toolset \"").append(AbstractTest.convertTcl(data.getName())).append("\"");
        }
    }

    /**
     * Appends the MQL commands within a create.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     * @see #fields
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4Create(_cmd);
        // cues
        for (final CueData<USER> data : this.cuesActive)  {
            _cmd.append(" active cue \"").append(AbstractTest.convertMql(data.getName())).append("\"");
        }
        for (final CueData<USER> data : this.cuesNotActive)  {
            _cmd.append(" inactive cue \"").append(AbstractTest.convertMql(data.getName())).append("\"");
        }
        // filters
        for (final FilterData<USER> data : this.filtersActive)  {
            _cmd.append(" active filter \"").append(AbstractTest.convertMql(data.getName())).append("\"");
        }
        for (final FilterData<USER> data : this.filtersNotActive)  {
            _cmd.append(" inactive filter \"").append(AbstractTest.convertMql(data.getName())).append("\"");
        }
        // tables
        for (final TableData<USER> data : this.tablesActive)  {
            _cmd.append(" active table \"").append(AbstractTest.convertMql(data.getName())).append("\"");
        }
        for (final TableData<USER> data : this.tablesNotActive)  {
            _cmd.append(" inactive table \"").append(AbstractTest.convertMql(data.getName())).append("\"");
        }
        // tips
        for (final TipData<USER> data : this.tipsActive)  {
            _cmd.append(" active tip \"").append(AbstractTest.convertMql(data.getName())).append("\"");
        }
        for (final TipData<USER> data : this.tipsNotActive)  {
            _cmd.append(" inactive tip \"").append(AbstractTest.convertMql(data.getName())).append("\"");
        }
        // tool sets
        for (final ToolSetData<USER> data : this.toolSetsActive)  {
            _cmd.append(" active toolset \"").append(AbstractTest.convertMql(data.getName())).append("\"");
        }
        for (final ToolSetData<USER> data : this.toolSetsNotActive)  {
            _cmd.append(" inactive toolset \"").append(AbstractTest.convertMql(data.getName())).append("\"");
        }
    }

    /**
     * Checks the export of this view data if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @SuppressWarnings("unchecked")
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        this.check(_exportParser, "active",     this.cuesActive,
                                                this.filtersActive,
                                                this.tablesActive,
                                                this.tipsActive,
                                                this.toolSetsActive);
        this.check(_exportParser, "inactive",   this.cuesNotActive,
                                                this.filtersNotActive,
                                                this.tablesNotActive,
                                                this.tipsNotActive,
                                                this.toolSetsNotActive);
    }

    /**
     * Tests that for given <code>_mxType</code> all related values of
     * <code>_datas</code> are defined.
     *
     * @param _exportParser     parsed export
     * @param _mxType           related MX administration type
     * @param _datas            set of visual workspace objects to test
     */
    private void check(final ExportParser _exportParser,
                       final String _mxType,
                       final Set<? extends AbstractWorkspaceObjectData<?,USER>>... _datas)
    {
        // prepare target values
        final Set<String> targets = new HashSet<String>();
        for (final Set<? extends AbstractWorkspaceObjectData<?,USER>> oneDatas : _datas)  {
            for (final AbstractWorkspaceObjectData<?,USER> data : oneDatas)  {
                targets.add(data.getMxAdminType() + " \"" + AbstractTest.convertTcl(data.getName()) + "\"");
            }
        }
        // check against parsed data
        for (final String current : _exportParser.getLines("/mql/" + _mxType + "/@value"))  {
            final String test = current.replaceAll("\\\\$", "").trim();
            Assert.assertTrue(targets.contains(test), "check that " + _mxType + " " + test + " must be defined");
            targets.remove(test);
        }
        Assert.assertTrue(targets.isEmpty(), "check that all " + _mxType + "'s are defined (not found " + targets + ")");
    }
}
