package ru.homecredit.confrestextension.service;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.confrestextension.response.AttachmentResponse;
import ru.homecredit.confrestextension.response.AttachmentResponse.Version;

import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ru.homecredit.confrestextension.response.AttachmentResponse.Result.*;

@Slf4j
@Named
public class AttachmentService {
    private final AttachmentManager attachmentManager;

    public AttachmentService(AttachmentManager attachmentManager) {
        log.warn("AttachmentService construction");
        this.attachmentManager = attachmentManager;
    }

    public AttachmentResponse getVersions(long attachmentId) {
        log.info("starting getVersions() method");
        AttachmentResponse attachmentResponse = new AttachmentResponse();
        Attachment attachment;
        try {
            attachment = attachmentManager.getAttachment(attachmentId);
            attachmentResponse = new AttachmentResponse(attachment);
        } catch (Exception e) {
            log.error("caught exception when acquiring attachment {} by Id", attachmentId);
            attachmentResponse.setAttachmentId(attachmentId);
            attachmentResponse.setResult(ERROR);
            attachmentResponse.setMessage("exception " +
                " caught when trying to acquire attachment with Id "
                                                  + attachmentId + " " + e.getMessage());
            return attachmentResponse;
        }
        log.info("acquired attachment {}", attachmentId);
        List<Attachment> allAttachmentVersions;
        try {
            allAttachmentVersions = attachmentManager
                    .getAllVersions(attachment);
            log.info("got attachments list {}", allAttachmentVersions.toString());
            Map<Integer, Version> versions = new HashMap<>();
            for (Attachment attachmentVersion : allAttachmentVersions) {
                versions.put(attachmentVersion.getVersion(), new Version(attachmentVersion));
            }
            attachmentResponse.setVersions(versions);
            attachmentResponse.setResult(SUCCESS);
            attachmentResponse.setMessage("got " + attachmentResponse.getVersions().size() +
                                                  " attachments");
            return attachmentResponse;
        } catch (Exception e) {
            log.error("caught exception when acquiring attachment {} versions", attachmentId);
            attachmentResponse.setResult(ERROR);
            attachmentResponse.setMessage("exception " + e.getMessage() +
                 "caught when trying to acquire attachment's " + attachmentId + " versions");
            return attachmentResponse;
        }
    }

    public AttachmentResponse deleteVersion(long attachmentId, int versionId) {
        log.info("starting deleteVersion() method");
        AttachmentResponse attachmentResponse = getVersions(attachmentId);
        if (attachmentResponse.getResult() == ERROR) {
            log.info("failed to get attachment versions");
            return attachmentResponse;
        }
        if (!attachmentResponse.getVersions().containsKey(versionId)) {
            log.error("version {} of attachment {} isn't exist", versionId, attachmentId);
            attachmentResponse.setResult(ERROR);
            attachmentResponse.setMessage("version " + versionId + " does not exist");
            return attachmentResponse;
        }
        log.info("attachment to be deleted version is {} from {}", versionId,
                 attachmentResponse.getVersions().size());
        long attachmentToRemoveId =
                attachmentResponse.getVersions().get(versionId).getAttachmentId();
        attachmentManager.removeAttachmentVersionFromServer(attachmentManager.getAttachment(attachmentToRemoveId));
        attachmentResponse.setResult(SUCCESS);
        attachmentResponse.setMessage("version " + versionId + " of attachment "
                                              + attachmentToRemoveId + " of parent " +
                                              "attachment " + attachmentId + " removed");
        log.info("attachmentToDelete removed");
        return attachmentResponse;
    }

    public Attachment getAttachment(long attachmentId) throws Exception {
        return attachmentManager.getAttachment(attachmentId);
    }
}
