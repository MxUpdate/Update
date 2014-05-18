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
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.datamodel.helper.AccessList_mxJPO;
import org.mxupdate.update.datamodel.policy.PolicyDefParser_mxJPO;
import org.mxupdate.update.util.AdminProperty_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.xml.sax.SAXException;

/**
 * The class is used to export and import / update policy configuration items.
 *
 * @author The MxUpdate Team
 */
public class Policy_mxJPO
    extends AbstractDMWithTriggers_mxJPO
{
    /**
     * Name of the parameter to define that the &quot;majorrevision&quot;
     * property for policies and &quot;delimited&quot; property for policy
     * states from current MX version is supported. The parameter is needed to
     * support the case that an old MX version is used....
     */
    private static final String PARAM_SUPPORT_MAJOR_MINOR = "DMPolicySupportsMajorMinor";

    /**
     * Name of the parameter to define that the policy states supports the
     * published property.
     */
    private static final String PARAM_SUPPORT_PUBLISHED = "DMPolicyStateSupportsPublished";

    /** Set of all ignored URLs from the XML definition for policies. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Policy_mxJPO.IGNORED_URLS.add("/defaultFormat");
        Policy_mxJPO.IGNORED_URLS.add("/formatRefList");
        Policy_mxJPO.IGNORED_URLS.add("/typeRefList");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/name");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/ownerAccess/access");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/ownerRevoke/access");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/publicAccess/access");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/publicRevoke/access");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/userAccessList");
        Policy_mxJPO.IGNORED_URLS.add("/allstateDef/userAccessList/userAccess/access");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/ownerAccess/access");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/publicAccess/access");
        Policy_mxJPO.IGNORED_URLS.add("/stateDefList/stateDef/ownerRevoke/access");
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

    /** Stores the flag the the update needs a create of the policy. */
    private boolean updateWithCreate = false;

    /** Default format of this policy. */
    private String defaultFormat = null;
    /** All possible formats of this policy. */
    private final Set<String> formats = new TreeSet<String>();
    /** Are all formats allowed of this policy? */
    private boolean allFormats;
    /** Locking enforced? */
    private boolean enforce;

    /** Delimiter between Major and Minor Revision. */
    private String delimiter = null;
    /** Minor Sequence of this policy. */
    private String minorsequence = null;
    /** Major Sequence of this policy. */
    private String majorsequence = null;

    /** Store of this policy. */
    private String store;

    /** Set of all types of this policy. */
    private final Set<String> types = new TreeSet<String>();

    /** Are all types allowed of this policy? */
    private boolean allTypes;

    /** Are access for all states defined? */
    private boolean allState = false;

    /** Access definitions for all states. */
    private final AccessList_mxJPO allStateAccess = new AccessList_mxJPO();

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
     * {@inheritDoc}
     * Parsing is only allowed if the update is not done within
     * {@link #updateWithCreate create}.
     */
    @Override()
    protected void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, SAXException, IOException
    {
        if (!this.updateWithCreate)  {
            super.parse(_paramCache);
        }
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
        } else if ("/enforceLocking".equals(_url))  {
            this.enforce = true;
            parsed = true;

        } else if ("/delimiter".equals(_url))  {
            this.delimiter = _content;
            parsed = true;
        } else if ("/sequence".equals(_url))  {
            this.minorsequence = _content;
            parsed = true;
        } else if ("/majorsequence".equals(_url))  {
            this.majorsequence = _content;
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
     * Original method is overridden because policies must be directly created
     * within update.
     *
     * @param _paramCache       not used
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    @Override()
    public void create(final ParameterCache_mxJPO _paramCache)
    {
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
        // formats and locking enforced
        if (this.allFormats)  {
            _out.append("\n  format all");
        } else  {
            _out.append("\n  format {")
                .append(StringUtil_mxJPO.joinTcl(' ', true, this.formats, null))
                .append("}");
        }
        _out.append("\n  defaultformat \"").append(StringUtil_mxJPO.convertTcl(this.defaultFormat)).append('\"');
        if (this.enforce)  {
            _out.append("\n  enforce \"true\"");
        }

        // major / minor sequence and delimiter
        if ((this.delimiter != null) && !this.delimiter.isEmpty())  {
            _out.append("\n  delimiter ").append(StringUtil_mxJPO.convertTcl(this.delimiter))
                .append("\n  minorsequence \"").append(StringUtil_mxJPO.convertTcl(this.minorsequence)).append('\"')
                .append("\n  majorsequence \"").append(StringUtil_mxJPO.convertTcl(this.majorsequence)).append('\"');
        } else  {
            _out.append("\n  sequence \"").append(StringUtil_mxJPO.convertTcl(this.minorsequence)).append('\"');
        }

        _out.append("\n  store \"").append(StringUtil_mxJPO.convertTcl(this.store)).append('\"')
            .append("\n  hidden \"").append(Boolean.toString(this.isHidden())).append("\"");
        // all state access
        if (this.allState)  {
            _out.append("\n  allstate {");
            this.allStateAccess.writeObject(_paramCache, _out);
            _out.append("\n  }");
        }
        // all states
        for (final State state : this.states)  {
            state.writeObject(_paramCache, _out);
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
     * The {@code _create} flag is stored in {@link #updateWithCreate}.
     */
    @Override()
    public void update(final ParameterCache_mxJPO _paramCache,
                       final boolean _create,
                       final File _file,
                       final String _newVersion)
        throws Exception
    {
        this.updateWithCreate = _create;
        super.update(_paramCache, _create, _file, _newVersion);
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

            final StringBuilder cmd = new StringBuilder();

            // creates policy if done within update
            if (this.updateWithCreate)  {
                _paramCache.logDebug("    - create policy");
                cmd.append("escape add policy \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"');
                if ((policy.delimiter != null) && !policy.delimiter.isEmpty())  {
                    cmd.append("delimiter ").append(policy.delimiter).append(" ")
                        .append("minorsequence \"").append(policy.minorsequence).append("\" ")
                        .append("majorsequence \"").append(policy.majorsequence).append('\"');
                }
                cmd.append(';');
            // check that delimiter is NOT updated
            } else if (((this.delimiter == null) && (policy.delimiter != null) && !policy.delimiter.isEmpty())
                    || ((this.delimiter != null) && !this.delimiter.isEmpty() && (policy.delimiter == null))
                    || ((this.delimiter != null) && !this.delimiter.equals(policy.delimiter)))  {
                throw new UpdateException_mxJPO(
                        UpdateException_mxJPO.Error.DM_POLICY_UPDATE_DELIMITER,
                        this.getTypeDef().getLogging(),
                        this.getName(),
                        this.delimiter,
                        policy.delimiter);
            }

            cmd.append("escape mod policy \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ");

            // basic information
            DeltaUtil_mxJPO.calcValueDelta(cmd, "description", policy.getDescription(), this.getDescription());

            // if all types are defined, the compare must be against set with all
            if (this.allTypes)  {
                final Set<String> curTypes = new HashSet<String>();
                curTypes.addAll(this.types);
                curTypes.addAll(Arrays.asList(new String[]{"all"}));
                DeltaUtil_mxJPO.calcListDelta(cmd, "type", policy.types, curTypes);
            } else  {
                DeltaUtil_mxJPO.calcListDelta(cmd, "type", policy.types, this.types);
            }

            // if all formats are defined, the compare must be against set with all
            if (this.allFormats)  {
                final Set<String> curFormats = new HashSet<String>();
                curFormats.addAll(this.formats);
                curFormats.addAll(Arrays.asList(new String[]{"all"}));
                DeltaUtil_mxJPO.calcListDelta(cmd, "format", policy.formats, curFormats);
            } else  {
                DeltaUtil_mxJPO.calcListDelta(cmd, "format", policy.formats, this.formats);
            }

            // if not default format => ADMINISTRATION must be default format
            if ((policy.defaultFormat == null) || "".equals(policy.defaultFormat))  {
                cmd.append(" defaultformat ADMINISTRATION");
            } else  {
                DeltaUtil_mxJPO.calcValueDelta(cmd, "defaultformat", policy.defaultFormat, this.defaultFormat);
            }

            // enforce only if not equal
            if (this.enforce != policy.enforce)  {
                cmd.append(' ');
                if (!policy.enforce)  {
                    cmd.append("not");
                }
                cmd.append("enforce");
            }

            DeltaUtil_mxJPO.calcValueDelta(cmd, "sequence", policy.minorsequence, this.minorsequence);
            if (_paramCache.getValueBoolean(Policy_mxJPO.PARAM_SUPPORT_MAJOR_MINOR))  {
                DeltaUtil_mxJPO.calcValueDelta(cmd, "majorsequence", policy.majorsequence, this.majorsequence);
            }
            // hidden flag, because hidden flag must be set with special syntax
            DeltaUtil_mxJPO.calcFlagDelta(cmd, "hidden", policy.isHidden(), this.isHidden());
            // because the store of a policy could not be removed....
            if ((policy.store != null) && !"".equals(policy.store))  {
                DeltaUtil_mxJPO.calcValueDelta(cmd, "store", policy.store, this.store);
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
                _cmd.append(" add allstate public none owner none");
            }
            _cmd.append(" allstate");
            if (this.allStateAccess != null)  {
                this.allStateAccess.cleanup(_cmd);
            }
            _newPolicy.allStateAccess.update("", _cmd);
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
                    .append("\" before \"").append(StringUtil_mxJPO.convertMql(curState.name)).append('\"')
                    .append(" public none owner none");
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
            _cmd.append(" add state \"").append(StringUtil_mxJPO.convertMql(newState.name)).append('\"')
                .append(" public none owner none");
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
            entry.getKey().calcDelta(_paramCache, _cmd, entry.getValue());
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
     * Class defining states of a policy.
     */
    public static class State
        extends AccessList_mxJPO
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

        /** Published flag? */
        private boolean published = false;

        /** Is the business object in this state (minor) revisionable? */
        private boolean minorrevisionable = false;
        /** Is the business object in this state major revisionable? */
        private boolean majorrevisionable = false;

        /** Is the business object in this state versionable? */
        private boolean versionable = false;

        /** Route message of this state. */
        private String routeMessage;
        /** Route users of this state. */
        private final Set<String> routeUsers = new TreeSet<String>();

        /** Stack with all triggers for this state. */
        private final Stack<Trigger> triggersStack = new Stack<Trigger>();
        /** Map with all triggers for this state. The key is the name of the trigger. */
        private final Triggers triggers = new Triggers();

        /** Holds the signatures for this state. */
        private final Stack<Signature> signatures = new Stack<Signature>();

        /**
         * {@inheritDoc}
         */
        @Override()
        public boolean parse(final ParameterCache_mxJPO _paramCache,
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
            } else if ("/published".equals(_url))  {
                this.published = true;
            } else if ("/revisionable".equals(_url))  {
                this.minorrevisionable = true;
            } else if ("/majorrevisionable".equals(_url))  {
                this.majorrevisionable = true;
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
         * The trigger instances are sorted.
         */
        protected void prepare()
        {
            // sort all triggers
            for (final Trigger trigger : this.triggersStack)  {
                this.triggers.put(trigger.name, trigger);
            }
        }

        /**
         * {@inheritDoc}
         * Further the complete information about the state is written:
         * <ul>
         * <li>{@link #name state name}
         * <li>{@link #symbolicNames symbolic name of the state}
         * <li>{@link #minorrevisionable minor is revisionable}
         * <li>{@link #majorrevisionable major is revisionable}
         * <li>{@link #versionable}
         * <li>{@link #autoPromotion auto promote}
         * <li>{@link #checkoutHistory}
         * <li>branches
         * <li>events
         * <li>triggers
         * <li>signatures
         * </ul>
         */
        @Override()
        public void writeObject(final ParameterCache_mxJPO _paramCache,
                                final Appendable _out)
            throws IOException
        {
            // state name and registered name
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

            // major / minor revision (old / new format)
            if (_paramCache.getValueBoolean(Policy_mxJPO.PARAM_SUPPORT_MAJOR_MINOR))  {
                _out.append("\n    majorrevision \"").append(Boolean.toString(this.majorrevisionable)).append('\"')
                    .append("\n    minorrevision \"").append(Boolean.toString(this.minorrevisionable)).append('\"');
            } else  {
                _out.append("\n    revision \"").append(Boolean.toString(this.minorrevisionable)).append('\"');
            }

            // other basics
            _out.append("\n    version \"").append(Boolean.toString(this.versionable)).append('\"')
                .append("\n    promote \"").append(Boolean.toString(this.autoPromotion)).append('\"')
                .append("\n    checkouthistory \"").append(Boolean.toString(this.checkoutHistory)).append('\"');
            // published flag (if supported)
            if (_paramCache.getValueBoolean(Policy_mxJPO.PARAM_SUPPORT_PUBLISHED))  {
                _out.append("\n    published \"").append(Boolean.toString(this.published)).append('\"');
            }
            // route
            if ((this.routeMessage != null) || !this.routeUsers.isEmpty())  {
                _out.append("\n    route {")
                    .append(StringUtil_mxJPO.joinTcl(' ', true, this.routeUsers, null))
                    .append("} \"")
                    .append(StringUtil_mxJPO.convertTcl(this.routeMessage)).append('\"');
            }
            // write access statements
            super.writeObject(_paramCache, _out);
            // write event statements
            _out.append("\n    action \"").append(StringUtil_mxJPO.convertTcl(this.actionProgram))
                .append("\" input \"").append(StringUtil_mxJPO.convertTcl(this.actionInput)).append('\"')
                .append("\n    check \"").append(StringUtil_mxJPO.convertTcl(this.checkProgram))
                .append("\" input \"").append(StringUtil_mxJPO.convertTcl(this.checkInput)).append('\"');
            // output of triggers, but sorted!
            this.triggers.write(_out, "\n    ", "");
            // signatures
            for (final Signature signature : this.signatures)  {
                signature.writeObject(_out);
            }
            _out.append("\n  }");
        }

        /**
         * Calculates the delta between this target state and current
         * {@code _oldState}.
         *
         * @param _paramCache   parameter cache (used for logging purposes)
         * @param _cmd          string builder for the MQL commands
         * @param _oldState     old state to update ({@code null} if no old
         *                      state exists).
         * @throws IOException if write failed
         */
        protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                                 final StringBuilder _cmd,
                                 final State _oldState)
            throws IOException
        {
            // basics
            _cmd.append(" promote ").append(String.valueOf(this.autoPromotion))
                .append(" checkouthistory ").append(String.valueOf(this.checkoutHistory))
                .append(" version ").append(String.valueOf(this.versionable))
                .append(" action \"").append(StringUtil_mxJPO.convertMql(this.actionProgram)).append('\"')
                .append(" input \"").append(StringUtil_mxJPO.convertMql(this.actionInput)).append('\"')
                .append(" check \"").append(StringUtil_mxJPO.convertMql(this.checkProgram)).append('\"')
                .append(" input \"").append(StringUtil_mxJPO.convertMql(this.checkInput)).append('\"');
            // minor / major revision (if supported)
            if (_paramCache.getValueBoolean(Policy_mxJPO.PARAM_SUPPORT_MAJOR_MINOR))  {
                _cmd.append(" majorrevision ").append(String.valueOf(this.majorrevisionable))
                    .append(" minorrevision ").append(String.valueOf(this.minorrevisionable));
            } else  {
                _cmd.append(" revision ").append(String.valueOf(this.minorrevisionable));
            }
            // published (if supported)
            if (_paramCache.getValueBoolean(Policy_mxJPO.PARAM_SUPPORT_PUBLISHED))  {
                _cmd.append(" published ").append(String.valueOf(this.published));
            }
            // route message
            _cmd.append(" route message \"").append(StringUtil_mxJPO.convertMql(this.routeMessage)).append('\"');
            for (final String routeUser : this.routeUsers)  {
                if ((_oldState == null) || !_oldState.routeUsers.contains(routeUser))  {
                    _cmd.append(" add route \"").append(StringUtil_mxJPO.convertMql(routeUser)).append('\"');
                }
            }

            // access list
            if (_oldState != null)  {
                _oldState.cleanup(_cmd);
            }
            this.update("", _cmd);

            // triggers
            this.triggers.calcDelta((_oldState != null) ? _oldState.triggers : null, _cmd);
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
                        _cmd.append(" remove signature \"")
                            .append(StringUtil_mxJPO.convertMql(signature.name))
                            .append('\"');
                    }
                }
            }
            for (final Signature signature : this.signatures)  {
                final Signature oldSig;
                if ((_oldState != null) && !oldSigs.containsKey(signature.name))  {
                    _cmd.append(" add ");
                    oldSig = null;
                } else  {
                    oldSig = oldSigs.get(signature.name);
                }
                _cmd.append(" signature \"").append(StringUtil_mxJPO.convertMql(signature.name)).append('\"');
                signature.calcDelta(_cmd, oldSig);
            }
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
