package com.sioma.spotsapi.domain.model;

public record PaginationParams(int page, int size, String sortBy, String sortDir) {

    private static final int MAX_SIZE = 100;
    private static final String DEFAULT_SORT_BY = "id";
    private static final String DEFAULT_SORT_DIR = "asc";

    public PaginationParams {
        if (page < 0) throw new IllegalArgumentException("page debe ser >= 0");
        if (size < 1 || size > MAX_SIZE) throw new IllegalArgumentException("size debe estar entre 1 y " + MAX_SIZE);

        sortBy = (sortBy == null || sortBy.isBlank()) ? DEFAULT_SORT_BY : sortBy;
        String dir = (sortDir == null || sortDir.isBlank()) ? DEFAULT_SORT_DIR : sortDir.toLowerCase();
        if (!"asc".equals(dir) && !"desc".equals(dir)) {
            throw new IllegalArgumentException("sortDir debe ser 'asc' o 'desc'");
        }

        sortDir = dir;
    }

    public static PaginationParams of(int page, int size, String sortBy, String sortDir) {
        return new PaginationParams(page, size, sortBy, sortDir);
    }
}