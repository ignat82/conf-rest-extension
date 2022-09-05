package ru.homecredit.confrestextension.response;

import com.atlassian.confluence.pages.Attachment;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.*;
import java.util.Map;

@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
@Slf4j
@Data
public class AttachmentResponse {

    @Getter
    public static class Version {
        @XmlElement(name = "versionNumb")
        private int versionNumb;
        @XmlElement(name = "creationDate")
        private String creationDate;
        @XmlElement(name = "creator")
        private String creator;
        @XmlElement(name = "attachmentId")
        private long attachmentId;

        public Version(Attachment attachment) {
            log.info("starting Version instance construction");
            versionNumb = attachment.getVersion();
            creationDate = attachment.getCreationDate().toString();
            creator = attachment.getCreator().getName();
            attachmentId = attachment.getId();
        }
    }

    @XmlElement(name = "attachmentId")
    private long attachmentId;
    @XmlElement(name = "fileName")
    private String fileName;
    @XmlElementWrapper(name = "versions")
    @XmlElement(name = "version")
    private Map<Integer, Version> versions;


    public AttachmentResponse(Attachment attachment) {
        log.warn("AttachmentResponse instance construction");
        attachmentId = attachment.getId();
        fileName = attachment.getFileName();
    }

    public void setVersions(Map<Integer, Version> versions) {
        log.info("running setVersions() method");
        this.versions = versions;
    }

    public Map<Integer, Version> getVersions(Map<Integer, Version> versions) {
        log.info("running getVersions() method");
        return versions;
    }
}
