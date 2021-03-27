package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
public class CerereDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private StatusEnum status;
    private LocalDateTime data;

    public CerereDTO(Long id, String firstName, String lastName, StatusEnum status, LocalDateTime time) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.data = time;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public LocalDateTime getTime() {
        return data;
    }

    public String getDataString()
    {
        return data.format(Constants.DATE_TIME_FORMATTER);
    }
}
