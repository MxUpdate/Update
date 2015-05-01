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

package org.mxupdate.update.datamodel.helper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * Handles list of attributes.
 *
 * @author The MxUpdate Team
 */
public class AttributeList_mxJPO
{
    /** Parent object (needed for throwing exceptions with delta calculation). */
    private final AbstractAdminObject_mxJPO parent;
    /** List of all attributes for this data model administration object. */
    private final SortedSet<String> attributes = new TreeSet<String>();

    public AttributeList_mxJPO(final AbstractAdminObject_mxJPO _parent)
    {
        this.parent = _parent;
    }

    /**
     * Checks if the URL to parse defined an attribute and appends this
     * name of attribute to the list of attributes {@link #attributes}.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #attributes
     * @see #IGNORED_URLS
     */
    public boolean parse(final ParameterCache_mxJPO _paramCache,
                         final String _url,
                         final String _content)
    {
        final boolean parsed;
        if ("/attributeDefRef".equals(_url))  {
            this.attributes.add(_content);
            parsed = true;
        } else  {
            parsed = false;
        }
        return parsed;
    }

    /**
     * Writes the sorted attributes information to the update builder
     *
     * @param _updateBuilder    update builder
     */
    public void write(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder.list("attribute", this.attributes);
    }

    /**
     * Calculates the delta between given {@code _current} attribute list
     * definition and this target attribute list definition and appends the MQL
     * append commands to {@code _mql}.
     *
     * @param _paramCache   parameter cache
     * @param _mql          builder to append the MQL commands
     * @param _current      current attribute list definition
     * @throws UpdateException_mxJPO if update is not allowed (because data can
     *                      be lost)
     */
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final AttributeList_mxJPO _current)
        throws UpdateException_mxJPO
    {
        final Set<String> ignoreAttrs = new HashSet<String>();
        final Set<String> removeAttrs = new HashSet<String>();

        // for relationship append ignored / removed attributes
        if ("relationship".equals(this.parent.getTypeDef().getMxAdminName()))  {
            final Collection<String> tmp1 = _paramCache.getValueList(ValueKeys.DMRelationAttrIgnore);
            if (tmp1 != null)  {
                ignoreAttrs.addAll(tmp1);
            }
            final Collection<String> tmp2 = _paramCache.getValueList(ValueKeys.DMRelationAttrRemove);
            if (tmp2 != null)  {
                removeAttrs.addAll(tmp2);
            }
        }

        boolean equal = (this.attributes.size() == _current.attributes.size());
        if (equal)  {
            for (final String attribute : _current.attributes)  {
                if (!this.attributes.contains(attribute))  {
                    equal = false;
                    break;
                }
            }
        }
        if (!equal)  {
            for (final String curValue : _current.attributes)  {
                if (!this.attributes.contains(curValue))  {
                    boolean ignore = false;
                    for (final String ignoreAttr : ignoreAttrs)  {
                        if (StringUtil_mxJPO.match(curValue, ignoreAttr))  {
                            ignore = true;
                            _paramCache.logDebug("    - attribute '" + curValue + "' is not assigned anymore and therefore ignored");
                            break;
                        }
                    }
                    if (!ignore)  {
                        boolean remove = false;
                        for (final String removeAttr : removeAttrs)  {
                            if (StringUtil_mxJPO.match(curValue, removeAttr))  {
                                remove = true;
                                _paramCache.logDebug("    - attribute '" + curValue + "' is not assigned anymore and therefore removed");
                                _mql.newLine().cmd("remove attribute ").arg(curValue);
                                break;
                            }
                        }
                        if (!remove)  {
                            throw new UpdateException_mxJPO(
                                    UpdateException_mxJPO.Error.DM_ABSTRACTWITHATTRIBUTES_UPDATE_ATTRIBUTE_REMOVED,
                                    curValue,
                                    this.parent.getTypeDef().getLogging(),
                                    this.parent.getName());
                        }
                    }
                }
            }
            for (final String newValue : this.attributes)  {
                if (!_current.attributes.contains(newValue))  {
                    _paramCache.logDebug("    - attribute '" + newValue + "' is added");
                    _mql.newLine().cmd("add attribute ").arg(newValue);
                }
            }
        }
    }
}
