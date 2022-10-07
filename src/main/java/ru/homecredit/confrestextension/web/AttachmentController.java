package ru.homecredit.confrestextension.web;

import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.confrestextension.response.AttachmentResponse;
import ru.homecredit.confrestextension.service.AttachmentService;
import ru.homecredit.confrestextension.service.AttachmentServiceImpl;
import ru.homecredit.confrestextension.service.PermissionService;
import ru.homecredit.confrestextension.service.PermissionServiceImpl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static ru.homecredit.confrestextension.response.AttachmentResponse.Result.*;
import static ru.homecredit.confrestextension.service.PermissionService.ContentPermissionLevel.*;
import static ru.homecredit.confrestextension.service.PermissionService.SpacePermissionLevel.*;

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
    public AttachmentController(AttachmentServiceImpl attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{attachmentId}/getversions")
    public Response getAttachmentVersions(@PathParam("attachmentId") String attachmentId) {
        log.info("starting getAttachmentVersions() method");
        return Response.ok(gson.toJson(attachmentService.getVersions(Long.parseLong(attachmentId)))).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{attachmentId}/deleteversion/{versionId}")
    public Response deleteVersion(@PathParam("attachmentId") String attachmentId,
                                  @PathParam("versionId") String versionId) {
        log.info("starting deleteVersion() method");
        return Response.ok(gson.toJson(attachmentService.deleteVersion(
                Long.parseLong(attachmentId), Integer.parseInt(versionId)))).build();
    }
}
