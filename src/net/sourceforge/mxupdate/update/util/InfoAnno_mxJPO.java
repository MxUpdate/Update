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

import net.sourceforge.mxupdate.util.Mapping_mxJPO.AdminTypeDef;
import net.sourceforge.mxupdate.util.Mapping_mxJPO.AttributeDef;
import net.sourceforge.mxupdate.util.Mapping_mxJPO.BusTypeDef;

/**
 * Internal Matrix administration type name and (if required) suffix.
 *
 * @author Tim Moxter
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface InfoAnno_mxJPO
{
    /**
     * Name of the administration type.
     */
    public AdminTypeDef adminType() default AdminTypeDef.Undef;

    /**
     * Name of the business type.
     */
    public BusTypeDef busType() default BusTypeDef.UnDef;

    /**
     * Defines the attributes which are ignored from the reset algorithm if
     * the update is make for a business object.
     */
    public AttributeDef[] busIgnoreAttributes() default {};
}
