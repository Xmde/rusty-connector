package group.aelysium.rustyconnector.core.lib.data_transit.cache;

import group.aelysium.rustyconnector.core.lib.packets.PacketStatus;

import java.util.Date;

public class CacheableMessage {
    private final Long snowflake;
    private final Date date;
    private final String contents;
    private String reason;
    private PacketStatus status;

    public CacheableMessage(Long snowflake, String contents, PacketStatus status) {
        this.snowflake = snowflake;
        this.contents = contents;
        this.date = new Date();
        this.status = status;
    }

    public Long getSnowflake() {
        return this.snowflake;
    }

    public String getContents() {
        return this.contents;
    }

    public Date getDate() {
        return this.date;
    }

    public PacketStatus getSentence() {
        return this.status;
    }
    public String getSentenceReason() {
        return this.reason;
    }

    /**
     * Sentence the message to a new status.
     * Will also unset `reason`.
     * @param status The new status to issue.
     */
    public void sentenceMessage(PacketStatus status) {
        this.status = status;
        this.reason = null;
    }

    /**
     * Sentence the message to a new status.
     * @param status The new status to issue.
     * @param reason The reason for the sentence.
     */
    public void sentenceMessage(PacketStatus status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Snowflake ID: "+this.snowflake.toString()+
               " Contents: "+this.contents+
               " Date: "+this.date+
               " Status: "+this.contents;
    }
}
