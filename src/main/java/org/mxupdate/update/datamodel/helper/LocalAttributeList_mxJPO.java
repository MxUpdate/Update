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

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.AttributeCI_mxJPO;
import org.mxupdate.update.datamodel.helper.LocalAttributeList_mxJPO.LocalAttribute;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateList;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

import matrix.util.MatrixException;

/**
 * Handles list of local attribute definitions.
 *
 * @author The MxUpdate Team
 */
public class LocalAttributeList_mxJPO
    extends TreeSet<LocalAttribute>
    implements UpdateList
{
    /** Generated serial version UID. */
    private static final long serialVersionUID = 5355483487463244678L;

    /** Owner of this attribute. */
    private final AbstractAdminObject_mxJPO<? extends AbstractAdminObject_mxJPO<?>> owner;

    /** Stack with all attributes for this type used within parsing. */
    private LocalAttribute curParsedAttr;

    /**
     * Constructor.
     *
     * @param _owner    owner of this attribute list
     */
    public LocalAttributeList_mxJPO(final AbstractAdminObject_mxJPO<? extends AbstractAdminObject_mxJPO<?>> _owner)
    {
        this.owner = _owner;
    }

    /**
     * Parses the local attribute list definition.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        boolean parsed = false;

        if ("".equals(_url))  {
            this.curParsedAttr = new LocalAttribute();
            this.add(this.curParsedAttr);
            parsed = true;
        } else if ("/adminProperties/name".equals(_url))  {
            this.curParsedAttr.setLocalName(_content);
            parsed = true;
        } else  {
            parsed = this.curParsedAttr.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }

        return parsed;
    }

    /**
     * Parses the symbolic names of all attributes.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if parse of symbolic names failed
     */
    public void parseSymbolicNames(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        for (final LocalAttribute attr : this)  {
            attr.parseDBFinish(_paramCache);
        }
    }

    /**
     * All attributes must be prepared.
     */
    public void prepare()
    {
        for (final LocalAttribute attr : this)  {
            attr.prepare(this.owner);
        }
        // sort local attributes (because name is set after parsing!)
        final Set<LocalAttribute> localAttributes = new HashSet<>(this);
        this.clear();
        this.addAll(localAttributes);
    }

    @Override
    public void write(final UpdateBuilder_mxJPO _updateBuilder)
    {
        for (final LocalAttribute attr : this)  {
            _updateBuilder
                    .stepStartNewLine()
                    .stepSingle("local attribute").stepString(attr.localName).stepEndLineWithStartChild();

            attr.writeUpdate(_updateBuilder);

            _updateBuilder.childEnd();
        }
    }

    /**
     * Calculates the delta between current LocalAttributeList and
     * LocalAttributeList.
     *
     * @param _paramCache           parameter cache
     * @param _mql                  MQL builder to append the delta
     * @param _errorKeyAttrRemoved  error key for the case that an attribute is
     *                              removed
     * @param _current              current properties
     * @throws Exception
     */
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final ErrorKey _errorKeyAttrRemoved,
                          final LocalAttributeList_mxJPO _current)
        throws UpdateException_mxJPO
    {
        // check for removed attributes
        if (_current != null)  {
            for (final LocalAttribute tmpAttr : _current) {
                boolean found = false;
                for (final LocalAttribute targetAttr : this)  {
                    if (tmpAttr.getName().equals(targetAttr.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found)  {
                    throw new UpdateException_mxJPO(_errorKeyAttrRemoved, tmpAttr.getName(), this.owner.getName());
                }
            }
        }

        // delta calculation for added / updated attributes
        for (final LocalAttribute targetAttr : this)  {
            LocalAttribute curAttr = null;
            if (_current != null)  {
                for (final LocalAttribute tmpAttr : _current) {
                    if (tmpAttr.getName().equals(targetAttr.getName())) {
                        curAttr = tmpAttr;
                        break;
                    }
                }
            }

            // create if no current attribute exists
            if (curAttr == null)  {
                _paramCache.logDebug("    - local attribute '" + targetAttr.getName() + "' is added");
                _mql.pushPrefix("")
                    .newLine().cmd("escape add ").cmd(EMxAdmin_mxJPO.Attribute.mxClass()).cmd(" ").arg(targetAttr.localName)
                                        .cmd(" type ").arg(targetAttr.getKind().getAttrTypeCreate())
                                        .cmd(" owner ").cmd(this.owner.mxClassDef().mxClass()).cmd(" ").arg(this.owner.getName())
                    .popPrefix();
            }

            // update attribute
            _mql.pushPrefix("escape mod " + EMxAdmin_mxJPO.Attribute.mxClass() + " $1", targetAttr.getName());
            targetAttr.calcDelta(_paramCache, _mql, curAttr);
            _mql.popPrefix();
        }
    }

    /**
     * Local attribute definition (attributes with owner).
     */
    public static class LocalAttribute
        extends AttributeCI_mxJPO
        implements Comparable<LocalAttribute>
    {
        /** Local attribute name. */
        private String localName;

        /**
         * Constructor used to initialize the type definition.
         */
        public LocalAttribute()
        {
            super((String) null);
        }

        /**
         * Defines the MX name of the local attribute.
         *
         * @param _mxName   MX name
         * @return this local attribute instance
         */
        public LocalAttribute setLocalName(final String _mxName)
        {
            this.localName = _mxName;
            return this;
        }

        @Override
        protected void parseDBFinish(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
        {
            super.parseDBFinish(_paramCache);
        }

        /**
         * Method is defined to be called from the locale attribute list.
         *
         * @param _owner    owner of this attribute
         */
        protected void prepare(final AbstractAdminObject_mxJPO<? extends AbstractAdminObject_mxJPO<?>> _owner)
        {
            this.setName(_owner.getName() + "." + this.localName);
            super.prepare();
        }

        /**
         * Calculates the delta between this local attribute and current
         * attribute definition.
         *
         * @param _paramCache   parameter cache
         * @param _mql          builder to append the MQL commands
         * @param _current      current admin object definition
         * @throws UpdateException_mxJPO if update is not allowed (e.g. if data
         *                      can be potentially lost)
         */
        protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                                 final MultiLineMqlBuilder _mql,
                                 final LocalAttribute _current)
            throws UpdateException_mxJPO
        {
            super.calcDelta(_paramCache, _mql, _current);
        }

        @Override
        public int compareTo(final LocalAttribute _compareTo)
        {
            int ret = 0;
            ret = CompareToUtil_mxJPO.compare(ret, this.localName, _compareTo.localName);
            return ret;
        }
    }
}
