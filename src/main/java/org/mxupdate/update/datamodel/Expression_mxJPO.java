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
import org.mxupdate.update.datamodel.expression.ExpressionDefParser_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.xml.sax.SAXException;

/**
 * Handles the export and the update of the expression configuration item.
 *
 * @author The MxUpdate Team
 */
public class Expression_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for expressions.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        // to be ignored and read from method prepare because
        // the expression export does not work correctly for XML tags
        Expression_mxJPO.IGNORED_URLS.add("/expression");
    }

    /** Hold the expression itself. */
    private String value = null;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the expression object
     */
    public Expression_mxJPO(final TypeDef_mxJPO _typeDef,
                            final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * {@inheritDoc}
     * Also the value of the expression is extracted from MX
     */
    @Override()
    protected void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, SAXException, IOException
    {
        super.parse(_paramCache);

        this.value = MqlBuilder_mxJPO.mql()
                .cmd("escape print expression ").arg(this.getName()).cmd(" select ").arg("value").cmd(" dump")
                .exec(_paramCache);
    }

    /**
     * Parses all expression specific expression URLs.
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
        if (Expression_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Writes the TCL update file for this expression.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not be written to the
     *                     writer instance
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        this.writeHeader(_paramCache, _out);

        _out.append("mxUpdate expression \"${NAME}\"  {\n")
            .append("    description \"").append(StringUtil_mxJPO.convertUpdate(this.getDescription())).append("\"\n");
        if (this.isHidden())  {
            _out.append("    ").append(this.isHidden() ? "" : "!").append("hidden\n");
        }
        _out.append("    value \"").append(StringUtil_mxJPO.convertUpdate(this.value)).append("\"\n");
        this.getProperties().writeProperties(_paramCache, _out, "    ");

        _out.append("}");
    }

    /**
     * The method is called from the TCL update code to define the this
     * expression.
     *
     * @param _paramCache   parameter cache
     * @param _args         first index defines the use case (must be
     *                      &quot;updateAttribute&quot; that the expression
     *                      is updated); second index the name of the expression
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
        if ((_args.length == 4) && "mxUpdate".equals(_args[0]) && "expression".equals(_args[1])) {
// TODO: Exception Handling
            // check that expression names are equal
            if (!this.getName().equals(_args[2]))  {
                throw new Exception("wrong expression '" + _args[2] + "' is set to update (currently expression '" + this.getName() + "' is updated!)");
            }

            final String code = _args[3].replaceAll("@0@0@", "'").replaceAll("@1@1@", "\\\"");

            final ExpressionDefParser_mxJPO parser = new ExpressionDefParser_mxJPO(new StringReader(code));
            final Expression_mxJPO expression = parser.parse(_paramCache, this.getTypeDef(), this.getName());

            final MultiLineMqlBuilder mql = MqlBuilder_mxJPO.multiLine("escape mod expression $1", this.getName());

            this.calcDelta(_paramCache, mql, expression);

            mql.exec(_paramCache);

        } else  {
            super.jpoCallExecute(_paramCache, _args);
        }
    }

    /**
     * Calculates the delta between this current expression definition and the
     * {@code _target} expression definition and appends the MQL append commands
     * to {@code _mql}.
     *
     * @param _paramCache   parameter cache
     * @param _cmd          string builder to append the MQL commands
     * @param _target       target expression definition
     * @throws UpdateException_mxJPO if update is not allowed (because data can
     *                      be lost)
     */
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Expression_mxJPO _target)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description", _target.getDescription(),   this.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      _target.isHidden(),         this.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "value",       _target.value,              this.value);

        _target.getProperties().calcDelta(_mql, "", this.getProperties());
    }
}
