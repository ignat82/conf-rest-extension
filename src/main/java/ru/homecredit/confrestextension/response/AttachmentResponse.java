package ru.homecredit.confrestextension.response;

import com.atlassian.confluence.pages.Attachment;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import javax.xml.bind.annotation.*;
import java.util.Map;

@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
public class AttachmentResponse {


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

    public static AttachmentResponse from(Attachment attachment) {
        AttachmentResponse attachmentResponse = new AttachmentResponse();
        attachmentResponse.setAttachmentId(attachment.getId());
        attachmentResponse.setFileName(attachment.getFileName());
        return attachmentResponse;
    }

    public enum Result {
        @SerializedName("success") SUCCESS,
        @SerializedName("error") ERROR;
    }

    @Getter
    public static class Version {
        @XmlElement(name = "versionNumb")
        private final int versionNumb;
        @XmlElement(name = "creationDate")
        private final String creationDate;
        @XmlElement(name = "creator")
        private final String creator;
        @XmlElement(name = "attachmentId")
        private final long attachmentId;

        public Version(Attachment attachment) {
            versionNumb = attachment.getVersion();
            creationDate = attachment.getCreationDate().toString();
            creator = attachment.getCreator().getName();
            attachmentId = attachment.getId();
        }
    }
}
