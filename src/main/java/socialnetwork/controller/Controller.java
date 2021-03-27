package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.CererePrietenie;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.UtilizatorDTO;
import socialnetwork.service.CerereService;
import socialnetwork.service.MesajeService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.service.exceptions.EntityNotFoundException;
import socialnetwork.utils.events.FriendReqChangeEvent;
import socialnetwork.utils.events.FriendsChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class Controller implements Observer<FriendReqChangeEvent> {

    private CerereService cerereService;
    private PrietenieService prietenieService;
    private UtilizatorService utilizatorService;
    private MesajeService mesajeService;
    ObservableList<UtilizatorDTO> model = FXCollections.observableArrayList();

    private Long id;

    @FXML
    TableView<UtilizatorDTO> tableView;
    @FXML
    TableColumn<UtilizatorDTO,String> tableColumnFirstName;
    @FXML
    TableColumn<UtilizatorDTO,String> tableColumnLastName;
    @FXML
    TableColumn<UtilizatorDTO, String> tableColumnDate;

    @FXML
    TextField textFieldName;
    @FXML
    TextField textFieldLastName;

    public void setService(PrietenieService prietenieService, UtilizatorService utilizatorService, Long id, CerereService cerereService,
                           MesajeService mesajeService)  {
        this.utilizatorService=utilizatorService;
        this.prietenieService = prietenieService;
        this.cerereService = cerereService;
        this.id=id;
        this.mesajeService = mesajeService;
        prietenieService.addObserver(this);
        cerereService.addObserver(this);
        initModel();
    }

    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<UtilizatorDTO, String>("numePrieten"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<UtilizatorDTO, String>("prenumePrieten"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<UtilizatorDTO, String>("dataPrietenieString"));
        tableView.setItems(model);
    }

    private void initModel(){
        //Iterable<Prietenie> prietenii = prietenieService.getAll();
        List<UtilizatorDTO> frList = null;
        try {
            frList = utilizatorService.getPrieteniiForUser(id);
        } catch (EntityNotFoundException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        model.setAll(frList);
    }

    public void handleAddFriend(ActionEvent actionEvent) {
        showAddFriendDialog(null);
    }

    public void showAddFriendDialog(UtilizatorDTO utilizatorDTO) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/addFriendView.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Friend");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AddFriendController addFriendController = loader.getController();
            addFriendController.setService(utilizatorService,cerereService, dialogStage, id);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showFriendRequestsDialog()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/friendRequestsView.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friend Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendRequestsController friendRequestsController = loader.getController();
            friendRequestsController.setService(utilizatorService,cerereService, dialogStage, id);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleFriendRequests(ActionEvent actionEvent) {
        showFriendRequestsDialog();
    }

    public void handleRemoveFriend(ActionEvent actionEvent) {

        UtilizatorDTO utilizatorDTO = tableView.getSelectionModel().getSelectedItem();
        if(utilizatorDTO == null)
            MessageAlert.showErrorMessage(null,"No user selected!");
        else
        {
            try {
                prietenieService.deletePrietenie(id,utilizatorDTO.getIdPrieten());
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Success",
                        "You are no longer friends with " + utilizatorDTO.getNumePrieten());
            } catch (EntityNotFoundException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }

    }

    @Override
    public void update(FriendReqChangeEvent friendsChangeEvent) {
        initModel();
    }

    public void handleFilter(KeyEvent keyEvent) {

        List<UtilizatorDTO> frList = null;
        try {
            frList=utilizatorService.getPrieteniiForUser(id);
        } catch (EntityNotFoundException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }

        Predicate<UtilizatorDTO> byName = x->x.getNumePrieten().startsWith(textFieldName.getText());
        Predicate<UtilizatorDTO> byLastName = x->x.getPrenumePrieten().startsWith(textFieldLastName.getText());
        model.setAll(frList.stream().filter(byName.and(byLastName)).collect(Collectors.toList()));

    }

    public void handleMessages(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/messagesView.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Messages");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            MessagesController messagesController = loader.getController();
            messagesController.setService(utilizatorService,mesajeService ,dialogStage, id);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
