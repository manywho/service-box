package com.manywho.services.box.test;

import org.apache.http.Header;

public class FlowResponseMock extends HttpResponseMock{
    public FlowResponseMock() {
        this.headers = new Header[]{};
        this.httpEntity = getNewEntity("Content-Type: application/json; charset=utf-8", "\"WAIT\"");
        this.statusLine = getNewStatusLine("HTTP", 1, 1, 200, "ok");
    }

    public FlowResponseMock(Header[] headers, String protocol, int mayorVersion, int minorVersion,
                            int statusCode, String reasonPhrase, String contentType, String body ) {

        this.headers = headers;
        this.statusLine = getNewStatusLine(protocol, mayorVersion, minorVersion, statusCode, reasonPhrase);
        this.httpEntity = getNewEntity(contentType, body);
    }
}
