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

import java.io.IOException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to evaluate information from string attributes within MX
 * used to export, delete and update a string attribute.
 *
 * @author The MxUpdate Team
 */
public class AttributeString_mxJPO
    extends AbstractAttribute_mxJPO<AttributeString_mxJPO>
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
     * <ul>
     * <li>{@link #multiline multiple line} flag</li>
     * <li>{@link #maxLength maximum length} (if parameter
     *     {@link ValueKeys#DMAttrSupportsPropMaxLength} is defined)</li>
     * </ul>
     */
    @Override()
    protected void writeAttributeSpecificValues(final ParameterCache_mxJPO _paramCache,
                                                final Appendable _out)
        throws IOException
    {
        _out.append("  ").append(this.multiline ? "" : "!").append("multiline\n");
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsPropMaxLength))  {
            _out.append("  maxlength ").append(this.maxLength).append("\n");
        }
    }

    /**
     * {@inheritDoc}
     * The method overwrites the original method to calculate following delta
     * values:
     * <ul>
     * <li>{@link #multiline multiple line} flag</li>
     * <li>{@link #maxLength maximum length} (if parameter
     *     {@link ValueKeys#DMAttrSupportsPropMaxLength} is defined)</li>
     * </ul>
     */
    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MqlBuilder_mxJPO _mql,
                             final AttributeString_mxJPO _target)
        throws UpdateException_mxJPO
    {
        super.calcDelta(_paramCache, _mql, _target);

        DeltaUtil_mxJPO.calcFlagDelta(_mql, "multiline", _target.multiline, this.multiline);
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsPropMaxLength))  {
            DeltaUtil_mxJPO.calcValueDelta(_mql, "maxlength", _target.maxLength, this.maxLength);
        }
    }
}
