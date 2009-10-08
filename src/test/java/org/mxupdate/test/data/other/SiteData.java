/*
 * Copyright 2008-2009 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.test.data.other;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractData;

/**
 * The class is used to define all site objects used to create / update and
 * to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class SiteData
    extends AbstractData<SiteData>
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
        super(_test, AbstractTest.CI.SITE, _name, null, null, null);
    }

    /**
     * Only a stub method, because required from {@link AbstractData}.
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
