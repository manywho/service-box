package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.describe.DescribeServiceResponse;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.box.managers.DescribeManager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
@Consumes("application/json")
@Produces("application/json")
public class DescribeController extends AbstractController {
    @Inject
    private DescribeManager describeManager;

    @Path("/metadata")
    @POST
    @AuthorizationRequired
    public DescribeServiceResponse describe(ServiceRequest serviceRequest) throws Exception {
        return describeManager.describe();
    }
}

