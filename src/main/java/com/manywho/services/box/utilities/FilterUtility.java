package com.manywho.services.box.utilities;

import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;

public class FilterUtility {

    public static int getLimit(ObjectDataRequest objectDataRequest) {
        int limit = 10;

        if (objectDataRequest.getListFilter() != null ) {
            limit = objectDataRequest.getListFilter().getLimit();

            if (limit <= 0) {
                limit = 1;
            }
        }

        return limit;
    }

    public static int getOffset(ObjectDataRequest objectDataRequest) {
        int offset = 0;

        if (objectDataRequest.getListFilter() != null ) {
            offset = objectDataRequest.getListFilter().getOffset();

            if (offset < 0) {
                offset = 0;
            }
        }

        return offset;
    }
}
