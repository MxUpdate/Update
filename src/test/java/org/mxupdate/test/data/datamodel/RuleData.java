/*
 * Copyright 2008-2011 The MxUpdate Team
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.datamodel.helper.Access;

/**
 * Used to define a rule, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class RuleData
    extends AbstractAdminData<RuleData>
{
    /**
     * Within export the description and default value must be defined.
     */
    private static final Map<String,String> REQUIRED_EXPORT_VALUES = new HashMap<String,String>();
    static  {
        RuleData.REQUIRED_EXPORT_VALUES.put("description", "");
    }

    /** Access definitions for this state. */
    private final List<Access> accessList = new ArrayList<Access>();

    /**
     * Initialize this rule with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this attribute is
     *                  defined)
     * @param _name     name of the rule
     */
    public RuleData(final AbstractTest _test,
                    final String _name)
    {
        super(_test, AbstractTest.CI.DM_RULE, _name, RuleData.REQUIRED_EXPORT_VALUES, null);
    }

    /**
     * Appends given {@code _accessList}.
     *
     * @param _accessList    access list to append
     * @return this rule data instance
     */
    public RuleData addAccess(final Access... _accessList)
    {
        this.accessList.addAll(Arrays.asList(_accessList));
        return this;
    }

    /**
     * Returns the TCL update file of this rule data instance.
     *
     * @return TCL update file content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
            .append("mql escape mod rule \"${NAME}\"");

        this.append4CIFileValues(cmd);

        return cmd.toString();
    }

    /**
     * Create the related rule in MX for this rule data instance.
     *
     * @return this rule data instance
     * @throws MatrixException if create failed
     */
    @Override()
    public RuleData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add rule \"").append(AbstractTest.convertMql(this.getName()))
               .append("\" owner none public none ");

            for (final Access access : this.accessList)  {
                cmd.append(' ').append(access.getMQLCreateString());
            }

            this.append4Create(cmd);

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * The defined users in the {@link #accessList access lists} are created.
     */
    @Override()
    public RuleData createDependings()
        throws MatrixException
    {
        for (final Access access : this.accessList)  {
            access.createDependings();
        }

        super.createDependings();

        return this;
    }

    /**
     * Appends the add statements for the owner / public access / revokes.
     *
     * @param _needAdds     set where all adds are appended
     */
    @Override()
    protected void evalAdds4CheckExport(final Set<String> _needAdds)
    {
        super.evalAdds4CheckExport(_needAdds);

        for (final Access access : this.accessList)  {
            _needAdds.add(access.getMQLCreateString().trim());
        }
    }
}
