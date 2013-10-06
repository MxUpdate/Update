/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.update.system;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Handles the export and update of the &quot;searchindex&quot; system
 * configuration.
 *
 * @author The MxUpdate Team
 */
public class ConfigSystemSearchIndex_mxJPO
    extends AbstractObject_mxJPO
{
    /**
     * Used default name of the search configuration.
     *
     * @see #getMxNames(ParameterCache_mxJPO)
     */
    private final static String DEFAULT_NAME = "Config"; //$NON-NLS-1$

    /**
     * MQL statement to get the search index configuration from the system.
     *
     * @see #parse(ParameterCache_mxJPO)
     */
    private final static String MQL_GET_INDEX = "print system searchindex"; //$NON-NLS-1$

    /**
     * Start string of the returned search index configuration (because the
     * search index is stored as a property...).
     *
     * @see #parse(ParameterCache_mxJPO)
     */
    private final static String START_KEYWORD = "SearchIndex="; //$NON-NLS-1$

    /**
     * Length of the start string of the returned search index configuration.
     *
     * @see #parse(ParameterCache_mxJPO)
     */
    private final static int START_KEYWORD_LENGTH = ConfigSystemSearchIndex_mxJPO.START_KEYWORD.length();

    /**
     * Stores the read search index configuration.
     *
     * @see #parse(ParameterCache_mxJPO)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String searchIndex;

    /**
     * Constructor used to initialize the type definition enumeration and the
     * name.
     *
     * @param _typeDef      defines the related type definition enumeration
     * @param _mxName       MX name of the page object
     */
    public ConfigSystemSearchIndex_mxJPO(final TypeDef_mxJPO _typeDef,
                                   final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Reads the search index configuration with {@link #MQL_GET_INDEX MQL},
     * removes the {@link #START_KEYWORD prefix} and stores the result in
     * {@link #searchIndex}.
     *
     * @param _paramCache   parameter cache with MX context
     * @throws MatrixException if the search index could not be read
     */
    @Override()
    protected void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        final String tmp = MqlUtil_mxJPO.execMql(_paramCache, ConfigSystemSearchIndex_mxJPO.MQL_GET_INDEX);
        if (tmp.startsWith(ConfigSystemSearchIndex_mxJPO.START_KEYWORD))  {
            this.searchIndex = tmp.substring(ConfigSystemSearchIndex_mxJPO.START_KEYWORD_LENGTH);
        }
    }

    /**
     * Appends the {@link #searchIndex search index} configuration to the
     * appendable instance {@code _out}.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _out          appendable instance to write the TCL update
     *                      code
     * @throws IOException  if write of the configuration file failed
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        _out.append(this.searchIndex);
    }

    /**
     * @param _paramCache   parameter cache with MX context (not used)
     */
    @Override()
    public Set<String> getMxNames(final ParameterCache_mxJPO _paramCache)
    {
        final Set<String> names = new HashSet<String>(1);
        names.add(ConfigSystemSearchIndex_mxJPO.DEFAULT_NAME);
        return names;
    }

    @Override()
    public void delete(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        // TODO Auto-generated method stub
throw new Exception("delete of search index not possible!");
    }

    /**
     * Deactivated because not possible.
     *
     * @param _paramCache   not used
     * @param _file         file
     */
    @Override()
    public void create(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        // TODO Auto-generated method stub
throw new Exception("create of search index not possible!");
    }

    @Override()
    public void update(final ParameterCache_mxJPO _paramCache,
                       final boolean _create,
                       final File _file,
                       final String _newVersion)
        throws MatrixException
    {
// TODO: use formatted strings...
        MqlUtil_mxJPO.execMql(
                _paramCache,
                new StringBuilder()
                        .append("escape set system searchindex file \"")
                        .append(StringUtil_mxJPO.convertMql(_file.toString())));
    }

    /**
     * Dummy implementation because the search index configuration does not
     * support property values.
     *
     * @return always {@code null} because a search index configuration could
     *         not have property values
     */
    @Override()
    public String getPropValue(final ParameterCache_mxJPO _paramCache,
                               final PropertyDef_mxJPO _prop)
    {
        return null;
    }

    /**
     * Dummy implementation because the search index configuration does not
     * support symbolic names (because exact one search index configuration
     * exists in MX).
     *
     * @return always {@code null} because a search index configuration could
     *         not have symbolic names
     */
    @Override()
    protected Set<String> getSymbolicNames()
    {
        return null;
    }
}
