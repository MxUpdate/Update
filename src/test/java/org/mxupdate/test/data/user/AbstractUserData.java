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

package org.mxupdate.test.data.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.other.SiteData;
import org.mxupdate.test.data.user.workspace.CueData;
import org.mxupdate.test.data.user.workspace.FilterData;
import org.mxupdate.test.data.user.workspace.QueryData;
import org.mxupdate.test.data.user.workspace.TableData;
import org.mxupdate.test.data.user.workspace.TipData;
import org.mxupdate.test.data.user.workspace.ToolSetData;
import org.mxupdate.test.data.user.workspace.ViewData;
import org.testng.Assert;

/**
 * The class is used to define all user objects used to create / update and to
 * export.
 *
 * @author The MxUpdate Team
 * @param <DATA> class derived from abstract user
 */
public abstract class AbstractUserData<DATA extends AbstractUserData<?>>
    extends AbstractAdminData<DATA>
{
    /** Assigned site for this user. */
    private SiteData site;

    /**
     * Related cues of the workspace data from this user.
     *
     * @see #newCue(String)
     * @see #getCues()
     * @see #ciFile()
     * @see #create()
     * @see #checkExport(ExportParser)
     */
    private final Set<CueData<DATA>> cues = new HashSet<CueData<DATA>>();

    /**
     * Related filters of the workspace data from this user.
     *
     * @see #newFilter(String)
     * @see #getFilters()
     * @see #ciFile()
     * @see #create()
     * @see #checkExport(ExportParser)
     */
    private final Set<FilterData<DATA>> filters = new HashSet<FilterData<DATA>>();

    /**
     * Related queries of the workspace data from this user.
     *
     * @see #newQuery(String)
     * @see #getQueries()
     * @see #ciFile()
     * @see #create()
     * @see #checkExport(ExportParser)
     */
    private final Set<QueryData<DATA>> queries = new HashSet<QueryData<DATA>>();

    /**
     * Related tables of the workspace data from this user.
     *
     * @see #newTable(String)
     * @see #getTables()
     * @see #ciFile()
     * @see #create()
     * @see #checkExport(ExportParser)
     */
    private final Set<TableData<DATA>> tables = new HashSet<TableData<DATA>>();

    /**
     * Related tips of the workspace data from this user.
     *
     * @see #newTip(String)
     * @see #getTips()
     * @see #ciFile()
     * @see #create()
     * @see #checkExport(ExportParser)
     */
    private final Set<TipData<DATA>> tips = new HashSet<TipData<DATA>>();

    /**
     * Related tool sets of the workspace data from this user.
     *
     * @see #newToolSet(String)
     * @see #getToolSets()
     * @see #ciFile()
     * @see #create()
     * @see #checkExport(ExportParser)
     */
    private final Set<ToolSetData<DATA>> toolSets = new HashSet<ToolSetData<DATA>>();

    /**
     * Related views of the workspace data from this user.
     *
     * @see #newView(String)
     * @see #getViews()
     * @see #ciFile()
     * @see #create()
     * @see #checkExport(ExportParser)
     */
    private final Set<ViewData<DATA>> views = new HashSet<ViewData<DATA>>();

    /**
     * Constructor to initialize this user.
     *
     * @param _test                 related test implementation (where this
     *                              user is defined)
     * @param _ci                   related configuration type
     * @param _name                 name of the user
     * @param _requiredExportValues defines the required values of the
     *                              export within the configuration item
     *                              file
     */
    protected AbstractUserData(final AbstractTest _test,
                               final AbstractTest.CI _ci,
                               final String _name)
    {
        super(_test, _ci, _name);
    }

    /**
     * Defines related site for this user.
     *
     * @param _site     site to assign
     * @return this user data instance
     * @see #site
     */
    @SuppressWarnings("unchecked")
    public DATA setSite(final SiteData _site)
    {
        this.site = _site;
        if (_site == null)  {
            this.getValues().remove("site");
        } else  {
            this.setValue("site", this.site.getName());
        }
        return (DATA) this;
    }

    /**
     * Returns related {@link #site} of this user.
     *
     * @return assigned site; or <code>null</code> if not defined
     * @see #site
     */
    public SiteData getSite()
    {
        return this.site;
    }

    /**
     * Creates for given <code>_name</code> for this user a new cue.
     *
     * @param _name     name of the new cue for this user
     * @return new created cue instance for this user
     * @see #cues
     */
    @SuppressWarnings("unchecked")
    public CueData<DATA> newCue(final String _name)
    {
        final CueData<DATA> ret = new CueData<DATA>(this.getTest(), (DATA) this, _name);
        this.cues.add(ret);
        return ret;
    }

    /**
     * Returns all defined {@link #cues} for this user.
     *
     * @return all defined cues
     * @see #cues
     */
    public Set<CueData<DATA>> getCues()
    {
        return this.cues;
    }

    /**
     * Creates for given <code>_name</code> for this user a new filter.
     *
     * @param _name     name of the new filter for this user
     * @return new created filter instance for this user
     * @see #filters
     */
    @SuppressWarnings("unchecked")
    public FilterData<DATA> newFilter(final String _name)
    {
        final FilterData<DATA> ret = new FilterData<DATA>(this.getTest(), (DATA) this, _name);
        this.filters.add(ret);
        return ret;
    }

    /**
     * Returns all defined {@link #filters} for this user.
     *
     * @return all defined filters
     * @see #filters
     */
    public Set<FilterData<DATA>> getFilters()
    {
        return this.filters;
    }

    /**
     * Creates for given <code>_name</code> for this user a new query.
     *
     * @param _name     name of the new query for this user
     * @return new created query instance for this user
     * @see #queries
     */
    @SuppressWarnings("unchecked")
    public QueryData<DATA> newQuery(final String _name)
    {
        final QueryData<DATA> ret = new QueryData<DATA>(this.getTest(), (DATA) this, _name);
        this.queries.add(ret);
        return ret;
    }

    /**
     * Returns all defined {@link #queries} for this user.
     *
     * @return all defined queries
     * @see #queries
     */
    public Set<QueryData<DATA>> getQueries()
    {
        return this.queries;
    }

    /**
     * Creates for given <code>_name</code> for this user a new table.
     *
     * @param _name     name of the new table for this user
     * @return new created table instance for this user
     * @see #tables
     */
    @SuppressWarnings("unchecked")
    public TableData<DATA> newTable(final String _name)
    {
        final TableData<DATA> ret = new TableData<DATA>(this.getTest(), (DATA) this, _name);
        this.tables.add(ret);
        return ret;
    }

    /**
     * Returns all defined {@link #tables} for this user.
     *
     * @return all defined tables
     * @see #toolSets
     */
    public Set<TableData<DATA>> getTables()
    {
        return this.tables;
    }

    /**
     * Creates for given <code>_name</code> for this user a new tip.
     *
     * @param _name     name of the new tip for this user
     * @return new created tip instance for this user
     * @see #tips
     */
    @SuppressWarnings("unchecked")
    public TipData<DATA> newTip(final String _name)
    {
        final TipData<DATA> ret = new TipData<DATA>(this.getTest(), (DATA) this, _name);
        this.tips.add(ret);
        return ret;
    }

    /**
     * Returns all defined {@link #tips} for this user.
     *
     * @return all defined tips
     * @see #tips
     */
    public Set<TipData<DATA>> getTips()
    {
        return this.tips;
    }

    /**
     * Creates for given <code>_name</code> for this user a new tool set.
     *
     * @param _name     name of the new tool set for this user
     * @return new created tool set instance for this user
     * @see #toolSets
     */
    @SuppressWarnings("unchecked")
    public ToolSetData<DATA> newToolSet(final String _name)
    {
        final ToolSetData<DATA> ret = new ToolSetData<DATA>(this.getTest(), (DATA) this, _name);
        this.toolSets.add(ret);
        return ret;
    }

    /**
     * Returns all defined {@link #toolSets tool sets} for this user.
     *
     * @return all defined tool sets
     * @see #toolSets
     */
    public Set<ToolSetData<DATA>> getToolSets()
    {
        return this.toolSets;
    }

    /**
     * Creates for given <code>_name</code> for this user a new view.
     *
     * @param _name     name of the new view for this user
     * @return new created view instance for this user
     * @see #views
     */
    @SuppressWarnings("unchecked")
    public ViewData<DATA> newView(final String _name)
    {
        final ViewData<DATA> ret = new ViewData<DATA>(this.getTest(), (DATA) this, _name);
        this.views.add(ret);
        return ret;
    }

    /**
     * Returns all defined {@link #views} for this user.
     *
     * @return all defined views
     * @see #views
     */
    public Set<ViewData<DATA>> getViews()
    {
        return this.views;
    }

    /**
     * <p>Prepares the configuration item update file depending on the
     * configuration of this user.</p>
     * This includes:
     * <ul>
     * <li>{@link #cues}</li>
     * <li>{@link #filters}</li>
     * <li>{@link #queries}</li>
     * <li>{@link #tables}</li>
     * <li>{@link #tips}</li>
     * <li>{@link #toolSets tool sets}</li>
     * <li>{@link #views}</li>
     * </ul>
     *
     * @return code for the configuration item update file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape mod " + this.getCI().getMxType() + " \"${NAME}\"");

        this.append4CIFileValues(cmd);

        cmd.append(";\n");

        // cues
        for (final CueData<DATA> cue : this.cues)  {
            cmd.append(cue.ciFile()).append('\n');
        }

        // filters
        for (final FilterData<DATA> filter : this.filters)  {
            cmd.append(filter.ciFile()).append('\n');
        }

        // queries
        for (final QueryData<DATA> query : this.queries)  {
            cmd.append(query.ciFile()).append('\n');
        }

        // tables
        for (final TableData<DATA> table : this.tables)  {
            cmd.append(table.ciFile()).append('\n');
        }

        // tips
        for (final TipData<DATA> tip : this.tips)  {
            cmd.append(tip.ciFile()).append('\n');
        }

        // tool sets
        for (final ToolSetData<DATA> toolSet : this.toolSets)  {
            cmd.append(toolSet.ciFile()).append('\n');
        }

        // views
        for (final ViewData<DATA> view : this.views)  {
            cmd.append(view.ciFile()).append('\n');
        }

        return cmd.toString();
    }

    /**
     * Creates this user. This includes:
     * <ul>
     * <li>{@link #cues}</li>
     * <li>{@link #filters}</li>
     * <li>{@link #queries}</li>
     * <li>{@link #tables}</li>
     * <li>{@link #tips}</li>
     * <li>{@link #toolSets tool sets}</li>
     * <li>{@link #views}</li>
     * </ul>
     *
     * @return this collection user data instance
     * @throws MatrixException if create failed
     */
    @SuppressWarnings("unchecked")
    @Override()
    public DATA create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder()
                    .append("escape add ")
                    .append(this.getCI().getMxType())
                    .append(" \"").append(AbstractTest.convertMql(this.getName())).append("\"");
            // if site assigned, the site must be created
            if (this.site != null)  {
                this.site.create();
            }
            this.append4Create(cmd);
            cmd.append(";\n");
            this.getTest().mql(cmd);

            // cues
            for (final CueData<DATA> cue : this.cues)  {
                cue.create();
            }

            // filters
            for (final FilterData<DATA> filter : this.filters)  {
                filter.create();
            }

            // queries
            for (final QueryData<DATA> query : this.queries)  {
                query.create();
            }

            // tables
            for (final TableData<DATA> table : this.tables)  {
                table.create();
            }

            // tips
            for (final TipData<DATA> tip : this.tips)  {
                tip.create();
            }

            // tool sets
            for (final ToolSetData<DATA> toolSet : this.toolSets)  {
                toolSet.create();
            }

            // views
            for (final ViewData<DATA> view : this.views)  {
                view.create();
            }
        }
        return (DATA) this;
    }

    /**
     * {@inheritDoc}
     * Creates depending {@link #site} and properties for {@link #cues},
     * {@link #filters}, {@link #queries}, {@link #tables}, {@link #tips},
     * {@link #toolSets} and {@link #views}.
     *
     * @see #site
     * @see #cues
     * @see #filters
     * @see #queries
     * @see #tables
     * @see #tips
     * @see #toolSets
     * @see #views
     */
    @Override()
    @SuppressWarnings("unchecked")
    public DATA createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create site
        if (this.site != null)  {
            this.site.create();
        }
        // create cue properties
        for (final CueData<DATA> cue : this.cues)  {
            cue.createDependings();
        }
        // create filter properties
        for (final FilterData<DATA> filter : this.filters)  {
            filter.createDependings();
        }
        // create query properties
        for (final QueryData<DATA> query : this.queries)  {
            query.createDependings();
        }
        // create table properties
        for (final TableData<DATA> table : this.tables)  {
            table.createDependings();
        }
        // create tip properties
        for (final TipData<DATA> tip : this.tips)  {
            tip.createDependings();
        }
        // create tool set properties and programs
        for (final ToolSetData<DATA> toolSet : this.toolSets)  {
            toolSet.createDependings();
        }
        // create view properties
        for (final ViewData<DATA> view : this.views)  {
            view.createDependings();
        }

        return (DATA) this;
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     * Following workspace objects for this user are checked that they are
     * defined:
     * <ul>
     * <li>hidden flag</li>
     * <li>{@link #site}</li>
     * <li>{@link #cues}</li>
     * <li>{@link #filters}</li>
     * <li>{@link #queries}</li>
     * <li>{@link #tables}</li>
     * <li>{@link #tips}</li>
     * <li>{@link #toolSets tool sets}</li>
     * <li>{@link #views}</li>
     * </ul>
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        // site
        if (this.site != null)  {
            this.checkSingleValue(
                    _exportParser,
                    this.getCI().getMxType(),
                    "site",
                    "\"" + AbstractTest.convertTcl(this.site.getName()) + "\"");
        } else  {
            this.checkNotExistingSingleValue(_exportParser,  this.getCI().getMxType(), "site");
        }

        // all workspace objects
        final Set<CueData<DATA>> tmpCues            = new HashSet<CueData<DATA>>(this.cues);
        final Set<FilterData<DATA>> tmpFilters      = new HashSet<FilterData<DATA>>(this.filters);
        final Set<QueryData<DATA>> tmpQueries       = new HashSet<QueryData<DATA>>(this.queries);
        final Set<TableData<DATA>> tmpTables        = new HashSet<TableData<DATA>>(this.tables);
        final Set<TipData<DATA>> tmpTips            = new HashSet<TipData<DATA>>(this.tips);
        final Set<ToolSetData<DATA>> tmpToolSets    = new HashSet<ToolSetData<DATA>>(this.toolSets);
        final Set<ViewData<DATA>> tmpViews          = new HashSet<ViewData<DATA>>(this.views);

        final List<ExportParser.Line> lines = new ArrayList<ExportParser.Line>();
        for (final ExportParser.Line rootLine : _exportParser.getRootLines())  {
            // cues
            if (rootLine.getValue().startsWith("escape add cue "))  {
                for (final CueData<DATA> cue : this.cues)  {
                    final String key = new StringBuilder()
                            .append("escape add cue \"")
                            .append(AbstractTest.convertTcl(cue.getName()))
                            .append("\"").toString();
                    if (key.equals(rootLine.getValue()))  {
                        tmpCues.remove(cue);
                        cue.checkExport(new ExportParser(cue.getName(), _exportParser.getLog(), rootLine));
                        break;
                    }
                }
            // filters
            } else if (rootLine.getValue().startsWith("escape add filter "))  {
                for (final FilterData<DATA> filter : this.filters)  {
                    final String key = new StringBuilder()
                            .append("escape add filter \"")
                            .append(AbstractTest.convertTcl(filter.getName()))
                            .append("\"").toString();
                    if (key.equals(rootLine.getValue()))  {
                        tmpFilters.remove(filter);
                        filter.checkExport(new ExportParser(filter.getName(), _exportParser.getLog(), rootLine));
                        break;
                    }
                }
            // queries
            } else if (rootLine.getValue().startsWith("escape add query "))  {
                for (final QueryData<DATA> query : this.queries)  {
                    final String key = new StringBuilder()
                            .append("escape add query \"")
                            .append(AbstractTest.convertTcl(query.getName()))
                            .append("\"").toString();
                    if (key.equals(rootLine.getValue()))  {
                        tmpQueries.remove(query);
                        query.checkExport(new ExportParser(query.getName(), _exportParser.getLog(), rootLine));
                        break;
                    }
                }
            // tables
            } else if (rootLine.getValue().startsWith("escape add table "))  {
                for (final TableData<DATA> table : this.tables)  {
                    final String key = new StringBuilder()
                            .append("escape add table \"")
                            .append(AbstractTest.convertTcl(table.getName()))
                            .append("\"").toString();
                    if (key.equals(rootLine.getValue()))  {
                        tmpTables.remove(table);
                        table.checkExport(new ExportParser(table.getName(), _exportParser.getLog(), rootLine));
                        break;
                    }
                }
            // tips
            } else if (rootLine.getValue().startsWith("escape add tip "))  {
                for (final TipData<DATA> tip : this.tips)  {
                    final String key = new StringBuilder()
                            .append("escape add tip \"")
                            .append(AbstractTest.convertTcl(tip.getName()))
                            .append("\"").toString();
                    if (key.equals(rootLine.getValue()))  {
                        tmpTips.remove(tip);
                        tip.checkExport(new ExportParser(tip.getName(), _exportParser.getLog(), rootLine));
                        break;
                    }
                }
            // tool sets
            } else if (rootLine.getValue().startsWith("escape add toolset "))  {
                for (final ToolSetData<DATA> toolSet : this.toolSets)  {
                    final String key = new StringBuilder()
                            .append("escape add toolset \"")
                            .append(AbstractTest.convertTcl(toolSet.getName()))
                            .append("\"").toString();
                    if (key.equals(rootLine.getValue()))  {
                        tmpToolSets.remove(toolSet);
                        toolSet.checkExport(new ExportParser(toolSet.getName(), _exportParser.getLog(), rootLine));
                        break;
                    }
                }
            // views
            } else if (rootLine.getValue().startsWith("escape add view "))  {
                for (final ViewData<DATA> view : this.views)  {
                    final String key = new StringBuilder()
                            .append("escape add view \"")
                            .append(AbstractTest.convertTcl(view.getName()))
                            .append("\"").toString();
                    if (key.equals(rootLine.getValue()))  {
                        tmpViews.remove(view);
                        view.checkExport(new ExportParser(view.getName(), _exportParser.getLog(), rootLine));
                        break;
                    }
                }
            // all other
            } else  {
                lines.add(rootLine);
            }
        }

        // and check all others
        super.checkExport(new ExportParser(
                _exportParser.getName(),
                _exportParser.getSymbolicName(),
                _exportParser.getLog(),
                lines.toArray(new ExportParser.Line[lines.size()])));

        Assert.assertTrue(tmpCues.isEmpty(),        "check that all cues are defined in the update file");
        Assert.assertTrue(tmpFilters.isEmpty(),     "check that all filters are defined in the update file");
        Assert.assertTrue(tmpQueries.isEmpty(),     "check that all queries are defined in the update file");
        Assert.assertTrue(tmpTables.isEmpty(),      "check that all tables are defined in the update file");
        Assert.assertTrue(tmpTips.isEmpty(),        "check that all tips are defined in the update file");
        Assert.assertTrue(tmpToolSets.isEmpty(),    "check that all tool sets are defined in the update file");
        Assert.assertTrue(tmpViews.isEmpty(),       "check that all views are defined in the update file");
    }
}
