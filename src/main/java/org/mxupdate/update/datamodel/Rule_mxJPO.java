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

package org.mxupdate.update.datamodel;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.helper.AccessList_mxJPO;
import org.mxupdate.update.datamodel.rule.RuleDefParser_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.xml.sax.SAXException;

/**
 * The class is used to export and import / update rule configuration items.
 *
 * @author The MxUpdate Team
 */
public class Rule_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /** Set of all ignored URLs from the XML definition for rules. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Rule_mxJPO.IGNORED_URLS.add("/ownerAccess/access");
        Rule_mxJPO.IGNORED_URLS.add("/ownerRevoke/access");
        Rule_mxJPO.IGNORED_URLS.add("/publicAccess/access");
        Rule_mxJPO.IGNORED_URLS.add("/publicRevoke/access");
        Rule_mxJPO.IGNORED_URLS.add("/userAccessList");
        Rule_mxJPO.IGNORED_URLS.add("/userAccessList/userAccess/access");
    }

    /** Access list. */
    private final AccessList_mxJPO accessList = new AccessList_mxJPO();
    /** Enoforcereserveaccess flag */
    private boolean enforcereserveaccess = false;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the rule object
     */
    public Rule_mxJPO(final TypeDef_mxJPO _typeDef,
                      final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * {@inheritDoc}
     * The rule {@link #accessList access} statements are sorted if defined.
     */
    @Override()
    protected void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, SAXException, IOException
    {
        super.parse(_paramCache);

        if (_paramCache.getValueBoolean(ValueKeys.DMRuleAllowExportAccessSorting))  {
            this.accessList.sort();
        }
    }

    /**
     * Parses all rule specific URLs.
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
        if (Rule_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if (this.accessList.parse(_paramCache, _url, _content))  {
            parsed = true;
        } else if ("/enforceReserveAccess".equals(_url))  {
            this.enforcereserveaccess = true;
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Writes specific information about the cached rule to the given
     * writer instance.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the rule could not be
     *                     written
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        this.writeHeader(_paramCache, _out);

        _out.append("mxUpdate rule \"${NAME}\"  {\n")
            .append("    description \"").append(StringUtil_mxJPO.convertUpdate(this.getDescription())).append("\"\n")
            .append("    ").append(this.isHidden() ? "" : "!").append("hidden\n");
        // enforcereserveaccess flag (if supported)
        if (_paramCache.getValueBoolean(ValueKeys.DMRuleSupportsEnforceReserveAccess))  {
            _out.append("    ").append(this.enforcereserveaccess ? "" : "!").append("enforcereserveaccess\n");
        }

        this.accessList.write(_paramCache, "    ", _out);
        this.getProperties().writeProperties(_paramCache, _out, "  ");

        _out.append("}");
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
        throws IOException
    {
    }


    /**
     * The method is called from the TCL update code to define the this
     * rule.
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
        if ((_args.length == 4) && "mxUpdate".equals(_args[0]) && "rule".equals(_args[1])) {
// TODO: Exception Handling
            // check that rule names are equal
            if (!this.getName().equals(_args[2]))  {
                throw new Exception("wrong rule '" + _args[1] + "' is set to update (currently rule '" + this.getName() + "' is updated!)");
            }

            final String code = _args[3].replaceAll("@0@0@", "'").replaceAll("@1@1@", "\\\"");

            final RuleDefParser_mxJPO parser = new RuleDefParser_mxJPO(new StringReader(code));
            final Rule_mxJPO rule = parser.parse(_paramCache, this.getTypeDef(), this.getName());

            final MultiLineMqlBuilder mql = MqlBuilder_mxJPO.multiLine("escape mod rule $1", this.getName());

            this.calcDelta(_paramCache, mql, rule);

            mql.exec(_paramCache);

        } else  {
            super.jpoCallExecute(_paramCache, _args);
        }
    }

    /**
     * Calculates the delta between this current rule definition and the
     * {@code _target} rule definition and appends the MQL append rules
     * to {@code _mql}.
     *
     * @param _paramCache   parameter cache
     * @param _mql          builder to append the MQL rules
     * @param _target       target format definition
     */
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Rule_mxJPO _target)
    {
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description", _target.getDescription(),   this.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      _target.isHidden(),         this.isHidden());
        if (_paramCache.getValueBoolean(ValueKeys.DMRuleSupportsEnforceReserveAccess))  {
            DeltaUtil_mxJPO.calcFlagDelta(_mql, "enforcereserveaccess", _target.enforcereserveaccess, this.enforcereserveaccess);
        }

        _target.accessList.cleanup(_mql);
        _target.accessList.update(_mql);

        _target.getProperties().calcDelta(_mql, "", this.getProperties());
    }
}
