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
 * Used to define a rule, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class RuleData
    extends AbstractAdminData<RuleData>
{
    /**
     * Owner access.
     *
     * @see #setOwnerAccess(String, String)
     */
    private String ownerAccess = "none";

    /**
     * Filter expression for owner access.
     *
     * @see #setOwnerAccess(String, String)
     */
    private String ownerAccessFilter;

    /**
     * Owner revoke.
     *
     * @see #setOwnerRevoke(String, String)
     */
    private String ownerRevoke;

    /**
     * Filter expression for owner revoke.
     *
     * @see #setOwnerRevoke(String, String)
     */
    private String ownerRevokeFilter;

    /**
     * Public access.
     *
     * @see #setPublicAccess(String, String)
     */
    private String publicAccess = "none";

    /**
     * Filter expression for public access.
     *
     * @see #setPublicAccess(String, String)
     */
    private String publicAccessFilter;

    /**
     * Public revoke.
     *
     * @see #setPublicRevoke(String, String)
     */
    private String publicRevoke;

    /**
     * Filter expression for public revoke.
     *
     * @see #setPublicRevoke(String, String)
     */
    private String publicRevokeFilter;

    /**
     * Within export the description and default value must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(2);
    static  {
        RuleData.REQUIRED_EXPORT_VALUES.add("description");
    }

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
        super(_test, AbstractTest.CI.DM_RULE, _name, RuleData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Defines the owner access including the filter expression for this rule.
     *
     * @param _access   access for the owner
     * @param _filter   filter expression
     * @return this rule data instance
     */
    public RuleData setOwnerAccess(final String _access,
                                   final String _filter)
    {
        this.ownerAccess = _access;
        this.ownerAccessFilter = _filter;
        return this;
    }

    /**
     * Defines the owner revoke including the filter expression for this rule.
     *
     * @param _access   revoke for the owner
     * @param _filter   filter expression
     * @return this rule data instance
     */
    public RuleData setOwnerRevoke(final String _access,
                                   final String _filter)
    {
        this.ownerRevoke = _access;
        this.ownerRevokeFilter = _filter;
        return this;
    }

    /**
     * Defines the public access including the filter expression for this rule.
     *
     * @param _access   access for the public
     * @param _filter   filter expression
     * @return this rule data instance
     */
    public RuleData setPublicAccess(final String _access,
                                    final String _filter)
    {
        this.publicAccess = _access;
        this.publicAccessFilter = _filter;
        return this;
    }

    /**
     * Defines the public revoke including the filter expression for this rule.
     *
     * @param _access   revoke for the public
     * @param _filter   filter expression
     * @return this rule data instance
     */
    public RuleData setPublicRevoke(final String _access,
                                    final String _filter)
    {
        this.publicRevoke = _access;
        this.publicRevokeFilter = _filter;
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
    public RuleData create() throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add rule \"").append(AbstractTest.convertMql(this.getName()))
               .append("\" ");
            // owner access
            cmd.append(" owner ").append(this.ownerAccess);
            if (this.ownerAccessFilter != null)  {
                cmd.append(" filter \"").append(AbstractTest.convertMql(this.ownerAccessFilter)).append("\" ");
            }
            // owner revoke
            if (this.ownerRevoke != null)  {
                cmd.append(" revoke owner ").append(this.ownerRevoke);
                if (this.ownerRevokeFilter != null)  {
                    cmd.append(" filter \"").append(AbstractTest.convertMql(this.ownerRevokeFilter)).append("\" ");
                }
            }
            // public access
            cmd.append(" public ").append(this.publicAccess);
            if (this.publicAccessFilter != null)  {
                cmd.append(" filter \"").append(AbstractTest.convertMql(this.publicAccessFilter)).append("\" ");
            }
            // public revoke
            if (this.publicRevoke != null)  {
                cmd.append(" revoke public ").append(this.publicRevoke);
                if (this.publicRevokeFilter != null)  {
                    cmd.append(" filter \"").append(AbstractTest.convertMql(this.publicRevokeFilter)).append("\" ");
                }
            }
            this.append4Create(cmd);

            this.getTest().mql(cmd);
        }
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
        // owner access
        final StringBuilder ownerAccess = new StringBuilder()
            .append("owner \"").append(this.ownerAccess).append("\"");
        if (this.ownerAccessFilter != null)  {
            ownerAccess.append(" filter \"").append(AbstractTest.convertTcl(this.ownerAccessFilter)).append("\"");
        }
        _needAdds.add(ownerAccess.toString());
        // owner revoke
        if (this.ownerRevoke != null)  {
            final StringBuilder ownerRevoke = new StringBuilder()
                .append("revoke owner \"").append(this.ownerRevoke).append("\"");
            if (this.ownerRevokeFilter != null)  {
                ownerRevoke.append(" filter \"").append(AbstractTest.convertTcl(this.ownerRevokeFilter)).append("\"");
            }
            _needAdds.add(ownerRevoke.toString());
        }
        // public access
        final StringBuilder publicAccess = new StringBuilder()
            .append("public \"").append(this.publicAccess).append("\"");
        if (this.publicAccessFilter != null)  {
            publicAccess.append(" filter \"").append(AbstractTest.convertTcl(this.publicAccessFilter)).append("\"");
        }
        // public revoke
        if (this.publicRevoke != null)  {
            final StringBuilder publicRevoke = new StringBuilder()
                .append("revoke public \"").append(this.publicRevoke).append("\"");
            if (this.publicRevokeFilter != null)  {
                publicRevoke.append(" filter \"").append(AbstractTest.convertTcl(this.publicRevokeFilter)).append("\"");
            }
            _needAdds.add(publicRevoke.toString());
        }
        _needAdds.add(publicAccess.toString());
    }
}
