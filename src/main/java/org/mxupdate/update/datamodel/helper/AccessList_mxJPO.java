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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateList;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

/**
 * Handles list of access definitions.
 *
 * @author The MxUpdate Team
 */
public class AccessList_mxJPO
    implements UpdateList
{
    /**
     * Class used to hold the user access for a state.
     */
    public static class Access
        implements Comparable<Access>
    {
        /** Revoke access. */
        private boolean revoke = false;
        /** Login access. */
        private boolean login = false;
        /** Access kind. */
        private String kind = "user";
        /** Holds the user references of a user access. */
        private String userRef = "";
        /** Key of the access filter. */
        private String key = "";
        /** Set holding the complete access. */
        private final SortedSet<String> access = new TreeSet<>();
        /** Organization of the access definition. */
        private String organization = "";
        /** Project of the access definition. */
        private String project = "";
        /** Owner of the access definition. */
        private String owner;
        /** Reserve of the access definition. */
        private String reserve = "";
        /** Maturity of the access definition. */
        private String maturity = "";
        /** Category of the access definition. */
        private String category = "";
        /** Filter expression. */
        private String filter = "";
        /** Local filter expression. */
        private String localfilter = "";

        /**
         * Returns the {@link #kind} of this access.
         *
         * @return kind
         */
        public String getKind()
        {
            return this.kind;
        }

        /**
         * Returns the {@link #userRef} of this access.
         *
         * @return user reference
         */
        public String getUserRef()
        {
            return this.userRef;
        }

        /**
         * Returns the {@link #key} of this access.
         *
         * @return key
         */
        public String getKey()
        {
            return this.key;
        }

        /**
         * Returns the {@link #access} of this access.
         *
         * @return access
         */
        public Set<String> getAccess()
        {
            return this.access;
        }

        /**
         * Returns the {@link #filter} of this access.
         *
         * @return filter
         */
        public String getFilter()
        {
            return this.filter;
        }

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
                    && ((this.filter == null) || this.filter.isEmpty())
                    && ((this.localfilter == null) || this.localfilter.isEmpty());
        }

        @Override()
        public int compareTo(final Access _compareTo)
        {
            int ret = 0;

            ret = CompareToUtil_mxJPO.compare(ret, this.revoke ? "1" : "0", _compareTo.revoke ? "1" : "0");
            ret = CompareToUtil_mxJPO.compare(ret, this.login ? "1" : "0",  _compareTo.login ? "1" : "0");
            ret = CompareToUtil_mxJPO.compare(ret, this.kind,               _compareTo.kind);
            ret = CompareToUtil_mxJPO.compare(ret, this.userRef,            _compareTo.userRef);
            ret = CompareToUtil_mxJPO.compare(ret, this.key,                _compareTo.key);
            ret = CompareToUtil_mxJPO.compare(ret, this.access,             _compareTo.access);
            ret = CompareToUtil_mxJPO.compare(ret, this.organization,       _compareTo.organization);
            ret = CompareToUtil_mxJPO.compare(ret, this.project,            _compareTo.project);
            ret = CompareToUtil_mxJPO.compare(ret, this.owner,              _compareTo.owner);
            ret = CompareToUtil_mxJPO.compare(ret, this.reserve,            _compareTo.reserve);
            ret = CompareToUtil_mxJPO.compare(ret, this.maturity,           _compareTo.maturity);
            ret = CompareToUtil_mxJPO.compare(ret, this.category,           _compareTo.category);
            ret = CompareToUtil_mxJPO.compare(ret, this.filter,             _compareTo.filter);
            ret = CompareToUtil_mxJPO.compare(ret, this.localfilter,        _compareTo.localfilter);

            return ret;
        }
    }

    /** Stack used to hold the user access while parsing. */
    private final Stack<Access> accessList = new Stack<>();

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
            final Access accessFilter = new Access();
            accessFilter.kind = "owner";
            this.accessList.add(accessFilter);
        } else if (_url.startsWith("/ownerAccess/access"))  {
            this.accessList.peek().access.add(_url.replaceAll("^/ownerAccess/access/", "").replaceAll("Access$", "").toLowerCase());
        } else if ("/ownerAccess/expressionFilter".equals(_url))  {
            this.accessList.peek().filter = _content;

        // obsolete parsing of 'owner revoke'
        } else if ("/ownerRevoke".equals(_url))  {
            final Access accessFilter = new Access();
            accessFilter.kind = "owner";
            accessFilter.revoke = true;
            this.accessList.add(accessFilter);
        } else if (_url.startsWith("/ownerRevoke/access"))  {
            this.accessList.peek().access.add(_url.replaceAll("^/ownerRevoke/access/", "").replaceAll("Access$", "").toLowerCase());
        } else if ("/ownerRevoke/expressionFilter".equals(_url))  {
            this.accessList.peek().filter = _content;

        // obsolete parsing of 'public'
        } else if ("/publicAccess".equals(_url))  {
            final Access accessFilter = new Access();
            accessFilter.kind = "public";
            this.accessList.add(accessFilter);
        } else if (_url.startsWith("/publicAccess/access"))  {
            this.accessList.peek().access.add(_url.replaceAll("^/publicAccess/access/", "").replaceAll("Access$", "").toLowerCase());
        } else if ("/publicAccess/expressionFilter".equals(_url))  {
            this.accessList.peek().filter = _content;

        // obsolete parsing of 'public revoke'
        } else if ("/publicRevoke".equals(_url))  {
            final Access accessFilter = new Access();
            accessFilter.kind = "public";
            accessFilter.revoke = true;
            this.accessList.add(accessFilter);
        } else if (_url.startsWith("/publicRevoke/access"))  {
            this.accessList.peek().access.add(_url.replaceAll("^/publicRevoke/access/", "").replaceAll("Access$", "").toLowerCase());
        } else if ("/publicRevoke/expressionFilter".equals(_url))  {
            this.accessList.peek().filter = _content;

        // current parsing
        } else if ("/userAccessList/userAccess".equals(_url))  {
            this.accessList.add(new Access());
        } else if (_url.startsWith("/userAccessList/userAccess/access"))  {
            this.accessList.peek().access.add(_url.replaceAll("^/userAccessList/userAccess/access/", "").replaceAll("Access$", "").toLowerCase());
        } else if ("/userAccessList/userAccess/matchCategory".equals(_url))  {
            this.accessList.peek().category = _content;
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
            this.accessList.peek().login = true;
        } else if ("/userAccessList/userAccess/userAccessRevoke".equals(_url))  {
            this.accessList.peek().revoke = true;
        } else if ("/userAccessList/userAccess/userRef".equals(_url))  {
            this.accessList.peek().userRef = _content;
        } else if ("/userAccessList/userAccess/expressionFilter".equals(_url))  {
            this.accessList.peek().filter = _content;
        } else if ("/userAccessList/userAccess/localExpressionFilter".equals(_url))  {
            this.accessList.peek().localfilter = _content;
        } else  {
            ret = false;
        }
        return ret;
    }

    public Collection<Access> getAccessList()
    {
        return this.accessList;
    }

    /**
     * Sort the complete {@link #accessList access list}.
     */
    public void sort()
    {
        final List<Access> tmp = new ArrayList<>(this.accessList);
        Collections.sort(tmp);
        this.accessList.clear();

        for (final Access access : tmp)  {
            if (!access.isEmpty())  {
                this.accessList.add(access);
            }
        }
    }

    /**
     * Writes specific information about all defined access definition to the
     * given writer instance {@code _out}.
     *
     * @param _updateBuilder    update builder
     */
    @Override()
    public void write(final UpdateBuilder_mxJPO _updateBuilder)
    {
        for (final AccessList_mxJPO.Access access : this.accessList)  {
            if (!access.isEmpty())  {
                _updateBuilder.stepStartNewLine();
                // revoke
                if (access.revoke)  {
                    _updateBuilder.stepSingle("revoke");
                }
                // login
                if (access.login)  {
                    _updateBuilder.stepSingle("login");
                }

                // kind
                _updateBuilder.stepSingle(access.kind);

                // append user reference (only if not public / owner definition)
                if (!"public".equals(access.kind) && !"owner".equals(access.kind))  {
                    _updateBuilder.stepString(access.userRef);
                }

                // key
                if ((access.key != null) && !access.key.isEmpty())  {
                    _updateBuilder.stepSingle("key").stepString(access.key);
                }

                // access
                _updateBuilder.stepSingle("{" + StringUtil_mxJPO.convertUpdate(false, access.access, null) + "}");

                // user items
                if ((access.organization != null) && !access.organization.isEmpty() && !"any".equals(access.organization))  {
                    _updateBuilder.stepSingle(access.organization).stepSingle("organization");
                }
                if ((access.project != null) && !access.project.isEmpty() && !"any".equals(access.project))  {
                    _updateBuilder.stepSingle(access.project).stepSingle("project");
                }
                if ((access.owner != null) && !access.owner.isEmpty() && !"any".equals(access.owner))  {
                    _updateBuilder.stepSingle(access.owner).stepSingle("owner");
                }
                if ((access.reserve != null) && !access.reserve.isEmpty() && !"any".equals(access.reserve))  {
                    _updateBuilder.stepSingle(access.reserve).stepSingle("reserve");
                }
                if ((access.maturity != null) && !access.maturity.isEmpty() && !"any".equals(access.maturity))  {
                    _updateBuilder.stepSingle(access.maturity).stepSingle("maturity");
                }
                if ((access.category != null) && !access.category.isEmpty() && !"any".equals(access.category))  {
                    _updateBuilder.stepSingle(access.category).stepSingle("category");
                }
                if ((access.filter != null) && !access.filter.isEmpty())  {
                    _updateBuilder.stepSingle("filter").stepString(access.filter);
                }
                if ((access.localfilter != null) && !access.localfilter.isEmpty())  {
                    _updateBuilder.stepSingle("localfilter").stepString(access.localfilter);
                }

                _updateBuilder.stepEndLine();
            }
        }
    }

    /**
     * Calculates the delta between current access definition and this
     * access definitions.
     *
     * @param _mql          MQL builder to append the delta
     * @param _propPrefix   prefix before the property command (e.g. to define
     *                      state properties)
     * @param _current      current properties
     */
    public void calcDelta(final MultiLineMqlBuilder _mql,
                          final AccessList_mxJPO _currents)
    {
        if (_currents == null)  {
            this.update(_mql);
        } else  {
            if (CompareToUtil_mxJPO.compare(0, this.accessList, _currents.accessList) != 0)  {
                _currents.cleanup(_mql);
                this.update(_mql);
            }
        }
    }

    /**
     * Appends the MQL Code to remove all defined access to {@code _out}.
     *
     * @param _mql  MQL builder
     */
    public void cleanup(final MultiLineMqlBuilder _mql)
    {
        // to ensure that public / owner filters are removed:
        // define them first with empty filter and remove them afterwards
        // (not possible via 'remove all' for all MX versions)
        _mql.newLine()
            .cmd("revoke public none filter ").arg("").cmd(" remove revoke public all ")
            .cmd("revoke owner none filter ").arg("").cmd(" remove revoke owner all ")
            .cmd("public none filter ").arg("").cmd(" remove public all ")
            .cmd("owner none filter ").arg("").cmd(" remove owner all");
        for (final AccessList_mxJPO.Access access : this.accessList)  {
            _mql.cmd(" remove");
            // revoke
            if (access.revoke)  {
                _mql.cmd(" revoke");
            }
            // login
            if (access.login)  {
                _mql.cmd(" login");
            }
            _mql.cmd(" ").cmd(access.kind);
            if (!"public".equals(access.kind) && !"owner".equals(access.kind))  {
                _mql.cmd(" ").arg(access.userRef);
            }
            if ((access.key != null) && !access.key.isEmpty())  {
                _mql.cmd(" key ").arg(access.key);
            }
            _mql.cmd(" all filter ").arg("");
        }
    }

    /**
     * Appends all access items to the {@code _out}. Each line is prefixed
     * with {@code _linePrefix}.
     *
     * @param _mql              mql builder
     */
    public void update(final MultiLineMqlBuilder _mql)
    {
        // append all new access definitions
        for (final AccessList_mxJPO.Access access : this.accessList)  {

            if (!access.isEmpty())  {

                _mql.newLine();
                // revoke
                if (access.revoke)  {
                    _mql.cmd(" revoke");
                }
                // login
                if (access.login)  {
                    _mql.cmd(" login");
                }
                // kind
                _mql.cmd(" ").cmd(access.kind);
                // append user reference (only if not public / owner definition)
                if (!"public".equals(access.kind) && !"owner".equals(access.kind))  {
                    _mql.cmd(" ").arg(access.userRef);
                }
                // access filter key
                if ((access.key != null) && !access.key.isEmpty())  {
                    _mql.cmd(" key ").arg(access.key);
                }
                // access (each access statement must be defined as argument!)
                _mql.cmd(" ");
                if (access.access.isEmpty())  {
                    _mql.arg("none");
                } else  {
                    boolean first = true;
                    for (final String oneAccess : access.access)  {
                        if (first)  {
                            first = false;
                        } else  {
                            _mql.cmd(",");
                        }
                        _mql.arg(oneAccess);
                    }
                }
                // user items
                if ((access.organization != null) && !access.organization.isEmpty() && !"any".equals(access.organization))  {
                    _mql.cmd(" ").arg(access.organization).cmd(" organization");
                }
                if ((access.project != null) && !access.project.isEmpty() && !"any".equals(access.project))  {
                    _mql.cmd(" ").arg(access.project).cmd(" project");
                }
                if ((access.owner != null) && !access.owner.isEmpty() && !"any".equals(access.owner))  {
                    _mql.cmd(" ").arg(access.owner).cmd(" owner");
                }
                if ((access.reserve != null) && !access.reserve.isEmpty() && !"any".equals(access.reserve))  {
                    _mql.cmd(" ").arg(access.reserve).cmd(" reserve");
                }
                if ((access.maturity != null) && !access.maturity.isEmpty() && !"any".equals(access.maturity))  {
                    _mql.cmd(" ").arg(access.maturity).cmd(" maturity");
                }
                if ((access.category != null) && !access.category.isEmpty() && !"any".equals(access.category))  {
                    _mql.cmd(" ").arg(access.category).cmd(" category");
                }
                if ((access.filter != null) && !access.filter.isEmpty())  {
                    _mql.cmd(" filter ").arg(access.filter);
                }
                if ((access.localfilter != null) && !access.localfilter.isEmpty())  {
                    _mql.cmd(" localfilter ").arg(access.localfilter);
                }
            }
        }
    }
}
