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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to export, create, delete and update forms within MX.
 *
 * @author The MxUpdate Team
 */
public class Form_mxJPO
    extends AbstractUIWithFields_mxJPO<Form_mxJPO>
{
    /**
     * TCL procedure used to order fields of form, because Matrix has a bug
     * that sometimes the fields of a form are not in the correct order.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String ORDER_PROC
            = "proc orderFields {_name _fields}  {\n"
                + "foreach offset [list 100000 1] {\n"
                    + "foreach field $_fields {"
                        + "mql mod form \"${_name}\" "
                            + "field modify name \"$field\" "
                            + "order ${offset}\n"
                        + "incr offset\n"
                    + "}\n"
                + "}"
            + "}";

    /**
     * Set of all ignored URLs from the XML definition for forms.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Form_mxJPO.IGNORED_URLS.add("/footer");
        Form_mxJPO.IGNORED_URLS.add("/header");
        Form_mxJPO.IGNORED_URLS.add("/height");
        Form_mxJPO.IGNORED_URLS.add("/leftMargin");
        Form_mxJPO.IGNORED_URLS.add("/rightMargin");
        Form_mxJPO.IGNORED_URLS.add("/webform");
        Form_mxJPO.IGNORED_URLS.add("/width");
    }

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Form_mxJPO(final TypeDef_mxJPO _typeDef,
                      final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
//        new FormdDefParser_mxJPO(new StringReader(_code)).parse(this);
//        this.prepare();
    }

    /**
     * <p>Parses all form specific URL values. No specific values for the web
     * form exists</p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      related content of the URL to parse
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
        if (Form_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Writes all field of the web form to the TCL update file. This includes
     * <ul>
     * <li>hidden flag (only if hidden)</li>
     * <li>all {@link #getFields() fields}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the fields could not be
     *                     written
     * @see AbstractUIWithFields_mxJPO.Field#write(Appendable)
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        if (this.isHidden())  {
            _out.append(" \\\n    hidden");
        }
        for (final Field field : this.getFields())  {
            _out.append(" \\\n    field");
            field.write(_out);
        }
    }

    /**
     * At the end of the TCL update file a call to a procedure must be included
     * to order all web form fields correctly.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     */
    @Override()
    protected void writeEnd(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
        throws IOException
    {
        _out.append("\n\norderFields \"${NAME}\" [list \\\n");
        for (final Field field : this.getFields())  {
            _out.append("    \"").append(StringUtil_mxJPO.convertTcl(field.getName())).append("\" \\\n");
        }
        _out.append("]");
    }

    /**
     * Creates given web form object with given name. Because the MQL add
     * command for a web form must include the string &quot;web&quot; a
     * specific create method must be written.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if the web form could not be created within MX
     */
    @Override()
    public void create(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("escape add ").append(this.getTypeDef().getMxAdminName())
                        .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" web;");
        MqlUtil_mxJPO.execMql(_paramCache, cmd);
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this form. Following steps are
     * done:
     * <ul>
     * <li>remove all fields of the web form</li>
     * <li>set to not hidden</li>
     * </ul>
     * The update of web forms works sometimes not correctly for the correct
     * order of fields. Because of that, the TCL update code is includes a
     * {@link #ORDER_PROC procedure} to order the form fields.
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
     * @see #ORDER_PROC
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
        // reset HRef, description, alt and label
        final StringBuilder preMQLCode = new StringBuilder()
                .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                .append(" !hidden description \"\" ");

        // remove all fields
        for (final Field field : this.getFields())  {
            preMQLCode.append(" field delete name \"").append(StringUtil_mxJPO.convertMql(field.getName())).append('\"');
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        // append procedure to order fields of the form
        final StringBuilder tclCode = new StringBuilder()
                .append(Form_mxJPO.ORDER_PROC)
                .append('\n')
                .append(_preTCLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, tclCode, _tclVariables, _sourceFile);
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Form_mxJPO _current)
        throws UpdateException_mxJPO
    {
    }
}
