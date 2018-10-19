package io.gravitee.management.service.impl.search;

import java.util.List;

public class SearchResult {

    private final List<String> documents;

    private long hits;

    public SearchResult(final List<String> documents) {
        this.documents = documents;
    }

    public SearchResult(final List<String> documents, long hits) {
        this.documents = documents;
        this.hits = hits;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public long getHits() {
        return hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public boolean hasResults() {
        return documents != null && !documents.isEmpty();
    }
}
