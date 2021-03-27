package socialnetwork.domain;

import socialnetwork.service.MesajeService;
import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long>{

    private Utilizator from;
    private List<Utilizator> to = new ArrayList<>();
    private String message;
    private LocalDateTime data;
    private Long replyTo = null;

    public Message(Utilizator from, List<Utilizator> to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    public Message(Utilizator from, List<Utilizator> to, String message,Long replyTo) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.replyTo = replyTo;
    }


    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public LocalDateTime getData() {
        return data;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public Utilizator getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getDataString()
    {
        return data.format(Constants.DATE_TIME_FORMATTER);
    }

    public Long getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Long replyTo) {
        this.replyTo = replyTo;
    }

    @Override
    public String toString() {
        return from.getId() + " " + message + " " +  data;
    }
}
