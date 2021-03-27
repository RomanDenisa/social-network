package socialnetwork.domain;
import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDTO extends Entity<Long>{

    private Long idFrom;
    private String numeFrom;
    private String prenumeFrom;
    private List<Long> to = new ArrayList<>();
    private String message;
    private LocalDateTime data;
    private Long replyTo = null;

    public MessageDTO(Long idFrom, String numeFrom, String prenumeFrom, List<Long> to,
                      String message, LocalDateTime data, Long replyTo) {
        this.idFrom = idFrom;
        this.numeFrom = numeFrom;
        this.prenumeFrom = prenumeFrom;
        this.to = to;
        this.message = message;
        this.data = data;
        this.replyTo = replyTo;
    }

    public String getNumePrenume()
    {
        return numeFrom+" "+prenumeFrom;
    }

    public Long getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(Long idFrom) {
        this.idFrom = idFrom;
    }

    public String getNumeFrom() {
        return numeFrom;
    }

    public void setNumeFrom(String numeFrom) {
        this.numeFrom = numeFrom;
    }

    public String getPrenumeFrom() {
        return prenumeFrom;
    }

    public void setPrenumeFrom(String prenumeFrom) {
        this.prenumeFrom = prenumeFrom;
    }

    public List<Long> getTo() {
        return to;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Long getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Long replyTo) {
        this.replyTo = replyTo;
    }

    public String getDataString()
    {
        return data.format(Constants.DATE_TIME_FORMATTER);
    }
}
