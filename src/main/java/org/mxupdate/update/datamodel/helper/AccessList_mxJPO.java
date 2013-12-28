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

package org.mxupdate.update.datamodel.helper;

import java.io.IOException;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Handles list of access definitions.
 *
 * @author The MxUpdate Team
 */
public class AccessList_mxJPO
{
    /**
     * Class used to hold the user access for a state.
     */
    public static class Access
    {
        /** Prefix for the access kind. */
        private Prefix prefix = Prefix.All;
        /** Access kind. */
        private String kind = "user";
        /** Holds the user references of a user access. */
        private String userRef = "";
        /** Key of the access filter. */
        private String key;
        /** Set holding the complete access. */
        private final Set<String> access = new TreeSet<String>();
        /** Organization of the access definition. */
        private String organization;
        /** Project of the access definition. */
        private String project;
        /** Owner of the access definition. */
        private String owner;
        /** Reserve of the access definition. */
        private String reserve;
        /** Maturity of the access definition. */
        private String maturity;
        /** String holding the filter expression. */
        private String filter;

        /**
         * Returns <i>true</i> if {@link #access} is empty or contains only
         * <code>none</code> and {@link #filter} is <code>null</code> or empty
         * string.
         *
         * @return <i>true</i> if empty; otherwise <i>false</i>
         */
        public boolean isEmpty()
        {
            return ((this.access.isEmpty() || ((this.access.size() == 1) && this.access.contains("none"))))
                    && ((this.filter == null) || "".equals(this.filter));
        }
    }

    /** Stack used to hold the user access while parsing. */
    private final Stack<AccessList_mxJPO.Access> accessList = new Stack<AccessList_mxJPO.Access>();

