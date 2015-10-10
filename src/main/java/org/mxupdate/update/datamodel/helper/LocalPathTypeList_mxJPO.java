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

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.datamodel.PathType_mxJPO;
import org.mxupdate.update.datamodel.helper.LocalPathTypeList_mxJPO.LocalPathType;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateList;

/**
 * Handles list of local path type definitions.
 *
 * @author The MxUpdate Team
 */
public class LocalPathTypeList_mxJPO
    extends TreeSet<LocalPathType>
    implements UpdateList
{
    /** Generated serial version UID. */
    private static final long serialVersionUID = -8536421946458610313L;

    /**
     * All path types rae prepared.
     */
    public void prepare()
    {
        for (final LocalPathType localPathType : this)  {
            localPathType.prepare();
        }
    }

    @Override
    public void write(final UpdateBuilder_mxJPO _updateBuilder)
    {
        for (final LocalPathType localPathType : this)  {
            _updateBuilder
                    .stepStartNewLine()
                    .stepSingle("local pathtype").stepString(localPathType.getName()).stepEndLineWithStartChild();
            localPathType.writeUpdate(_updateBuilder);
            _updateBuilder.childEnd();
        }
    }

    /**
     * Local path type definition (path types with owner).
     */
    public static class LocalPathType
        extends PathType_mxJPO
        implements Comparable<LocalPathType>
    {
        /**
         * Constructor used to initialize the local path type definition.
         *
         * @param _mxName   MX name of the local path type object
         */
        public LocalPathType(final String _mxName)
        {
            super((TypeDef_mxJPO) null, _mxName);
        }

        /**
         * Defines the MX name of the local path type.
         *
         * @param _mxName   MX name
         */
        @Override
        protected void setName(final String _mxName)
        {
            super.setName(_mxName);
        }

        /**
         * Method is defined to be called from the locale attribute list.
         */
        @Override
        protected void prepare()
        {
            super.prepare();
        }

        /**
         * Method is defined to be called from the locale attribute list.
         */
        @Override
        protected void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
        {
            super.writeUpdate(_updateBuilder);
        }

        @Override
        public int compareTo(final LocalPathType _compareTo)
        {
            int ret = 0;
            ret = CompareToUtil_mxJPO.compare(ret, this.getName(),           _compareTo.getName());
            return ret;
        }
    }
}
