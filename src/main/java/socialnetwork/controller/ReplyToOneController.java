package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.MesajeService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.service.exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
public class ReplyToOneController {

    private UtilizatorService utilizatorService;
    private MesajeService mesajeService;
    private Long idFrom;
    private Long idTo;
    private String message;
    private Long messageID;
    private Stage stageDialog;
    private List<Long> to;

    @FXML
    ComboBox<Utilizator> comboBoxUser;

    public void setService(UtilizatorService utilizatorService, MesajeService mesajeService, Stage dialogStage, Long idFrom
                          , List<Long> to, String message, Long messageId) {
        this.utilizatorService = utilizatorService;
        this.mesajeService = mesajeService;
        this.stageDialog= dialogStage;
        this.idFrom=idFrom;
        this.message = message;
        this.messageID = messageId;
        this.to = to;
        initmodel();
    }

    private void initmodel()
    {
        List<Utilizator> users = new ArrayList<>();
        for(Long id : to)
        {
            try {
                users.add(utilizatorService.findOne(id));
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }
        }
        ObservableList<Utilizator> users2 = FXCollections.observableArrayList(users);
        comboBoxUser.setItems(users2);

    }

    public void handleChange(ActionEvent actionEvent) {
        Utilizator user = comboBoxUser.getSelectionModel().getSelectedItem();
        this.idTo = user.getId();
    }

    public void handleOkButton(ActionEvent actionEvent) {
        try {
            mesajeService.replyToOne(idFrom,idTo,message,messageID);
            stageDialog.close();
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Success","Reply sent!");
        } catch (EntityNotFoundException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
    }
}
