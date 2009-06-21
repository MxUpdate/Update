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

package org.mxupdate.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
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
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.util.MqlUtil_mxJPO;

/**
 * @author Tim Moxter
 * @version $Id$
 */
public class Policy_mxJPO
        extends AbstractDMWithTriggers_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 8645304838663417963L;

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
                + "mql exec prog org.mxupdate.update.util.JPOCaller policy $_sPolicy \"${sArg}\"\n"
            + "}\n";


    /**
     * Default format of this policy.
     */
    private String defaultFormat = null;

    /**
     * All possible formats of this policy.
     */
    private final Set<String> formats = new TreeSet<String>();

    /**
     * Are all formats allowed of this policy?
     */
    private boolean allFormats;

    /**
     * Sequence of this policy.
     */
    private String sequence = null;

    /**
     * Store of this policy.
     */
    private String store;

    /**
     * Set of all types of this policy.
     *
     * @see #parse(String, String)
     */
    private final Set<String> types = new TreeSet<String>();

    /**
     * Are all types allowed of this policy?
     */
    private boolean allTypes;

    /**
     * Stack with all states of this policy.
     */
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
     * Parses all format specific URLs.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/defaultFormat".equals(_url))  {
            // to be ignored ...
        } else if ("/defaultFormat/formatRef".equals(_url))  {
            this.defaultFormat = _content;
        } else if ("/formatRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/formatRefList/formatRef".equals(_url))  {
            this.formats.add(_content);
        } else if ("/allowAllFormats".equals(_url))  {
            this.allFormats = true;

        } else if ("/sequence".equals(_url))  {
            this.sequence = _content;

        } else if ("/storeRef".equals(_url))  {
            this.store = _content;

        } else if ("/typeRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/typeRefList/typeRef".equals(_url))  {
            this.types.add(_content);
        } else if ("/allowAllTypes".equals(_url))  {
            this.allTypes = true;

        } else if ("/stateDefList".equals(_url))  {
            // to be ignored ...
        } else if ("/stateDefList/stateDef".equals(_url))  {
            this.states.add(new State());
        } else if ("/stateDefList/stateDef/name".equals(_url))  {
            this.states.peek().name = _content;
        } else if ("/stateDefList/stateDef/autoPromotion".equals(_url))  {
            this.states.peek().autoPromotion = true;
        } else if ("/stateDefList/stateDef/checkoutHistory".equals(_url))  {
            this.states.peek().checkoutHistory = true;
        } else if ("/stateDefList/stateDef/revisionable".equals(_url))  {
            this.states.peek().revisionable = true;
        } else if ("/stateDefList/stateDef/versionable".equals(_url))  {
            this.states.peek().versionable = true;

        } else if ("/stateDefList/stateDef/ownerAccess".equals(_url))  {
            // to be ignored ...
        } else if ("/stateDefList/stateDef/ownerAccess/access".equals(_url))  {
            // to be ignored ...
        } else if (_url.startsWith("/stateDefList/stateDef/ownerAccess/access"))  {
            this.states.peek().ownerAccess.add(_url.replaceAll("^/stateDefList/stateDef/ownerAccess/access/", "")
                                                   .replaceAll("Access$", "")
                                                   .toLowerCase());

        } else if ("/stateDefList/stateDef/publicAccess".equals(_url))  {
            // to be ignored ...
        } else if ("/stateDefList/stateDef/publicAccess/access".equals(_url))  {
            // to be ignored ...
        } else if (_url.startsWith("/stateDefList/stateDef/publicAccess/access"))  {
            this.states.peek().publicAccess.add(_url.replaceAll("^/stateDefList/stateDef/publicAccess/access/", "")
                                                    .replaceAll("Access$", "")
                                                    .toLowerCase());

        } else if ("/stateDefList/stateDef/userAccessList".equals(_url))  {
            // to be ignored ...
        } else if ("/stateDefList/stateDef/userAccessList/userAccess".equals(_url))  {
            this.states.peek().userAccess.add(new UserAccess());
        } else if ("/stateDefList/stateDef/userAccessList/userAccess/userRef".equals(_url))  {
            this.states.peek().userAccess.peek().userRef = _content;
        } else if ("/stateDefList/stateDef/userAccessList/userAccess/access".equals(_url))  {
            // to be ignored ...
        } else if (_url.startsWith("/stateDefList/stateDef/userAccessList/userAccess/access"))  {
            this.states.peek().userAccess.peek().access.add(
                    _url.replaceAll("^/stateDefList/stateDef/userAccessList/userAccess/access/", "")
                        .replaceAll("Access$", "")
                        .toLowerCase());
        } else if ("/stateDefList/stateDef/userAccessList/userAccess/expressionFilter".equals(_url))  {
            this.states.peek().userAccess.peek().expressionFilter = _content;

        } else if ("/stateDefList/stateDef/signatureDefList".equals(_url))  {
            // to be ignored ...
        } else if ("/stateDefList/stateDef/signatureDefList/signatureDef".equals(_url))  {
            this.states.peek().signatures.add(new Signature());
        } else if ("/stateDefList/stateDef/signatureDefList/signatureDef/name".equals(_url))  {
            this.states.peek().signatures.peek().name = _content;
        } else if ("/stateDefList/stateDef/signatureDefList/signatureDef/expressionFilter".equals(_url))  {
            this.states.peek().signatures.peek().filter= _content;
        } else if ("/stateDefList/stateDef/signatureDefList/signatureDef/stateDefRef".equals(_url))  {
            this.states.peek().signatures.peek().branch = _content;
        } else if ("/stateDefList/stateDef/signatureDefList/signatureDef/approveUserList".equals(_url))  {
            // to be ignored ...
        } else if ("/stateDefList/stateDef/signatureDefList/signatureDef/approveUserList/userRef".equals(_url))  {
            this.states.peek().signatures.peek().approverUsers.add(_content);
        } else if ("/stateDefList/stateDef/signatureDefList/signatureDef/ignoreUserList".equals(_url))  {
            // to be ignored ...
        } else if ("/stateDefList/stateDef/signatureDefList/signatureDef/ignoreUserList/userRef".equals(_url))  {
            this.states.peek().signatures.peek().ignoreUsers.add(_content);
        } else if ("/stateDefList/stateDef/signatureDefList/signatureDef/rejectUserList".equals(_url))  {
            // to be ignored ...
        } else if ("/stateDefList/stateDef/signatureDefList/signatureDef/rejectUserList/userRef".equals(_url))  {
            this.states.peek().signatures.peek().rejectUsers.add(_content);

        } else if ("/stateDefList/stateDef/actionProgram".equals(_url))  {
            // to be ignored ...
        } else if ("/stateDefList/stateDef/actionProgram/programRef".equals(_url))  {
            this.states.peek().actionProgram = _content;
        } else if ("/stateDefList/stateDef/actionProgram/inputArguments".equals(_url))  {
            this.states.peek().actionInput = _content;

        } else if ("/stateDefList/stateDef/checkProgram".equals(_url))  {
            // to be ignored ...
        } else if ("/stateDefList/stateDef/checkProgram/programRef".equals(_url))  {
            this.states.peek().checkProgram = _content;
        } else if ("/stateDefList/stateDef/checkProgram/inputArguments".equals(_url))  {
            this.states.peek().checkInput = _content;

        } else if ("/stateDefList/stateDef/routeMessage".equals(_url))  {
            this.states.peek().routeMessage = _content;
        } else if ("/stateDefList/stateDef/routeUser".equals(_url))  {
            // to be ignored ...
        } else if ("/stateDefList/stateDef/routeUser/userRef".equals(_url))  {
            this.states.peek().routeUsers.add(_content);

        } else if ("/stateDefList/stateDef/triggerList".equals(_url))  {
            // to be ignored ...
        } else if ("/stateDefList/stateDef/triggerList/trigger".equals(_url))  {
            this.states.peek().triggersStack.add(new Trigger());
        } else if ("/stateDefList/stateDef/triggerList/trigger/triggerName".equals(_url))  {
            this.states.peek().triggersStack.peek().name = _content;
        } else if ("/stateDefList/stateDef/triggerList/trigger/programRef".equals(_url))  {
            this.states.peek().triggersStack.peek().program = _content;
        } else if ("/stateDefList/stateDef/triggerList/trigger/inputArguments".equals(_url))  {
            this.states.peek().triggersStack.peek().arguments = _content;

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Calls the state prepare method. All policies properties starting with
     * <code>state_</code> are checked if they are defining a symbolic name of
     * a state. If yes the related symbolic name of the state is updated.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the prepare within super class failed
     */
    @Override
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
                        state.nameSymbolic = property.getName();
                    }
                }
            }
        }
    }

    /**
     * Writes the update script for this policy.
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws IOException if the TCL update code could not be written
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
            throws Exception
    {
        final String code = _args[2].replaceAll("@0@0@", "'")
                                    .replaceAll("@1@1@", "\\\"");

        final PolicyDefParser_mxJPO parser = new PolicyDefParser_mxJPO(new StringReader(code));
        final Policy_mxJPO policy = parser.policy(this.getTypeDef(), _args[1]);
        policy.prepare(_paramCache);

        final StringBuilder cmd = new StringBuilder()
                .append("mod policy \"").append(this.getName()).append("\" ");

        // basic information
        this.calcValueDelta(cmd, "description", policy.getDescription(), this.getDescription());
        this.calcListDelta(cmd, "type", policy.types, this.types);
        this.calcListDelta(cmd, "format", policy.formats, this.formats);
        this.calcValueDelta(cmd, "defaultformat", policy.defaultFormat, this.defaultFormat);
        this.calcValueDelta(cmd, "sequence", policy.sequence, this.sequence);
        // hidden flag, because hidden flag must be set with special syntax
        if (this.isHidden() != policy.isHidden())  {
            if (!policy.isHidden())  {
                cmd.append('!');
            }
            cmd.append("hidden ");
        }
        // because the store of a policy could not be removed....
        if ((policy.store != null) && !"".equals(policy.store))  {
            this.calcValueDelta(cmd, "store", policy.store, this.store);
        }

        // states....
        // (first add new states because of references in branches)
        final Iterator<State> curStateIter = this.states.iterator();
        final Iterator<State> newStateIter = policy.states.iterator();
        final Map<State,State> stateDeltaMap = new HashMap<State,State>();
        while (curStateIter.hasNext() && newStateIter.hasNext())  {
            final State curState = curStateIter.next();
            State newState = newStateIter.next();
            while (!curState.name.equals(newState.name) && newStateIter.hasNext())  {
                cmd.append("add state \"").append(StringUtil_mxJPO.convertMql(newState.name))
                   .append("\" before \"").append(StringUtil_mxJPO.convertMql(curState.name)).append("\" ");
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
            cmd.append("add state \"").append(StringUtil_mxJPO.convertMql(newState.name)).append("\" ");
            _paramCache.logDebug("    - add new state '" + newState.name + "'");
            stateDeltaMap.put(newState, null);
        }
        // check for already existing state, but not defined anymore!
        if (curStateIter.hasNext())  {
throw new Exception("some states are not defined anymore!");
        }

        // now update state information itself
        cmd.append(';')
           .append("mod policy \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ");
        for (final Map.Entry<State, State> entry : stateDeltaMap.entrySet())  {
            cmd.append("state \"").append(StringUtil_mxJPO.convertMql(entry.getKey().name)).append("\" ");
            entry.getKey().calcDelta(cmd, entry.getValue());
        }

        // set symbolic names for all policy states
        cmd.append(';')
           .append("mod policy \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ");
        for (final State state : policy.states)  {
            if ((state.nameSymbolic != null) && !"".equals(state.nameSymbolic))  {
                cmd.append(" add property \"").append(StringUtil_mxJPO.convertMql(state.nameSymbolic))
                   .append("\" value \"").append(StringUtil_mxJPO.convertMql(state.name)).append('\"');
            }
        }

        final boolean isMqlEscapeOn = MqlUtil_mxJPO.isEscapeOn(_paramCache.getContext());
        try  {
            MqlUtil_mxJPO.setEscapeOn(_paramCache.getContext());
            MqlUtil_mxJPO.execMql(_paramCache.getContext(), cmd);
        } finally  {
            if (!isMqlEscapeOn)  {
                MqlUtil_mxJPO.setEscapeOff(_paramCache.getContext());
            }
        }
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
            _out.append(_kind).append(" \"").append(StringUtil_mxJPO.convertMql(newVal)).append("\" ");
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
            for (final String format : _current)  {
                if (!_new.contains(format))  {
                    _out.append("remove ").append(_kind).append(" \"").append(format).append("\" ");
                }
            }
            for (final String format : _new)  {
                if (!_current.contains(format))  {
                    _out.append("add ").append(_kind).append(" \"").append(format).append("\" ");
                }
            }
        }
    }

    /**
     * Class defining states of a policy.
     */
    public static class State
            implements Serializable
    {
        /**
         * Defines the serialize version unique identifier.
         */
        private static final long serialVersionUID = -5507116248555405867L;

        /**
         * Name of the state.
         */
        private String name;

        /**
         * Symbolic Name of the state.
         */
        private String nameSymbolic;

        /**
         * Called action program for this state.
         */
        private String actionProgram;

        /**
         * Input arguments for the action program for this state.
         */
        private String actionInput;

        /**
         * Called check program for this state.
         */
        private String checkProgram;

        /**
         * Input arguments for the check program for this state.
         */
        private String checkInput;

        /**
         * Does the state have an auto promotion?
         */
        private boolean autoPromotion = false;

        /**
         * Must a checkout written in the history?
         */
        private boolean checkoutHistory = false;

        /**
         * Is the business object in this state revisionable?
         */
        private boolean revisionable = false;

        /**
         * Is the business object in this state versionable?
         */
        private boolean versionable = false;

        /**
         * Route message of this state.
         */
        private String routeMessage;

        /**
         * Route users of this state.
         */
        private final Set<String> routeUsers = new TreeSet<String>();

        /**
         * Set holding the complete owner access.
         */
        private final Set<String> ownerAccess = new TreeSet<String>();

        /**
         * Set holding the complete public access.
         */
        private final Set<String> publicAccess = new TreeSet<String>();

        /**
         * Stack used to hold the user access while parsing.
         *
         * @see #parse(String, String)
         */
        private final Stack<UserAccess> userAccess = new Stack<UserAccess>();

        /**
         * Sorted set of user access (by name of the user).
         *
         * @see #prepare()     method used to sort the user access instances
         */
        private final Set<UserAccess> userAccessSorted = new TreeSet<UserAccess>();

        /**
         * Stack with all triggers for this state.
         */
        private final Stack<Trigger> triggersStack = new Stack<Trigger>();

        /**
         * Map with all triggers for this state. The key is the name of the
         * trigger.
         *
         * @see #prepare()      method where the map is prepared
         * @see #triggersStack  stack with trigger from parsing method
         */
        private final Map<String,Trigger> triggers = new TreeMap<String,Trigger>();

        /**
         * Holds the signatures for this state.
         */
        private final Stack<Signature> signatures = new Stack<Signature>();

        /**
         * The user access and trigger instances are sorted.
         *
         * @see #userAccess         unsorted list of user access
         * @see #userAccessSorted   sorted list user access (after this method is
         *                          called)
         */
        protected void prepare()
        {
            // sort user access
            for (final UserAccess range : this.userAccess)  {
                this.userAccessSorted.add(range);
            }
            // sort all triggers
            for (final Trigger trigger : this.triggersStack)  {
                this.triggers.put(trigger.name, trigger);
            }
        }

        /**
         * Writes specific information about this state to the given writer
         * instance.
         *
         * @param _out      writer instance
         * @throws IOException if the TCL update code could not be written
         */
        protected void writeObject(final Appendable _out)
                throws IOException
        {
            // basics
            _out.append("\n  state \"").append(StringUtil_mxJPO.convertTcl(this.name)).append("\"  {")
                .append("\n    registeredName \"").append((this.nameSymbolic != null)
                                              ? StringUtil_mxJPO.convertTcl(this.nameSymbolic)
                                              : "state_" + StringUtil_mxJPO.convertTcl(this.name.replaceAll(" ", "_")))
                                                  .append('\"')
                .append("\n    revision \"").append(Boolean.toString(this.revisionable)).append('\"')
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
            // owner access
            _out.append("\n    owner {")
                .append(StringUtil_mxJPO.joinTcl(' ', false, this.ownerAccess, null))
                .append("}")
            // public access
                .append("\n    public {")
                .append(StringUtil_mxJPO.joinTcl(' ', false, this.publicAccess, null))
                .append("}");
            // user access
            for (final UserAccess userAccess : this.userAccessSorted)  {
                _out.append("\n    user \"").append(StringUtil_mxJPO.convertTcl(userAccess.userRef)).append("\" {")
                    .append(StringUtil_mxJPO.joinTcl(' ', false, userAccess.access, null))
                    .append('}');
                if (userAccess.expressionFilter != null)  {
                    _out.append(" filter \"")
                        .append(StringUtil_mxJPO.convertTcl(userAccess.expressionFilter))
                        .append("\"");
                }
            }
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
            _out.append("promote ").append(String.valueOf(this.autoPromotion)).append(' ')
                .append("revision ").append(String.valueOf(this.revisionable)).append(' ')
                .append("checkouthistory ").append(String.valueOf(this.checkoutHistory)).append(' ')
                .append("version ").append(String.valueOf(this.versionable)).append(' ')
                .append("action \"").append(StringUtil_mxJPO.convertMql(this.actionProgram)).append("\" ")
                .append("input \"").append(StringUtil_mxJPO.convertMql(this.actionInput)).append("\" ")
                .append("check \"").append(StringUtil_mxJPO.convertMql(this.checkProgram)).append("\" ")
                .append("input \"").append(StringUtil_mxJPO.convertMql(this.checkInput)).append("\" ");
            // route message
            _out.append("route message \"").append(StringUtil_mxJPO.convertMql(this.routeMessage)).append("\" ");
            for (final String routeUser : this.routeUsers)  {
                if ((_oldState == null) || !_oldState.routeUsers.contains(routeUser))  {
                    _out.append("add route \"").append(StringUtil_mxJPO.convertMql(routeUser)).append("\" ");
                }
            }
            // owner access
            _out.append("owner ")
                .append(StringUtil_mxJPO.joinMql(',', false, this.ownerAccess, "none"))
                .append(' ')
            // public access
                .append("public ")
                .append(StringUtil_mxJPO.joinMql(',', false, this.publicAccess, "none"))
                .append(' ');
            // user access
            final Set<String> newUsers = new HashSet<String>();
            for (final UserAccess userAccess : this.userAccess)  {
                newUsers.add(userAccess.userRef);
            }
            final Set<String> oldUser = new HashSet<String>();
            if (_oldState != null)  {
                for (final UserAccess userAccess : _oldState.userAccess)  {
                    if (newUsers.contains(userAccess.userRef))  {
                        oldUser.add(userAccess.userRef);
                    } else  {
                        _out.append("remove user \"")
                            .append(StringUtil_mxJPO.convertMql(userAccess.userRef))
                            .append("\" all ");
                    }
                }
            }
            for (final UserAccess userAccess : this.userAccess)  {
                if ((_oldState != null) && !oldUser.contains(userAccess.userRef))  {
                    _out.append("add ");
                }
                _out.append("user \"").append(userAccess.userRef).append("\" ")
                    .append(StringUtil_mxJPO.joinMql(',', false, userAccess.access, "none"))
                    .append(" ")
                    .append("filter \"")
                    .append(StringUtil_mxJPO.convertMql(userAccess.expressionFilter))
                    .append("\" ");
            }
            // triggers
            if (_oldState != null)  {
                for (final Trigger trigger : _oldState.triggers.values())  {
                    if (!this.triggers.containsKey(trigger.name))  {
                        _out.append("remove trigger ")
                            .append(trigger.getEventType())
                            .append(' ')
                            .append(trigger.getKind())
                            .append(' ');
                    }
                }
            }
            for (final Trigger trigger : this.triggers.values())  {
                _out.append("add trigger ").append(trigger.getEventType()).append(' ').append(trigger.getKind())
                    .append(" \"").append(StringUtil_mxJPO.convertMql(trigger.program)).append("\"")
                    .append(" input \"").append(StringUtil_mxJPO.convertMql(trigger.arguments)).append("\" ");
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
                        _out.append("remove signature \"")
                            .append(StringUtil_mxJPO.convertMql(signature.name))
                            .append("\" ");
                    }
                }
            }
            for (final Signature signature : this.signatures)  {
                final Signature oldSig;
                if ((_oldState != null) && !oldSigs.containsKey(signature.name))  {
                    _out.append("add ");
                    oldSig = null;
                } else  {
                    oldSig = oldSigs.get(signature.name);
                }
                _out.append("signature \"").append(StringUtil_mxJPO.convertMql(signature.name)).append("\" ");
                signature.calcDelta(_out, oldSig);
            }
        }
    }

    /**
     * Class used to hold the user access for a state.
     */
    public static class UserAccess
            implements Comparable<Policy_mxJPO.UserAccess>, Serializable
    {
        /**
         * Defines the serialize version unique identifier.
         */
        private static final long serialVersionUID = -8442325362152826050L;

        /**
         * Holds the user references of a user access.
         */
        private String userRef;

        /**
         * Holds the access of the user.
         */
        private final Set<String> access = new TreeSet<String>();

        /**
         * Holds the expression filter of a user access.
         */
        private String expressionFilter;

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
        public int compareTo(final UserAccess _userAccess)
        {
            return this.userRef.compareTo(_userAccess.userRef);
        }
    }

    /**
     * Class defining a signature for a state.
     */
    public static class Signature
            implements Serializable
    {
        /**
         * Defines the serialize version unique identifier.
         */
        private static final long serialVersionUID = -8959837947086801473L;

        /**
         * Name of the signature.
         */
        private String name;

        /**
         * Branch to state?
         */
        private String branch;

        /**
         * Expression filter of the signature.
         */
        private String filter;

        /**
         * Set of users which could approve the signature.
         */
        private final Set<String> approverUsers = new TreeSet<String>();

        /**
         * Set of users which could ignore the signature.
         */
        private final Set<String> ignoreUsers = new TreeSet<String>();

        /**
         * Set of users which could reject the signature.
         */
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
                _out.append("branch \"").append(StringUtil_mxJPO.convertMql(this.branch)).append("\" ");
            }
            _out.append("filter \"").append(StringUtil_mxJPO.convertMql(this.filter)).append("\" ");
            // update approve users
            for (final String approver : this.approverUsers)  {
                if ((_oldSignature == null) || !_oldSignature.approverUsers.contains(approver))  {
                    _out.append("add approve \"").append(StringUtil_mxJPO.convertMql(approver)).append("\" ");
                }
            }
            if (_oldSignature != null)  {
                for (final String approver : _oldSignature.approverUsers)  {
                    if (!this.approverUsers.contains(approver))  {
                        _out.append("remove approve \"").append(StringUtil_mxJPO.convertMql(approver)).append("\" ");
                    }
                }
            }
            // update ignore user
            for (final String ignore : this.ignoreUsers)  {
                if ((_oldSignature == null) || !_oldSignature.ignoreUsers.contains(ignore))  {
                    _out.append("add ignore \"").append(StringUtil_mxJPO.convertMql(ignore)).append("\" ");
                }
            }
            if (_oldSignature != null)  {
                for (final String ignore : _oldSignature.ignoreUsers)  {
                    if (!this.ignoreUsers.contains(ignore))  {
                        _out.append("remove ignore \"").append(StringUtil_mxJPO.convertMql(ignore)).append("\" ");
                    }
                }
            }
            // update reject users
            for (final String reject : this.rejectUsers)  {
                if ((_oldSignature == null) || !_oldSignature.rejectUsers.contains(reject))  {
                    _out.append("add reject \"").append(StringUtil_mxJPO.convertMql(reject)).append("\" ");
                }
            }
            if (_oldSignature != null)  {
                for (final String reject : _oldSignature.rejectUsers)  {
                    if (!this.rejectUsers.contains(reject))  {
                        _out.append("remove reject \"").append(StringUtil_mxJPO.convertMql(reject)).append("\" ");
                    }
                }
            }
        }
    }
}
