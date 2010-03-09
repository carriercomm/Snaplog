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
package com.lyndir.lhunath.snaplog.data;

/**
 * <h2>{@link MediaData}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 6, 2010</i>
 * </p>
 * 
 * @param <P>
 *            The type of {@link Provider} that provides the resources cached by this media data.
 * @param <A>
 *            The type of {@link Album} that contains M.
 * @param <M>
 *            The type of {@link Media} we hold data for.
 * @author lhunath
 */
public abstract class MediaData<P extends Provider, A extends Album<P>, M extends Media<P>> {

    private M media;


    /**
     * Create a new {@link MediaData} instance.
     * 
     * @param media
     *            The media we hold data for.
     */
    public MediaData(M media) {

        this.media = media;
    }

    /**
     * @return The media of this {@link MediaData}.
     */
    public M getMedia() {

        return media;
    }
}