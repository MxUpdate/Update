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

package org.mxupdate.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Data model type class.
 *
 * @author The MxUpdate Team
 */
public class Type_mxJPO
    extends AbstractDMWithAttributes_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for types.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Type_mxJPO.IGNORED_URLS.add("/derivedFrom");
        Type_mxJPO.IGNORED_URLS.add("/derivedFrom/typeRefList");
        Type_mxJPO.IGNORED_URLS.add("/methodList");
    }

    /**
     * Is the type abstract?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private boolean abstractFlag = false;

    /**
     * From which type is this type derived?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String derived = "ADMINISTRATION";

    /**
     * Defines all methods of this type.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Set<String> methods = new TreeSet<String>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the type object
     */
    public Type_mxJPO(final TypeDef_mxJPO _typeDef,
                      final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * <p>Parses all type specific values. This includes:
     * <ul>
     * <li>{@link #abstractFlag information about is the type abstract}</li>
     * <li>{@link #derived from information from which type this type is
     *     derived}</li>
     * <li>{@link #methods type methods}</li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #IGNORED_URLS
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Type_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/abstract".equals(_url))  {
            this.abstractFlag = true;
            parsed = true;
        } else if ("/derivedFrom/typeRefList/typeRef".equals(_url))  {
            this.derived = _content;
            parsed = true;
        } else if ("/methodList/programRef".equals(_url))  {
            this.methods.add(_content);
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Writes the type specific information in the TCL update file
     * <code>_out</code>.
     * The type specific information is:
     * <ul>
     * <li>{@link #derived}</li>
     * <li>{@link #isHidden()}</li>
     * <li>{@link #abstractFlag}</li>
     * <li>{@link #methods}</li>
     * <li>and triggers with {@link #writeTriggers(Appendable)}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the type could not be
     *                     written
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        _out.append(" \\\n    derived \"").append(StringUtil_mxJPO.convertTcl(this.derived)).append('\"')
            .append(" \\\n    ").append(this.isHidden() ? "" : "!").append("hidden")
            .append(" \\\n    abstract \"").append(Boolean.toString(this.abstractFlag)).append('\"');
        // methods
        for (final String method : this.methods)  {
            _out.append(" \\\n    add method \"").append(StringUtil_mxJPO.convertTcl(method)).append('\"');
        }
        // triggers
        this.getTriggers().write(_out, " \\\n    add ", "");
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this type. Following steps are
     * done:
     * <ul>
     * <li>set to not hidden</li>
     * <li>set to not abstract</li>
     * <li>reset description</li>
     * <li>remove all {@link #methods}</li>
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
        final StringBuilder preMQLCode = new StringBuilder()
                .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                .append(" !hidden description \"\" abstract false");
        // remove methods
        for (final String method : this.methods)  {
            preMQLCode.append(" remove method \"")
                      .append(StringUtil_mxJPO.convertMql(method))
                      .append('\"');
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}
