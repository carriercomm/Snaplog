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
package com.lyndir.lhunath.snaplog.data.object.media.aws;

import com.lyndir.lhunath.snaplog.data.object.media.Album;
import com.lyndir.lhunath.snaplog.data.object.user.UserProfile;


/**
 * <h2>{@link S3Album}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * [description / usage].
 * </p>
 *
 * <p>
 * <i>Jan 10, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class S3Album extends Album {

    /**
     * @param ownerProfile The profile of the user that owns this album.
     * @param name         A unique, user-visible name of this album amongst the user's albums.
     */
    public S3Album(final UserProfile ownerProfile, final String name) {

        super( ownerProfile, name );
    }
}