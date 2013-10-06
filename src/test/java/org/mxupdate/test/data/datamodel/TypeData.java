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

package org.mxupdate.test.data.datamodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.program.AbstractProgramData;

/**
 * Used to define a type, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class TypeData
    extends AbstractDataWithTrigger<TypeData>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,String> REQUIRED_EXPORT_VALUES = new HashMap<String,String>(1);
    static  {
        TypeData.REQUIRED_EXPORT_VALUES.put("description", "");
        TypeData.REQUIRED_EXPORT_VALUES.put("abstract", "false");
    }

    /**
     * All methods of this type.
     *
     * @see #addMethod(AbstractProgramData)
     * @see #create()
     */
    private final Set<AbstractProgramData<?>> methods = new HashSet<AbstractProgramData<?>>();

    /**
     * Initialize this type data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this type is
     *                  defined)
     * @param _name     name of the type
     */
    public TypeData(final AbstractTest _test,
                    final String _name)
    {
        super(_test, AbstractTest.CI.DM_TYPE, _name,
              TypeData.REQUIRED_EXPORT_VALUES,
              null);
    }

    /**
     * Assigns the <code>_method</code> to this type.
     *
     * @param _method   method to assign
     * @return this type data instance
     * @see #methods
     */
    public TypeData addMethod(final AbstractProgramData<?> _method)
    {
        this.methods.add(_method);
        return this;
    }

    /**
     * Returns all methods for this type data instance.
     *
     * @return all methods of this type data instance
     * @see #methods
     */
    public Set<AbstractProgramData<?>> getMethods()
    {
        return this.methods;
    }

    /**
     * Returns the TCL update file of this type data instance.
     *
     * @return TCL update file content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape mod type \"${NAME}\"");

        this.append4CIFileValues(cmd);

        // append attributes
        this.append4CIAttributes(cmd);

        return cmd.toString();
    }

    /**
     * Create the related type in MX for this type data instance and appends
     * the {@link #methods} and {@link #attributes}.
     *
     * @return this type data instance
     * @throws MatrixException if create failed
     * @see #methods
     * @see #attributes
     */
    @Override()
    public TypeData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add type \"").append(AbstractTest.convertMql(this.getName())).append('\"');

            // append methods
            for (final AbstractProgramData<?> method : this.methods)  {
                cmd.append(" method \"").append(AbstractTest.convertMql(method.getName())).append("\"");
            }

            this.append4Create(cmd);

            this.getTest().mql(cmd);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     * Creates all programs referenced by {@link #methods}.
     *
     * @see #methods
     */
    @Override()
    public TypeData createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create programs
        for (final AbstractProgramData<?> prog : this.methods)  {
            prog.create();
        }

        return this;
    }

    /**
     * Appends the adds for the {@link #methods} and {@link #attributes}.
     *
     * @param _needAdds     set with add strings used to append the adds
     * @see #methods
     * @see #attributes
     */
    @Override()
    protected void evalAdds4CheckExport(final Set<String> _needAdds)
    {
        super.evalAdds4CheckExport(_needAdds);

        // append methods
        for (final AbstractProgramData<?> method : this.methods)  {
            final StringBuilder cmd = new StringBuilder()
                    .append("method \"").append(AbstractTest.convertTcl(method.getName())).append("\"");
            _needAdds.add(cmd.toString());
        }
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

    }
}
