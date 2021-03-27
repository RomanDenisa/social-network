package socialnetwork.domain;
import socialnetwork.utils.Constants;

import java.time.LocalDateTime;

public class UtilizatorDTO {

    Long idPrieten;
    String numePrieten;
    String prenumePrieten;
    LocalDateTime dataPrietenie;

    public UtilizatorDTO(Long idPrieten, String numePrieten, String prenumePrieten, LocalDateTime dataPrietenie) {
        this.numePrieten = numePrieten;
        this.prenumePrieten = prenumePrieten;
        this.dataPrietenie = dataPrietenie;
        this.idPrieten=idPrieten;
    }

    public String getNumePrieten() {
        return numePrieten;
    }

    public void setNumePrieten(String numePrieten) {
        this.numePrieten = numePrieten;
    }

    public String getPrenumePrieten() {
        return prenumePrieten;
    }

    public void setPrenumePrieten(String prenumePrieten) {
        this.prenumePrieten = prenumePrieten;
    }

    public LocalDateTime getDataPrietenie() {
        return dataPrietenie;
    }

    public void setDataPrietenie(LocalDateTime dataPrietenie) {
        this.dataPrietenie = dataPrietenie;
    }

    public Long getIdPrieten() {
        return idPrieten;
    }

    @Override
    public String toString() {
        return numePrieten + " | " + prenumePrieten + " | " + dataPrietenie;
    }

    public String getDataPrietenieString()
    {
        return dataPrietenie.format(Constants.DATE_TIME_FORMATTER);
    }
}
