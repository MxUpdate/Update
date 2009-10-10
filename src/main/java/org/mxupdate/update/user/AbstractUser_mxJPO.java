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

package org.mxupdate.update.user;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.user.workspace.Cue_mxJPO;
import org.mxupdate.update.user.workspace.Filter_mxJPO;
import org.mxupdate.update.user.workspace.Query_mxJPO;
import org.mxupdate.update.user.workspace.Table_mxJPO;
import org.mxupdate.update.user.workspace.Tip_mxJPO;
import org.mxupdate.update.user.workspace.ToolSet_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export, create, delete and update common user
 * information within MX. Common user information are all workspace related
 * objects.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public abstract class AbstractUser_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 4072711818597692656L;

    /**
     * Set of all ignored URLs from the XML definition for common stuff of
     * users.
     *
     * @see #parse(String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        AbstractUser_mxJPO.IGNORED_URLS.add("/cueList");
        AbstractUser_mxJPO.IGNORED_URLS.add("/filterList");
        AbstractUser_mxJPO.IGNORED_URLS.add("/queryList");
        AbstractUser_mxJPO.IGNORED_URLS.add("/tableList");
        AbstractUser_mxJPO.IGNORED_URLS.add("/tableList/table/derivedtable");
        AbstractUser_mxJPO.IGNORED_URLS.add("/tableList/table/adminProperties");
        AbstractUser_mxJPO.IGNORED_URLS.add("/tipList");
        AbstractUser_mxJPO.IGNORED_URLS.add("/toolsetList");
    }

    /**
     * Maps depending on the name of the cue to related cue information. The
     * map is used to sort the cues depending on the name.
     *
     * @see #parse(String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeWorkspaceObjects(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,Cue_mxJPO> cues = new TreeMap<String,Cue_mxJPO>();

    /**
     * Current cue which is read.
     *
     * @see #parse(String, String)
     */
    private Cue_mxJPO currentCue;

    /**
     * Maps depending on the name of the filter to related filter information.
     * The map is used to sort the filters depending on the name.
     *
     * @see #parse(String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeWorkspaceObjects(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,Filter_mxJPO> filters = new TreeMap<String,Filter_mxJPO>();

    /**
     * Current filter which is read.
     *
     * @see #parse(String, String)
     */
    private Filter_mxJPO currentFilter;

    /**
     * Maps depending on the name of the query to related query information.
     * The map is used to sort the queries depending on the name.
     *
     * @see #parse(String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeWorkspaceObjects(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,Query_mxJPO> queries = new TreeMap<String,Query_mxJPO>();

    /**
     * Current query which is read.
     *
     * @see #parse(String, String)
     */
    private Query_mxJPO currentQuery;

    /**
     * Maps depending on the name of the table to related table information.
     * The map is used to sort the tables depending on the name.
     *
     * @see #parse(String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeWorkspaceObjects(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,Table_mxJPO> tables = new TreeMap<String,Table_mxJPO>();

    /**
     * Current table which is read.
     *
     * @see #parse(String, String)
     */
    private Table_mxJPO currentTable;

    /**
     * Maps depending on the name of the tip to related tip information.
     * The map is used to sort the tips depending on the name.
     *
     * @see #parse(String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeWorkspaceObjects(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,Tip_mxJPO> tips = new TreeMap<String,Tip_mxJPO>();

    /**
     * Current tip which is read.
     *
     * @see #parse(String, String)
     */
    private Tip_mxJPO currentTip;

    /**
     * Maps depending on the name of the tool set to related tool set
     * information. The map is used to sort the tool sets depending on the
     * name.
     *
     * @see #parse(String, String)
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeWorkspaceObjects(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Map<String,ToolSet_mxJPO> toolSets = new TreeMap<String,ToolSet_mxJPO>();

    /**
     * Current tip which is read.
     *
     * @see #parse(String, String)
     */
    private ToolSet_mxJPO currentToolSet;

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
     * <p>Parses all common user specific URL values. This includes:
     * <ul>
     * <li>{@link #cues}</li>
     * <li>{@link #filters}</li>
     * <li>{@link #queries}</li>
     * <li>{@link #tables}</li>
     * <li>{@link #tips}</li>
     * <li>{@link #toolSets}</li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     * @see #IGNORED_URLS
     */
    @Override()
    protected void parse(final String _url,
                         final String _content)
    {
        if (!AbstractUser_mxJPO.IGNORED_URLS.contains(_url))  {
            if ("/cueList/cue".equals(_url))  {
                this.currentCue = new Cue_mxJPO(this);
            } else if ("/cueList/cue/name".equals(_url))  {
                this.cues.put(_content, this.currentCue);
                this.currentCue.parse(_url.substring(12), _content);
            } else if (_url.startsWith("/cueList/cue/"))  {
                this.currentCue.parse(_url.substring(12), _content);

            } else if ("/filterList/filter".equals(_url))  {
                this.currentFilter = new Filter_mxJPO(this);
            } else if ("/filterList/filter/name".equals(_url))  {
                this.filters.put(_content, this.currentFilter);
                this.currentFilter.parse(_url.substring(18), _content);
            } else if (_url.startsWith("/filterList/filter/"))  {
                this.currentFilter.parse(_url.substring(18), _content);

            } else if ("/queryList/query".equals(_url))  {
                this.currentQuery = new Query_mxJPO(this);
            } else if ("/queryList/query/name".equals(_url))  {
                this.queries.put(_content, this.currentQuery);
                this.currentQuery.parse(_url.substring(16), _content);
            } else if (_url.startsWith("/queryList/query/"))  {
                this.currentQuery.parse(_url.substring(16), _content);

            } else if ("/tableList/table".equals(_url))  {
                this.currentTable = new Table_mxJPO(this);
            } else if ("/tableList/table/adminProperties/name".equals(_url))  {
                this.tables.put(_content, this.currentTable);
                this.currentTable.parse(_url.substring(32), _content);
            } else if (_url.startsWith("/tableList/table/adminProperties/"))  {
                this.currentTable.parse(_url.substring(32), _content);
            } else if (_url.startsWith("/tableList/table/"))  {
                this.currentTable.parse(_url.substring(16), _content);

            } else if ("/tipList/tip".equals(_url))  {
                this.currentTip = new Tip_mxJPO(this);
            } else if ("/tipList/tip/name".equals(_url))  {
                this.tips.put(_content, this.currentTip);
                this.currentTip.parse(_url.substring(12), _content);
            } else if (_url.startsWith("/tipList/tip/"))  {
                this.currentTip.parse(_url.substring(12), _content);

            } else if ("/toolsetList/toolset".equals(_url))  {
                this.currentToolSet = new ToolSet_mxJPO(this);
            } else if ("/toolsetList/toolset/name".equals(_url))  {
                this.toolSets.put(_content, this.currentToolSet);
                this.currentToolSet.parse(_url.substring(20), _content);
            } else if (_url.startsWith("/toolsetList/toolset/"))  {
                this.currentToolSet.parse(_url.substring(20), _content);

            } else  {
                super.parse(_url, _content);
            }
        }
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
     * <li>{@link #toolSets}</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the symbolic names could not be extracted
     */
    @Override
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        super.prepare(_paramCache);

        // cues
        for (final Cue_mxJPO cue : this.cues.values())  {
            cue.prepare(_paramCache);
        }

        // filters
        for (final Filter_mxJPO filter : this.filters.values())  {
            filter.prepare(_paramCache);
        }

        // queries
        for (final Query_mxJPO query : this.queries.values())  {
            query.prepare(_paramCache);
        }

        // tables
        for (final Table_mxJPO table : this.tables.values())  {
            table.prepare(_paramCache);
        }

        // tips
        for (final Tip_mxJPO tip : this.tips.values())  {
            tip.prepare(_paramCache);
        }

        // tool sets
        for (final ToolSet_mxJPO toolSet : this.toolSets.values())  {
            toolSet.prepare(_paramCache);
        }
    }

    /**
     * Writes specific information about the cached role to the given
     * writer instance. The included information is:
     * <ul>
     * <li>{@link #cues}</li>
     * <li>{@link #filters}</li>
     * <li>{@link #queries}</li>
     * <li>{@link #tables}</li>
     * <li>{@link #tips}</li>
     * <li>{@link #toolSets}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not written
     */
    protected void writeWorkspaceObjects(final ParameterCache_mxJPO _paramCache,
                                         final Appendable _out)
        throws IOException
    {
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
    }
    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this user. Following steps are
     * done:
     * <ul>
     * <li>remove all {@link #cues}</li>
     * <li>remove all {@link #filters}</li>
     * <li>remove all {@link #queries}</li>
     * <li>remove all {@link #tables}</li>
     * <li>remove all {@link #tips}</li>
     * <li>remove all {@link #toolSets}</li>
     * </ul>
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

        // remove all assigned cues
        for (final Cue_mxJPO cue : this.cues.values())  {
            preMQLCode.append("escape delete cue \"")
                      .append(StringUtil_mxJPO.convertMql(cue.getName()))
                      .append("\" user \"")
                      .append(StringUtil_mxJPO.convertMql(this.getName()))
                      .append("\";\n");
        }

        // remove all assigned filters
        for (final Filter_mxJPO filter : this.filters.values())  {
            preMQLCode.append("escape delete filter \"")
                      .append(StringUtil_mxJPO.convertMql(filter.getName()))
                      .append("\" user \"")
                      .append(StringUtil_mxJPO.convertMql(this.getName()))
                      .append("\";\n");
        }

        // remove all assigned queries
        for (final Query_mxJPO query : this.queries.values())  {
            preMQLCode.append("escape delete query \"")
                      .append(StringUtil_mxJPO.convertMql(query.getName()))
                      .append("\" user \"")
                      .append(StringUtil_mxJPO.convertMql(this.getName()))
                      .append("\";\n");
        }

        // remove all assigned tables
        for (final Table_mxJPO table : this.tables.values())  {
            preMQLCode.append("escape delete table \"")
                      .append(StringUtil_mxJPO.convertMql(table.getName()))
                      .append("\" user \"")
                      .append(StringUtil_mxJPO.convertMql(this.getName()))
                      .append("\";\n");
        }

        // remove all assigned tips
        for (final Tip_mxJPO tip : this.tips.values())  {
            preMQLCode.append("escape delete tip \"")
                      .append(StringUtil_mxJPO.convertMql(tip.getName()))
                      .append("\" user \"")
                      .append(StringUtil_mxJPO.convertMql(this.getName()))
                      .append("\";\n");
        }

        // remove all assigned tool sets
        for (final ToolSet_mxJPO toolSet : this.toolSets.values())  {
            preMQLCode.append("escape delete toolset \"")
                      .append(StringUtil_mxJPO.convertMql(toolSet.getName()))
                      .append("\" user \"")
                      .append(StringUtil_mxJPO.convertMql(this.getName()))
                      .append("\";\n");
        }

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}
