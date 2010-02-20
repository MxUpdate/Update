/*
 * Copyright 2008-2010 The MxUpdate Team
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

package org.mxupdate.test.data.datamodel;

import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * Used to define a format, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class FormatData
    extends AbstractAdminData<FormatData>
{
    /**
     * Within export the description must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(1);
    static  {
        FormatData.REQUIRED_EXPORT_VALUES.add("description");
    }

    /**
     * Initialize this format data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this format is
     *                  defined)
     * @param _name     name of the format
     */
    public FormatData(final AbstractTest _test,
                      final String _name)
    {
        super(_test, AbstractTest.CI.DM_FORMAT, _name, FormatData.REQUIRED_EXPORT_VALUES);
    }

    @Override
    public String ciFile()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FormatData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add format \"").append(AbstractTest.convertMql(this.getName())).append('\"');
            this.append4Create(cmd);
            this.getTest().mql(cmd);
        }
        return this;
    }

}
