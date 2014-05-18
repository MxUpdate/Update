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
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;


/**
 * The class is used to evaluate information from attributes within MX used to
 * export, delete and update a numeric attribute.
 *
 * @author The MxUpdate Team
 * @param <CLASS> class defined from this class
 */
public abstract class AbstractAttributeNumeric_mxJPO<CLASS extends AbstractAttributeNumeric_mxJPO<CLASS>>
    extends AbstractAttribute_mxJPO<CLASS>
{
    /** Range value flag. */
    private boolean rangeValue;
    /** Stores the reference to the dimension of an attribute. */
    private String dimension;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the attribute object
     * @param _attrTypeCreate   attribute type used to create new attribute
     * @param _attrTypeList     attribute type incl. &quot;,&quot; returned
     *                          from the select list statement
     */
    public AbstractAttributeNumeric_mxJPO(final TypeDef_mxJPO _typeDef,
                                          final String _mxName,
                                          final String _attrTypeCreate,
                                          final String _attrTypeList)
    {
        super(_typeDef, _mxName, _attrTypeCreate, _attrTypeList);
    }

    /**
     * The method parses the real attribute specific XML URLs. This includes
     * information about:
     * <ul>
     * <li>contains the attribute {@link #rangeValue rangevalues}?</li>
     * </ul>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if ("/attrValueType".equals(_url) && "2".equals(_content))  {
            this.rangeValue = true;
            parsed = true;
        } else if ("/dimensionRef".equals(_url))  {
            this.dimension = _content;
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * {@inheritDoc}
     * Appends the integer attribute specific values. Following values are
     * written:
     * <ul>
     * <li>{@link #rangeValue range value} flag (if
     *     {@link ValueKeys#DMAttrSupportsFlagRangeValue} is defined)</li>
     * <li>{@link #dimension} (if
     *     {@link ValueKeys#DMAttrSupportsDimension} is defined)</li>
     * </ul>
     */
    @Override()
    protected void writeAttributeSpecificValues(final ParameterCache_mxJPO _paramCache,
                                                final Appendable _out)
        throws IOException
    {
        super.writeAttributeSpecificValues(_paramCache, _out);
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagRangeValue))  {
            _out.append("  ").append(this.rangeValue ? "" : "!").append("rangevalue\n");
        }
        if ((this.dimension != null) && _paramCache.getValueBoolean(ValueKeys.DMAttrSupportsDimension))  {
            _out.append("  dimension \"").append(StringUtil_mxJPO.convertTcl(this.dimension)).append("\"\n");
        }
    }

    /**
     * {@inheritDoc}
     * Appends the integer attribute specific delta values. Following values are
     * calculated:
     * <ul>
     * <li>{@link #rangeValue range value} flag (if
     *     {@link ValueKeys#DMAttrSupportsFlagRangeValue} is defined)</li>
     * <li>{@link #dimension} (if
     *     {@link ValueKeys#DMAttrSupportsDimension} is defined)</li>
     * <ul>
     *
     * @throws UpdateException_mxJPO if range values flag is removed or
     *                               existing dimension is updated
     */
    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MqlBuilder_mxJPO _mql,
                             final CLASS _target)
        throws UpdateException_mxJPO
    {
        super.calcDelta(_paramCache, _mql, _target);

        final AbstractAttributeNumeric_mxJPO<CLASS> target = _target;

        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagRangeValue))  {
            if (!this.rangeValue)  {
                DeltaUtil_mxJPO.calcFlagDelta(_mql, "rangevalue", target.rangeValue, this.rangeValue);
            } else if (!target.rangeValue)  {
                throw new UpdateException_mxJPO(
                        UpdateException_mxJPO.Error.ABSTRACTATTRIBUTE_UPDATE_RANGEVALUEFLAG_UPDATED,
                        this.getName());
            }
        }

        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsDimension))  {
            if ((this.dimension == null) || this.dimension.isEmpty()) {
                if ((target.dimension != null) && !target.dimension.isEmpty())  {
                    _paramCache.logDebug("    - set dimension '" + target.dimension + "'");
                    _mql.newLine()
                        .append("dimension \"").append(StringUtil_mxJPO.convertMql(target.dimension)).append('\"');
                }
            } else if (!this.dimension.equals(target.dimension))  {
                throw new UpdateException_mxJPO(
                        UpdateException_mxJPO.Error.ABSTRACTATTRIBUTE_UPDATE_DIMENSION_UPDATED,
                        this.getName(),
                        this.dimension,
                        (target.dimension != null) ? target.dimension : "");
            }
        }
    }
}
