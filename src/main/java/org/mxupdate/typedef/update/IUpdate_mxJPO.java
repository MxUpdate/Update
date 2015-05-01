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

package org.mxupdate.typedef.update;

import java.io.File;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * Abstract definition to update a CI object with given file.
 *
 * @author The MxUpdate Team
 */
public interface IUpdate_mxJPO
{
    /**
     * Updates this administration (business) object if the stored information
     * about the version is not the same as the file date. If an update is
     * required, the file is read and the object is updated with
     *
     * @param _paramCache   parameter cache
     * @param _typeDef      type definition
     * @param _create       <i>true</i> if the CI object is new created (and
     *                      first update is done)
     * @param _name         MX name to update
     * @param _file         file with TCL update code
     * @throws Exception if the update from the derived class failed
     */
    void update(final ParameterCache_mxJPO _paramCache,
                final TypeDef_mxJPO _typeDef,
                final boolean _create,
                final String _name,
                final File _file)
        throws Exception;
}
