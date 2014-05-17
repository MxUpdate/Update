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

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.helper.TriggerList_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Abstract class for all data model administration objects with triggers.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractDMWithTriggers_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /** Set of all ignored URLs from the XML definition for policies. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        AbstractDMWithTriggers_mxJPO.IGNORED_URLS.add("/triggerList");
    }

    /** Map with all triggers. The key is the name of the trigger. */
    private final TriggerList_mxJPO triggers = new TriggerList_mxJPO();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public AbstractDMWithTriggers_mxJPO(final TypeDef_mxJPO _typeDef,
                                        final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Returns all defined {@link #triggers} sorted.
     *
     * @return all defined triggers
     */
    protected TriggerList_mxJPO getTriggers()
    {
        return this.triggers;
    }

    /**
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (AbstractDMWithTriggers_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if (_url.startsWith("/triggerList"))  {
            parsed = this.triggers.parse(_paramCache, _url.substring(12), _content);
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * After the type XML file is parsed, the triggers must be sorted.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the preparation from derived class failed
     * @see #triggersStack
     */
    @Override()
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        // sort all triggers
        this.triggers.prepare();

        super.prepare(_paramCache);
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to remove all current assigned triggers.
     * Then the update method of the super class is called.
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
        final StringBuilder preMQLCode = new StringBuilder();
        // remove all triggers
        if (!this.triggers.isEmpty())  {
            preMQLCode.append("escape mod ").append(this.getTypeDef().getMxAdminName())
                      .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                      .append(this.getTypeDef().getMxAdminSuffix());

            this.triggers.appendResetMQLStatement(preMQLCode);

            preMQLCode.append(";\n");
        }

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}
