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

package org.mxupdate.mapping;

import java.util.Collection;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
class ParameterValues_mxJPO
{

    /**
     * Defines the parameter description.
     *
     * @see Mode_mxJPO#getParameterDesc()
     * @see Mode_mxJPO#defineValue(String, String)
     */
    String paramDesc;

    /**
     * Defines the list of parameters.
     *
     * @see Mode_mxJPO#getParameterList()
     * @see Mode_mxJPO#defineValue(String, String)
     */
    Collection<String> paramList;

    /**
     * Returns the description of parameters which defines mode.
     *
     * @return description of parameter
     * @see ParameterValues_mxJPO#paramDesc
     */
    public String getParameterDesc()
    {
        return this.paramDesc;
    }

    /**
     * Returns the list of parameters which defines this mode.
     *
     * @return list of parameter strings
     * @see ParameterValues_mxJPO#paramList
     */
    public Collection<String> getParameterList()
    {
        return this.paramList;
    }
}