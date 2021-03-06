package com.lyndir.lhunath.snaplog.data.service.impl.db4o.nq;

import static com.google.common.base.Preconditions.checkState;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.QueryComparator;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.DateUtils;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.snaplog.data.object.media.*;
import com.lyndir.lhunath.snaplog.data.service.MediaDAO;
import java.util.List;


/**
 * <h2>{@link MediaDAOImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 16, 2010</i> </p>
 *
 * @author lhunath
 */
public class MediaDAOImpl implements MediaDAO {

    static final Logger logger = Logger.get( MediaDAOImpl.class );

    private final ObjectContainer db;

    @Inject
    public MediaDAOImpl(final ObjectContainer db) {

        this.db = db;
    }

    @Override
    public void update(final Media media) {

        db.store( media );
    }

    @Override
    public void update(final MediaData<?> mediaData) {

        db.store( mediaData );
    }

    @Override
    public void update(final Iterable<MediaData<?>> mediaDatas) {

        DateUtils.startTiming( "updateMediaDatas" );
        try {
            db.store( mediaDatas );
        }
        finally {
            DateUtils.popTimer().logFinish( logger );
        }
    }

    @Override
    public <M extends Media> M findMedia(final Source source, final String mediaName) {

        DateUtils.startTiming( "findMedia" );
        try {
            ObjectSet<M> results = db.query( new Predicate<M>() {

                @Override
                public boolean match(final M candidate) {

                    return ObjectUtils.isEqual( candidate.getSource(), source ) && ObjectUtils.isEqual( candidate.getName(), mediaName );
                }
            } );
            if (results.hasNext()) {
                M result = results.next();
                checkState( !results.hasNext(), "Multiple media data found for %s named %s", source, mediaName );

                return result;
            }

            return null;
        }
        finally {
            DateUtils.popTimer().logFinish( logger );
        }
    }

    @Override
    public <D extends MediaData<M>, M extends Media> D findMediaData(final M media) {

        DateUtils.startTiming( "findMediaData" );
        try {
            ObjectSet<D> results = db.query( new Predicate<D>() {

                @Override
                public boolean match(final D candidate) {

                    return ObjectUtils.isEqual( candidate.getMedia(), media );
                }
            } );
            if (results.hasNext()) {
                D result = results.next();
                checkState( !results.hasNext(), "Multiple media data found for %s", media );

                return result;
            }

            return null;
        }
        finally {
            DateUtils.popTimer().logFinish( logger );
        }
    }

    @Override
    public <M extends Media> List<M> listMedia(final Source source, final boolean ascending) {

        return db.query( new Predicate<M>() {

            @Override
            public boolean match(final M candidate) {

                return ObjectUtils.isEqual( candidate.getSource(), source );
            }
        }, new QueryComparator<M>() {

            @Override
            public int compare(final M first, final M second) {

                return first.compareTo( second ) * (ascending? 1: -1);
            }
        } );
    }

    @Override
    public <D extends MediaData<?>> List<D> listMediaData(final Source source, final boolean ascending) {

        return db.query( new Predicate<D>() {

            @Override
            public boolean match(final D candidate) {

                return ObjectUtils.isEqual( candidate.getMedia().getSource(), source );
            }
        }, new QueryComparator<D>() {

            @Override
            public int compare(final D first, final D second) {

                return first.getMedia().compareTo( second.getMedia() ) * (ascending? 1: -1);
            }
        } );
    }

    @Override
    public <M extends Media> void delete(final Iterable<M> medias) {

        for (final M media : medias)
            db.delete( media );
    }

    @Override
    public MediaMapping newMapping(final MediaMapping mapping) {

        db.store( mapping );

        return mapping;
    }

    @Override
    public MediaMapping findMediaMapping(final String mapping) {

        ObjectSet<MediaMapping> results = db.query( new Predicate<MediaMapping>() {

            @Override
            public boolean match(final MediaMapping candidate) {

                return ObjectUtils.isEqual( candidate.getMapping(), mapping );
            }
        } );
        if (results.hasNext()) {
            MediaMapping result = results.next();
            checkState( !results.hasNext(), "Multiple media mappings found for %s", mapping );

            return result;
        }

        return null;
    }
}
