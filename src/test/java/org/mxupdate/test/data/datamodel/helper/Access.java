/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.test.data.datamodel.helper;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.user.AbstractUserData;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Holds the information about allowed access and filter.
 *
 * @author The MxUpdate Team
 */
public class Access
{
    /** Prefix. */
    private String prefix;
    /** Kind. */
    private String kind;
    /** Related user of this user access filter. Empty for owner or public access. */
    private AbstractUserData<?> user;
    /** Key. */
    private String key;
    /** Related access definitions. */
    private final Set<String> accessList = new TreeSet<String>();
    /** Organization. */
    private String organization;
    /** Project. */
    private String project;
    /** Owner. */
    private String owner;
    /** Reserve. */
    private String reserve;
    /** Maturity. */
    private String maturity;
    /** Category. */
    private String category;
    /** Related filter expression. */
    private String filter;

    /**
     * Create depending users.
     *
     * @throws MatrixException if create failed
     */
    public void createDependings()
        throws MatrixException
    {
        if (this.user != null)  {
            this.user.create();
        }
    }

    /**
     * Defines the {@link #prefix} of this access filter.
     *
     * @param _prefix       new prefix
     * @return this instance
     */
    public Access setPrefix(final String _prefix)
    {
        this.prefix = _prefix;
        return this;
    }

    /**
     * Defines the {@link #kind} of this access filter.
     *
     * @param _user     referenced user
     * @return this instance
     */
    public Access setKind(final String _kind)
    {
        this.kind = _kind;
        return this;
    }

    /**
     * Defines the {@link #user} of this user access filter.
     *
     * @param _user     referenced user
     * @return this instance
     */
    public Access setUser(final AbstractUserData<?> _user)
    {
        this.user = _user;
        return this;
    }

    /**
     * Defines the {@link #key} of this access filter.
     *
     * @param _key      new key
     * @return this instance
     */
    public Access setKey(final String _key)
    {
        this.key = _key;
        return this;
    }

    /**
     * Appends access.
     *
     * @param _access   access to append
     * @return this instance
     */
    public Access addAccess(final String... _access)
    {
        this.accessList.addAll(Arrays.asList(_access));
        return this;
    }

    /**
     * Defines the {@link #filter}.
     *
     * @param _filter   new filter
     * @return this instance
     */
    public Access setFilter(final String _filter)
    {
        this.filter = _filter;
        return this;
    }

    /**
     * Defines the {@link #organization}.
     *
     * @param _organization     organization
     * @return this instance
     */
    public Access setOrganization(final String _organization)
    {
        this.organization = _organization;
        return this;
    }

    /**
     * Defines the {@link #project}.
     *
     * @param _project      project
     * @return this instance
     */
    public Access setProject(final String _project)
    {
        this.project = _project;
        return this;
    }

    /**
     * Defines the {@link #owner}.
     *
     * @param _owner    owner
     * @return this instance
     */
    public Access setOwner(final String _owner)
    {
        this.owner = _owner;
        return this;
    }

    /**
     * Defines the {@link #reserve}.
     *
     * @param _reserve      reserve
     * @return this instance
     */
    public Access setReserve(final String _reserve)
    {
        this.reserve = _reserve;
        return this;
    }

    /**
     * Defines the {@link #maturity}.
     *
     * @param _maturity     maturity
     * @return this instance
     */
    public Access setMaturity(final String _maturity)
    {
        this.maturity = _maturity;
        return this;
    }

    /**
     * Defines the {@link #category}.
     *
     * @param _maturity     maturity
     * @return this instance
     */
    public Access setCategory(final String _category)
    {
        this.category = _category;
        return this;
    }

    /**
     * Appends the TCL string which are used within the CI file.
     *
     * @param _cmd  string builder where to append the MQL statements
     */
    public void append4CIFile(final StringBuilder _cmd)
    {
        _cmd.append("     ");

        if (this.prefix != null)  {
            _cmd.append(' ').append(this.prefix);
        }

        _cmd.append(' ').append(this.kind);

        if (this.user != null)  {
            _cmd.append(" \"").append(StringUtil_mxJPO.convertTcl(this.user.getName())).append('\"');
        }

        if (this.key != null)  {
            _cmd.append(" key \"").append(StringUtil_mxJPO.convertTcl(this.key)).append('\"');
        }

        _cmd.append(" {").append(StringUtil_mxJPO.joinTcl(' ', false, this.accessList, "none")).append("}");

        if (this.organization != null)  {
            _cmd.append(' ').append(this.organization).append(" organization");
        }
        if (this.project != null)  {
            _cmd.append(' ').append(this.project).append(" project");
        }
        if (this.owner != null)  {
            _cmd.append(' ').append(this.owner).append(" owner");
        }
        if (this.reserve != null)  {
            _cmd.append(' ').append(this.reserve).append(" reserve");
        }
        if (this.maturity != null)  {
            _cmd.append(' ').append(this.maturity).append(" maturity");
        }
        if (this.category != null)  {
            _cmd.append(' ').append(this.category).append(" category");
        }
        if (this.filter != null)  {
            _cmd.append(" filter \"").append(StringUtil_mxJPO.convertTcl(this.filter)).append('\"');
        }

        _cmd.append('\n');
    }

    /**
     * Returns the MQL string used within create of the policy.
     *
     * @return MQL create string
     */
    public String getMQLCreateString()
    {
        final StringBuilder ret = new StringBuilder();

        if (this.prefix != null)  {
            ret.append(this.prefix).append(' ');
        }

        ret.append(this.kind).append(' ');

        if (this.user != null)  {
            ret .append("\"" + AbstractTest.convertMql(this.user.getName()) + "\" ");
        }

        if (this.key != null)  {
            ret.append("key \"").append(AbstractTest.convertMql(this.key)).append("\" ");
        }

        ret.append(StringUtil_mxJPO.joinMql(',', false, this.accessList, "none")).append(' ');

        if (this.organization != null)  {
            ret.append(this.organization).append(" organization ");
        }
        if (this.project != null)  {
            ret.append(this.project).append(" project ");
        }
        if (this.owner != null)  {
            ret.append(this.owner).append(" owner ");
        }
        if (this.reserve != null)  {
            ret.append(this.reserve).append(" reserve ");
        }
        if (this.maturity != null)  {
            ret.append(this.maturity).append(" maturity ");
        }
        if (this.category != null)  {
            ret.append(this.category).append(" category ");
        }
        if (this.filter != null)  {
            ret.append("filter \"").append(AbstractTest.convertTcl(this.filter)).append("\" ");
        }
        return ret.toString();
    }
}
