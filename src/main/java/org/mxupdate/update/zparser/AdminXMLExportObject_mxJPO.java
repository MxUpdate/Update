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

package org.mxupdate.update.zparser;

import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * Interface to define method which are called within read of the admin XML
 * export.
 *
 * @author The MxUpdate Team
 */
public interface AdminXMLExportObject_mxJPO
{
    /**
     * Method is called from the admin XML export parser as event for each
     * {@code _url} and depending {@code _code}.
     *
     * @param _paramCache   parameter cache
     * @param _url          current URL
     * @param _content      content for given URL
     * @return <i>true</i> if URL can be parsed; otherwise <i>false</i>
     */
    boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                     final String _url,
                                     final String _content);
}
