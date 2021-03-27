package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.Cont;
import socialnetwork.domain.UtilizatorDTO;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.*;
import socialnetwork.service.exceptions.EntityNotFoundException;

import java.io.IOException;

public class LoginController {

    private ContService contService;
    private CerereService cerereService;
    private PrietenieService prietenieService;
    private UtilizatorService utilizatorService;
    private MesajeService mesajeService;
    private Long id;

    @FXML
    TextField textFieldUsername;

    @FXML
    PasswordField textFieldPassword;

    Stage primaryStage;

    public void setService(PrietenieService prietenieService,UtilizatorService utilizatorService,CerereService cerereService
    ,ContService contService,MesajeService mesajeService,Stage primaryStage){
        this.utilizatorService=utilizatorService;
        this.prietenieService = prietenieService;
        this.cerereService = cerereService;
        this.contService = contService;
        this.primaryStage=  primaryStage;
        this.mesajeService = mesajeService;
    }

    public void initialize() {
    }

    public void handleLogin(ActionEvent actionEvent) {
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();
        try {
            Cont cont = contService.findOne(username,password);
            showMainView(cont.getId());
            //primaryStage.close();
        } catch (EntityNotFoundException e) {
            MessageAlert.showWarningMessage(null,e.getMessage());
        }
    }

    public void showMainView(Long id)
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/view.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Welcome to your Social Network, " + textFieldUsername.getText() + "!");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            Controller controller = loader.getController();
            controller.setService(prietenieService,utilizatorService,id,cerereService,mesajeService);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleCreateAccount(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/createAccountView.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Welcome to your Social Network!");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            CreateAccountController controller = loader.getController();
            controller.setService(utilizatorService,contService,dialogStage);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
