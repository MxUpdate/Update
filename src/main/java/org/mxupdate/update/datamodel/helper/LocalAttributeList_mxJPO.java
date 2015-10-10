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

import java.util.TreeSet;

import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.AttributeCI_mxJPO;
import org.mxupdate.update.datamodel.helper.LocalAttributeList_mxJPO.LocalAttribute;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateList;
import org.mxupdate.update.util.UpdateException_mxJPO;

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

    /** Stack with all attributes for this type used within parsing. */
    private LocalAttribute curParsedAttr;

    /**
     * All attributes must be prepared.
     */
    public void prepare()
    {
        for (final LocalAttribute attr : this)  {
            attr.prepare();
        }
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
            this.curParsedAttr = new LocalAttribute(null);
            this.add(this.curParsedAttr);
            parsed = true;
        } else if ("/adminProperties/name".equals(_url))  {
            this.curParsedAttr.setName(_content);
            parsed = true;
        } else  {
            parsed = this.curParsedAttr.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }

        return parsed;
    }

    @Override
    public void write(final UpdateBuilder_mxJPO _updateBuilder)
    {
        for (final LocalAttribute attr : this)  {
            _updateBuilder
                    .stepStartNewLine()
                    .stepSingle("local attribute").stepString(attr.getName()).stepEndLineWithStartChild();

            attr.writeUpdate(_updateBuilder);

            _updateBuilder.childEnd();
        }
    }

    /**
     * Calculates the delta between current LocalAttributeList and
     * LocalAttributeList.
     *
     * @param _paramCache   parameter cache
     * @param _mql          MQL builder to append the delta
     * @param _owner        owner of the attributes
     * @param _current      current properties
     * @throws Exception
     */
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final AbstractAdminObject_mxJPO<? extends AbstractAdminObject_mxJPO<?>> _owner,
                          final LocalAttributeList_mxJPO _current)
        throws UpdateException_mxJPO
    {
        for (final LocalAttribute targetAttr : this)  {
            LocalAttribute curAttr = null;
            for (final LocalAttribute tmpAttr : _current) {
                if (tmpAttr.getName().equals(targetAttr.getName())) {
                    curAttr = tmpAttr;
                    break;
                }
            }

            // create if no current attribute exists
            if (curAttr == null)  {
                _mql.pushPrefix("")
                    .newLine().cmd("escape add attribute ").arg(targetAttr.getName())
                                        .cmd(" type ").arg(targetAttr.getKind().getAttrTypeCreate())
                                        .cmd(" owner ").cmd(_owner.getTypeDef().getMxUpdateType()).cmd(" ").arg(_owner.getName())
                    .popPrefix();
            }

            _mql.pushPrefix("escape mod attribute $1", _owner.getName() + "." + targetAttr.getName());
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
        /**
         * Constructor used to initialize the type definition.
         *
         * @param _mxName   MX name of the attribute object
         */
        public LocalAttribute(final String _mxName)
        {
            super(null, _mxName);
        }

        /**
         * Defines the MX name of the local attribute.
         *
         * @param _mxName   MX name
         */
        @Override()
        protected void setName(final String _mxName)
        {
            super.setName(_mxName);
        }

        /**
         * Method is defined to be called from the locale attribute list.
         */
        @Override()
        protected void prepare()
        {
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

        /**
         * Method is defined to be called from the locale attribute list.
         */
        @Override()
        protected void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
        {
            super.writeUpdate(_updateBuilder);
        }

        @Override()
        public int compareTo(final LocalAttribute _compareTo)
        {
            int ret = 0;
            ret = CompareToUtil_mxJPO.compare(ret, this.getName(),           _compareTo.getName());
            return ret;
        }
    }
}
