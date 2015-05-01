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
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to export, create, delete and update tables within MX.
 *
 * @author The MxUpdate Team
 */
public class Table_mxJPO
    extends AbstractUIWithFields_mxJPO<Table_mxJPO>
{
    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Table_mxJPO(final TypeDef_mxJPO _typeDef,
                       final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * <p>Parses all table specific URL values. If a derived table is defined
     * and the value from where the table is derived is not <code>null</code>
     * and error message is shown.</p>
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
        if ("/derivedtable".equals(_url))  {
            if (_content != null)  {
System.err.println("The table is derived from '" + _content + "'! This is currently not supported!");
            }
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new TableParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        final UpdateBuilder_mxJPO updateBuilder = new UpdateBuilder_mxJPO(_paramCache);

        this.writeHeader(_paramCache, updateBuilder.getStrg());

        updateBuilder
                .start("table")
                //              tag             | default | value                              | write?
                .string(        "description",              this.getDescription())
                .flagIfTrue(    "hidden",           false,  this.isHidden(),                    this.isHidden())
                .list(this.getFields())
                .properties(this.getProperties())
                .end();

        _out.append(updateBuilder.toString());
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Table_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcValueDelta(  _mql,              "description",              this.getDescription(),  _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(   _mql,              "hidden",            false, this.isHidden(),        _current.isHidden());

        final Iterator<AbstractField> currentFieldIter = _current.getFields().iterator();
        final Iterator<AbstractField> targetFieldIter = this.getFields().iterator();

        int idx = 1;
        boolean equal = _current.getFields().size() == this.getFields().size();
        // compare the field in sequence
        AbstractField targetField = null;
        while(currentFieldIter.hasNext() && targetFieldIter.hasNext()) {
            final AbstractField currentField = currentFieldIter.next();
            targetField = targetFieldIter.next();
            if (currentField.compareTo(targetField) == 0)  {
                idx++;
                targetField = null;
            } else {
                equal = false;
                break;
            }
        }

        // the fields are not the same from idx on or the current has more than the target
        // --> updates must be done
        if (!equal) {
            // remove all field after the ones that were equal
            int idy = _current.getFields().size();
            while (idy > idx - 1) {
                _mql.newLine()
                    .cmd("column delete ").arg(String.valueOf(idy));
                idy--;
            }

            if (!equal) {
                // append new fields
                if (targetField != null) {
                    targetField.calcDelta(_mql, idx++);
                }
                while(targetFieldIter.hasNext()) {
                    targetField = targetFieldIter.next();
                    targetField.calcDelta(_mql, idx++);
                }
            }
        }
        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }

    /**
     * Class used to define a column of a web table.
     */
    public static class Column
        extends AbstractField
    {
        /** Constructor setting the tag. */
        public Column()
        {
            super("column");
        }
    }
}
