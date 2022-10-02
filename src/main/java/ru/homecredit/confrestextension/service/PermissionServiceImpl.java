package ru.homecredit.confrestextension.service;

import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService{
    private final AttachmentManager attachmentManager;
    private final ContentPermissionManager contentPermissionManager;
    private final SpacePermissionManager spacePermissionManager;
    private final UserAccessor userAccessor;
    private final UserManager userManager;

    public boolean hasPermission(long attachmentId,
                                 SpacePermissionLevel spacePermissionLevel,
                                 ContentPermissionLevel contentPermissionLevel) {
        User user = userAccessor.getUserByName(userManager.getRemoteUsername());
        if (isUserAnAdmin(user.getName())) {
            log.info("current user \"{}\" is an admin. granting permission", user.getName());
            return true;
        }
        Attachment  attachment = attachmentManager.getAttachment(attachmentId);
        if (attachment == null) {
            log.error("failed to acquire attachment {}", attachmentId);
            return false;
        }
        Space space = attachment.getSpace();
        if (hasSContentLevelPermission(contentPermissionLevel, attachment, user)) {
            return true;
        }
        if (hasSpaceLevelPermission(spacePermissionLevel, space, user)) {
            return true;
        }
        log.error("seems user \"{}\" has no permission to handle this attachment", user.getName());
        return false;
    }

    private boolean hasSContentLevelPermission(ContentPermissionLevel contentPermissionLevel,
                                               Attachment attachment,
                                               User user) {
        if (contentPermissionManager.hasContentLevelPermission(user,
                                                               contentPermissionLevel.toString(),
                                                               attachment.getContainer())) {
            log.info("user \"{}\" does have content-level \"{}\" permission for attachment {} on page {}",
                     user.getName(),
                     contentPermissionLevel,
                     attachment.getFileName(),
                     attachment.getContainer());
            return true;
        } else {
            log.error("user \"{}\" has no content-level \"{}\" permission for attachment {} on page {}",
                      user.getName(),
                      contentPermissionLevel,
                      attachment.getFileName(),
                      attachment.getContainer());
            return false;
        }
    }
    private boolean hasSpaceLevelPermission(SpacePermissionLevel spacePermissionLevel,
                                            Space space, User user) {
        log.info("checking {} space \"{}\" permission for user \"{}\"",
                 space.getName(), spacePermissionLevel, user.getName());
        Map<String, Long> users =
                spacePermissionManager.getUsersForPermissionType(spacePermissionLevel.toString(),
                                                                      space);
        log.info("users {} have \"{}\" permission in space {}",
                 users.keySet(), spacePermissionLevel, space);
        if (users.get(user.getName()) != null) {
            log.info("user \"{}\" is among them", user.getName());
            return true;
        }
        Map<String, Long> groups =
                spacePermissionManager.getGroupsForPermissionType(spacePermissionLevel.toString(),
                                                                  space);
        log.info("groups with \"{}\" permission in space {} are {}",
                 spacePermissionLevel, space.getName(), groups.toString());
        for (String groupName : groups.keySet()) {
            Group group = userAccessor.getGroup(groupName);
            if (userAccessor.hasMembership(group, user)) {
                log.info("user \"{}\" belongs to group {}", user.getName(), group.getName());
                return true;
            }
        }
        log.warn("user \"{}\" has no \"{}\" permission in space {}",
                 user.getName(), spacePermissionLevel, space.getName());
        return false;
    }

    private boolean isUserAnAdmin(String username) {
        log.info("current user is \"{}\"... he's an admin - {}", username,
                 userManager.isSystemAdmin(username));
        return userManager.isSystemAdmin(username);
    }
}

