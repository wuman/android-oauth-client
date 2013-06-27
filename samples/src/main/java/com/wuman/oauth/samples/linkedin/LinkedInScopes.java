
package com.wuman.oauth.samples.linkedin;

public class LinkedInScopes {

    /** Name, photo, headline, and current positions */
    public static final String READ_BASICPROFILE = "r_basicprofile";

    /**
     * Full profile including experience, education, skills, and recommendations
     */
    public static final String READ_FULLPROFILE = "r_fullprofile";

    /** The primary email address you use for your LinkedIn account */
    public static final String READ_EMAILADDRESS = "r_emailaddress";

    /** Your 1st and 2nd degree connections */
    public static final String READ_NETWORK = "r_network";

    /** Address, phone number, and bound accounts */
    public static final String READ_CONTACTINFO = "r_contactinfo";

    /** Retrieve and post updates to LinkedIn as you */
    public static final String READ_WRITE_NETWORKUPDATES = "rw_nus";

    /** Retrieve and post group discussions as you */
    public static final String READ_WRITE_GROUPS = "rw_groups";

    /** Send messages and invitations to connect as you */
    public static final String WRITE_MESSAGES = "w_messages";

    private LinkedInScopes() {
    }

}
