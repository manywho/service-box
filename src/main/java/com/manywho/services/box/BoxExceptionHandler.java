package com.manywho.services.box;

import com.box.sdk.BoxAPIException;
import com.manywho.sdk.services.providers.ExceptionMapperProvider;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BoxExceptionHandler implements ExceptionMapper<BoxAPIException> {
    @Inject
    private ExceptionMapperProvider exceptionMapperProvider;

    @Override
    public Response toResponse(BoxAPIException exception) {
        String message = exception.getMessage();

        switch (exception.getResponseCode()) {
            case 401:
                message = "Unauthorized. Check if your permissions are correct on Box.";
                break;
            case 404:
                message = "The file or folder could not be found.";
                break;
            case 409:
                message = "A file or folder already exists with that name.";
                break;
        }

        if (StringUtils.isNotEmpty(exception.getResponse())) {
            message += " with the response " + StringEscapeUtils.unescapeJson(exception.getResponse());
        }

        return exceptionMapperProvider.toResponse(new Exception(message));
    }
}
