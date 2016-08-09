package com.manywho.services.box;

import com.box.sdk.BoxAPIRequest;
import com.box.sdk.BoxJSONResponse;
import com.box.sdk.RequestInterceptor;

import java.util.ArrayList;
import java.util.List;

public class RequestIntersectorTestsImpl implements RequestInterceptor {
    private List<BoxJSONResponse> boxJSONResponseList;
    private int executedCalls = 0;

    public RequestIntersectorTestsImpl() {
        boxJSONResponseList = new ArrayList<>();
    }

    public void addApiResponse(BoxJSONResponse boxAPIResponse) {
        boxJSONResponseList.add(boxAPIResponse);
    }

    @Override
    public BoxJSONResponse onRequest(BoxAPIRequest request) {

        BoxJSONResponse boxAPIResponse = boxJSONResponseList.get(0);
        if (! (boxJSONResponseList.size() > 0)) {
            throw new RuntimeException("The pile of BoxJSONResponse for box is empty");
        }

        boxJSONResponseList.remove(0);
        executedCalls++;

        return boxAPIResponse;
    }

    public int executedCalls() {
        return executedCalls;
    }
}
