package ru.homecredit.confrestextension.response;

import com.atlassian.confluence.pages.Attachment;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.*;
import java.util.Map;

@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
@Slf4j
@Data
@NoArgsConstructor
public class AttachmentResponse {

    @AllArgsConstructor
    @Getter
    public enum Result {
        @SerializedName("success") SUCCESS,
        @SerializedName("error") ERROR;
    }

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
            versionNumb = attachment.getVersion();
            creationDate = attachment.getCreationDate().toString();
            creator = attachment.getCreator().getName();
            attachmentId = attachment.getId();
        }
    }

    @XmlElement(name = "result")
    private Result result;
    @XmlElement(name = "message")
    private String message;
    @XmlElement(name = "attachmentId")
    private long attachmentId;
    @XmlElement(name = "fileName")
    private String fileName;
    @XmlElementWrapper(name = "versions")
    @XmlElement(name = "version")
    private Map<Integer, Version> versions;

    public AttachmentResponse(Attachment attachment) {
        attachmentId = attachment.getId();
        fileName = attachment.getFileName();
    }

}
