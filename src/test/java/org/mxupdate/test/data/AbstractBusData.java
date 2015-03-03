/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.test.data;

import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.datamodel.TypeData;

/**
 * Defines common information from business objects used to create, update and
 * check them.
 *
 * @param <DATA>    class which is derived from this class
 * @author The MxUpdate Team
 */
public abstract class AbstractBusData<DATA extends AbstractBusData<?>>
    extends AbstractData<DATA>
{
    /**
     * Used to separate type, name and revision of business objects within
     * name of files.
     */
    public static final String SEPARATOR = "________";

    /**
     * Related business type of this business object (if the type has children).
     *
     * @see #AbstractBusData(AbstractTest, AbstractTest.CI, String, String)
     */
    private final TypeData type;

    /**
     * Name of the business object.
     */
    private final String busName;

    /**
     * Revision of the business object.
     */
    private final String busRevision;

    /**
     * Description of the business object.
     *
     * @see #setDescription(String)
     * @see #create()
     * @see #ciFile()
     * @see #checkExport(ExportParser)
     */
    private String description;

    /**
     * Constructor to initialize this data piece.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _name                 name of the business object
     * @param _revision             revision of the business object
     */
    protected AbstractBusData(final AbstractTest _test,
                              final AbstractTest.CI _ci,
                              final String _name,
                              final String _revision)
    {
        this(_test, _ci, null, _name, _revision);
    }

    /**
     * Constructor to initialize this data piece.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _type                 derived type
     * @param _name                 name of the business object
     * @param _revision             revision of the business object
     */
    protected AbstractBusData(final AbstractTest _test,
                              final AbstractTest.CI _ci,
                              final TypeData _type,
                              final String _name,
                              final String _revision)
    {
        super(_test,
              _ci,
              ((_type != null)
                      ? (_type.getName() + AbstractBusData.SEPARATOR + AbstractBusData.SEPARATOR)
                      : (((_ci != null) && _ci.hasBusTypeDerived())
                              ? (_ci.getBusType() + AbstractBusData.SEPARATOR + AbstractBusData.SEPARATOR)
                              : ""))
                            + AbstractTest.PREFIX + _name + AbstractBusData.SEPARATOR
                            + _revision);
        this.type = _type;
        this.busName = AbstractTest.PREFIX + _name;
        this.busRevision = _revision;
    }

    /**
     * Returns the name of the configuration item file. Because it could be
     * that business objects which have derived types, but where no type is
     * defined, the CI file name is without the type.
     *
     * @return name of the CI file
     */
    @Override()
    public String getCIFileName()
    {
        String ret = super.getCIFileName();
        if ((this.type == null) && this.getCI().hasBusTypeDerived())  {
            ret = ret.replace(this.getCI().getBusType() + AbstractBusData.SEPARATOR + AbstractBusData.SEPARATOR, "");
        }
        return ret;
    }

    /**
     * Returns expected name of the CI file from the export. Because
     * {@link #getCIFileName()} is overwritten, the original method
     * {@link AbstractData#getCIFileName()} is called.
     *
     * @return expected name of the CI file
     */
    @Override()
    public String getCIFileNameFromExport()
    {
        return super.getCIFileName();
    }

    /**
     * Returns the type in the case that derived types are used for this
     * business object.
     *
     * @return related type (or <code>null</code> if not a specific derived type
     *         is defined)
     * @see #type
     */
    public TypeData getType()
    {
        return this.type;
    }

    /**
     * Returns the business object {@link #busName name}.
     *
     * @return business object name
     * @see #busName
     */
    public String getBusName()
    {
        return this.busName;
    }

    /**
     * Returns the business object {@link #busRevision revision}.
     *
     * @return business object revision
     * @see #busRevision
     */
    public String getBusRevision()
    {
        return this.busRevision;
    }

    /**
     * Defines the {@link #description} of this business object instance.
     *
     * @param _description  description of the business object
     * @return this business object instance
     * @see #description
     */
    @SuppressWarnings("unchecked")
    public DATA setDescription(final String _description)
    {
        this.description =  _description;
        return (DATA) this;
    }

    /**
     * Creates the related MX business object for this data piece.
     *
     * @return this instance
     * @throws MatrixException if create failed
     */
    @Override()
    @SuppressWarnings("unchecked")
    public DATA create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);
            final StringBuilder cmd = new StringBuilder()
                    .append("escape add bus \"")
                        .append(AbstractTest.convertMql((this.type != null) ? this.type.getName() : this.getCI().getBusType()))
                    .append("\" \"").append(AbstractTest.convertMql(this.busName))
                    .append("\" \"").append(AbstractTest.convertMql((this.busRevision != null) ? this.busRevision : ""))
                    .append("\" description \"").append(AbstractTest.convertMql((this.description != null) ? this.description : ""))
                    .append("\" policy \"").append(AbstractTest.convertMql(this.getCI().getBusPolicy()))
                    .append("\" vault \"").append(AbstractTest.convertMql(this.getCI().getBusVault()))
                    .append('\"');
            for (final Map.Entry<String,Object> value : this.getValues().entrySet())  {
                cmd.append(" \"").append(AbstractTest.convertMql(value.getKey()))
                   .append("\" \"").append(AbstractTest.convertMql(value.getValue().toString()))
                   .append('\"');
            }
            this.getTest().mql(cmd);
        }
        return (DATA) this;
    }

    /**
     * Returns the TCL update file of this business object data instance.
     *
     * @return TCL update file content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape mod bus \"${OBJECTID}\" description \"")
                .append(AbstractTest.convertTcl((this.description != null) ? this.description : ""))
                .append('\"');
        for (final Map.Entry<String,Object> value : this.getValues().entrySet())  {
            cmd.append(" \\\n    \"").append(AbstractTest.convertTcl(value.getKey()))
               .append("\" \"").append(AbstractTest.convertTcl(value.getValue().toString()))
               .append('\"');
        }
        return cmd.toString();
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);
        // check for defined values
        this.checkSingleValue(_exportParser,
                              "description",
                              "description",
                              "\"" + AbstractTest.convertTcl((this.description != null) ? this.description : "") + "\"");
        // check for defined values
        for (final Map.Entry<String,Object> entry : this.getValues().entrySet())  {
            this.checkSingleValue(_exportParser,
                                  entry.getKey(),
                                  "\"" + AbstractTest.convertTcl(entry.getKey()) + "\"",
                                  "\"" + AbstractTest.convertTcl(entry.getValue().toString()) + "\"");
        }
    }
}
