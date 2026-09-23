package com.openisle.model;

/**
 * Types of user notifications.
 */
public enum NotificationType {
    /** Someone viewed your post */
    POST_VIEWED,
    /** Someone replied to your post or comment */
    COMMENT_REPLY,
    /** Someone reacted to your post or comment */
    REACTION,
    /** Your post under review was approved or rejected */
    POST_REVIEWED
}
