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
import org.mxupdate.update.datamodel.PathType_mxJPO;
import org.mxupdate.update.datamodel.helper.LocalPathTypeList_mxJPO.LocalPathType;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateList;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

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

    /** Stack with all local path type used within parsing. */
    private LocalPathType curParsedPathType;

    /**
     * All path types are prepared.
     */
    public void prepare()
    {
        for (final LocalPathType localPathType : this)  {
            localPathType.prepare();
        }
    }

    /**
     * Parses the local path type list definition.
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
            this.curParsedPathType = new LocalPathType(null);
            this.add(this.curParsedPathType);
            parsed = true;
        } else if ("/adminProperties/name".equals(_url))  {
            this.curParsedPathType.setName(_content);
            parsed = true;
        } else  {
            parsed = this.curParsedPathType.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }

        return parsed;
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
     * Calculates the delta between current local path type list and
     * local path type list.
     *
     * @param _paramCache           parameter cache
     * @param _mql                  MQL builder to append the delta
     * @param _owner                owner of the attributes
     * @param _errorKeyAttrRemoved  error key for the case that an attribute is
     *                              removed
     * @param _current              current properties
     * @throws UpdateException_mxJPO if calculation of the delta failed
     */
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final AbstractAdminObject_mxJPO<? extends AbstractAdminObject_mxJPO<?>> _owner,
                          final ErrorKey _errorKeyAttrRemoved,
                          final LocalPathTypeList_mxJPO _current)
        throws UpdateException_mxJPO
    {
        // check for removed attributes
        for (final LocalPathType tmpLocalPathType : _current) {
            boolean found = false;
            for (final LocalPathType targetLocalPathType : this)  {
                if (tmpLocalPathType.getName().equals(targetLocalPathType.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found)  {
                throw new UpdateException_mxJPO(_errorKeyAttrRemoved, tmpLocalPathType.getName(), _owner.getName());
            }
        }

        // delta calculation for added / updated attributes
        for (final LocalPathType targetLocalPathType : this)  {
            LocalPathType curLocalPathType = null;
            for (final LocalPathType tmpLocalPathType : _current) {
                if (tmpLocalPathType.getName().equals(targetLocalPathType.getName())) {
                    curLocalPathType = tmpLocalPathType;
                    break;
                }
            }

            // create if no current attribute exists
            if (curLocalPathType == null)  {
                _paramCache.logDebug("    - local path type '" + targetLocalPathType.getName() + "' is added");
                _mql.pushPrefix("")
                    .newLine().cmd("escape add pathtype ").arg(targetLocalPathType.getName())
                                        .cmd(" owner ").cmd(_owner.mxClassDef().mxClass()).cmd(" ").arg(_owner.getName())
                    .popPrefix();
            }

            // update attribute
            // (hint: the name of the local path type is set to new name,
            // because a local path type can also contain local attributes!)
            final String tmp = targetLocalPathType.getName();
            targetLocalPathType.setName(_owner.getName() + "." + targetLocalPathType.getName());
            _mql.pushPrefix("escape mod pathtype $1", targetLocalPathType.getName());
            targetLocalPathType.calcDelta(_paramCache, _mql, curLocalPathType);
            _mql.popPrefix();
            targetLocalPathType.setName(tmp);
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
            super(_mxName);
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
         * Calculates the delta between this local path type and current
         * local path type definition.
         *
         * @param _paramCache   parameter cache
         * @param _mql          builder to append the MQL commands
         * @param _current      current admin object definition
         * @throws UpdateException_mxJPO if update is not allowed (e.g. if data
         *                      can be potentially lost)
         */
        protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                                 final MultiLineMqlBuilder _mql,
                                 final LocalPathType _current)
            throws UpdateException_mxJPO
        {
            super.calcDelta(_paramCache, _mql, _current);
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
