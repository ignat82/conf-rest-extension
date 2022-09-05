package ru.homecredit.confrestextension.web;

import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.confrestextension.response.AttachmentResponse;
import ru.homecredit.confrestextension.service.AttachmentService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * A resource of message.
 */
@Path("/attachment")
@Named
@Slf4j
public class AttachmentController {
    private final AttachmentService attachmentService;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Inject
    public AttachmentController(@ComponentImport AttachmentManager attachmentManager) {
        log.warn("AttachmentController instance construction");
        attachmentService = new AttachmentService(attachmentManager);
    }

    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{attachmentId}/getversions")
    public Response getAttachmentVersions(@PathParam("attachmentId") String attachmentId) {
        log.info("starting getAttachmentVersions() method");
        AttachmentResponse attachmentResponse =
                attachmentService.getVersions(Long.parseLong(attachmentId));
        return Response.ok(gson.toJson(attachmentResponse)).build();
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{attachmentId}/deleteversion/{versionId}")
    public Response deleteVersion(@PathParam("attachmentId") String attachmentId,
                                  @PathParam("versionId") String versionId) {
        log.info("starting deleteVersion() method");
        AttachmentResponse attachmentResponse =
                attachmentService.deleteVersion(Long.parseLong(attachmentId),
                                                Integer.parseInt(versionId));
        return Response.ok(attachmentResponse).build();
    }
}
