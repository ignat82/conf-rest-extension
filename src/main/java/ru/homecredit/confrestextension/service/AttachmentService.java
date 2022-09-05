package ru.homecredit.confrestextension.service;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.confrestextension.response.AttachmentResponse;
import ru.homecredit.confrestextension.response.AttachmentResponse.Version;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AttachmentService {
    private final AttachmentManager attachmentManager;

    public AttachmentService(AttachmentManager attachmentManager) {
        log.warn("AttachmentService construction");
        this.attachmentManager = attachmentManager;
    }

    public AttachmentResponse getVersions(long attachmentId) {
        log.info("starting getVersions() method");
        try {
            Attachment attachment = attachmentManager.getAttachment(attachmentId);
            if (attachment == null) {
                log.error("getting attachment fail loose massage. Returning null " +
                                  "attachmentResponse");
                return null;
            }
            AttachmentResponse attachmentResponse = new AttachmentResponse(attachment);
            updateVersions(attachmentResponse);
            return attachmentResponse;
        } catch (Exception e) {
            log.error("getting attachment fail good massage. Returning null attachmentResponse");
            return null;
        }

    }

    public void updateVersions(AttachmentResponse attachmentResponse) {
        log.info("starting updateVersions() method");
        List<Attachment> allAttachmentVersions = attachmentManager
                .getAllVersions(attachmentManager.getAttachment(attachmentResponse.getAttachmentId()));
        log.info("got attachments list {}",allAttachmentVersions.toString());
        Map<Integer, Version> versions = new HashMap<>();
        for (Attachment attachmentVersion : allAttachmentVersions) {
            log.info("going with attachment {}", attachmentVersion);
            try {
                attachmentVersion.getVersion();
            } catch (Exception e) {
                log.error("caught {} exception when getting version number", e.getMessage());
            }
            versions.put(attachmentVersion.getVersion(), new Version(attachmentVersion));
        }
        log.info("versions assembled");
        attachmentResponse.setVersions(versions);
    }

    public AttachmentResponse deleteVersion(long attachmentId, int versionId) {
        log.info("starting deleteVersion() method");
        AttachmentResponse attachmentResponse = getVersions(attachmentId);
        log.info("attachment response constructed and is null - {}", attachmentResponse == null);
        try {
            long attachmentToDeleteId =
                    attachmentResponse.getVersions().get(versionId).getAttachmentId();
            log.info("attachmentToDeleteId is {}", attachmentToDeleteId);
            Attachment attachmentToDelete = attachmentManager.getAttachment(attachmentToDeleteId);
            log.info("attachmentToDelete constructed");
            attachmentManager.removeAttachmentVersionFromServer(attachmentToDelete);
            log.info("attachmentToDelete removed");
            updateVersions(attachmentResponse);
        } catch (NullPointerException npe) {
            log.error("version {} isn't exist", versionId);
        }
        log.info("attachmentResponse is null - {}", attachmentResponse==null);
        return attachmentResponse;
    }

}
