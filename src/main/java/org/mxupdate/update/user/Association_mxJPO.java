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

package org.mxupdate.update.user;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 *
 * @author The MxUpdate Team
 */
public class Association_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Stores the definition of this association instance.
     */
    private String definition = null;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the association object
     */
    public Association_mxJPO(final TypeDef_mxJPO _typeDef,
                             final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses all association specific URLs.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if ("/definition".equals(_url))  {
            this.definition = _content;
            parsed = true;

        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Writes specific information about the cached associations to the given
     * writer instance.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the association could not
     *                     be written
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        _out.append(" \\\n    ").append(this.isHidden() ? "hidden" : "!hidden")
            .append(" \\\n    definition \"").append(StringUtil_mxJPO.convertTcl(this.definition)).append("\"");
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this association. Following
     * steps are done:
     * <ul>
     * <li>reset description</li>
     * <li>set definition to current context user</li>
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
        // description and definition
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append('\"')
                .append(" description \"\"")
                .append(" definition \"").append(_paramCache.getContext().getUser()).append("\";\n");

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * Returns the stored file date within Matrix for administration object
     * with given name. The original method is overwritten, because a select
     * statement of a &quot;print&quot; command does not work.
     *
     * @param _paramCache   parameter cache
     * @param _prop         property for which the date value is searched
     * @return modified date of given update object
     * @throws MatrixException if the MQL print failed
     */
    @Override()
    public String getPropValue(final ParameterCache_mxJPO _paramCache,
                               final PropertyDef_mxJPO _prop)
        throws MatrixException
    {
        final String text = _prop.getPropName(_paramCache) + " on association " + this.getName() + " value ";
        final String curValue = MqlUtil_mxJPO.execMql(_paramCache, new StringBuilder()
                .append("list property on asso \"").append(this.getName()).append("\""));

        final int idx = curValue.indexOf(text);
        final String value;
        if (idx >= 0)  {
            final int last = curValue.indexOf('\n', idx);
            if (last > 0)  {
                value = curValue.substring(idx + text.length(), last);
            } else  {
                value = curValue.substring(idx + text.length());
            }
        } else  {
            value = null;
        }

        return value;
    }
}
