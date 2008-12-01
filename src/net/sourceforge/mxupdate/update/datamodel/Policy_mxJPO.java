/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update.datamodel;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;

import net.sourceforge.mxupdate.update.datamodel.policy.PolicyDefParser_mxJPO;
import net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO;
import net.sourceforge.mxupdate.update.util.JPOCaller_mxJPO.JPOCallerInterface;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convertMql;
import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static net.sourceforge.mxupdate.util.MqlUtil_mxJPO.execMql;
import static net.sourceforge.mxupdate.util.MqlUtil_mxJPO.isEscapeOn;
import static net.sourceforge.mxupdate.util.MqlUtil_mxJPO.setEscapeOff;
import static net.sourceforge.mxupdate.util.MqlUtil_mxJPO.setEscapeOn;

/**
 * @author tmoxter
 * @version $Id$
 */
@InfoAnno_mxJPO(adminType = "policy",
                title = "POLICY",
                filePrefix = "POLICY_",
                fileSuffix = ".tcl",
                filePath = "datamodel/policy",
                description = "policy")
public class Policy_mxJPO
        extends AbstractDMWithTriggers_mxJPO
        implements JPOCallerInterface
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 8645304838663417963L;

    /**
     * Called TCL procedure within the TCL update to parse the new policy
     * definition. The TCL procedure calls method
     * {@link #jpoCallExecute(Context, String...)} with the new policy
     * definition. All quot's are replaced by <code>@0@0@</code> and all
     * apostroph's are replaced by <code>@1@1@</code>.
     *
     * @see #update(Context, CharSequence, CharSequence, Map)
     * @see #jpoCallExecute(Context, String...)
     */
    private final static String TCL_PROCEDURE
            = "proc updatePolicy {_sPolicy _lsArgs}  {\n"
                + "global JPO_CALLER_INSTANCE\n"
                + "regsub -all {'} $_lsArgs {@0@0@} sArg\n"
                + "regsub -all {\\\"} $sArg {@1@1@} sArg\n"
                + "regsub -all {\\\\\\[} $sArg {[} sArg\n"
                + "regsub -all {\\\\\\]} $sArg {]} sArg\n"
                + "mql exec prog net.sourceforge.mxupdate.update.util.JPOCaller $JPO_CALLER_INSTANCE $_sPolicy \"${sArg}\"\n"
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
    private boolean allFormats = false;

    /**
     * Sequence of this policy.
     */
    private String sequence = null;

    /**
     * Store of this policy.
     */
    private String store = null;

    /**
     * Set of all types of this policy.
     *
     * @see #parse(String, String)
     */
    private final Set<String> types = new TreeSet<String>();

    /**
     * Stack with all states of this policy.
     */
    private final Stack<State> states = new Stack<State>();

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
            this.states.peek().userAccess.peek().access.add(_url.replaceAll("^/stateDefList/stateDef/userAccessList/userAccess/access/", "")
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
     * Calls the state prepare method.
     *
     * @param _context   context for this request
     */
    @Override
    protected void prepare(final Context _context)
            throws MatrixException
    {
        for (final State state : this.states)  {
            state.prepare(_context);
        }
        super.prepare(_context);
        for (final Property property : new HashSet<Property>(this.getPropertiesMap().values()))  {
            if (property.getName().startsWith("state_"))  {
                for (final State state : this.states)  {
                    if (state.name.equals(property.getValue()))  {
                        state.nameSymbolic = property.getName();
                        this.getPropertiesMap().remove(property.getName());
                    }
                }
            }
        }
    }

    /**
     * Writes the update script for this policy.
     *
     * @param _out      writer instance
     */
    @Override
    protected void write(final Writer _out)
            throws IOException
    {
        writeHeader(_out);
        _out.append("updatePolicy \"${NAME}\" {");
        final String suffix = getInfoAnno().adminTypeSuffix();
        if (!"".equals(suffix))  {
            _out.append(" ").append(suffix);
        }
        _out.append("\n  description \"").append(convertTcl(getDescription())).append("\"");
        // types
        boolean first = true;
        _out.append("\n  type {");
        for (final String type : this.types)  {
            if (first)  {
                first = false;
            } else  {
                _out.append(',');
            }
            _out.append('\"').append(convertTcl(type)).append('\"');
        }
        _out.append("}");
        // formats
        if (this.allFormats)  {
            _out.append("\n  format all");
        } else  {
            first = true;
            _out.append("\n  format {");
            for (final String format : this.formats)  {
                if (first)  {
                    first = false;
                } else  {
                    _out.append(' ');
                }
                _out.append('\"').append(convertTcl(format)).append('\"');
            }
            _out.append("}");
        }
        _out.append("\n  defaultformat \"").append(convertTcl(this.defaultFormat)).append('\"')
            .append("\n  sequence \"").append(convertTcl(this.sequence)).append('\"')
            .append("\n  store \"").append(convertTcl(this.store)).append('\"')
            .append("\n  hidden \"").append(Boolean.toString(isHidden())).append("\"");
        // all states
        for (final State state : this.states)  {
            state.writeObject(_out);
        }
        _out.append("\n}");
        writeProperties(_out);
    }

    /**
     * Only implemented as stub because {@link #write(Writer)} is new
     * implemented.
     *
     * @param _out      writer instance (not used)
     */
    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this policy:
     * <ul>
     * <li>reset description</li>
     * <li>remove hidden and prevent duplicate flag</li>
     * <li>reset from and to information</li>
     * <li>remove all from and to types</li>
     * </ul>
     *      * Adds the TCL procedure {@link #TCL_PROCEDURE} so that attributes could
     * be assigned to this administration object. The instance itself
     * is stored as encoded string in the TCL variable
     * <code>JPO_CALLER_INSTANCE</code>.
     *
     * @param _context          context for this request
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _tclCode          TCL code from the file used to update
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     */
    @Override
    protected void update(final Context _context,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _tclCode,
                          final Map<String,String> _tclVariables)
            throws Exception
    {
        // define TCL variable for this instance
        final String[] instance = JPO.packArgs(this);
        final Map<String,String> tclVariables = new HashMap<String,String>();
        tclVariables.putAll(_tclVariables);
        tclVariables.put("JPO_CALLER_INSTANCE", instance[1]);

        // add TCL code for the procedure
        final StringBuilder tclCode = new StringBuilder()
                .append(TCL_PROCEDURE)
                .append(_tclCode);

        super.update(_context, _preMQLCode, _postMQLCode, tclCode, tclVariables);
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
     * <li>The delta MQL script is executed.</li>
     * </ul>
     *
     * @param _contex   context for this request
     * @param _args     arguments from the TCL procedure
     * @throws Exception if a state is not defined anymore or the policy could
     *                   not be updated
     * @see #TCL_PROCEDURE
     */
    public void jpoCallExecute(final Context _context,
                               final String... _args)
            throws Exception
    {
        final String code = _args[1].replaceAll("@0@0@", "'")
                                    .replaceAll("@1@1@", "\\\"");

        final PolicyDefParser_mxJPO parser = new PolicyDefParser_mxJPO(new StringReader(code));
        final Policy_mxJPO policy = parser.policy();
        policy.prepare(_context);

        final StringBuilder cmd = new StringBuilder()
                .append("mod policy \"").append(this.getName()).append("\" ");

        // basic information
        this.calcDelta(cmd, "description", policy.getDescription(), this.getDescription());
        this.calcDelta(cmd, "type", policy.types, this.types);
        this.calcDelta(cmd, "format", policy.formats, this.formats);
        this.calcDelta(cmd, "defaultformat", policy.defaultFormat, this.defaultFormat);
        this.calcDelta(cmd, "sequence", policy.sequence, this.sequence);
        // hidden flag, because hidden flag must be set with special syntax
        if (this.isHidden() != policy.isHidden())  {
            if (!policy.isHidden())  {
                cmd.append('!');
            }
            cmd.append("hidden ");
        }
        // because the store of a policy could not be removed....
        if ((policy.store != null) && !"".equals(policy.store))  {
            this.calcDelta(cmd, "store", policy.store, this.store);
        }

        // states....
        final Iterator<State> curStateIter = this.states.iterator();
        final Iterator<State> newStateIter = policy.states.iterator();
        while (curStateIter.hasNext() && newStateIter.hasNext())  {
            final State curState = curStateIter.next();
            State newState = newStateIter.next();
            while (!curState.name.equals(newState.name) && newStateIter.hasNext())  {
                cmd.append("add state \"").append(convertMql(newState.name))
                   .append("\" before \"").append(convertMql(curState.name)).append("\" ");
System.out.println("    - insert new state '" + newState.name + "' before '" + curState.name + "'");
                newState.calcDelta(cmd, null);
                newState = newStateIter.next();
            }
            if (curState.name.equals(newState.name))  {
                cmd.append("state \"").append(convertMql(newState.name)).append("\" ");
                newState.calcDelta(cmd, curState);
            }
        }
        while (newStateIter.hasNext())  {
            final State newState = newStateIter.next();
            cmd.append("add state \"").append(convertMql(newState.name)).append("\" ");
            newState.calcDelta(cmd, null);
System.out.println("    - add new state '" + newState.name + "'");
        }
        // check for already existing state, but not defined anymore!
        if (curStateIter.hasNext())  {
throw new Exception("some states are not defined anymore!");
        }

        final boolean isMqlEscapeOn = isEscapeOn(_context);
        try  {
            setEscapeOn(_context);
            execMql(_context, cmd);
        } finally  {
            if (!isMqlEscapeOn)  {
                setEscapeOff(_context);
            }
        }
    }

    /**
     *
     * @param _cmd      string build where the delta must be append
     * @param _kind     kind of the delta
     * @param _curVal   current value in the database
     * @param _newVal   new target value
     */
    protected void calcDelta(final StringBuilder _cmd,
                             final String _kind,
                             final String _curVal,
                             final String _newVal)
    {
        final String curVal = (_curVal == null) ? "" : _curVal;
        final String newVal = (_newVal == null) ? "" : _newVal;

        if (!curVal.equals(newVal))  {
            _cmd.append(_kind).append(" \"").append(newVal).append("\" ");
        }
    }

    /**
     *
     * @param _cmd      string build where the delta must be append
     * @param _kind     kind of the delta
     * @param _curVal   current value in the database
     * @param _newVal   new target value
     */
    protected void calcDelta(final StringBuilder _cmd,
                             final String _kind,
                             final Set<String> _new,
                             final Set<String> _current)
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
                    _cmd.append("remove ").append(_kind).append(" \"").append(format).append("\" ");
                }
            }
            for (final String format : _new)  {
                if (!_current.contains(format))  {
                    _cmd.append("add ").append(_kind).append(" \"").append(format).append("\" ");
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
        String name = null;

        /**
         * Symbolic Name of the state.
         */
        String nameSymbolic = null;

        /**
         * Called action program for this state.
         */
        String actionProgram = null;

        /**
         * Input arguments for the action program for this state.
         */
        String actionInput = null;

        /**
         * Called check program for this state.
         */
        String checkProgram = null;

        /**
         * Input arguments for the check program for this state.
         */
        String checkInput = null;

        /**
         * Does the state have an auto promotion?
         */
        boolean autoPromotion = false;

        /**
         * Must a checkout written in the history?
         */
        boolean checkoutHistory = false;

        /**
         * Is the business object in this state revisionable?
         */
        boolean revisionable = false;

        /**
         * Is the business object in this state versionable?
         */
        boolean versionable = false;

        /**
         * Route message of this state.
         */
        String routeMessage = null;

        /**
         * Route users of this state.
         */
        final Set<String> routeUsers = new TreeSet<String>();

        /**
         * Set holding the complete owner access.
         */
        private final Stack<String> ownerAccess = new Stack<String>();

        /**
         * Set holding the complete public access.
         */
        private final Stack<String> publicAccess = new Stack<String>();

        /**
         * Stack used to hold the user access while parsing.
         *
         * @see #parse(String, String)
         */
        private final Stack<UserAccess> userAccess = new Stack<UserAccess>();

        /**
         * Sorted set of user access (by name of the user).
         *
         * @see #prepare(Context)   method used to sort the user access instances
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
         * @see #prepare(Context)      method where the map is prepared
         * @see #triggersStack         stack with trigger from parsing method
         */
        private final Map<String,Trigger> triggers = new TreeMap<String,Trigger>();

        /**
         * Holds the signatures for this state.
         */
        private final Stack<Signature> signatures = new Stack<Signature>();

        /**
         * The user access and trigger instances are sorted.
         *
         * @param _context   context for this request
         * @see #userAccess         unsorted list of user access
         * @see #userAccessSorted   sorted list user access (after this method is
         *                          called)
         */
        protected void prepare(final Context _context)
                throws MatrixException
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
         */
        protected void writeObject(final Writer _out)
                throws IOException
        {
            // basics
            _out.append("\n  state \"").append(convertTcl(this.name)).append("\"  {")
                .append("\n    registeredName \"").append((this.nameSymbolic != null)
                                                          ? convertTcl(this.nameSymbolic)
                                                          : "state_" + convertTcl(this.name.replaceAll(" ", "_"))).append('\"')
                .append("\n    revision \"").append(Boolean.toString(this.revisionable)).append('\"')
                .append("\n    version \"").append(Boolean.toString(this.versionable)).append('\"')
                .append("\n    promote \"").append(Boolean.toString(this.autoPromotion)).append('\"')
                .append("\n    checkouthistory \"").append(Boolean.toString(this.checkoutHistory)).append('\"');
            // route
            if ((this.routeMessage != null) || !this.routeUsers.isEmpty())  {
                _out.append("\n    route {");
                boolean first = true;
                for (final String user : this.routeUsers)  {
                    if (first)  {
                        first = false;
                    } else  {
                        _out.append(' ');
                    }
                    _out.append('\"').append(convertTcl(user)).append('\"');
                }
                _out.append("} \"").append(convertTcl(this.routeMessage)).append('\"');
            }
            // owner access
            _out.append("\n    owner {");
            this.appendAccess(_out, ' ', this.ownerAccess, false);
            // public access
            _out.append("}")
                .append("\n    public {");
            this.appendAccess(_out, ' ', this.publicAccess, false);
            _out.append("}");
            // user access
            for (final UserAccess userAccess : this.userAccessSorted)  {
                _out.append("\n    user \"").append(convertTcl(userAccess.userRef)).append("\" {");
                this.appendAccess(_out, ' ', userAccess.access, false);
                _out.append('}');
                if (userAccess.expressionFilter != null)  {
                    _out.append(" filter \"")
                        .append(convertTcl(userAccess.expressionFilter))
                        .append("\"");
                }
            }
            _out.append("\n    action \"").append(convertTcl(this.actionProgram)).append("\" input \"").append(convertTcl(this.actionInput)).append('\"')
                .append("\n    check \"").append(convertTcl(this.checkProgram)).append("\" input \"").append(convertTcl(this.checkInput)).append('\"');
            // output of triggers, but sorted!
            for (final Trigger trigger : this.triggers.values())  {
                _out.append("\n    trigger ").append(trigger.getEventType()).append(' ').append(trigger.getKind())
                    .append(" \"").append(convertTcl(trigger.program)).append("\"")
                    .append(" input \"").append(convertTcl(trigger.arguments)).append("\"");
            }
            // signatures
            for (final Signature signature : this.signatures)  {
                _out.append("\n    signature \"").append(convertTcl(signature.name)).append("\" {")
                    .append("\n      branch \"").append(convertTcl(signature.branch)).append("\"")
// TODO: approve, ignore, reject users
                    .append("\n      approve {}")
                    .append("\n      ignore {}")
                    .append("\n      reject {}")
                    .append("\n      filter \"").append(convertTcl(signature.filter)).append("\"")
                    .append("\n    }");

            }
            _out.append("\n  }");
        }

        /**
         *
         * @param _appendable   where the access statement is appended
         * @param _separator    separator between two access definitions
         * @param _access       list of access strings
         * @param _writeNone    if <i>true</i> a <code>none</code> is written
         *                      if the list of access string is empty,
         *                      otherwise nothing is written
         * @throws IOException
         */
        protected void appendAccess(final Appendable _appendable,
                                    final char _separator,
                                    final Stack<String> _access,
                                    final boolean _writeNone)
                throws IOException
        {
            boolean first = true;
            if (_access.isEmpty())  {
                if (_writeNone)  {
                    _appendable.append("none");
                }
            } else  {
                for (final String access : _access)  {
                    if (!first)  {
                        _appendable.append(_separator);
                    } else  {
                        first = false;
                    }
                    _appendable.append(access);
                }
            }
        }


    /*
     * where STATE_ITEM is:
    | notify USER_NAME {,USER_NAME} message VALUE            |
    | notify signer                 message VALUE            |
    | route USER_NAME message VALUE                          |
    | signature SIGN_NAME [SIGNATURE_ITEM {,SIGNATURE_ITEM}] |
     */
        protected void calcDelta(final StringBuilder _cmd,
                                 final State _oldState)
                throws IOException
        {
            // basics
            _cmd.append("promote ").append(this.autoPromotion).append(' ')
                .append("revision ").append(this.revisionable).append(' ')
                .append("checkouthistory ").append(this.checkoutHistory).append(' ')
                .append("version ").append(this.versionable).append(' ')
                .append("action \"").append(convertMql(this.actionProgram)).append("\" ")
                .append("input \"").append(convertMql(this.actionInput)).append("\" ")
                .append("check \"").append(convertMql(this.checkProgram)).append("\" ")
                .append("input \"").append(convertMql(this.checkInput)).append("\" ");
// TODO: route
            // owner access
            _cmd.append("owner ");
            this.appendAccess(_cmd, ',', this.ownerAccess, true);
            _cmd.append(" ");
            // public access
            _cmd.append("public ");
            this.appendAccess(_cmd, ',', this.publicAccess, true);
            _cmd.append(" ");
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
                        _cmd.append("remove user \"")
                            .append(convertMql(userAccess.userRef))
                            .append("\" all ");
                    }
                }
            }
            for (final UserAccess userAccess : this.userAccess)  {
                if ((_oldState != null) && !oldUser.contains(userAccess.userRef))  {
                    _cmd.append("add ");
                }
                _cmd.append("user \"").append(userAccess.userRef).append("\" ");
                this.appendAccess(_cmd, ',', userAccess.access, true);
                _cmd.append(" ")
                    .append("filter \"")
                    .append(convertMql(userAccess.expressionFilter))
                    .append("\" ");
            }
            // triggers
            if (_oldState != null)  {
                for (final Trigger trigger : _oldState.triggers.values())  {
                    if (!this.triggers.containsKey(trigger.name))  {
                        _cmd.append("remove trigger ")
                            .append(trigger.getEventType())
                            .append(' ')
                            .append(trigger.getKind())
                            .append(' ');
                    }
                }
            }
            for (final Trigger trigger : this.triggers.values())  {
                _cmd.append("add trigger ").append(trigger.getEventType()).append(' ').append(trigger.getKind())
                    .append(" \"").append(convertMql(trigger.program)).append("\"")
                    .append(" input \"").append(convertMql(trigger.arguments)).append("\" ");
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
                        _cmd.append("remove signature \"")
                            .append(convertMql(signature.name))
                            .append("\" ");
                    }
                }
            }
            for (final Signature signature : this.signatures)  {
                final Signature oldSig;
                if ((_oldState != null) && !oldSigs.containsKey(signature.name))  {
                    _cmd.append("add ");
                    oldSig = null;
                } else  {
                    oldSig = oldSigs.get(signature.name);
                }
                _cmd.append("signature \"").append(convertMql(signature.name)).append("\" ");
                signature.calcDelta(_cmd, oldSig);
            }
        }
    }

    /**
     * Class used to hold the user access for a state.
     */
    public static class UserAccess
            implements Comparable<UserAccess>, Serializable
    {
        /**
         * Defines the serialize version unique identifier.
         */
        private static final long serialVersionUID = -8442325362152826050L;

        /**
         * Holds the user references of a user access.
         */
        String userRef = null;

        /**
         * Holds the access of the user.
         */
        final Stack<String> access = new Stack<String>();

        /**
         * Holds the expression filter of a user access.
         */
        String expressionFilter = null;

        /**
         * Compares this user access instance to another user access instance.
         * Only the user reference {@link #userRef} is used to compare.
         *
         * @param _userAccess   user access instance to which this instance
         *                      must be compared to
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
         * Calculates the delta between the old signature and this signature.
         * If for the old signature a branch is defined and for the new
         * signature no branch, an error is thrown.
         *
         * @param _cmd          command
         * @param _oldSignature old signature to compare
         */
        protected void calcDelta(final StringBuilder _cmd,
                                 final Signature _oldSignature)
        {
//TODO: approve, ignore, reject users
            if ("".equals(this.branch))  {
                if ((_oldSignature != null) && (_oldSignature.branch != null) && !"".equals(_oldSignature.branch))  {
throw new Error("branch '" + _oldSignature.branch + "' exists for signature " + this.name + ", but is not defined anymore");
                }
            } else  {
                _cmd.append("branch \"").append(convertMql(this.branch)).append("\" ");
            }
            _cmd.append("filter \"").append(convertMql(this.filter)).append("\" ");
        }
    }
}
