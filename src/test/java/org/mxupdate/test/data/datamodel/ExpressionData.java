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

import java.util.HashMap;
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * Used to define an expression, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class ExpressionData
    extends AbstractAdminData<ExpressionData>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,String> REQUIRED_EXPORT_VALUES = new HashMap<String,String>();
    static  {
        ExpressionData.REQUIRED_EXPORT_VALUES.put("description", "");
    }

    /**
     * Initialize this expression data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this expression is
     *                  defined)
     * @param _name     name of the expression
     */
    public ExpressionData(final AbstractTest _test,
                          final String _name)
    {
        super(_test, AbstractTest.CI.DM_EXPRESSION, _name, ExpressionData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Returns the TCL update file of this expression data instance.
     *
     * @return TCL update file content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder();

        this.append4CIFileHeader(cmd);

        cmd.append("mql escape mod expression \"${NAME}\"");

        // append hidden flag
        if (this.isHidden() != null)  {
            cmd.append(' ');
            if (!this.isHidden())  {
                cmd.append('!');
            }
            cmd.append("hidden");
        }

        this.append4CIFileValues(cmd);

        return cmd.toString();
    }

    /**
     * Create the related expression in MX for this expression data instance.
     *
     * @return this expression data instance
     * @throws MatrixException if create failed
     */
    @Override()
    public ExpressionData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add expression \"").append(AbstractTest.convertMql(this.getName())).append('\"');
            this.append4Create(cmd);

            // append hidden flag
            if (this.isHidden() != null)  {
                cmd.append(' ');
                if (!this.isHidden())  {
                    cmd.append('!');
                }
                cmd.append("hidden");
            }

            cmd.append(";\n")
               .append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to expression \"").append(AbstractTest.convertMql(this.getName())).append("\"");

            this.getTest().mql(cmd);
        }
        return this;
    }
}
