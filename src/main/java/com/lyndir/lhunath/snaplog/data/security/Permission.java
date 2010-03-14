/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.snaplog.data.security;

import com.lyndir.lhunath.snaplog.data.user.User;


/**
 * <h2>{@link Permission}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 14, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public enum Permission {

    /**
     * This permission grants a {@link User} no access to the objects it applies to.
     */
    NONE( "permission.none" ),

    /**
     * This causes the {@link User}'s permissions to be resolved against the parent of the objects it applies to.
     */
    INHERIT( "permission.inherit" ),

    /**
     * This permission grants a {@link User} the ability to read all objects it applies to.
     */
    VIEW( "permission.view" ),

    /**
     * This permission grants a {@link User} the ability to modify all objects it applies to.
     */
    CONTRIBUTE( "permission.contribute" );

    private String localizationKey;


    /**
     * Create a new {@link Permission} instance.
     * 
     * @param localizationKey
     */
    private Permission(String localizationKey) {

        this.localizationKey = localizationKey;
    }

    /**
     * @return The key by which the localized name of this permission type resolves.
     */
    public String getLocalizationKey() {

        return localizationKey;
    }
}
