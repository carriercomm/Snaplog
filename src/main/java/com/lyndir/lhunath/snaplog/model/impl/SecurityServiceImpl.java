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
package com.lyndir.lhunath.snaplog.model.impl;

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.security.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.data.security.SecureObject;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;
import com.lyndir.lhunath.snaplog.model.SecurityService;


/**
 * <h2>{@link SecurityServiceImpl}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 14, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class SecurityServiceImpl implements SecurityService {

    static final Logger logger = Logger.get( SecurityServiceImpl.class );


    /**
     * {@inheritDoc}
     */
    @Override
    public void assertAccess(Permission permission, SecurityToken token, SecureObject<?> o)
            throws PermissionDeniedException {

        if (o == null || permission == Permission.NONE)
            return;

        if (token == null)
            throw logger.wrn( "No security token in request on object %s.", o )
                        .toError( PermissionDeniedException.class );

        Permission actorPermission = o.getACL().getUserPermission( token.getActor() );
        if (actorPermission == Permission.INHERIT) {
            if (o.getParent() != null)
                assertAccess( permission, token, o.getParent() );
            return;
        }

        if (permission != actorPermission)
            throw logger.wrn( "Security Token %s grants permissions %s but request required %s on object %s", token, o )
                        .toError( PermissionDeniedException.class );
    }
}
