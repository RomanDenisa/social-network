package socialnetwork.domain;

import java.time.LocalDateTime;

public class CererePrietenie extends Entity<Tuple<Long,Long>>{

    private StatusEnum status;

    private LocalDateTime dataCerere;

    public CererePrietenie() {
        this.status = StatusEnum.PENDING;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getDataCerere() {
        return dataCerere;
    }

    public void setDataCerere(LocalDateTime dataCerere) {
        this.dataCerere = dataCerere;
    }

}
