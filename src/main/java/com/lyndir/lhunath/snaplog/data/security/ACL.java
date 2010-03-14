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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import com.lyndir.lhunath.snaplog.data.user.User;


/**
 * <h2>{@link ACL}<br>
 * <sub>A list of access control grants.</sub></h2>
 * 
 * <p>
 * <i>Mar 14, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class ACL {

    private Map<User, Permission> userPermissions;


    /**
     * An {@link ACL} that grants users the {@link Permission#INHERIT} permission by default.
     */
    public ACL() {

        this( Permission.INHERIT );
    }

    /**
     * @param defaultPermission
     *            The permission granted to users not explicitly specified.
     */
    public ACL(Permission defaultPermission) {

        userPermissions = new HashMap<User, Permission>();
        setDefaultPermission( defaultPermission );
    }

    /**
     * Change the permission of a user in this access control. Any current permission of the user is revoked and
     * replaced by the given permission.
     * 
     * @param permission
     *            The permission that will be granted to the given user.
     */
    public void setDefaultPermission(Permission permission) {

        checkNotNull( permission, "Given permission must not be null." );

        userPermissions.put( null, permission );
    }

    /**
     * Change the permission of a user in this access control. Any current permission of the user is revoked and
     * replaced by the given permission.
     * 
     * @param user
     *            The user that will be granted the given permission.
     * @param permission
     *            The permission that will be granted to the given user.
     */
    public void setUserPermission(User user, Permission permission) {

        checkNotNull( user, "Given user must not be null." );
        checkNotNull( permission, "Given permission must not be null." );

        userPermissions.put( user, permission );
    }

    /**
     * Revoke the permission of a user in this access control.
     * 
     * @param user
     *            The user that will be granted the given permission.
     */
    public void revokeUserPermission(User user) {

        checkNotNull( user, "Given user must not be null." );

        userPermissions.remove( user );
    }

    /**
     * The user's permission is either the one set for him through {@link #setUserPermission(User, Permission)} or the
     * default permission of this ACL.
     * 
     * @param user
     *            The user whose permission to look up.
     * @return The permission granted to the given user by this access control.
     */
    public Permission getUserPermission(User user) {

        checkNotNull( user, "Given user must not be null." );

        if (userPermissions.containsKey( user ))
            return checkNotNull( userPermissions.get( user ), "Permission for %s is unset.", user );

        return userPermissions.get( null );
    }
}
