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

package net.sourceforge.mxupdate.update.userinterface;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
public abstract class AbstractUIObject_mxJPO
        extends net.sourceforge.mxupdate.update.MatrixObject_mxJPO
{

    protected AbstractUIObject_mxJPO(final String _prefix,
                                     final String _suffix)
    {
        super("userinterface/" + _prefix, _prefix, _suffix);
    }

    protected AbstractUIObject_mxJPO(final String _prefix)
    {
        super("userinterface/" + _prefix, _prefix, null);
    }
}
