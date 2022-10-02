package ru.homecredit.confrestextension.service;

import ru.homecredit.confrestextension.response.AttachmentResponse;

public interface AttachmentService {
    AttachmentResponse getVersions(long attachmentId);
    AttachmentResponse deleteVersion(long attachmentId, int versionId);
}
