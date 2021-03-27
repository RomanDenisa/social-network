package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.ContService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.service.exceptions.DuplicatedIDException;

public class CreateAccountController {

    private UtilizatorService utilizatorService;
    private ContService contService;
    Stage dialogStage;

    @FXML
    TextField textFieldFirstName;

    @FXML
    TextField textFieldLastName;

    @FXML
    TextField textFieldUsername;

    @FXML
    TextField textFieldPassword;


    public void setService(UtilizatorService utilizatorService,ContService contService,Stage stage)
    {
        this.utilizatorService = utilizatorService;
        this.contService = contService;
        this.dialogStage = stage;
    }

    public void initialize() {
    }

    public void handleCreateAccount(ActionEvent actionEvent) {

        Long maxId = utilizatorService.getMaxID();
        try {
            contService.addCont(maxId+1,textFieldUsername.getText(),
                    textFieldPassword.getText(),textFieldFirstName.getText(),
                    textFieldLastName.getText());
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,
                    "Success!","Your account has been created!");
            dialogStage.close();
        } catch (DuplicatedIDException |ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }

    }
}
