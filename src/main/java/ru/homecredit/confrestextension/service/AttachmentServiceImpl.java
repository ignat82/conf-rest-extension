package ru.homecredit.confrestextension.service;

import com.atlassian.confluence.core.AbstractVersionedEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.confrestextension.response.AttachmentResponse;
import ru.homecredit.confrestextension.response.AttachmentResponse.Version;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.homecredit.confrestextension.response.AttachmentResponse.Result.*;
import static ru.homecredit.confrestextension.service.PermissionService.ContentPermissionLevel.Edit;
import static ru.homecredit.confrestextension.service.PermissionService.ContentPermissionLevel.View;
import static ru.homecredit.confrestextension.service.PermissionService.SpacePermissionLevel.REMOVEATTACHMENT;
import static ru.homecredit.confrestextension.service.PermissionService.SpacePermissionLevel.VIEWATTACHMENT;

@Slf4j
@Named
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentManager attachmentManager;
    private final PermissionService permissionService;

    @Inject
    public AttachmentServiceImpl(@ComponentImport AttachmentManager attachmentManager,
                                 PermissionServiceImpl permissionService) {
        this.attachmentManager = attachmentManager;
        this.permissionService = permissionService;
    }

    public AttachmentResponse getVersions(long attachmentId) {
        AttachmentResponse attachmentResponse = new AttachmentResponse();
        Attachment attachment = attachmentManager.getAttachment(attachmentId);
        if (attachment == null) {
            attachmentResponse.setMessage(
                    "failed to acquire attachment with id " + attachmentId);
            attachmentResponse.setAttachmentId(attachmentId);
            attachmentResponse.setResult(ERROR);
            log.error(attachmentResponse.getMessage());
            return attachmentResponse;
        }
        attachmentResponse = AttachmentResponse.from(attachment);
        log.info("acquired attachment with Id {}", attachmentId);
        if (permissionService.hasPermission(attachmentId, VIEWATTACHMENT, View)) {
            List<Attachment> allAttachmentVersions = attachmentManager
                    .getAllVersions(attachment);
            log.info("got attachments list {}", allAttachmentVersions.toString());
            Map<Integer, Version> versions = allAttachmentVersions.stream().collect(
                    Collectors.toMap(AbstractVersionedEntityObject::getVersion,
                                     Version::new));
            attachmentResponse.setVersions(versions);
            attachmentResponse.setResult(SUCCESS);
            attachmentResponse.setMessage("got " + versions.size() + " versions of attachment ");
        } else {
            log.error("permission denied");
            attachmentResponse.setResult(ERROR);
            attachmentResponse.setMessage("the user is is not authorized to view this attachment");
        }
        return attachmentResponse;
    }

    public AttachmentResponse deleteVersion(long attachmentId, int versionId) {
        AttachmentResponse attachmentResponse = getVersions(attachmentId);
        if (attachmentResponse.getResult() == ERROR) {
            log.info("shutting down deleteVersion()");
        } else if (!permissionService.hasPermission(attachmentId, REMOVEATTACHMENT, Edit)) {
            attachmentResponse.setResult(ERROR);
            attachmentResponse.setMessage(
                    "the user is is not authorized to delete this attachment");
            attachmentResponse.setVersions(null);
        } else if (!attachmentResponse.getVersions().containsKey(versionId)) {
            log.error("version {} of attachment {} isn't exist", versionId, attachmentId);
            attachmentResponse.setResult(ERROR);
            attachmentResponse.setMessage("version " + versionId + " does not exist");
        } else {
            log.info("attachment version to be deleted is {} from {}", versionId,
                     attachmentResponse.getVersions().size());
            long attachmentToRemoveId =
                    attachmentResponse.getVersions().get(versionId).getAttachmentId();
            attachmentManager.removeAttachmentVersionFromServer(attachmentManager.getAttachment(attachmentToRemoveId));
            attachmentResponse.setResult(SUCCESS);
            attachmentResponse.setMessage("version " + versionId + " of attachment "
                    + attachmentToRemoveId + " of parent " + "attachment "
                    + attachmentId + " removed");
            log.info("attachment version deleted");
        }
        return attachmentResponse;
    }
}
