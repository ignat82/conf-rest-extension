package ru.homecredit.confrestextension.service;

import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.google.gson.annotations.SerializedName;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class PermissionService {
    public enum ContentPermissionLevel {
        @SerializedName("Edit") Edit,
        @SerializedName("View") View;
    }

    public enum SpacePermissionLevel {
        @SerializedName("REMOVEATTACHMENT") REMOVEATTACHMENT,
        @SerializedName("VIEWATTACHMENT") VIEWATTACHMENT;
    }
    private UserManager userManager;
    private SpacePermissionManager spacePermissionManager;
    private ContentPermissionManager contentPermissionManager;
    private UserAccessor userAccessor;
    private AttachmentService attachmentService;

    public PermissionService(UserManager userManager,
                             SpacePermissionManager spacePermissionManager,
                             UserAccessor userAccessor,
                             AttachmentService attachmentService,
                             ContentPermissionManager contentPermissionManager) {
        this.userManager = userManager;
        this.spacePermissionManager = spacePermissionManager;
        this.userAccessor = userAccessor;
        this.attachmentService = attachmentService;
        this.contentPermissionManager = contentPermissionManager;
    }

    public User getCurrentUser() {
        log.info("current user is {}", userManager.getRemoteUsername());
        return userAccessor.getUserByName(userManager.getRemoteUsername());
    }

    public boolean isCurrentUserAdmin() {
        log.info("current user is {}... he's admin {}", userManager.getRemoteUsername(),
                 userManager.isSystemAdmin(userManager.getRemoteUsername()));
        return userManager.isSystemAdmin(userManager.getRemoteUsername());
    }

    public boolean hasPermission(long attachmentId, SpacePermissionLevel spacePermissionLevel,
                                 ContentPermissionLevel contentPermissionLevel) {
        log.info("starting hasPermission() method");
        Space space;
        Attachment  attachment;
        try {
            attachment = attachmentService.getAttachment(attachmentId);
            space = attachment.getSpace();
        } catch (Exception e) {
            log.error("caught exception acquiring attachment");
            return false;
        }
        User user = getCurrentUser();
        log.info("checking content-level permission for user {}", user.getName());
        if (!contentPermissionManager.hasContentLevelPermission(user, contentPermissionLevel.toString(),
                                                                attachment.getContainer())) {
            log.error("user {} has no content-level permission to delete attachment {} on page " +
                              "{}", user.getName(), attachment.getFileName(),
                      attachment.getContainer());
            return false;
        }
        log.info("checking space permission for {} space", space.getName());
        Map<String, Long> users =
                this.spacePermissionManager.getUsersForPermissionType(spacePermissionLevel.toString(),
                                                                      space);
        log.info("users {} have permission {} in space {}", users.keySet(),
                 spacePermissionLevel, space);
        if (users.get(user.getName()) != null) {
            log.info("user has user permission");
            return true;
        }
        Map<String, Long> groups =
                spacePermissionManager.getGroupsForPermissionType(spacePermissionLevel.toString(), space);
        log.info("groups with edit permission are {}", groups.toString());
        for (String groupName : groups.keySet()) {
            Group group = userAccessor.getGroup(groupName);
            if (userAccessor.hasMembership(group, user)) {
                log.info("user has group permission from group {}", group.getName());
                return true;
            }
        }
        log.warn("seems user \"{}\" has no permission to handle this attachment", user.getName());
        return false;
    }

}

