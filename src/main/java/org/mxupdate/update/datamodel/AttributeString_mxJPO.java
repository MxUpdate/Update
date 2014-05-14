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
import java.util.Map;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to evaluate information from string attributes within MX
 * used to export, delete and update a string attribute.
 *
 * @author The MxUpdate Team
 */
public class AttributeString_mxJPO
    extends AbstractAttribute_mxJPO
{
    /** The attribute is a multi line attribute. */
    private boolean multiline = false;

    /** Maximum length of the value for string attributes. */
    private String maxLength = "0";

    /**
     * Constructor used to initialize the string attribute instance with
     * related type definition and attribute name.
     *
     * @param _typeDef  defines the related type definition
     * @param _mxName   MX name of the string attribute object
     */
    public AttributeString_mxJPO(final TypeDef_mxJPO _typeDef,
                                 final String _mxName)
    {
        super(_typeDef, _mxName, "string", "string,");
    }

    /**
     * The method parses the string attribute specific XML URLs. This includes
     * information about:
     * <ul>
     * <li>{@link #maxLength max length}</li>
     * <li>is the attribute {@link #multiline multi line}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
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
        if ("/maxlength".equals(_url))  {
            this.maxLength = _content;
            parsed = true;
        } else if ("/multiline".equals(_url))  {
            this.multiline = true;
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * {@inheritDoc}
     * Appends the string attribute specific values. Following values are
     * written:
     * <li>{@link #multiline multiple line} flag</li>
     * <li>{@link #maxLength maximum length} (if attribute is from
     *     {@link #type} string and if parameter
     *     {@link ValueKeys#DMAttrSupportsPropMaxLength} is defined)</li>
     */
    @Override()
    protected void writeAttributeSpecificValues(final ParameterCache_mxJPO _paramCache,
                                                final Appendable _out)
        throws IOException
    {
        _out.append(" \\\n    ").append(this.multiline ? "" : "!").append("multiline");
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsPropMaxLength))  {
            _out.append(" \\\n    maxlength \"").append(this.maxLength).append("\"");
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this string attribute. This
     * includes:
     * <ul>
     * <li>flag &quot;multiline&quot; is disabled</li>
     * <li>maximum length is set to 0</li>
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
        // remove all properties
        final StringBuilder preMQLCode = new StringBuilder()
                .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                .append(" !multiline");
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsPropMaxLength))  {
            preMQLCode.append(" maxlength 0");
        }
        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}
