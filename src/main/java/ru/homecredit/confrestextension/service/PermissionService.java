package ru.homecredit.confrestextension.service;

import com.google.gson.annotations.SerializedName;

public interface PermissionService {
    boolean hasPermission(long attachmentId,
                          SpacePermissionLevel spacePermissionLevel,
                          ContentPermissionLevel contentPermissionLevel);

    /**
     * Enums were cast to fit atlassian Java API, that uses string parameters
     */
    enum ContentPermissionLevel {
        @SerializedName("Edit") Edit,
        @SerializedName("View") View;
    }

    enum SpacePermissionLevel {
        @SerializedName("REMOVEATTACHMENT") REMOVEATTACHMENT,
        @SerializedName("VIEWATTACHMENT") VIEWATTACHMENT;
    }
}
