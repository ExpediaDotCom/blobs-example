package com.blobExample;

import com.expedia.blobs.core.BlobContext;
import com.expedia.blobs.core.BlobStore;

public class BlobResources {
    private BlobContext blobContext;

    private BlobStore blobStore;

    public BlobResources(BlobContext blobContext, BlobStore blobStore) {
        this.blobContext = blobContext;
        this.blobStore = blobStore;
    }

    public BlobContext getBlobContext() {
        return blobContext;
    }

    public BlobStore getBlobStore() {
        return blobStore;
    }
}
