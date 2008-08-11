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

/**
 * Abstract class for all data model administration objects (not business
 * objects!).
 *
 * @author tmoxter
 */
public abstract class AbstractDMObject_mxJPO
        extends net.sourceforge.mxupdate.update.MatrixObject_mxJPO
{
    /**
     * Constructor with prefix (suffix is not needed, because all data model
     * administration objects do not need any suffix...).
     *
     * @param _prefix
     */
    protected AbstractDMObject_mxJPO(final String _prefix)
    {
        super("datamodel/" + _prefix, _prefix, null);
    }
}
