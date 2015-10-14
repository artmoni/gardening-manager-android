package org.gots.provider;

import org.nuxeo.android.contentprovider.AbstractNuxeoReadOnlyContentProvider;

/**
 * Created by sfleury on 14/10/15.
 */
public class NuxeoDocumentContentProvider extends AbstractNuxeoReadOnlyContentProvider {

    @Override
    protected int getDefaultPageSize() {
        return 10;
    }

}
