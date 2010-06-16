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
package com.lyndir.lhunath.snaplog.data.object.security;

import com.lyndir.lhunath.lib.wayward.i18n.Localized;
import com.lyndir.lhunath.snaplog.data.object.user.User;


/**
 * <h2>{@link SecureObject}<br> <sub>Any object whose access should be controlled through access policies.</sub></h2>
 *
 * <p> It is imperative that implementations that use SecureObject do not emit its #toString publicly!  #secureToString should be used
 * instead. </p>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 * @param <P> The type of the parent object.
 */
public interface SecureObject<P extends SecureObject<?>> extends Localized {

    /**
     * @return The {@link SecureObject} that we inherit metadata from.
     */
    P getParent();

    /**
     * @return The user that owns this object.
     */
    User getOwner();

    /**
     * @return The access control set governing the permissions users have over this object.
     */
    ACL getACL();
}