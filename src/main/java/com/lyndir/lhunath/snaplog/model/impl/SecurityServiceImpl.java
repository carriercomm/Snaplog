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

import static com.google.common.base.Preconditions.checkNotNull;

import com.db4o.ObjectContainer;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.collection.Pair;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.logging.exception.InternalInconsistencyException;
import com.lyndir.lhunath.snaplog.data.media.AlbumData;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.media.MediaTimeFrame;
import com.lyndir.lhunath.snaplog.data.security.Permission;
import com.lyndir.lhunath.snaplog.data.security.SecureObject;
import com.lyndir.lhunath.snaplog.data.security.SecurityToken;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.SecurityService;
import java.util.Iterator;


/**
 * <h2>{@link SecurityServiceImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 */
public class SecurityServiceImpl implements SecurityService {

    static final Logger logger = Logger.get( SecurityServiceImpl.class );

    final ObjectContainer db;

    /**
     * @param db See {@link ServicesModule}.
     */
    @Inject
    public SecurityServiceImpl(final ObjectContainer db) {

        this.db = db;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAccess(final Permission permission, final SecurityToken token, final SecureObject<?> o) {

        try {
            assertAccess( permission, token, o );
            return true;
        }

        catch (PermissionDeniedException ignored) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void assertAccess(final Permission permission, final SecurityToken token, final SecureObject<?> o)
            throws PermissionDeniedException {

        if (o == null || permission == Permission.NONE) {
            // No permission required.
            logger.dbg( "Permission Granted: No permission necessary for: %s@%s", //
                        permission, o );
            return;
        }

        if (token == null) {
            // Permission required but no token given.
            logger.dbg( "Permission Denied: Missing security token for: %s@%s", //
                        permission, o );
            throw new PermissionDeniedException( permission, o, "No security token" );
        }

        if (token.isInternalUseOnly()) {
            // Token is "Internal Use", grant everything.
            logger.dbg( "Permission Granted: INTERNAL_USE token for: %s@%s", //
                        permission, o );
            return;
        }

        Permission tokenPermission = o.getACL().getUserPermission( token.getActor() );
        if (tokenPermission == Permission.INHERIT) {
            if (o.getParent() == null) {
                logger.dbg( "Permission Denied: Can't inherit permissions, no parent set for: %s@%s", //
                            permission, o );
                throw new PermissionDeniedException( permission, o, "Had to inherit permission but no parent set" );
            }

            logger.dbg( "Inheriting permission for: %s@%s", //
                        permission, o );
            assertAccess( permission, token, o.getParent() );
            return;
        }

        if (!isPermissionProvided( tokenPermission, permission )) {
            logger.dbg( "Permission Denied: Token authorizes %s (ACL default? %s), insufficient for: %s@%s", //
                        tokenPermission, o.getACL().isUserPermissionDefault( token.getActor() ), permission, o );
            throw new PermissionDeniedException( permission, o, "Security Token %s grants permissions %s ", token, tokenPermission );
        }

        logger.dbg( "Permission Granted: Token authorization %s matches for: %s@%s", //
                    tokenPermission, permission, o );
    }

    private static boolean isPermissionProvided(final Permission givenPermission, final Permission requestedPermission) {

        if (givenPermission == requestedPermission)
            return true;
        if (givenPermission == null || requestedPermission == null)
            return false;

        for (final Permission inheritedGivenPermission : givenPermission.getProvided())
            if (isPermissionProvided( inheritedGivenPermission, requestedPermission ))
                return true;

        return false;
    }

    @Override
    public Permission getEffectivePermissions(final SecurityToken token, final User user, final SecureObject<?> o)
            throws PermissionDeniedException {

        checkNotNull( o, "Given secure object must not be null." );
        assertAccess( Permission.ADMINISTER, token, o );

        Permission permission = o.getACL().getUserPermission( user );
        if (permission == Permission.INHERIT) {
            SecureObject<?> parent = checkNotNull( o.getParent(), "Secure object's default permission is INHERIT but has no parent." );

            return getEffectivePermissions( token, user, parent );
        }

        return permission;
    }

    @Override
    public Iterator<Pair<User, Permission>> iterateUserPermissions(final SecurityToken token, final SecureObject<?> o)
            throws PermissionDeniedException {

        assertAccess( Permission.ADMINISTER, token, o );

        return Iterators.unmodifiableIterator( new AbstractIterator<Pair<User, Permission>>() {
            public Iterator<User> permittedUsers;

            {
                permittedUsers = o.getACL().getPermittedUsers().iterator();
            }

            @Override
            protected Pair<User, Permission> computeNext() {

                try {
                    if (permittedUsers.hasNext()) {
                        User user = permittedUsers.next();
                        return new Pair<User, Permission>( user, getEffectivePermissions( token, user, o ) );
                    }
                }
                catch (PermissionDeniedException e) {
                    throw new InternalInconsistencyException( "While evaluating user permissions", e );
                }

                return endOfData();
            }
        } );
    }

    @Override
    public int countPermittedUsers(final SecurityToken token, final SecureObject<?> o)
            throws PermissionDeniedException {

        assertAccess( Permission.ADMINISTER, token, o );
        return o.getACL().getPermittedUsers().size();
    }

    @Override
    public void setUserPermission(final SecurityToken token, final SecureObject<?> o, final User user, final Permission permission)
            throws PermissionDeniedException {

        assertAccess( Permission.ADMINISTER, token, o );
        o.getACL().setUserPermission( user, permission );
        db.store( o );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Media> iterateFilesFor(final SecurityToken token, final AlbumData albumData) {

        return Iterators.filter( albumData.getInternalFiles( this ).iterator(), new Predicate<Media>() {

            @Override
            public boolean apply(final Media input) {

                return hasAccess( Permission.VIEW, token, input );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<MediaTimeFrame> iterateTimeFramesFor(final SecurityToken token, final AlbumData albumData) {

        return Iterators.filter( albumData.getInternalTimeFrames( this ).iterator(), new Predicate<MediaTimeFrame>() {

            @Override
            public boolean apply(final MediaTimeFrame input) {

                // TODO: Implement security on MediaTimeFrames.
                return true;// hasAccess( Permission.VIEW, token, input );
            }
        } );
    }
}
