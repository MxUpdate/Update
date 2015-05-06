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

package org.mxupdate.update.userinterface;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.userinterface.command.CommandDefParser_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to export and import / update command configuration items.
 *
 * @author The MxUpdate Team
 */
public class Command_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for commands.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Command_mxJPO.IGNORED_URLS.add("/code");
        Command_mxJPO.IGNORED_URLS.add("/input");
        Command_mxJPO.IGNORED_URLS.add("/userRefList");
    }

    /** Alt label of the command. */
    private String alt;
    /** Label of the command. */
    private String label;
    /** HRef of the command. */
    private String href;
    /** Sorted list of assigned users of the command. */
    private final SortedSet<String> users = new TreeSet<String>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Command_mxJPO(final TypeDef_mxJPO _typeDef,
                         final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses all command specific values. This includes:
     * <ul>
     * <li>{@link #alt}</li>
     * <li>{@link #href}</li>
     * <li>user references in {@link #users}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      related content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Command_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/alt".equals(_url))  {
            this.alt = _content;
            parsed = true;
        } else if ("/href".equals(_url))  {
            this.href = _content;
            parsed = true;
        } else if ("/label".equals(_url))  {
            this.label = _content;
            parsed = true;
        } else if ("/userRefList/userRef".equals(_url))  {
            this.users.add(_content);
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
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

        _out.append("mxUpdate command \"${NAME}\"  {\n")
            .append("    description \"").append(StringUtil_mxJPO.convertUpdate(this.getDescription())).append("\"\n");
        if (this.isHidden())  {
            _out.append("    hidden\n");
        }
        _out.append("    label \"").append(StringUtil_mxJPO.convertUpdate(this.label)).append("\"\n")
            .append("    href \"").append(StringUtil_mxJPO.convertUpdate(this.href)).append("\"\n")
            .append("    alt \"").append(StringUtil_mxJPO.convertUpdate(this.alt)).append("\"\n");
        // users
        for (final String user : this.users)  {
            _out.append("    user \"").append(StringUtil_mxJPO.convertUpdate(user)).append("\"\n");
        }
        this.getProperties().writeSettings(_paramCache, _out, "    ");
        this.getProperties().writeProperties(_paramCache, _out, "    ");

        _out.append("}");
    }

    /**
     * Only implemented as stub because
     * {@link #write(ParameterCache_mxJPO, Appendable)} is new implemented.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the command could not be
     *                     written
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
    }

    /**
     * The method is called from the TCL update code to define the this
     * attribute. If the correct use case is defined method
     * {@link #updateDimension(ParameterCache_mxJPO, String)} is called.
     *
     * @param _paramCache   parameter cache
     * @param _args         first index defines the use case (must be
     *                      &quot;updateAttribute&quot; that the attribute
     *                      is updated); second index the name of the attribute
     *                      to update
     * @throws Exception if the update of the dimension failed or for all other
     *                   use cases from super JPO call
     */
    @Override()
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
        throws Exception
    {
        // check if dimension is defined
        if ((_args.length == 4) && "mxUpdate".equals(_args[0]) && "command".equals(_args[1])) {
// TODO: Exception Handling
            // check that command names are equal
            if (!this.getName().equals(_args[2]))  {
                throw new Exception("wrong command '" + _args[1] + "' is set to update (currently command '" + this.getName() + "' is updated!)");
            }

            final String code = _args[3].replaceAll("@0@0@", "'").replaceAll("@1@1@", "\\\"");

            final CommandDefParser_mxJPO parser = new CommandDefParser_mxJPO(new StringReader(code));
            final Command_mxJPO command = parser.command(_paramCache, this.getTypeDef(), this.getName());

            final MqlBuilder_mxJPO mql = MqlBuilder_mxJPO.init("escape mod command $1", this.getName());

            this.calcDelta(_paramCache, mql, command);

            mql.exec(_paramCache);

        } else  {
            super.jpoCallExecute(_paramCache, _args);
        }
    }

    /**
     * Calculates the delta between this current command definition and the
     * {@code _target} command definition and appends the MQL append commands
     * to {@code _cmd}.
     *
     * @param _paramCache   parameter cache
     * @param _cmd          string builder to append the MQL commands
     * @param _target       target format definition
     * @throws UpdateException_mxJPO if update is not allowed (because data can
     *                      be lost)
     */
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MqlBuilder_mxJPO _mql,
                             final Command_mxJPO _target)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description", _target.getDescription(),   this.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      _target.isHidden(),         this.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "alt",         _target.alt,                this.alt);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "href",        _target.href,               this.href);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "label",       _target.label,              this.label);
        DeltaUtil_mxJPO.calcListDelta(_mql, "user",         _target.users,              this.users);

        _target.getProperties().calcDelta("", this.getProperties(), _mql);
    }
}
