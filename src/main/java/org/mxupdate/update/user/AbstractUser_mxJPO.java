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

package org.mxupdate.update.user;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import matrix.util.MatrixException;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.user.workspace.Cue_mxJPO;
import org.mxupdate.update.user.workspace.Filter_mxJPO;
import org.mxupdate.update.user.workspace.Query_mxJPO;
import org.mxupdate.update.user.workspace.Table_mxJPO;
import org.mxupdate.update.user.workspace.Tip_mxJPO;
import org.mxupdate.update.user.workspace.ToolSet_mxJPO;
import org.mxupdate.update.user.workspace.View_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export, create, delete and update common user
 * information within MX. Common user information are all workspace related
 * objects.
 *
 * @author The MxUpdate Team
 * @param <CLASS> derived from this class
 */
public abstract class AbstractUser_mxJPO<CLASS extends AbstractAdminObject_mxJPO<CLASS>>
    extends AbstractAdminObject_mxJPO<CLASS>
{
    /** Set of all ignored URLs from the XML definition for common stuff of users. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        AbstractUser_mxJPO.IGNORED_URLS.add("/homeSite");
        AbstractUser_mxJPO.IGNORED_URLS.add("/cueList");
        AbstractUser_mxJPO.IGNORED_URLS.add("/filterList");
        AbstractUser_mxJPO.IGNORED_URLS.add("/queryList");
        AbstractUser_mxJPO.IGNORED_URLS.add("/tableList");
        AbstractUser_mxJPO.IGNORED_URLS.add("/tableList/table/derivedtable");
        AbstractUser_mxJPO.IGNORED_URLS.add("/tableList/table/adminProperties");
        AbstractUser_mxJPO.IGNORED_URLS.add("/tipList");
        AbstractUser_mxJPO.IGNORED_URLS.add("/toolsetList");
        AbstractUser_mxJPO.IGNORED_URLS.add("/viewList");
    }

    /**
     * Defines the parameter for the match of users for which workspace objects
     * are not handled (neither exported nor updated).
     *
     * @see #ignoreWorkspaceObjects(ParameterCache_mxJPO)
     */
    private static final String PARAM_IGNORE_WSO_USERS = "UserIgnoreWSO4Users";

    /** Related site of this group. */
    private String site = "";

    /**
     * Maps depending on the name of the cue to related cue information. The
     * map is used to sort the cues depending on the name.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,Cue_mxJPO> cues = new TreeMap<String,Cue_mxJPO>();

    /**
     * Current cue which is read.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     */
    private Cue_mxJPO currentCue;

    /**
     * Maps depending on the name of the filter to related filter information.
     * The map is used to sort the filters depending on the name.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,Filter_mxJPO> filters = new TreeMap<String,Filter_mxJPO>();

    /**
     * Current filter which is read.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     */
    private Filter_mxJPO currentFilter;

    /**
     * Maps depending on the name of the query to related query information.
     * The map is used to sort the queries depending on the name.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,Query_mxJPO> queries = new TreeMap<String,Query_mxJPO>();

    /**
     * Current query which is read.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     */
    private Query_mxJPO currentQuery;

    /**
     * Maps depending on the name of the table to related table information.
     * The map is used to sort the tables depending on the name.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,Table_mxJPO> tables = new TreeMap<String,Table_mxJPO>();

    /**
     * Current table which is read.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     */
    private Table_mxJPO currentTable;

    /**
     * Maps depending on the name of the tip to related tip information.
     * The map is used to sort the tips depending on the name.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,Tip_mxJPO> tips = new TreeMap<String,Tip_mxJPO>();

    /**
     * Current tip which is read.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     */
    private Tip_mxJPO currentTip;

    /**
     * Maps depending on the name of the tool set to related tool set
     * information. The map is used to sort the tool sets depending on the
     * name.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,ToolSet_mxJPO> toolSets = new TreeMap<String,ToolSet_mxJPO>();

    /**
     * Current tip which is read.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     */
    private ToolSet_mxJPO currentToolSet;

    /**
     * Maps depending on the name of the view to related view information. The
     * map is used to sort the tool sets depending on the name.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,View_mxJPO> views = new TreeMap<String,View_mxJPO>();

    /**
     * Current tip which is read.
     *
     * @see #parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)
     */
    private View_mxJPO currentView;

    /**
     * Constructor used to initialize this user definition with related type
     * definition <code>_typeDef</code> for given <code>_name</code>.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    protected AbstractUser_mxJPO(final TypeDef_mxJPO _typeDef,
                                 final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Returns current defined {@link #site}.
     *
     * @return site name
     */
    protected String getSite()
    {
        return this.site;
    }

    /**
     * <p>Parses all common user specific URL values. This includes:
     * <ul>
     * <li>{@link #site}</li>
     * <li>{@link #cues}</li>
     * <li>{@link #filters}</li>
     * <li>{@link #queries}</li>
     * <li>{@link #tables}</li>
     * <li>{@link #tips}</li>
     * <li>{@link #toolSets tool sets}</li>
     * <li>{@link #views}</li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if (AbstractUser_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/homeSite/siteRef".equals(_url))  {
            this.site = _content;
            parsed = true;

        } else if ("/cueList/cue".equals(_url))  {
            this.currentCue = new Cue_mxJPO(this);
            parsed = true;
        } else if ("/cueList/cue/name".equals(_url))  {
            this.cues.put(_content, this.currentCue);
            parsed = this.currentCue.parse(_paramCache, _url.substring(12), _content);
        } else if (_url.startsWith("/cueList/cue/"))  {
            parsed = this.currentCue.parse(_paramCache, _url.substring(12), _content);

        } else if ("/filterList/filter".equals(_url))  {
            this.currentFilter = new Filter_mxJPO(this);
            parsed = true;
        } else if ("/filterList/filter/name".equals(_url))  {
            this.filters.put(_content, this.currentFilter);
            parsed = this.currentFilter.parse(_paramCache, _url.substring(18), _content);
        } else if (_url.startsWith("/filterList/filter/"))  {
            parsed = this.currentFilter.parse(_paramCache, _url.substring(18), _content);

        } else if ("/queryList/query".equals(_url))  {
            this.currentQuery = new Query_mxJPO(this);
            parsed = true;
        } else if ("/queryList/query/name".equals(_url))  {
            this.queries.put(_content, this.currentQuery);
            parsed = this.currentQuery.parse(_paramCache, _url.substring(16), _content);
        } else if (_url.startsWith("/queryList/query/"))  {
            parsed = this.currentQuery.parse(_paramCache, _url.substring(16), _content);

        } else if ("/tableList/table".equals(_url))  {
            this.currentTable = new Table_mxJPO(this);
            parsed = true;
        } else if ("/tableList/table/adminProperties/name".equals(_url))  {
            this.tables.put(_content, this.currentTable);
            parsed = this.currentTable.parse(_paramCache, _url.substring(32), _content);
        } else if (_url.startsWith("/tableList/table/adminProperties/"))  {
            parsed = this.currentTable.parse(_paramCache, _url.substring(32), _content);
        } else if (_url.startsWith("/tableList/table/"))  {
            parsed = this.currentTable.parse(_paramCache, _url.substring(16), _content);

        } else if ("/tipList/tip".equals(_url))  {
            this.currentTip = new Tip_mxJPO(this);
            parsed = true;
        } else if ("/tipList/tip/name".equals(_url))  {
            this.tips.put(_content, this.currentTip);
            parsed = this.currentTip.parse(_paramCache, _url.substring(12), _content);
        } else if (_url.startsWith("/tipList/tip/"))  {
            parsed = this.currentTip.parse(_paramCache, _url.substring(12), _content);

        } else if ("/toolsetList/toolset".equals(_url))  {
            this.currentToolSet = new ToolSet_mxJPO(this);
            parsed = true;
        } else if ("/toolsetList/toolset/name".equals(_url))  {
            this.toolSets.put(_content, this.currentToolSet);
            parsed = this.currentToolSet.parse(_paramCache, _url.substring(20), _content);
        } else if (_url.startsWith("/toolsetList/toolset/"))  {
            parsed = this.currentToolSet.parse(_paramCache, _url.substring(20), _content);

        } else if ("/viewList/view".equals(_url))  {
            this.currentView = new View_mxJPO(this);
            parsed = true;
        } else if ("/viewList/view/name".equals(_url))  {
            this.views.put(_content, this.currentView);
            parsed = this.currentView.parse(_paramCache, _url.substring(14), _content);
        } else if (_url.startsWith("/viewList/view/"))  {
            parsed = this.currentView.parse(_paramCache, _url.substring(14), _content);

        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * <p>Sorted the workspace object related properties. This includes the
     * properties for:
     * <ul>
     * <li>{@link #cues}</li>
     * <li>{@link #filters}</li>
     * <li>{@link #queries}</li>
     * <li>{@link #tables}</li>
     * <li>{@link #tips}</li>
     * <li>{@link #toolSets tool sets}</li>
     * <li>{@link #views}</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the symbolic names could not be extracted
     */
    @Override()
    protected void prepare()
    {
        super.prepare();

        // cues
        for (final Cue_mxJPO cue : this.cues.values())  {
            cue.prepare();
        }

        // filters
        for (final Filter_mxJPO filter : this.filters.values())  {
            filter.prepare();
        }

        // queries
        for (final Query_mxJPO query : this.queries.values())  {
            query.prepare();
        }

        // tables
        for (final Table_mxJPO table : this.tables.values())  {
            table.prepare();
        }

        // tips
        for (final Tip_mxJPO tip : this.tips.values())  {
            tip.prepare();
        }

        // tool sets
        for (final ToolSet_mxJPO toolSet : this.toolSets.values())  {
            toolSet.prepare();
        }

        // views
        for (final View_mxJPO view : this.views.values())  {
            view.prepare();
        }
    }

    /**
     * <p>Writes specific information about the cached role to the given
     * writer instance. The included information is:
     * <ul>
     * <li>{@link #cues}</li>
     * <li>{@link #filters}</li>
     * <li>{@link #queries}</li>
     * <li>{@link #tables}</li>
     * <li>{@link #tips}</li>
     * <li>{@link #toolSets tool sets}</li>
     * <li>{@link #views}</li>
     * </ul></p>
     * <p>The workspace specific objects are only written to the TCL update
     * file if the name of this user object does not match to the matches
     * defined with the parameter {@link #PARAM_IGNORE_WSO_USERS}.</p>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not written
     * @see #ignoreWorkspaceObjects(ParameterCache_mxJPO)
     */
/*    @Override()
    protected void writeEnd(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
        throws IOException
    {

        if (!this.ignoreWorkspaceObjects(_paramCache))  {
            // cues
            for (final Cue_mxJPO cue : this.cues.values())  {
                cue.write(_paramCache, _out);
            }
            // filters
            for (final Filter_mxJPO filter : this.filters.values())  {
                filter.write(_paramCache, _out);
            }
            // queries
            for (final Query_mxJPO query : this.queries.values())  {
                query.write(_paramCache, _out);
            }
            // tables
            for (final Table_mxJPO table : this.tables.values())  {
                table.write(_paramCache, _out);
            }
            // tips
            for (final Tip_mxJPO tip : this.tips.values())  {
                tip.write(_paramCache, _out);
            }
            // tool sets
            for (final ToolSet_mxJPO toolSet : this.toolSets.values())  {
                toolSet.write(_paramCache, _out);
            }
            // views
            for (final View_mxJPO view : this.views.values())  {
                view.write(_paramCache, _out);
            }
        }
    }*/

    /**
     * <p>The method overwrites the original method to append the MQL
     * statements in the <code>_preMQLCode</code> to reset this user. Following
     * steps are done:
     * <ul>
     * <li>remove assigned {@link #site}</li>
     * <li>remove all {@link #cues}</li>
     * <li>remove all {@link #filters}</li>
     * <li>remove all {@link #queries}</li>
     * <li>remove all {@link #tables}</li>
     * <li>remove all {@link #tips}</li>
     * <li>remove all {@link #toolSets tool sets}</li>
     * <li>remove all {@link #views}</li>
     * </ul></p>
     * <p>The workspace specific objects are only removed if the name of this
     * user object does not match to the matches defined with the parameter
     * {@link #PARAM_IGNORE_WSO_USERS}.</p>
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     * @see #ignoreWorkspaceObjects(ParameterCache_mxJPO)
     */
    @Override()
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
        throws Exception
    {
        final StringBuilder preMQLCode = new StringBuilder();

        if (!this.ignoreWorkspaceObjects(_paramCache))  {
            // remove all assigned cues
            for (final Cue_mxJPO cue : this.cues.values())  {
                preMQLCode.append("escape delete cue \"").append(StringUtil_mxJPO.convertMql(cue.getName()))
                          .append("\" user \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\";\n");
            }
            // remove all assigned filters
            for (final Filter_mxJPO filter : this.filters.values())  {
                preMQLCode.append("escape delete filter \"").append(StringUtil_mxJPO.convertMql(filter.getName()))
                          .append("\" user \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\";\n");
            }
            // remove all assigned queries
            for (final Query_mxJPO query : this.queries.values())  {
                preMQLCode.append("escape delete query \"").append(StringUtil_mxJPO.convertMql(query.getName()))
                          .append("\" user \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\";\n");
            }
            // remove all assigned tables
            for (final Table_mxJPO table : this.tables.values())  {
                preMQLCode.append("escape delete table \"").append(StringUtil_mxJPO.convertMql(table.getName()))
                          .append("\" user \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\";\n");
            }
            // remove all assigned tips
            for (final Tip_mxJPO tip : this.tips.values())  {
                preMQLCode.append("escape delete tip \"").append(StringUtil_mxJPO.convertMql(tip.getName()))
                          .append("\" user \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\";\n");
            }
            // remove all assigned tool sets
            for (final ToolSet_mxJPO toolSet : this.toolSets.values())  {
                preMQLCode.append("escape delete toolset \"").append(StringUtil_mxJPO.convertMql(toolSet.getName()))
                          .append("\" user \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\";\n");
            }
            // remove all assigned views
            for (final View_mxJPO view : this.views.values())  {
                preMQLCode.append("escape delete view \"").append(StringUtil_mxJPO.convertMql(view.getName()))
                          .append("\" user \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\";\n");
            }
        }

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * Calculates if workspace objects for this user are not handled. This is
     * done by checking if the name of this user matches one of the match lists
     * defined with parameter {@link #PARAM_IGNORE_WSO_USERS}. In this case the
     * the workspace objects are ignored for this user.
     *
     * @param _paramCache       parameter cache
     * @return <i>true</i> if the handling of workspace objects for this user
     *         is ignored
     * @see #PARAM_IGNORE_WSO_USERS
     */
    protected boolean ignoreWorkspaceObjects(final ParameterCache_mxJPO _paramCache)
    {
        final Collection<String> ignoreMatches = _paramCache.getValueList(AbstractUser_mxJPO.PARAM_IGNORE_WSO_USERS);
        boolean ignore = false;
        if (ignoreMatches != null)  {
            for (final String ignoreMatch : ignoreMatches)  {
                if (StringUtil_mxJPO.match(this.getName(), ignoreMatch))  {
                    ignore = true;
                    break;
                }
            }
        }

        return ignore;
    }
}
