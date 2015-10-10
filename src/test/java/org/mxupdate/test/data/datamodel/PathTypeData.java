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

package org.mxupdate.test.data.datamodel;

import java.util.Arrays;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.datamodel.helper.LocaleAttributeList;
import org.mxupdate.test.data.util.DataList;
import org.mxupdate.test.data.util.SingleValueList;

import matrix.util.MatrixException;

/**
 * Used to define a path type, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class PathTypeData
    extends AbstractAdminData<PathTypeData>
{
    /** Local attributes. */
    private final LocaleAttributeList attributes = new LocaleAttributeList();

    /** From / to single values of this data piece. */
    private final SingleValueList fromSingles = new SingleValueList(), toSingles = new SingleValueList();
    /** From / to defined data elements. */
    private final DataList<AbstractAdminData<?>> fromDatas = new DataList<>(), toDatas = new DataList<>();

    /**
     * Initialize this type data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this type is
     *                  defined)
     * @param _name     name of the type
     */
    public PathTypeData(final AbstractTest _test,
                        final String _name)
    {
        super(_test, AbstractTest.CI.DM_PATHTYPE, _name);
    }

    /**
     * Appends given {@code _attributes}.
     *
     * @param _attributes    attributes list to append
     * @return this type data instance
     */
    public PathTypeData addLocalAttribute(final AbstractAttributeData<?>... _attributes)
    {
        this.attributes.addAll(Arrays.asList(_attributes));
        return this;
    }

    /**
     * Defines a new value entry which is put into {@link #fromSingles}.
     *
     * @param _key      key of the value (e.g. &quot;description&quot;)
     * @param _value    value of the value
     * @return this path type data instance
     */
    public PathTypeData def4FromSingle(final String _key,
                                       final String _value)
    {
        this.fromSingles.def(_key, _value);
        return this;
    }

    /**
     * Defines a new value entry which is put into {@link #fromDatas}.
     *
     * @param _key      key of the data (e.g. &quot;description&quot;)
     * @param _data     data instance
     * @return this path type data instance
     */
    public PathTypeData def4FromData(final String _key,
                                    final AbstractAdminData<?> _data)
    {
        this.fromDatas.add(_key, _data);
        return this;
    }

    /**
     * Defines a new value entry which is put into {@link #toSingles}.
     *
     * @param _key      key of the value (e.g. &quot;description&quot;)
     * @param _value    value of the value
     * @return this path type data instance
     */
    public PathTypeData def4ToSingle(final String _key,
                                     final String _value)
    {
        this.toSingles.def(_key, _value);
        return this;
    }

    /**
     * Defines a new value entry which is put into {@link #toDatas}.
     *
     * @param _key      key of the data (e.g. &quot;description&quot;)
     * @param _data     data instance
     * @return this path type data instance
     */
    public PathTypeData def4ToData(final String _key,
                                   final AbstractAdminData<?> _data)
    {
        this.toDatas.add(_key, _data);
        return this;
    }

    @Override
    public PathTypeData createDependings()
        throws MatrixException
    {
        super.createDependings();

        this.fromDatas.createDependings();
        this.toDatas  .createDependings();

        return this;
    }

    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate " + this.getCI().getMxType() + " \"${NAME}\" {\n");

        this.getFlags()     .append4Update("    ", strg);
        this.getValues()    .append4Update("    ", strg);
        this.getSingles()   .append4Update("    ", strg);
        this.getKeyValues() .append4Update("    ", strg);
        this.getDatas()     .append4Update("    ", strg);

        // from
        strg.append("    from {\n");
        this.fromSingles    .append4Update("        ", strg);
        this.fromDatas      .append4Update("        ", strg);
        strg.append("    }\n");

        // to
        strg.append("    to {\n");
        this.toSingles      .append4Update("        ", strg);
        this.toDatas        .append4Update("        ", strg);
        strg.append("    }\n");

        this.attributes     .append4Update("    ", strg);
        this.getProperties().append4Update("    ", strg);

        strg.append("}");

        return strg.toString();
    }

    @Override()
    public void checkExport(final ExportParser _exportParser)
    {
        super.checkExport(_exportParser);

        this.attributes.checkExport(_exportParser);

        // from
        this.fromSingles.check4Export(_exportParser, "from");
        this.fromDatas.check4Export(_exportParser, "from");

        // to
        this.toSingles.check4Export(_exportParser, "to");
        this.toDatas.check4Export(_exportParser, "to");
    }
}
