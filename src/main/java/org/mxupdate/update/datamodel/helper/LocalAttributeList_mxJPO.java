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

import org.mxupdate.update.datamodel.AttributeCI_mxJPO;
import org.mxupdate.update.datamodel.helper.LocalAttributeList_mxJPO.LocalAttribute;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateList;

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

    /**
     * All attributes must be prepared.
     */
    public void prepare()
    {
        for (final LocalAttribute attr : this)  {
            attr.prepare();
        }
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
         * Method is defined to be called from the locale attribute list.
         */
        @Override()
        protected void prepare()
        {
            super.prepare();
        }

        /**
         * Method is defined to be called from the locale attribute list.
         */
        @Override()
        protected void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
        {
            super.writeUpdate(_updateBuilder);
        }

        @Override
        public int compareTo(final LocalAttribute _compareTo)
        {
            int ret = 0;
            ret = CompareToUtil_mxJPO.compare(ret, this.getName(),           _compareTo.getName());
            return ret;
        }
    }
}
