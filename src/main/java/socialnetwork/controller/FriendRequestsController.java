package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.domain.CerereDTO;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.UtilizatorDTO;
import socialnetwork.service.CerereService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.service.exceptions.DuplicatedIDException;
import socialnetwork.service.exceptions.EntityNotFoundException;
import socialnetwork.utils.events.FriendReqChangeEvent;
import socialnetwork.utils.events.FriendRequestsChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsController implements Observer<FriendReqChangeEvent> {

    UtilizatorService service;
    CerereService cerereService;
    ObservableList<CerereDTO> modelSent = FXCollections.observableArrayList();
    ObservableList<CerereDTO> modelReceived = FXCollections.observableArrayList();
    Stage dialogStage;
    Long id;

    @FXML
    TableView<CerereDTO> tableViewSent;
    @FXML
    TableView<CerereDTO> tableViewReceived;

    @FXML
    TableColumn<CerereDTO, String> tableSentColumnFirstName;
    @FXML
    TableColumn<CerereDTO, String> tableSentColumnLastName;
    @FXML
    TableColumn<CerereDTO, String> tableSentColumnStatus;
    @FXML
    TableColumn<CerereDTO, String> tableSentColumnDate;

    @FXML
    TableColumn<CerereDTO, String> tableReceivedColumnFirstName;
    @FXML
    TableColumn<CerereDTO, String> tableReceivedColumnLastName;
    @FXML
    TableColumn<CerereDTO, String> tableReceivedColumnStatus;
    @FXML
    TableColumn<CerereDTO, String> tableReceivedColumnDate;

    public void setService(UtilizatorService utilizatorService, CerereService cerereService, Stage stage, Long id){
        this.service = utilizatorService;
        this.cerereService = cerereService;
        this.id = id;
        this.dialogStage = stage;
        cerereService.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        tableSentColumnFirstName.setCellValueFactory(new PropertyValueFactory<CerereDTO, String>("firstName"));
        tableSentColumnLastName.setCellValueFactory(new PropertyValueFactory<CerereDTO, String>("lastName"));
        tableSentColumnStatus.setCellValueFactory(new PropertyValueFactory<CerereDTO, String>("status"));
        tableSentColumnDate.setCellValueFactory(new PropertyValueFactory<CerereDTO, String>("dataString"));

        tableReceivedColumnFirstName.setCellValueFactory(new PropertyValueFactory<CerereDTO, String>("firstName"));
        tableReceivedColumnLastName.setCellValueFactory(new PropertyValueFactory<CerereDTO, String>("lastName"));
        tableReceivedColumnStatus.setCellValueFactory(new PropertyValueFactory<CerereDTO, String>("status"));
        tableReceivedColumnDate.setCellValueFactory(new PropertyValueFactory<CerereDTO, String>("dataString"));

        tableViewSent.setItems(modelSent);
        tableViewReceived.setItems(modelReceived);
    }

    private void initModel() {

        List<CerereDTO> cereriTrimise = null;
        try {
            cereriTrimise = cerereService.getCereriSentForUser(id);
        } catch (EntityNotFoundException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        List<CerereDTO> cereriPrimite = null;
        try {
            cereriPrimite = cerereService.getCereriReceivedForUser(id);
        } catch (EntityNotFoundException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        modelSent.setAll(cereriTrimise);
        modelReceived.setAll(cereriPrimite);

    }

    public void handleApproveRequest(ActionEvent actionEvent) {

        CerereDTO cerereDTO = tableViewReceived.getSelectionModel().getSelectedItem();
        if(cerereDTO == null)
            MessageAlert.showErrorMessage(null,"No friend request selected!");
        else {
            try {
                cerereService.approve(id, cerereDTO.getId());
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Success", "You are now friends with" + cerereDTO.getFirstName());
            } catch (EntityNotFoundException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }
    }

    public void handleRejectRequest(ActionEvent actionEvent) {

        CerereDTO cerereDTO = tableViewReceived.getSelectionModel().getSelectedItem();
        if(cerereDTO == null)
            MessageAlert.showErrorMessage(null,"No friend request selected!");
        else {
            try {
                cerereService.reject(id, cerereDTO.getId());
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Success", "You rejected " + cerereDTO.getFirstName() + "'s friend request");
            } catch (EntityNotFoundException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }
    }

    @Override
    public void update(FriendReqChangeEvent friendRequestsChangeEvent) {
        initModel();
    }

    public void handleCancelSendRequest(ActionEvent actionEvent) {
        CerereDTO cerereDTO = tableViewSent.getSelectionModel().getSelectedItem();
        if(cerereDTO == null)
            MessageAlert.showErrorMessage(null,"No friend request selected!");
        else {
            try {
                cerereService.deleteRequest(id, cerereDTO.getId());
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Success", "You cancelled the friend request to " + cerereDTO.getFirstName());
            } catch (EntityNotFoundException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }
    }
}