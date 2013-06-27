
package com.wuman.oauth.samples.github.api;

public class GitHubScopes {

    /**
     * Read/write access to profile info only. Note: this scope includes
     * {@link #USER_EMAIL} and {@link #USER_FOLLOW}.
     */
    public static final String USER = "user";

    /** Read access to a user’s email addresses. */
    public static final String USER_EMAIL = "user:email";

    /** Access to follow or unfollow other users. */
    public static final String USER_FOLLOW = "user:follow";

    /** Read/write access to public repos and organizations. */
    public static final String PUBLIC_REPO = "public_repo";

    /** Read/write access to public and private repos and organizations. */
    public static final String REPO = "repo";

    /**
     * Read/write access to public and private repository commit statuses. This
     * scope is only necessary to grant other users or services access to
     * private repository commit statuses without granting access to the code.
     * The {@link #REPO} and {@link #PUBLIC_REPO} scopes already include access
     * to commit status for private and public repositories respectively.
     */
    public static final String REPO_STATUS = "repo:status";

    /** Delete access to adminable repositories. */
    public static final String DELETE_REPO = "delete_repo";

    /** Read access to a user’s notifications. {@link #REPO} is accepted too. */
    public static final String NOTIFICATIONS = "notifications";

    /** Write access to gists. */
    public static final String GIST = "gist";

    private GitHubScopes() {
    }

}
