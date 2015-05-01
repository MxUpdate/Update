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

package org.mxupdate.typedef.filenames;

import java.io.File;
import java.util.Collection;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * Matches given file names against business objects depending on match
 * criteria's.
 *
 * @author The MxUpdate Team
 */
public class FileNamesBusObject_mxJPO
    extends FileNamesAdmin_mxJPO
{
    /**
     * {@inheritDoc}
     * If the type definition has derived types and the extracted name does not
     * include the the type, the business type is added.
     */
    @Override()
    protected String evalMxName(final ParameterCache_mxJPO _paramCache,
                                final TypeDef_mxJPO _typeDef,
                                final File _file)
        throws UpdateException_mxJPO
    {
        String ret = super.evalMxName(_paramCache, _typeDef, _file);
        if ((ret != null) && _typeDef.hasMxBusTypeDerived() && !ret.contains(BusObject_mxJPO.SPLIT_TYPE))  {
            ret = new StringBuilder().append(_typeDef.getMxBusType())
                                     .append(BusObject_mxJPO.SPLIT_TYPE)
                                     .append(ret).toString();
        }
        return ret;
    }

    /**
     * Checks if given MX name without prefix and suffix matches given match
     * string. The MX name is split with {@link #SPLIT_NAME} to get
     * the name and revision of the business object. A MX name matches if the
     * business object name or revision matches.
     *
     * @param _paramCache   parameter cache
     * @param _mxName       name (and revision) of the administration business
     *                      object
     * @param _matches      collection string which must be matched
     * @return <i>true</i> if the given MX name matches; otherwise <i>false</i>
     */
    @Override()
    protected boolean matchMxName(final ParameterCache_mxJPO _paramCache,
                                  final String _mxName,
                                  final Collection<String> _matches)
    {
        final String[] nameRev = _mxName.split(BusObject_mxJPO.SPLIT_NAME);
        return (StringUtil_mxJPO.match(nameRev[0], _matches) || ((nameRev.length > 1) && StringUtil_mxJPO.match(nameRev[1], _matches)));
    }
}
