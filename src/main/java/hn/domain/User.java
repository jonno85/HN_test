package hn.domain;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Class to map Json User response
 */
public class User {

    private String id;
    private Integer delay;
    private ZonedDateTime created;
    private Integer karma;
    private String about;
    private List<Integer> submitted;

    public User() {

    }

    public User(String id, Integer delay, ZonedDateTime created, Integer karma, String about, List<Integer> submitted) {
        this.id = id;
        this.delay = delay;
        this.created = created;
        this.karma = karma;
        this.about = about;
        this.submitted = submitted;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the delay
     */
    public Integer getDelay() {
        return delay;
    }

    /**
     * @return the created
     */
    public ZonedDateTime getCreated() {
        return created;
    }

    /**
     * @return the karma
     */
    public Integer getKarma() {
        return karma;
    }

    /**
     * @return the about
     */
    public String getAbout() {
        return about;
    }

    /**
     * @return the submitted
     */
    public List<Integer> getSubmitted() {
        return submitted;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", delay=" + delay +
                ", created=" + created +
                ", karma=" + karma +
                ", about='" + about + '\'' +
                ", submitted=" + submitted +
                '}';
    }
}
