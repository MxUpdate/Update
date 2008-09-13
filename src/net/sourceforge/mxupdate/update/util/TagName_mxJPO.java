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

package net.sourceforge.mxupdate.update.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The tag name is internally used to store the shown name of the
 * administration objects used for user logging purpose. This help e.g. to show
 * &quot;web form&quot; for forms intead of using the internal used name
 * &quot;form&quot;.
 *
 * @author Tim Moxter
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TagName_mxJPO
{
    /**
     * Value of the shown name.
     */
    public String value();
}
