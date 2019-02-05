package hn.domain;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Class to map Json Item response
 */
public class Item {

    private Integer id;
    private Boolean deleted;
    private String type;
    private String by;
    private ZonedDateTime time;
    private String text;
    private Boolean dead;
    private Long parent;
    private Long poll;
    private List<Integer> kids;
    private String url;
    private Long score;
    private String title;
    private List<Integer> parts;
    private Long descendants;

    public Item() {

    }

    public Item(Integer id, Boolean deleted, String type, String by, ZonedDateTime time, String text, Boolean dead, Long parent, Long poll,
                List<Integer> kids, String url, Long score, String title, List<Integer> parts, Long descendants) {
        this.id = id;
        this.deleted = deleted;
        this.type = type;
        this.by = by;
        this.time = time;
        this.text = text;
        this.dead = dead;
        this.parent = parent;
        this.poll = poll;
        this.kids = kids;
        this.url = url;
        this.score = score;
        this.title = title;
        this.parts = parts;
        this.descendants = descendants;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the deleted
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the time
     */
    public ZonedDateTime getTime() {
        return time;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @return the dead
     */
    public Boolean getDead() {
        return dead;
    }

    /**
     * @return the parent
     */
    public Long getParent() {
        return parent;
    }

    /**
     * @return the poll
     */
    public Long getPoll() {
        return poll;
    }

    /**
     * @return the kids
     */
    public List<Integer> getKids() {
        return kids;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the score
     */
    public Long getScore() {
        return score;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the parts
     */
    public List<Integer> getParts() {
        return parts;
    }

    /**
     * @return the descendants
     */
    public Long getDescendants() {
        return descendants;
    }

    public String getBy() {
        return by;
    }

    public String toShortString() {
        return new StringBuffer("Item: { ")
                .append("id="+id)
                .append(" title="+title)
                .append(" time="+time.toEpochSecond())
                .append(" }")
                .toString();

    }

    @Override
    public String toString() {
        return new StringBuffer("Item: { ")
            .append("id="+id)
            .append(" text="+text)
            .append(" type="+type)
            .append(" time="+time.toEpochSecond())
            .append(" }")
            .toString();
            
    }
}
