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

package org.mxupdate.test.data.other;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * The class is used to define all site objects used to create / update and
 * to export.
 *
 * @author The MxUpdate Team
 */
public class SiteData
    extends AbstractAdminData<SiteData>
{
    /**
     * Constructor to initialize this site.
     *
     * @param _test     related test implementation (where this site is
     *                  defined)
     * @param _name     name of the site
     */
    public SiteData(final AbstractTest _test,
                    final String _name)
    {
        super(_test, AbstractTest.CI.OTHER_SITE, _name);
    }

    /**
     * Only a stub method, because required from {@link AbstractAdminData}.
     *
     * @return always <code>null</code>
     */
    @Override()
    public String ciFile()
    {
        return null;
    }

    /**
     * Creates this site.
     *
     * @return this site instance
     * @throws MatrixException if create failed
     */
    @Override()
    public SiteData create() throws MatrixException
    {
        if (!this.isCreated())  {
            final StringBuilder cmd = new StringBuilder()
                    .append("escape add site \"").append(AbstractTest.convertMql(this.getName())).append("\"");
            this.getTest().mql(cmd);
            this.setCreated(true);
        }
        return this;
    }
}
