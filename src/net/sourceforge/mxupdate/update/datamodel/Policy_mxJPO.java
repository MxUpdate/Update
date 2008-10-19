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
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;

import matrix.db.Context;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO(adminType = "policy",
                                                     title = "POLICY",
                                                     filePrefix = "POLICY_",
                                                     fileSuffix = ".tcl",
                                                     filePath = "datamodel/policy",
                                                     description = "policy")
public class Policy_mxJPO
        extends net.sourceforge.mxupdate.update.datamodel.AbstractDMWithTriggers_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 8645304838663417963L;

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
        _out.append("\n  description \"").append(convert(getDescription())).append("\"");
        // types
        boolean first = true;
        _out.append("\n  type {");
        for (final String type : this.types)  {
            if (first)  {
                first = false;
            } else  {
                _out.append(',');
            }
            _out.append('\"').append(convert(type)).append('\"');
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
                _out.append('\"').append(convert(format)).append('\"');
            }
            _out.append("}");
        }
        _out.append("\n  defaultformat \"").append(convert(this.defaultFormat)).append('\"')
            .append("\n  sequence \"").append(convert(this.sequence)).append('\"')
            .append("\n  store \"").append(convert(this.store)).append('\"')
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
     * Class defining states of a policy.
     */
    private class State
    {
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
            _out.append("\n  state \"").append(convert(this.name)).append("\"  {")
                .append("\n    registeredName \"").append((this.nameSymbolic != null)
                                                          ? convert(this.nameSymbolic)
                                                          : "state_" + convert(this.name.replaceAll(" ", "_"))).append('\"')
                .append("\n    revision \"").append(Boolean.toString(this.revisionable)).append('\"')
                .append("\n    version \"").append(Boolean.toString(this.versionable)).append('\"')
                .append("\n    promote \"").append(Boolean.toString(this.autoPromotion)).append('\"')
                .append("\n    checkouthistory \"").append(Boolean.toString(this.checkoutHistory)).append('\"');
            // route
            if ((this.routeMessage != null) ||!this.routeUsers.isEmpty())  {
                _out.append("\n    route {");
                boolean first = true;
                for (final String user : this.routeUsers)  {
                    if (first)  {
                        first = false;
                    } else  {
                        _out.append(' ');
                    }
                    _out.append('\"').append(convert(user)).append('\"');
                }
                _out.append("} \"").append(convert(this.routeMessage)).append('\"');
            }
            // owner access
            boolean first = true;
            _out.append("\n    owner {");
            for (final String access : this.ownerAccess)  {
                if (first)  {
                    first = false;
                } else  {
                    _out.append(' ');
                }
                _out.append(access);
            }
            _out.append("}")
                .append("\n    public {");
            // public access
            first = true;
            for (final String access : this.publicAccess)  {
                if (first)  {
                    first = false;
                } else  {
                    _out.append(' ');
                }
                _out.append(access);
            }
            _out.append("}");
            // user access
            for (final UserAccess userAccess : this.userAccessSorted)  {
                _out.append("\n    user \"").append(convert(userAccess.userRef)).append("\" {");
                first = true;
                for (final String access : userAccess.access)  {
                    if (!first)  {
                        _out.append(' ');
                    } else  {
                        first = false;
                    }
                    _out.append(access);
                }
                _out.append('}');
                if (userAccess.expressionFilter != null)  {
                    _out.append(" filter \"")
                        .append(convert(userAccess.expressionFilter))
                        .append("\"");
                }
            }
            _out.append("\n    action \"").append(convert(this.actionProgram)).append("\" input \"").append(convert(this.actionInput)).append('\"')
                .append("\n    check \"").append(convert(this.checkProgram)).append("\" input \"").append(convert(this.checkInput)).append('\"');
            // output of triggers, but sorted!
            for (final Trigger trigger : this.triggers.values())  {
                // parse event type
                final Matcher matchEventType = Trigger.PATTERN_EVENTTYPE.matcher(trigger.name);
                matchEventType.find();
                final String eventType = matchEventType.group();
                // parse kind
                final Matcher matchKind = Trigger.PATTERN_KIND.matcher(trigger.name);
                matchKind.find();
                final String kind = matchKind.group();
                _out.append("\n    trigger ").append(eventType.toLowerCase()).append(' ').append(kind.toLowerCase())
                    .append(" \"").append(convert(trigger.program)).append("\"")
                    .append(" input \"").append(convert(trigger.arguments)).append("\"");
            }
            // signatures
            for (final Signature signature : this.signatures)  {
                _out.append("\n    signature \"").append(convert(signature.name)).append("\" {")
                    .append("\n      branch \"").append(convert(signature.branch)).append("\"")
                    .append("\n      approve {}")
                    .append("\n      ignore {}")
                    .append("\n      reject {}")
                    .append("\n      filter \"").append(convert(signature.filter)).append("\"")
                    .append("\n    }");

            }
            _out.append("\n  }");
        }
    }

    /**
     * Class used to hold the user access for a state.
     */
    private class UserAccess
            implements Comparable<UserAccess>
    {
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
    private class Signature
    {
        /**
         * Name of the signature.
         */
        String name = null;

        /**
         * Branch to state?
         */
        String branch = null;

        /**
         * Expression filter of the signature.
         */
        String filter = null;

        /**
         * Set of users which could approve the signature.
         */
        final Set<String> approverUsers = new TreeSet<String>();

        /**
         * Set of users which could ignore the signature.
         */
        final Set<String> ignoreUsers = new TreeSet<String>();

        /**
         * Set of users which could reject the signature.
         */
        final Set<String> rejectUsers = new TreeSet<String>();
    }
}
