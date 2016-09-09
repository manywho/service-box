package com.manywho.services.box.test;

import com.box.sdk.BoxAPIRequest;
import com.box.sdk.BoxJSONResponse;
import com.box.sdk.RequestInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestIntersectorTestsImpl implements RequestInterceptor {
    private List<BoxJSONResponse> boxJSONResponseList;
    private List<String> expectedRequestUrlList;
    private int executedCalls = 0;

    public RequestIntersectorTestsImpl() {
        boxJSONResponseList = new ArrayList<>();
        expectedRequestUrlList = new ArrayList<>();
    }

    public void addApiResponse(BoxJSONResponse boxAPIResponse) {
        boxJSONResponseList.add(boxAPIResponse);
        expectedRequestUrlList.add(null);
    }

    public void addApiResponse(BoxJSONResponse boxAPIResponse, String expectedRequestUrl) {
        boxJSONResponseList.add(boxAPIResponse);
        expectedRequestUrlList.add(expectedRequestUrl);
    }

    @Override
    public BoxJSONResponse onRequest(BoxAPIRequest request) {
        BoxJSONResponse boxAPIResponse = boxJSONResponseList.get(0);

        if (! (boxJSONResponseList.size() > 0)) {
            throw new RuntimeException("The pile of BoxJSONResponse for box is empty");
        }

        if(expectedRequestUrlList.get(0) != null) {
            if (!Objects.equals(expectedRequestUrlList.get(0), expectedRequestUrlList.get(0))) {
                throw new RuntimeException("Request Url for box not as expected");
            }
        }

        //request.getUrl().toString()

        boxJSONResponseList.remove(0);
        expectedRequestUrlList.remove(0);
        executedCalls++;

        return boxAPIResponse;
    }

    public int executedCalls() {
        return executedCalls;
    }
}
