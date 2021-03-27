package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import socialnetwork.domain.CererePrietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.CerereService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.service.exceptions.DuplicatedIDException;
import socialnetwork.service.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
public class AddFriendController {

    UtilizatorService service;
    CerereService cerereService;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    Stage dialogStage;
    Long id;

    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator,String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator,String> tableColumnLastName;
    @FXML
    TextField textFieldName;

    public void setService(UtilizatorService utilizatorService,CerereService cerereService, Stage stage, Long id) {
        this.service = utilizatorService;
        this.cerereService = cerereService;
        this.id=id;
        this.dialogStage = stage;
        //service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableView.setItems(model);
    }

    private void initModel() {
        Iterable<Utilizator> users = service.getAll();
        List<Utilizator> userList = StreamSupport.stream(users.spliterator(), false)
                .filter(x->!x.getId().equals(id))
                .collect(Collectors.toList());
        model.setAll(userList);
    }

    public void handleSendRequest(ActionEvent actionEvent) {
        Utilizator utilizator = tableView.getSelectionModel().getSelectedItem();
        if(utilizator== null)
            MessageAlert.showWarningMessage(null, "No user selected!");
        else
        {
            try {
                cerereService.save(id, utilizator.getId());
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Success","You sent a friend request to "+ utilizator.getFirstName());
            } catch (EntityNotFoundException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            } catch (DuplicatedIDException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }
    }

    public void handleFilterName(KeyEvent keyEvent) {

        Predicate<Utilizator> byName = x->x.getFirstName().startsWith(textFieldName.getText())
                || x.getLastName().startsWith(textFieldName.getText());
        model.setAll(StreamSupport.stream(service.getAll().spliterator(),false)
        .filter(byName)
        .collect(Collectors.toList()));
    }
}