    /**
     * Parses given access <code>_url</code>.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          access URL to parse
     * @param _content      content of the access URL
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    public boolean parse(final ParameterCache_mxJPO _paramCache,
                         final String _url,
                         final String _content)
    {
        boolean ret = true;
        // obsolete parsing of 'owner'
        if ("/ownerAccess".equals(_url))  {
            final AccessList_mxJPO.Access accessFilter = new AccessList_mxJPO.Access();
            accessFilter.kind = "owner";
            this.accessList.add(accessFilter);
        } else if (_url.startsWith("/ownerAccess/access"))  {
            this.accessList.peek().access.add(_url.replaceAll("^/ownerAccess/access/", "").replaceAll("Access$", "").toLowerCase());
        } else if ("/ownerAccess/expressionFilter".equals(_url))  {
            this.accessList.peek().filter = _content;

        // obsolete parsing of 'owner revoke'
        } else if ("/ownerRevoke".equals(_url))  {
            final AccessList_mxJPO.Access accessFilter = new AccessList_mxJPO.Access();
            accessFilter.kind = "owner";
            accessFilter.prefix = Prefix.Revoke;
            this.accessList.add(accessFilter);
        } else if (_url.startsWith("/ownerRevoke/access"))  {
            this.accessList.peek().access.add(_url.replaceAll("^/ownerRevoke/access/", "").replaceAll("Access$", "").toLowerCase());
        } else if ("/ownerRevoke/expressionFilter".equals(_url))  {
            this.accessList.peek().filter = _content;

        // obsolete parsing of 'public'
        } else if ("/publicAccess".equals(_url))  {
            final AccessList_mxJPO.Access accessFilter = new AccessList_mxJPO.Access();
            accessFilter.kind = "public";
            this.accessList.add(accessFilter);
        } else if (_url.startsWith("/publicAccess/access"))  {
            this.accessList.peek().access.add(_url.replaceAll("^/publicAccess/access/", "").replaceAll("Access$", "").toLowerCase());
        } else if ("/publicAccess/expressionFilter".equals(_url))  {
            this.accessList.peek().filter = _content;

        // obsolete parsing of 'public revoke'
        } else if ("/publicRevoke".equals(_url))  {
            final AccessList_mxJPO.Access accessFilter = new AccessList_mxJPO.Access();
            accessFilter.kind = "public";
            accessFilter.prefix = Prefix.Revoke;
            this.accessList.add(accessFilter);
        } else if (_url.startsWith("/publicRevoke/access"))  {
            this.accessList.peek().access.add(_url.replaceAll("^/publicRevoke/access/", "").replaceAll("Access$", "").toLowerCase());
        } else if ("/publicRevoke/expressionFilter".equals(_url))  {
            this.accessList.peek().filter = _content;

        } else if ("/userAccessList/userAccess".equals(_url))  {
            this.accessList.add(new AccessList_mxJPO.Access());
        } else if (_url.startsWith("/userAccessList/userAccess/access"))  {
            this.accessList.peek().access.add(_url.replaceAll("^/userAccessList/userAccess/access/", "").replaceAll("Access$", "").toLowerCase());
        } else if ("/userAccessList/userAccess/matchMaturity".equals(_url))  {
            this.accessList.peek().maturity = _content;
        } else if ("/userAccessList/userAccess/matchOrganization".equals(_url))  {
            this.accessList.peek().organization = _content;
        } else if ("/userAccessList/userAccess/matchOwner".equals(_url))  {
            this.accessList.peek().owner = _content;
        } else if ("/userAccessList/userAccess/matchProject".equals(_url))  {
            this.accessList.peek().project = _content;
        } else if ("/userAccessList/userAccess/matchReserve".equals(_url))  {
            this.accessList.peek().reserve = _content;
        } else if ("/userAccessList/userAccess/userAccessKey".equals(_url))  {
            this.accessList.peek().key = _content;
        } else if ("/userAccessList/userAccess/userAccessKind".equals(_url))  {
            this.accessList.peek().kind = _content;
        } else if ("/userAccessList/userAccess/userAccessLoginRole".equals(_url))  {
            this.accessList.peek().prefix = Prefix.Login;
        } else if ("/userAccessList/userAccess/userAccessRevoke".equals(_url))  {
            this.accessList.peek().prefix = Prefix.Revoke;
        } else if ("/userAccessList/userAccess/userRef".equals(_url))  {
            this.accessList.peek().userRef = _content;
        } else if ("/userAccessList/userAccess/expressionFilter".equals(_url))  {
            this.accessList.peek().filter = _content;
        } else  {
            ret = false;
        }
        return ret;
    }


    /**
     * Writes specific information about all defined access definition to the
     * given writer instance {@code _out}.
     *
     * @param _paramCache   parameter cache
     * @param _out      writer instance
     * @throws IOException if the TCL update code could not be written
     */
    public void writeObject(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
        throws IOException
    {
        for (final AccessList_mxJPO.Access access : this.accessList)  {
            if (!access.isEmpty())  {
                _out.append("\n   ");

                // revoke?
                if (access.prefix != Prefix.All)  {
                    _out.append(' ').append(access.prefix.mxValue);
                }

                // kind
                _out.append(' ').append(access.kind);

                // append user reference (only if not public / owner definition)
                if (!"public".equals(access.kind) && !"owner".equals(access.kind))  {
                    _out.append(" \"").append(StringUtil_mxJPO.convertTcl(access.userRef)).append('\"');
                }

                // key
                if ((access.key != null) && !access.key.isEmpty())  {
                    _out.append(" key \"").append(StringUtil_mxJPO.convertTcl(access.key)).append('\"');
                }

                // access
                _out.append(" {")
                    .append(StringUtil_mxJPO.joinTcl(' ', false, access.access, null))
                    .append('}');

                // user items
                if ((access.organization != null) && !access.organization.isEmpty() && !"any".equals(access.organization))  {
                    _out.append(' ').append(access.organization).append(" organization");
                }
                if ((access.project != null) && !access.project.isEmpty() && !"any".equals(access.project))  {
                    _out.append(' ').append(access.project).append(" project");
                }
                if ((access.owner != null) && !access.owner.isEmpty() && !"any".equals(access.owner))  {
                    _out.append(' ').append(access.owner).append(" owner");
                }
                if ((access.reserve != null) && !access.reserve.isEmpty() && !"any".equals(access.reserve))  {
                    _out.append(' ').append(access.reserve).append(" reserve");
                }
                if ((access.maturity != null) && !access.maturity.isEmpty() && !"any".equals(access.maturity))  {
                    _out.append(' ').append(access.maturity).append(" maturity");
                }
                if ((access.filter != null) && !access.filter.isEmpty())  {
                    _out.append(" filter \"").append(StringUtil_mxJPO.convertTcl(access.filter)).append('\"');
                }
            }
        }
    }

    /**
     * Appends the MQL Code to remove all defined access to {@code _out}.
     *
     * @param _out      writer instance
     * @throws IOException if write failed
     */
    public void cleanup(final Appendable _out)
        throws IOException
    {
        // to ensure that public / owner filters are removed:
        // define them first with empty filter and remove them afterwards
        // (not possible via 'remove all' for all MX versions)
        _out.append(" revoke public none filter \"\" remove revoke public all ")
            .append(" revoke owner none filter \"\" remove revoke owner all ")
            .append(" public none filter \"\" remove public all ")
            .append(" owner none filter \"\" remove owner all ");
        for (final AccessList_mxJPO.Access access : this.accessList)  {
            _out.append(" remove");
            if (access.prefix != Prefix.All)  {
                _out.append(' ').append(access.prefix.mxValue);
            }
            _out.append(' ').append(access.kind);
            if (!"public".equals(access.kind) && !"owner".equals(access.kind))  {
                _out.append(" \"").append(StringUtil_mxJPO.convertMql(access.userRef)).append('\"');
            }
            if ((access.key != null) && !access.key.isEmpty())  {
                _out.append(" key \"").append(StringUtil_mxJPO.convertMql(access.key)).append('\"');
            }
            _out.append(" all filter \"\"");
        }
    }

    /**
     * Appends all access items to the {@code _out}. Each line is prefixed
     * with {@code _linePrefix}.
     *
     * @param _linePrefix       line prefix
     * @param _out              writer instance
     * @throws IOException if write failed
     */
    public void update(final String _linePrefix,
                       final Appendable _out)
        throws IOException
    {
        // append all new access definitions
        for (final AccessList_mxJPO.Access access : this.accessList)  {

            if (!access.isEmpty())  {
                _out.append(_linePrefix);

                // prefix login / revoke?
                if (access.prefix != Prefix.All)  {
                    _out.append(' ').append(access.prefix.mxValue);
                }
                // kind
                _out.append(' ').append(access.kind);
                // append user reference (only if not public / owner definition)
                if (!"public".equals(access.kind) && !"owner".equals(access.kind))  {
                    _out.append(" \"").append(StringUtil_mxJPO.convertMql(access.userRef)).append('\"');
                }
                // access filter key
                if ((access.key != null) && !access.key.isEmpty())  {
                    _out.append(" key \"").append(StringUtil_mxJPO.convertMql(access.key)).append('\"');
                }
                // access
                _out.append(' ').append(StringUtil_mxJPO.joinMql(',', false, access.access, "none"));
                // user items
                if ((access.organization != null) && !access.organization.isEmpty() && !"any".equals(access.organization))  {
                    _out.append(' ').append(access.organization).append(" organization");
                }
                if ((access.project != null) && !access.project.isEmpty() && !"any".equals(access.project))  {
                    _out.append(' ').append(access.project).append(" project");
                }
                if ((access.owner != null) && !access.owner.isEmpty() && !"any".equals(access.owner))  {
                    _out.append(' ').append(access.owner).append(" owner");
                }
                if ((access.reserve != null) && !access.reserve.isEmpty() && !"any".equals(access.reserve))  {
                    _out.append(' ').append(access.reserve).append(" reserve");
                }
                if ((access.maturity != null) && !access.maturity.isEmpty() && !"any".equals(access.maturity))  {
                    _out.append(' ').append(access.maturity).append(" maturity");
                }
                if (access.filter != null)  {
                    _out.append(" filter \"");
                    if (access.filter != null)  {
                        _out.append(StringUtil_mxJPO.convertMql(access.filter));
                    }
                    _out.append('\"');
                }
            }
        }
    }

    /**
     * Possible prefixes for the access definition.
     */
    public enum Prefix
    {
        /** All prefix. */
        All(""),
        /** Login prefix. */
        Login("login"),
        /** Revoke prefix. */
        Revoke("revoke");

        /** Internal used value. */
        private final String mxValue;

        /**
         * Constructor.
         *
         * @param _mxValue      internal MX value
         */
        private Prefix(final String _mxValue)
        {
            this.mxValue = _mxValue;
        }
    }
}
