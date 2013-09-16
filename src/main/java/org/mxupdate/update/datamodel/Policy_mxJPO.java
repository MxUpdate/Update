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

package org.mxupdate.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.datamodel.policy.PolicyDefParser_mxJPO;
import org.mxupdate.update.util.AdminProperty_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export and import / update policy configuration items.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Policy_mxJPO
    extends AbstractDMWithTriggers_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for policies.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Policy_mxJPO.IGNORED_URLS.add("/defaultFormat");
        Policy_mxJPO.IGNORED_URLS.add("/formatRefList");
        Policy_mxJPO.IGNORED_URLS.add("/typeRefList");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/name");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/ownerAccess");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/ownerAccess/access");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/ownerRevoke");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/ownerRevoke/access");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/publicAccess");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/publicAccess/access");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/publicRevoke");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/publicRevoke/access");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/userAccessList");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/userAccessList/userAccess/access");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/ownerAccess");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/ownerAccess/access");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/publicAccess");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/publicAccess/access");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/ownerRevoke");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/ownerRevoke/access");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/publicRevoke");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/publicRevoke/access");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/userAccessList");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/userAccessList/userAccess/access");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/signatureDefList");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/signatureDefList/signatureDef/approveUserList");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/signatureDefList/signatureDef/ignoreUserList");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/signatureDefList/signatureDef/rejectUserList");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/actionProgram");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/checkProgram");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/routeUser");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/triggerList");
    }

    /**
     * Key used to identify the update of a policy within
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)}.
     *
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     * @see #TCL_PROCEDURE
     */
    private static final String JPO_CALLER_KEY = "policy";

    /**
     * Called TCL procedure within the TCL update to parse the new policy
     * definition. The TCL procedure calls method
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)} with the new
     * policy definition. All quot's are replaced by <code>@0@0@</code> and all
     * apostroph's are replaced by <code>@1@1@</code>.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private static final String TCL_PROCEDURE
            = "proc updatePolicy {_sPolicy _lsArgs}  {\n"
                + "regsub -all {'} $_lsArgs {@0@0@} sArg\n"
                + "regsub -all {\\\"} $sArg {@1@1@} sArg\n"
                + "regsub -all {\\\\\\[} $sArg {[} sArg\n"
                + "regsub -all {\\\\\\]} $sArg {]} sArg\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller " + Policy_mxJPO.JPO_CALLER_KEY + " $_sPolicy \"${sArg}\"\n"
            + "}\n";

    /** Default format of this policy. */
    private String defaultFormat = null;

    /** All possible formats of this policy. */
    private final Set<String> formats = new TreeSet<String>();

    /** Are all formats allowed of this policy? */
    private boolean allFormats;

    /**
     * Sequence of this policy.
     */
    private String sequence = null;

    /** Store of this policy. */
    private String store;

    /** Set of all types of this policy. */
    private final Set<String> types = new TreeSet<String>();

    /** Are all types allowed of this policy? */
    private boolean allTypes;

    /** Are access for all states defined? */
    private boolean allState = false;

    /** Access definitions for all states. */
    private final Access allStateAccess = new Access();

    /** Stack with all states of this policy. */
    private final Stack<State> states = new Stack<State>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the policy object
     */
    public Policy_mxJPO(final TypeDef_mxJPO _typeDef,
                        final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses all policy specific URLs.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #IGNORED_URLS
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Policy_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/defaultFormat/formatRef".equals(_url))  {
            this.defaultFormat = _content;
            parsed = true;
        } else if ("/formatRefList/formatRef".equals(_url))  {
            this.formats.add(_content);
            parsed = true;
        } else if ("/allowAllFormats".equals(_url))  {
            this.allFormats = true;
            parsed = true;

        } else if ("/sequence".equals(_url))  {
            this.sequence = _content;
            parsed = true;

        } else if ("/storeRef".equals(_url))  {
            this.store = _content;
            parsed = true;

        } else if ("/typeRefList/typeRef".equals(_url))  {
            this.types.add(_content);
            parsed = true;
        } else if ("/allowAllTypes".equals(_url))  {
            this.allTypes = true;
            parsed = true;

        } else if ("/allstateDef".equals(_url))  {
            this.allState = true;
            parsed = true;
        } else if (_url.startsWith("/allstateDef"))  {
            parsed = this.allStateAccess.parse(_paramCache, _url.substring(12), _content);

        } else if ("/stateDefList/stateDef".equals(_url))  {
            this.states.add(new State());
            parsed = true;
        } else if (_url.startsWith("/stateDefList/stateDef"))  {
            parsed = this.states.peek().parse(_paramCache, _url.substring(22), _content);

        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Calls the {@link #allStateAccess all states} and single state prepare
     * methods. All policies properties starting with <code>state_</code> are
     * checked if they are defining a symbolic name of a state. If yes the
     * related symbolic name of the state is updated and removed from the
     * property list.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the prepare within super class failed
     */
    @Override()
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        this.allStateAccess.prepare();
        for (final State state : this.states)  {
            state.prepare();
        }
        super.prepare(_paramCache);
        for (final AdminProperty_mxJPO property : new HashSet<AdminProperty_mxJPO>(this.getPropertiesMap().values()))  {
            if ((property.getName() != null) && property.getName().startsWith("state_"))  {
                for (final State state : this.states)  {
                    if (state.name.equals(property.getValue()))  {
                        state.symbolicNames.add(property.getName());
                        this.getPropertiesMap().remove(property.getName());
                    }
                }
            }
        }
    }

    /**
     * Writes the update script for this policy.
     * The policy specific information are:
     * <ul>
     * <li>{@link #allState all state access flag}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws IOException if the TCL update code could not be written
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        this.writeHeader(_paramCache, _out);
        _out.append("updatePolicy \"${NAME}\" {");
        final String suffix = this.getTypeDef().getMxAdminSuffix();
        if (!"".equals(suffix))  {
            _out.append(" ").append(suffix);
        }
        _out.append("\n  description \"").append(StringUtil_mxJPO.convertTcl(this.getDescription())).append("\"");
        // types
        if (this.allTypes)  {
            _out.append("\n  type all");
        } else  {
            _out.append("\n  type {")
                .append(StringUtil_mxJPO.joinTcl(' ', true, this.types, null))
                .append("}");
        }
        // formats
        if (this.allFormats)  {
            _out.append("\n  format all");
        } else  {
            _out.append("\n  format {")
                .append(StringUtil_mxJPO.joinTcl(' ', true, this.formats, null))
                .append("}");
        }
        _out.append("\n  defaultformat \"").append(StringUtil_mxJPO.convertTcl(this.defaultFormat)).append('\"')
            .append("\n  sequence \"").append(StringUtil_mxJPO.convertTcl(this.sequence)).append('\"')
            .append("\n  store \"").append(StringUtil_mxJPO.convertTcl(this.store)).append('\"')
            .append("\n  hidden \"").append(Boolean.toString(this.isHidden())).append("\"");
        // all state access
        if (this.allState)  {
            _out.append("\n  allstate {");
            this.allStateAccess.writeObject(_out);
            _out.append("\n  }");
        }
        // all states
        for (final State state : this.states)  {
            state.writeObject(_out);
        }
        _out.append("\n}");
        this.writeProperties(_paramCache, _out);
    }

    /**
     * Only implemented as stub because
     * {@link #write(ParameterCache_mxJPO, Appendable)} is new implemented.
     *
     * @param _paramCache   parameter cache (not used)
     * @param _out          appendable instance to the TCL update file (not
     *                      used)
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
    {
    }

    /**
     * The method overwrites the original method to add the TCL procedure
     * {@link #TCL_PROCEDURE} so that the dimension could be updated with
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)}.
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     */
    @Override()
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
        throws Exception
    {
        // add TCL code for the procedure
        final StringBuilder tclCode = new StringBuilder()
                .append(Policy_mxJPO.TCL_PROCEDURE)
                .append(_preTCLCode);

        super.update(_paramCache, _preMQLCode, _postMQLCode, tclCode, _tclVariables, _sourceFile);
    }

    /**
     * The method is called within the update of an administration object. The
     * method is called directly within the update.
     * <ul>
     * <li>All <code>@0@0@</code> are replaced by quot's and all
     *     <code>@1@1@</code> are replaced by apostroph's.</li>
     * <li>The new policy definition is parsed.</li>
     * <li>A delta MQL script generated to update the policy to the new target
     *     definition.</li>
     * <li>All symbolic names for states are defined (as property on the
     *     policy).</li>
     * <li>The delta MQL script is executed.</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _args         arguments from the TCL procedure
     * @throws Exception if a state is not defined anymore or the policy could
     *                   not be updated
     * @see #TCL_PROCEDURE
     */
    @Override()
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
        throws Exception
    {
        if ((_args.length == 0) || !Policy_mxJPO.JPO_CALLER_KEY.equals(_args[0]))  {
            super.jpoCallExecute(_paramCache, _args);
        } else  {
            final String code = _args[2].replaceAll("@0@0@", "'")
                                        .replaceAll("@1@1@", "\\\"");

            final PolicyDefParser_mxJPO parser = new PolicyDefParser_mxJPO(new StringReader(code));
            final Policy_mxJPO policy = parser.policy(_paramCache, this.getTypeDef(), _args[1]);

            final StringBuilder cmd = new StringBuilder()
                    .append("escape mod policy \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ");

            // basic information
            this.calcValueDelta(cmd, "description", policy.getDescription(), this.getDescription());

            // if all types are defined, the compare must be against set with all
            if (this.allTypes)  {
                final Set<String> curTypes = new HashSet<String>();
                curTypes.addAll(this.types);
                curTypes.addAll(Arrays.asList(new String[]{"all"}));
                this.calcListDelta(cmd, "type", policy.types, curTypes);
            } else  {
                this.calcListDelta(cmd, "type", policy.types, this.types);
            }

            // if all formats are defined, the compare must be against set with all
            if (this.allFormats)  {
                final Set<String> curFormats = new HashSet<String>();
                curFormats.addAll(this.formats);
                curFormats.addAll(Arrays.asList(new String[]{"all"}));
                this.calcListDelta(cmd, "format", policy.formats, curFormats);
            } else  {
                this.calcListDelta(cmd, "format", policy.formats, this.formats);
            }

            // if not default format => ADMINISTRATION must be default format
            if ((policy.defaultFormat == null) || "".equals(policy.defaultFormat))  {
                cmd.append(" defaultformat ADMINISTRATION");
            } else  {
                this.calcValueDelta(cmd, "defaultformat", policy.defaultFormat, this.defaultFormat);
            }

            this.calcValueDelta(cmd, "sequence", policy.sequence, this.sequence);
            // hidden flag, because hidden flag must be set with special syntax
            if (this.isHidden() != policy.isHidden())  {
                cmd.append(' ');
                if (!policy.isHidden())  {
                    cmd.append('!');
                }
                cmd.append("hidden ");
            }
            // because the store of a policy could not be removed....
            if ((policy.store != null) && !"".equals(policy.store))  {
                this.calcValueDelta(cmd, "store", policy.store, this.store);
            // instead store 'ADMINISTRATION' must be assigned
            } else  {
                cmd.append(" store ADMINISTRATION");
            }
            cmd.append(";\n");

            this.calcAllStateAccess(_paramCache, cmd, policy);
            this.calcStatesDelta(_paramCache, cmd, policy);

            MqlUtil_mxJPO.execMql(_paramCache, cmd);
        }
    }

    /**
     * Calculates the delta for the all state definition between this policy
     * and given new (target) policy <code>_newPolicy</code>.
     *
     * @param _paramCache   parameter cache (used for logging purposes)
     * @param _cmd          string builder for the MQL commands
     * @param _newPolicy    new target policy definition
     * @throws Exception if calculation of the delta failed
     */
    protected void calcAllStateAccess(final ParameterCache_mxJPO _paramCache,
                                      final StringBuilder _cmd,
                                      final Policy_mxJPO _newPolicy)
        throws Exception
    {
        if (_newPolicy.allState)  {
            _cmd.append("escape mod policy \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"');
            if (!this.allState)  {
                _cmd.append(" add allstate");
            }
            _cmd.append(" allstate");
            _newPolicy.allStateAccess.calcDelta(_cmd, this.allStateAccess);
            _cmd.append(";\n");
        } else if (this.allState)  {
            _cmd.append("escape mod policy \"").append(StringUtil_mxJPO.convertMql(this.getName()))
                .append("\" remove allstate").append(";\n");
        }
    }

    /**
     * Calculates the delta for states between this policy and given new policy
     * <code>_newPolicy</code>.
     *
     * @param _paramCache   parameter cache (used for logging purposes)
     * @param _cmd          string builder for the MQL commands
     * @param _newPolicy    new target policy definition
     * @throws Exception if calculation of the delta failed
     */
    protected void calcStatesDelta(final ParameterCache_mxJPO _paramCache,
                                   final StringBuilder _cmd,
                                   final Policy_mxJPO _newPolicy)
        throws Exception
    {
        _cmd.append("escape mod policy \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"');

        // states....
        // (first add new states because of references in branches)
        final Iterator<State> curStateIter = this.states.iterator();
        final Iterator<State> newStateIter = _newPolicy.states.iterator();
        final Map<State,State> stateDeltaMap = new HashMap<State,State>();
        while (curStateIter.hasNext() && newStateIter.hasNext())  {
            final State curState = curStateIter.next();
            State newState = newStateIter.next();
            while (!curState.name.equals(newState.name) && newStateIter.hasNext())  {
                _cmd.append(" add state \"").append(StringUtil_mxJPO.convertMql(newState.name))
                    .append("\" before \"").append(StringUtil_mxJPO.convertMql(curState.name)).append('\"');
                _paramCache.logDebug("    - insert new state '" + newState.name + "' before '" + curState.name + "'");
                stateDeltaMap.put(newState, null);
                newState = newStateIter.next();
            }
            if (curState.name.equals(newState.name))  {
                stateDeltaMap.put(newState, curState);
            }
        }
        while (newStateIter.hasNext())  {
            final State newState = newStateIter.next();
            _cmd.append(" add state \"").append(StringUtil_mxJPO.convertMql(newState.name)).append('\"');
            _paramCache.logDebug("    - add new state '" + newState.name + "'");
            stateDeltaMap.put(newState, null);
        }
        // check for already existing state, but not defined anymore!
        if (curStateIter.hasNext())  {
throw new Exception("some states are not defined anymore!");
        }

        // now update state information itself
        _cmd.append(";\n")
            .append("escape mod policy \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"');
        for (final Map.Entry<State, State> entry : stateDeltaMap.entrySet())  {
            _cmd.append(" state \"").append(StringUtil_mxJPO.convertMql(entry.getKey().name)).append('\"');
            entry.getKey().calcDelta(_cmd, entry.getValue());
        }

        // set symbolic names for all policy states
        _cmd.append(";\n")
            .append("escape mod policy \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"');
        for (final State state : _newPolicy.states)  {
            for (final String symbolicName : state.symbolicNames)  {
                _cmd.append(" add property \"").append(StringUtil_mxJPO.convertMql(symbolicName))
                    .append("\" value \"").append(StringUtil_mxJPO.convertMql(state.name)).append('\"');
            }
        }
        _cmd.append(";\n");
    }

    /**
     * Calculates the delta between the new and the old value. If a delta
     * exists, the kind with the new delta is added to the string builder.
     *
     * @param _out      appendable instance where the delta must be append
     * @param _kind     kind of the delta
     * @param _newVal   new target value
     * @param _curVal   current value in the database
     * @throws IOException if the delta could not appended
     */
    protected void calcValueDelta(final Appendable _out,
                                  final String _kind,
                                  final String _newVal,
                                  final String _curVal)
            throws IOException
    {
        final String curVal = (_curVal == null) ? "" : _curVal;
        final String newVal = (_newVal == null) ? "" : _newVal;

        if (!curVal.equals(newVal))  {
            _out.append(' ').append(_kind).append(" \"").append(StringUtil_mxJPO.convertMql(newVal)).append('\"');
        }
    }

    /**
     *
     * @param _out      appendable instance where the delta must be append
     * @param _kind     kind of the delta
     * @param _new      new target values
     * @param _current  current values in MX
     * @throws IOException if the delta could not appended
     */
    protected void calcListDelta(final Appendable _out,
                                 final String _kind,
                                 final Set<String> _new,
                                 final Set<String> _current)
            throws IOException
    {
        boolean equal = (_current.size() == _new.size());
        if (equal)  {
            for (final String format : _current)  {
                if (!_new.contains(format))  {
                    equal = false;
                    break;
                }
            }
        }
        if (!equal)  {
            for (final String curValue : _current)  {
                if (!_new.contains(curValue))  {
                    _out.append(" remove ").append(_kind).append(" \"").append(StringUtil_mxJPO.convertMql(curValue)).append('\"');
                }
            }
            for (final String newValue : _new)  {
                if (!_current.contains(newValue))  {
                    _out.append(" add ").append(_kind).append(" \"").append(StringUtil_mxJPO.convertMql(newValue)).append('\"');
                }
            }
        }
    }

    /**
     * Class used to define the access and filter double.
     */
    protected static class AccessFilterDouble
    {
        /** Set holding the complete access. */
        protected final Set<String> access = new TreeSet<String>();
        /** String holding the filter expression. */
        protected String filter;

        /**
         * Returns <i>true</i> if {@link #access} is empty or contains only
         * <code>none</code> and {@link #filter} is <code>null</code> or empty
         * string.
         *
         * @return <i>true</i> if empty; otherwise <i>false</i>
         */
        protected boolean isEmpty()
        {
            return ((this.access.isEmpty() || ((this.access.size() == 1) && this.access.contains("none"))))
                    && ((this.filter == null) || "".equals(this.filter));
        }

        /**
         * Returns the string used within the configuration item.
         *
         * @return CI string
         */
        protected String getCIString()
        {
            final StringBuilder strg = new StringBuilder();
            strg.append("{")
                .append(StringUtil_mxJPO.joinTcl(' ', false, this.access, null))
                .append("}");
            if ((this.filter != null) && !"".equals(this.filter))  {
                strg.append(" filter \"")
                    .append(StringUtil_mxJPO.convertTcl(this.filter))
                    .append('\"');
            }
            return strg.toString();
        }

        /**
         * Prepares the MQL update string for this access / filter double.
         *
         * @param _oldAccessFilter      old access filter (which is used if
         *                              there is a filter defined which must be
         *                              overwritten)
         * @return MQL update string
         */
        protected String getMQLUpdateString(final AccessFilterDouble _oldAccessFilter)
        {
            final StringBuilder strg = new StringBuilder();
            strg.append(StringUtil_mxJPO.joinMql(',', false, this.access, "none")).append(' ');
            if ((this.filter != null) || ((_oldAccessFilter != null) && (_oldAccessFilter.filter != null)))  {
                strg.append("filter \"");
                if (this.filter != null)  {
                    strg.append(StringUtil_mxJPO.convertMql(this.filter));
                }
                strg.append("\" ");
            }
            return strg.toString();
        }
    }

    /**
     * Access definition for owner / public definitions (depending on one state
     * or for {@link Policy_mxJPO#allStateAccess all states}).
     */
    public static class Access
    {
        /** Set holding the complete owner access. */
        protected final AccessFilterDouble ownerAccess = new AccessFilterDouble();
        /** Set holding the complete owner revoke. */
        protected final AccessFilterDouble ownerRevoke = new AccessFilterDouble();

        /** Set holding the complete public access. */
        protected final AccessFilterDouble publicAccess = new AccessFilterDouble();
        /** Set holding the complete public revoke. */
        protected final AccessFilterDouble publicRevoke = new AccessFilterDouble();

        /** Stack used to hold the user access while parsing. */
        protected final Stack<UserAccessFilter> userAccess = new Stack<UserAccessFilter>();
        /** Sorted set of user access (by name of the user). */
        protected final Set<UserAccessFilter> userAccessSorted = new TreeSet<UserAccessFilter>();

        /**
         * Parses given access <code>_url</code>.
         *
         * @param _paramCache   parameter cache with MX context
         * @param _url          access URL to parse
         * @param _content      content of the access URL
         * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
         *         <i>false</i>
         */
        protected boolean parse(final ParameterCache_mxJPO _paramCache,
                                final String _url,
                                final String _content)
        {
            boolean ret = true;
            if (_url.startsWith("/ownerAccess/access"))  {
                this.ownerAccess.access.add(_url.replaceAll("^/ownerAccess/access/", "").replaceAll("Access$", "").toLowerCase());
            } else if ("/ownerAccess/expressionFilter".equals(_url))  {
                this.ownerAccess.filter = _content;

            } else if (_url.startsWith("/ownerRevoke/access"))  {
                this.ownerRevoke.access.add(_url.replaceAll("^/ownerRevoke/access/", "").replaceAll("Access$", "").toLowerCase());
            } else if ("/ownerRevoke/expressionFilter".equals(_url))  {
                this.ownerRevoke.filter = _content;

            } else if (_url.startsWith("/publicAccess/access"))  {
                this.publicAccess.access.add(_url.replaceAll("^/publicAccess/access/", "").replaceAll("Access$", "").toLowerCase());
            } else if ("/publicAccess/expressionFilter".equals(_url))  {
                this.publicAccess.filter = _content;

            } else if (_url.startsWith("/publicRevoke/access"))  {
                this.publicRevoke.access.add(_url.replaceAll("^/publicRevoke/access/", "").replaceAll("Access$", "").toLowerCase());
            } else if ("/publicRevoke/expressionFilter".equals(_url))  {
                this.publicRevoke.filter = _content;

            } else if ("/userAccessList/userAccess".equals(_url))  {
                this.userAccess.add(new UserAccessFilter());
            } else if ("/userAccessList/userAccess/userRef".equals(_url))  {
                this.userAccess.peek().userRef = _content;
            } else if (_url.startsWith("/userAccessList/userAccess/access"))  {
                this.userAccess.peek().access.add(_url.replaceAll("^/userAccessList/userAccess/access/", "").replaceAll("Access$", "").toLowerCase());
            } else if ("/userAccessList/userAccess/expressionFilter".equals(_url))  {
                this.userAccess.peek().filter = _content;
            } else  {
                ret = false;
            }
            return ret;
        }

        /**
         * The user access are sorted.
         *
         * @see #userAccess         unsorted list of user access
         * @see #userAccessSorted   sorted list user access (after this method is
         *                          called)
         */
        protected void prepare()
        {
            this.userAccessSorted.addAll(this.userAccess);
        }

        /**
         * Writes specific information about this state to the given writer
         * instance.
         * <ul>
         * <li>{@link #ownerAccess owner access}</li>
         * <li>{@link #ownerRevoke owner revoke}</li>
         * <li>{@link #publicAccess public access}</li>
         * <li>{@link #publicRevoke public revoke}</li>
         * <li>{@link #userAccessSorted user access}</li>
         * </ul>
         *
         * @param _out      writer instance
         * @throws IOException if the TCL update code could not be written
         */
        protected void writeObject(final Appendable _out)
            throws IOException
        {
            // owner access
            _out.append("\n    owner ").append(this.ownerAccess.getCIString());
            // owner revoke
            if (!this.ownerRevoke.isEmpty())  {
                _out.append("\n    revoke owner ").append(this.ownerRevoke.getCIString());
            }
            // public access
            _out.append("\n    public ").append(this.publicAccess.getCIString());
            // public revoke (only written if defined (and not none!)
            if (!this.publicRevoke.isEmpty())  {
                _out.append("\n    revoke public ").append(this.publicRevoke.getCIString());
            }
            // user access
            for (final UserAccessFilter userAccess : this.userAccessSorted)  {
                _out.append("\n    user \"").append(StringUtil_mxJPO.convertTcl(userAccess.userRef)).append("\" {")
                    .append(StringUtil_mxJPO.joinTcl(' ', false, userAccess.access, null))
                    .append('}');
                if (userAccess.filter != null)  {
                    _out.append(" filter \"")
                        .append(StringUtil_mxJPO.convertTcl(userAccess.filter))
                        .append("\"");
                }
            }
        }

        /**
         * Calculates the delta between this new target access definition and
         * current definition <code>_oldAccess</code> within MX. The MQL
         * statements to change are written to <code>_out</code>.
         *
         * @param _out          writer instance
         * @param _oldAccess    current access definition
         * @throws IOException if write failed
         */
        protected void calcDelta(final Appendable _out,
                                 final Access _oldAccess)
            throws IOException
        {
            // owner access
            _out.append(" owner ").append(this.ownerAccess.getMQLUpdateString((_oldAccess != null) ? _oldAccess.ownerAccess : null));
            // owner revoke
            if (!this.ownerRevoke.isEmpty() || ((_oldAccess != null) && (!_oldAccess.ownerRevoke.isEmpty())))  {
                _out.append(" revoke owner ").append(this.ownerRevoke.getMQLUpdateString((_oldAccess != null) ? _oldAccess.ownerRevoke : null));
            }
            // public access
            _out.append(" public ").append(this.publicAccess.getMQLUpdateString((_oldAccess != null) ? _oldAccess.publicAccess : null));
            // public revoke
            if (!this.publicRevoke.isEmpty() || ((_oldAccess != null) && (!_oldAccess.publicRevoke.isEmpty())))  {
                _out.append(" revoke public ").append(this.publicRevoke.getMQLUpdateString((_oldAccess != null) ? _oldAccess.publicRevoke : null));
            }
            // user access
            final Set<String> newUsers = new HashSet<String>();
            for (final UserAccessFilter userAccess : this.userAccess)  {
                newUsers.add(userAccess.userRef);
            }
            final Map<String,UserAccessFilter> oldUser = new HashMap<String,UserAccessFilter>();
            if (_oldAccess != null)  {
                for (final UserAccessFilter userAccess : _oldAccess.userAccess)  {
                    if (newUsers.contains(userAccess.userRef))  {
                        oldUser.put(userAccess.userRef, userAccess);
                    } else  {
                        _out.append(" remove user \"")
                            .append(StringUtil_mxJPO.convertMql(userAccess.userRef))
                            .append("\" all");
                    }
                }
            }
            for (final UserAccessFilter userAccess : this.userAccess)  {
                if ((_oldAccess != null) && !oldUser.containsKey(userAccess.userRef))  {
                    _out.append(" add");
                }
                _out.append(" user ").append(userAccess.getMQLUpdateString(oldUser.get(userAccess.userRef)));
            }
        }
    }

    /**
     * Class defining states of a policy.
     */
    public static class State
        extends Policy_mxJPO.Access
    {
        /** Name of the state. */
        private String name;
        /** Symbolic Name of the state. */
        private final Set<String> symbolicNames = new TreeSet<String>();

        /** Called action program for this state. */
        private String actionProgram;
        /** Input arguments for the action program for this state. */
        private String actionInput;

        /** Called check program for this state. */
        private String checkProgram;
        /** Input arguments for the check program for this state. */
        private String checkInput;

        /** Does the state have an auto promotion? */
        private boolean autoPromotion = false;

        /** Must a checkout written in the history? */
        private boolean checkoutHistory = false;

        /**
         * Is the business object in this state revisionable?
         */
        private boolean revisionable = false;

        /** Is the business object in this state versionable? */
        private boolean versionable = false;

        /** Route message of this state. */
        private String routeMessage;
        /** Route users of this state. */
        private final Set<String> routeUsers = new TreeSet<String>();

        /** Stack with all triggers for this state. */
        private final Stack<Trigger> triggersStack = new Stack<Trigger>();
        /** Map with all triggers for this state. The key is the name of the trigger. */
        private final Map<String,Trigger> triggers = new TreeMap<String,Trigger>();

        /** Holds the signatures for this state. */
        private final Stack<Signature> signatures = new Stack<Signature>();

        /**
         * {@inheritDoc}
         */
        @Override()
        protected boolean parse(final ParameterCache_mxJPO _paramCache,
                                final String _url,
                                final String _content)
        {
            boolean ret = true;
            if ("/name".equals(_url))  {
                this.name = _content;
            } else if ("/autoPromotion".equals(_url))  {
                this.autoPromotion = true;
            } else if ("/checkoutHistory".equals(_url))  {
                this.checkoutHistory = true;
            } else if ("/revisionable".equals(_url))  {
                this.revisionable = true;
            } else if ("/versionable".equals(_url))  {
                this.versionable = true;

            } else if ("/signatureDefList/signatureDef".equals(_url))  {
                this.signatures.add(new Signature());
            } else if ("/signatureDefList/signatureDef/name".equals(_url))  {
                this.signatures.peek().name = _content;
            } else if ("/signatureDefList/signatureDef/expressionFilter".equals(_url))  {
                this.signatures.peek().filter= _content;
            } else if ("/signatureDefList/signatureDef/stateDefRef".equals(_url))  {
                this.signatures.peek().branch = _content;
            } else if ("/signatureDefList/signatureDef/approveUserList/userRef".equals(_url))  {
                this.signatures.peek().approverUsers.add(_content);
            } else if ("/signatureDefList/signatureDef/ignoreUserList/userRef".equals(_url))  {
                this.signatures.peek().ignoreUsers.add(_content);
            } else if ("/signatureDefList/signatureDef/rejectUserList/userRef".equals(_url))  {
                this.signatures.peek().rejectUsers.add(_content);

            } else if ("/actionProgram/programRef".equals(_url))  {
                this.actionProgram = _content;
            } else if ("/actionProgram/inputArguments".equals(_url))  {
                this.actionInput = _content;

            } else if ("/checkProgram/programRef".equals(_url))  {
                this.checkProgram = _content;
            } else if ("/checkProgram/inputArguments".equals(_url))  {
                this.checkInput = _content;

            } else if ("/routeMessage".equals(_url))  {
                this.routeMessage = _content;
            } else if ("/routeUser/userRef".equals(_url))  {
                this.routeUsers.add(_content);

            } else if ("/triggerList/trigger".equals(_url))  {
                this.triggersStack.add(new Trigger());
            } else if ("/triggerList/trigger/triggerName".equals(_url))  {
                this.triggersStack.peek().name = _content;
            } else if ("/triggerList/trigger/programRef".equals(_url))  {
                this.triggersStack.peek().program = _content;
            } else if ("/triggerList/trigger/inputArguments".equals(_url))  {
                this.triggersStack.peek().arguments = _content;
            } else  {
                ret = super.parse(_paramCache, _url, _content);
            }
            return ret;
        }

        /**
         * The user access and trigger instances are sorted.
         */
        @Override()
        protected void prepare()
        {
            super.prepare();
            // sort all triggers
            for (final Trigger trigger : this.triggersStack)  {
                this.triggers.put(trigger.name, trigger);
            }
        }

        /**
         * Writes specific information about this state to the given writer
         * instance.
         * <ul>
         * </ul>
         *
         * @param _out      writer instance
         * @throws IOException if the TCL update code could not be written
         */
        @Override
        protected void writeObject(final Appendable _out)
                throws IOException
        {
            // basics
            _out.append("\n  state \"").append(StringUtil_mxJPO.convertTcl(this.name)).append("\"  {");
            if (this.symbolicNames.isEmpty())  {
                _out.append("\n    registeredName \"")
                    .append("state_").append(StringUtil_mxJPO.convertTcl(this.name.replaceAll(" ", "_")))
                    .append('\"');
            } else  {
                for (final String symbolicName : this.symbolicNames)  {
                    _out.append("\n    registeredName \"")
                        .append(StringUtil_mxJPO.convertTcl(symbolicName))
                        .append('\"');
                }
            }
            _out.append("\n    revision \"").append(Boolean.toString(this.revisionable)).append('\"')
                .append("\n    version \"").append(Boolean.toString(this.versionable)).append('\"')
                .append("\n    promote \"").append(Boolean.toString(this.autoPromotion)).append('\"')
                .append("\n    checkouthistory \"").append(Boolean.toString(this.checkoutHistory)).append('\"');
            // route
            if ((this.routeMessage != null) || !this.routeUsers.isEmpty())  {
                _out.append("\n    route {")
                    .append(StringUtil_mxJPO.joinTcl(' ', true, this.routeUsers, null))
                    .append("} \"")
                    .append(StringUtil_mxJPO.convertTcl(this.routeMessage)).append('\"');
            }
            // write access statements
            super.writeObject(_out);
            _out.append("\n    action \"").append(StringUtil_mxJPO.convertTcl(this.actionProgram))
                .append("\" input \"").append(StringUtil_mxJPO.convertTcl(this.actionInput)).append('\"')
                .append("\n    check \"").append(StringUtil_mxJPO.convertTcl(this.checkProgram))
                .append("\" input \"").append(StringUtil_mxJPO.convertTcl(this.checkInput)).append('\"');
            // output of triggers, but sorted!
            for (final Trigger trigger : this.triggers.values())  {
                _out.append("\n    trigger ").append(trigger.getEventType()).append(' ').append(trigger.getKind())
                    .append(" \"").append(StringUtil_mxJPO.convertTcl(trigger.program)).append("\"")
                    .append(" input \"").append(StringUtil_mxJPO.convertTcl(trigger.arguments)).append("\"");
            }
            // signatures
            for (final Signature signature : this.signatures)  {
                signature.writeObject(_out);
            }
            _out.append("\n  }");
        }

        /**
         *
         * @param _out      appendable instance where the delta must be append
         * @param _oldState old state to compare to (or <code>null</code> if
         *                  this state is new)
         * @throws IOException if the delta could not appended
         */
        protected void calcDelta(final Appendable _out,
                                 final State _oldState)
            throws IOException
        {
            // basics
            _out.append(" promote ").append(String.valueOf(this.autoPromotion))
                .append(" revision ").append(String.valueOf(this.revisionable))
                .append(" checkouthistory ").append(String.valueOf(this.checkoutHistory))
                .append(" version ").append(String.valueOf(this.versionable))
                .append(" action \"").append(StringUtil_mxJPO.convertMql(this.actionProgram)).append('\"')
                .append(" input \"").append(StringUtil_mxJPO.convertMql(this.actionInput)).append('\"')
                .append(" check \"").append(StringUtil_mxJPO.convertMql(this.checkProgram)).append('\"')
                .append(" input \"").append(StringUtil_mxJPO.convertMql(this.checkInput)).append('\"');
            // route message
            _out.append(" route message \"").append(StringUtil_mxJPO.convertMql(this.routeMessage)).append('\"');
            for (final String routeUser : this.routeUsers)  {
                if ((_oldState == null) || !_oldState.routeUsers.contains(routeUser))  {
                    _out.append(" add route \"").append(StringUtil_mxJPO.convertMql(routeUser)).append('\"');
                }
            }
            super.calcDelta(_out, _oldState);
            // triggers
            if (_oldState != null)  {
                for (final Trigger trigger : _oldState.triggers.values())  {
                    if (!this.triggers.containsKey(trigger.name))  {
                        _out.append(" remove trigger ")
                            .append(trigger.getEventType())
                            .append(' ')
                            .append(trigger.getKind());
                    }
                }
            }
            for (final Trigger trigger : this.triggers.values())  {
                _out.append(" add trigger ").append(trigger.getEventType()).append(' ').append(trigger.getKind())
                    .append(" \"").append(StringUtil_mxJPO.convertMql(trigger.program)).append("\"")
                    .append(" input \"").append(StringUtil_mxJPO.convertMql(trigger.arguments)).append('\"');
            }
            // signatures
            final Set<String> newSigs = new HashSet<String>();
            for (final Signature signature : this.signatures)  {
                newSigs.add(signature.name);
            }
            final Map<String,Signature> oldSigs = new HashMap<String,Signature>();
            if (_oldState != null)  {
                for (final Signature signature : _oldState.signatures)  {
                    if (newSigs.contains(signature.name))  {
                        oldSigs.put(signature.name, signature);
                    } else  {
                        _out.append(" remove signature \"")
                            .append(StringUtil_mxJPO.convertMql(signature.name))
                            .append('\"');
                    }
                }
            }
            for (final Signature signature : this.signatures)  {
                final Signature oldSig;
                if ((_oldState != null) && !oldSigs.containsKey(signature.name))  {
                    _out.append(" add ");
                    oldSig = null;
                } else  {
                    oldSig = oldSigs.get(signature.name);
                }
                _out.append(" signature \"").append(StringUtil_mxJPO.convertMql(signature.name)).append('\"');
                signature.calcDelta(_out, oldSig);
            }
        }
    }

    /**
     * Class used to hold the user access for a state.
     */
    public static class UserAccessFilter
        extends Policy_mxJPO.AccessFilterDouble
        implements Comparable<Policy_mxJPO.UserAccessFilter>
    {
        /**
         * Holds the user references of a user access.
         */
        private String userRef;

        /**
         * Compares this user access instance to another user access instance.
         * Only the user reference {@link #userRef} is used to compare.
         *
         * @param _userAccess   user access instance to which this instance
         *                      must be compared to
         * @return a negative integer, zero, or a positive integer as this
         *         {@link #userRef} is less than, equal to, or greater than the
         *         specified {@link #userRef} defined with
         *         <code>_userAccess</code>
         */
        public int compareTo(final UserAccessFilter _userAccess)
        {
            return this.userRef.compareTo(_userAccess.userRef);
        }

        /**
         * The {@link #userRef reference to the user} is prefixed to the access
         * string.
         * {@inheritDoc}
         */
        @Override()
        protected String getCIString()
        {
            return new StringBuilder()
                .append('\"').append(StringUtil_mxJPO.convertTcl(this.userRef)).append("\" ")
                .append(super.getCIString())
                .toString();
        }

        /**
         * Prepares the MQL update string for this access / filter double.
         *
         * @param _oldAccessFilter      old access filter (which is used if
         *                              there is a filter defined which must be
         *                              overwritten)
         * @return MQL update string
         */
        @Override()
        protected String getMQLUpdateString(final AccessFilterDouble _oldAccessFilter)
        {
            return new StringBuilder()
                .append('\"').append(StringUtil_mxJPO.convertTcl(this.userRef)).append("\" ")
                .append(super.getMQLUpdateString(_oldAccessFilter))
                .toString();
        }
    }

    /**
     * Class defining a signature for a state.
     */
    public static class Signature
    {
        /** Name of the signature. */
        private String name;

        /** Branch to state? */
        private String branch;
        /** Expression filter of the signature. */
        private String filter;

        /** Set of users which could approve the signature. */
        private final Set<String> approverUsers = new TreeSet<String>();
        /** Set of users which could ignore the signature. */
        private final Set<String> ignoreUsers = new TreeSet<String>();
        /** Set of users which could reject the signature. */
        private final Set<String> rejectUsers = new TreeSet<String>();

        /**
         * Appends the signature information including the branch, filter and
         * the approve, ignore and reject users.
         *
         * @param _out  appendable instance
         * @throws IOException if the TCL update code could not appended
         */
        protected void writeObject(final Appendable _out)
            throws IOException
        {
            _out.append("\n    signature \"").append(StringUtil_mxJPO.convertTcl(this.name)).append("\" {")
                .append("\n      branch \"").append(StringUtil_mxJPO.convertTcl(this.branch)).append("\"")
                // append approver users
                .append("\n      approve {")
                .append(StringUtil_mxJPO.joinTcl(' ', true, this.approverUsers, null))
                .append("}")
                // append ignore users
                .append("\n      ignore {")
                .append(StringUtil_mxJPO.joinTcl(' ', true, this.ignoreUsers, null))
                .append("}")
                // append reject users
                .append("\n      reject {")
                .append(StringUtil_mxJPO.joinTcl(' ', true, this.rejectUsers, null))
                .append("}")
                // append filters
                .append("\n      filter \"")
                .append(StringUtil_mxJPO.convertTcl(this.filter)).append("\"")
                .append("\n    }");
        }

        /**
         * Calculates the delta between the old signature and this signature.
         * If for the old signature a branch is defined and for the new
         * signature no branch, an error is thrown.
         *
         * @param _out          appendable instance where the delta must be
         *                      append
         * @param _oldSignature old signature to compare
         * @throws IOException if the delta could not appended
         */
        protected void calcDelta(final Appendable _out,
                                 final Signature _oldSignature)
            throws IOException
        {
            if ("".equals(this.branch))  {
                if ((_oldSignature != null) && (_oldSignature.branch != null) && !"".equals(_oldSignature.branch))  {
throw new Error("branch '" + _oldSignature.branch + "' exists for signature "
            + this.name + ", but is not defined anymore");
                }
            } else  {
                _out.append(" branch \"").append(StringUtil_mxJPO.convertMql(this.branch)).append('\"');
            }
            _out.append(" filter \"").append(StringUtil_mxJPO.convertMql(this.filter)).append('\"');
            // update approve users
            for (final String approver : this.approverUsers)  {
                if ((_oldSignature == null) || !_oldSignature.approverUsers.contains(approver))  {
                    _out.append(" add approve \"").append(StringUtil_mxJPO.convertMql(approver)).append('\"');
                }
            }
            if (_oldSignature != null)  {
                for (final String approver : _oldSignature.approverUsers)  {
                    if (!this.approverUsers.contains(approver))  {
                        _out.append(" remove approve \"").append(StringUtil_mxJPO.convertMql(approver)).append('\"');
                    }
                }
            }
            // update ignore user
            for (final String ignore : this.ignoreUsers)  {
                if ((_oldSignature == null) || !_oldSignature.ignoreUsers.contains(ignore))  {
                    _out.append(" add ignore \"").append(StringUtil_mxJPO.convertMql(ignore)).append('\"');
                }
            }
            if (_oldSignature != null)  {
                for (final String ignore : _oldSignature.ignoreUsers)  {
                    if (!this.ignoreUsers.contains(ignore))  {
                        _out.append(" remove ignore \"").append(StringUtil_mxJPO.convertMql(ignore)).append('\"');
                    }
                }
            }
            // update reject users
            for (final String reject : this.rejectUsers)  {
                if ((_oldSignature == null) || !_oldSignature.rejectUsers.contains(reject))  {
                    _out.append(" add reject \"").append(StringUtil_mxJPO.convertMql(reject)).append('\"');
                }
            }
            if (_oldSignature != null)  {
                for (final String reject : _oldSignature.rejectUsers)  {
                    if (!this.rejectUsers.contains(reject))  {
                        _out.append(" remove reject \"").append(StringUtil_mxJPO.convertMql(reject)).append('\"');
                    }
                }
            }
        }
    }
}
