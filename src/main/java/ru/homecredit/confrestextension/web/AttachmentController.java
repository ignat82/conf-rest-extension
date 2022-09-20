package ru.homecredit.confrestextension.web;

import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.confrestextension.response.AttachmentResponse;
import ru.homecredit.confrestextension.service.AttachmentService;
import ru.homecredit.confrestextension.service.PermissionService;

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
    private final PermissionService permissionService;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Inject
    public AttachmentController(@ComponentImport AttachmentManager attachmentManager,
                                @ComponentImport UserManager userManager,
                                @ComponentImport SpacePermissionManager spacePermissionManager,
                                @ComponentImport UserAccessor userAccessor,
                                @ComponentImport ContentPermissionManager contentPermissionManager) {
        attachmentService = new AttachmentService(attachmentManager);
        permissionService = new PermissionService(attachmentManager,
                                                  contentPermissionManager,
                                                  spacePermissionManager,
                                                  userAccessor,
                                                  userManager);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{attachmentId}/getversions")
    public Response getAttachmentVersions(@PathParam("attachmentId") String attachmentId) {
        log.info("starting getAttachmentVersions() method");
        AttachmentResponse attachmentResponse =
            attachmentService.getVersions(Long.parseLong(attachmentId));
        if (attachmentResponse.getResult().equals(SUCCESS) &&
                !permissionService.hasPermission(
                        Long.parseLong(attachmentId), VIEWATTACHMENT, View)) {
            log.error("permission denied");
            attachmentResponse.setResult(ERROR);
            attachmentResponse.setMessage(
                    "the user is is not authorized to view this attachment");
            attachmentResponse.setVersions(null);
        }
        return Response.ok(gson.toJson(attachmentResponse)).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{attachmentId}/deleteversion/{versionId}")
    public Response deleteVersion(@PathParam("attachmentId") String attachmentId,
                                  @PathParam("versionId") String versionId) {
        log.info("starting deleteVersion() method");
        AttachmentResponse attachmentResponse =
                attachmentService.getVersions(Long.parseLong(attachmentId));
        if (attachmentResponse.getResult() == SUCCESS) {
            boolean userHasPermission =
                    permissionService.hasPermission(Long.parseLong(attachmentId),
                                                    REMOVEATTACHMENT,
                                                    Edit);
            if (!userHasPermission) {
                attachmentResponse.setResult(ERROR);
                attachmentResponse.setMessage(
                        "the user is is not authorized to delete this attachment");
                attachmentResponse.setVersions(null);
            } else {
                attachmentResponse = attachmentService.deleteVersion(Long.parseLong(attachmentId),
                                                                     Integer.parseInt(versionId));
            }
        }
        return Response.ok(gson.toJson(attachmentResponse)).build();
    }
}
