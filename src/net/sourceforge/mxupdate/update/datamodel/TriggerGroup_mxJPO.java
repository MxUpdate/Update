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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.db.RelationshipWithSelect;
import matrix.util.MatrixException;
import matrix.util.StringList;

import net.sourceforge.mxupdate.update.AbstractBusObject_mxJPO;
import net.sourceforge.mxupdate.update.util.BusObject_mxJPO;
import net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO;
import net.sourceforge.mxupdate.util.Mapping_mxJPO.BusTypeDef;
import net.sourceforge.mxupdate.util.Mapping_mxJPO.RelationDef;

/**
 * @author tmoxter
 * @version $Id$
 */
@InfoAnno_mxJPO(busType = BusTypeDef.TriggerGroup)
public class TriggerGroup_mxJPO
        extends AbstractBusObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -794340646547560659L;

    /**
     * Holds all to connected objects.
     *
     * @see #prepare(Context)
     * @see #write(Writer)
     */
    private final Set<BusObject_mxJPO> tos = new TreeSet<BusObject_mxJPO>();

    /**
     * Holds all from connected objects.
     *
     * @see #prepare(Context)
     * @see #write(Writer)
     */
    private final Set<BusObject_mxJPO> froms = new TreeSet<BusObject_mxJPO>();

    /**
     * Evaluated all from and to objects of this trigger group.
     *
     * @param _context  context for this request
     * @see #tos
     * @see #froms
     */
    @Override
    protected void prepare(final Context _context)
            throws MatrixException
    {
        super.prepare(_context);
        // read current state
        final BusinessObject bus = new BusinessObject(this.getBusType(),
                                                      this.getBusName(),
                                                      this.getBusRevision(),
                                                      this.getBusVault());
        final StringList busSelect = new StringList();
        busSelect.addElement("type");
        busSelect.addElement("name");
        busSelect.addElement("revision");
        // get to objects
        final ExpansionWithSelect expandTo = bus.expandSelect(_context,
                                                              RelationDef.TriggerGroup.getMxName(),
                                                              "*",
                                                              busSelect,
                                                              new StringList(),
                                                              false,
                                                              true,
                                                              (short) 1,
                                                              null,
                                                              null,
                                                              (short) 0,
                                                              true);
        for (final Object obj : expandTo.getRelationships())  {
            final BusinessObjectWithSelect map = ((RelationshipWithSelect) obj).getTarget();
            this.tos.add(new BusObject_mxJPO((String) map.getSelectDataList("type").get(0),
                                             (String) map.getSelectDataList("name").get(0),
                                             (String) map.getSelectDataList("revision").get(0)));
        }
        // get from objects
        final ExpansionWithSelect expandFrom = bus.expandSelect(_context,
                                                                RelationDef.TriggerGroup.getMxName(),
                                                                "*",
                                                                busSelect,
                                                                new StringList(),
                                                                true,
                                                                false,
                                                                (short) 1,
                                                                null,
                                                                null,
                                                                (short) 0,
                                                                true);
        for (final Object obj : expandFrom.getRelationships())  {
            final BusinessObjectWithSelect map = ((RelationshipWithSelect) obj).getTarget();
            this.froms.add(new BusObject_mxJPO((String) map.getSelectDataList("type").get(0),
                                               (String) map.getSelectDataList("name").get(0),
                                               (String) map.getSelectDataList("revision").get(0)));
        }
    }

    /**
     * Appends the information about all from and to connected objects.
     *
     * @param _out      writer instance
     * @see #tos
     * @see #froms
     */
    @Override
    protected final void write(final Writer _out)
            throws IOException
    {
        super.write(_out);
        // write all from objects
        for (final BusObject_mxJPO bus : this.froms)  {
            _out.append("\nmql connect bus \"${OBJECTID}\" \\")
                .append("\n    relationship \"").append(RelationDef.TriggerGroup.getMxName()).append("\" \\")
                .append("\n    from \"").append(bus.getType()).append("\" \"")
                        .append(bus.getName()).append("\" \"")
                        .append(bus.getRevision()).append("\"");
        }
        // write all to objects
        for (final BusObject_mxJPO bus: this.tos)  {
            _out.append("\nmql connect bus \"${OBJECTID}\" \\")
                .append("\n    relationship \"").append(RelationDef.TriggerGroup.getMxName()).append("\" \\")
                .append("\n    to \"").append(bus.getType()).append("\" \"")
                        .append(bus.getName()).append("\" \"")
                        .append(bus.getRevision()).append("\"");
        }
    }


    /**
     * Appends to the MQL statements which must called before the TCL is
     * executed the MQL statements to disconnected all {@link #tos} and
     * {@link #froms} objects.
     *
     * @param _context          context for this request
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _tclCode          TCL code from the file used to update
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     */
    @Override
    protected void update(final Context _context,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _tclCode,
                          final Map<String,String> _tclVariables)
            throws Exception
    {
        final StringBuilder preMQLCode = new StringBuilder();

        // disconnect all from objects
        for (final BusObject_mxJPO bus : this.froms)  {
            preMQLCode.append("disconnect bus ").append(this.getBusOid())
                      .append(" relationship \"").append(RelationDef.TriggerGroup.getMxName()).append("\"")
                      .append(" from \"").append(bus.getType()).append("\" \"")
                              .append(bus.getName()).append("\" \"")
                              .append(bus.getRevision()).append("\";\n");
        }
        // disconnect all to objects
        for (final BusObject_mxJPO bus: this.tos)  {
            preMQLCode.append("disconnect bus ").append(this.getBusOid())
                      .append(" relationship \"").append(RelationDef.TriggerGroup.getMxName()).append("\"")
                      .append(" to \"").append(bus.getType()).append("\" \"")
                              .append(bus.getName()).append("\" \"")
                              .append(bus.getRevision()).append("\";\n");
        }

        // append rest of pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_context, preMQLCode, _postMQLCode, _tclCode, _tclVariables);
    }
}
