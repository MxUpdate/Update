/*
 * Copyright 2008-2009 The MxUpdate Team
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

package org.mxupdate.update.program;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * Common definition for the code of a program or page object.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public abstract class AbstractCode_mxJPO
        extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -6353366924945315894L;

    /**
     * Inserted text in the {@link #code} if the program includes a
     * <code>CDATA</code> (two closing brackets '&#93;&#93;').
     *
     * @see #parse(String, String)
     */
    private static final String INSERT_TEXT = "Inserted" + "_by_" + "ENO" + "VIA";

    /**
     * Source code of this program.
     *
     * @see #parse(String, String)
     */
    private String code;

    /**
     * Constructor used to initialize the type definition enumeration and the
     * name.
     *
     * @param _typeDef  type definition of the program
     * @param _mxName   MX name of the program object
     */
    protected AbstractCode_mxJPO(final TypeDef_mxJPO _typeDef,
                                 final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Returns the {@link #code} of this program.
     *
     * @return code of this program
     * @see #code
     */
    protected String getCode()
    {
        return this.code;
    }

    /**
     * <p>Parses all common code specific URL values. This includes:
     * <ul>
     * <li>{@link #code}</li>
     * </ul></p>
     *
     * @param _url      URL to parse
     * @param _content  content depending on the URL
     */
    @Override()
    protected void parse(final String _url,
                         final String _content)
    {
        // JPO + MQL programs
        if ("/code".equals(_url))  {
            this.code = (_content != null)
                        ? _content.replaceAll(AbstractCode_mxJPO.INSERT_TEXT, "")
                        : "";
        // page programs
        } else if ("/pageContent".equals(_url))  {
            this.code = (_content != null)
                        ? _content.replaceAll(AbstractCode_mxJPO.INSERT_TEXT, "")
                        : "";
        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Must be implemented so that the class could be derived. The method is
     * only a stub implementation.
     *
     * @param _paramCache   parameter cache; ignored
     * @param _out          ignored
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
    {
    }
}
