package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.Message;
import socialnetwork.domain.MessageDTO;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.UtilizatorDTO;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.MesajeService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.service.exceptions.EntityNotFoundException;
import socialnetwork.utils.events.MessagesEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessagesController implements Observer<MessagesEvent> {

    public UtilizatorService utilizatorService;
    public MesajeService mesajeService;
    Stage stageDialog;
    Long id;

    ObservableList<MessageDTO> model = FXCollections.observableArrayList();
    List<Long> listTo = new ArrayList<>();

    @FXML
    TableView<MessageDTO> tableViewMessages;

    @FXML
    TableColumn<MessageDTO,String> tableColumnFrom;

    @FXML
    TableColumn<MessageDTO,String> tableColumnMessage;

    @FXML
    TableColumn<MessageDTO,String> tableColumnDate;

    @FXML
    TextField textFieldFrom;

    @FXML
    TextField textFieldTo;

    @FXML
    TextArea textAreaMessage;

    @FXML
    TextArea textAreaReply;

    @FXML
    ComboBox<Utilizator> comboBoxUsers;

    public void setService(UtilizatorService utilizatorService, MesajeService mesajeService, Stage dialogStage, Long id) {
        this.utilizatorService = utilizatorService;
        this.mesajeService = mesajeService;
        this.stageDialog= dialogStage;
        this.id=id;
        mesajeService.addObserver(this);
        initModel();
    }

    public void initialize() {
        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("NumePrenume"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("message"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("DataString"));
        tableViewMessages.setItems(model);
        tableViewMessages.getSelectionModel().selectedItemProperty().addListener(e->handleSelection());
    }

    private void initModel(){

        List<MessageDTO> msgList = null;
        msgList = mesajeService.findReceivedMessages2(id);
        model.setAll(msgList);
        textFieldFrom.setText("You");
        comboBoxUsers.setPromptText("Choose recipients");
        Iterable<Utilizator> users = utilizatorService.getAll();
        ObservableList<Utilizator> users2 = FXCollections.observableArrayList(
                StreamSupport.stream(users.spliterator(),false)
                        .filter(x->!x.getId().equals(this.id))
                .collect(Collectors.toList()));
        comboBoxUsers.setItems(users2);
    }


    public void handleSendMessage(ActionEvent actionEvent) {

        try {
            mesajeService.addMessage(id,listTo,textAreaReply.getText());
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Success","Message sent successfully!");
        } catch (EntityNotFoundException | ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        comboBoxUsers.getSelectionModel().clearSelection();
        textFieldTo.setText("");
        textAreaReply.setText("");
        listTo.clear();
    }

    public void handleSelectedUser(ActionEvent actionEvent) {
        Utilizator user = comboBoxUsers.getSelectionModel().getSelectedItem();
        if(user!=null)
        {listTo.add(user.getId());
        textFieldTo.appendText(user.toString() + ",");
        System.out.println(listTo);}

    }

    private void handleSelection()
    {
        MessageDTO messageDTO = tableViewMessages.getSelectionModel().getSelectedItem();
        if(messageDTO != null)
        {
            textFieldFrom.setText(messageDTO.getNumePrenume());
            String to = "";
            for (Long id : messageDTO.getTo()) {
                if(id.equals(this.id))
                    to += "you,";
                else {
                    try {
                        Utilizator user = utilizatorService.findOne(id);
                        to += user.toString() + ",";
                    } catch (EntityNotFoundException e) {
                        MessageAlert.showErrorMessage(null, e.getMessage());
                    }
                }
            }
            textFieldTo.setText(to);

            textAreaMessage.setText("");
            if(messageDTO.getReplyTo()!=null)
            {
                Message message = mesajeService.findOne(messageDTO.getReplyTo());
                if(message.getId().equals(id))
                    textAreaMessage.setText("You"+" "+
                            "said at " +message.getDataString()+"\n"+"--------->"+message.getMessage());
                else
                    textAreaMessage.setText(message.getFrom().getFirstName()+" "+message.getFrom().getLastName()+" "+
                            "said at " +message.getDataString()+"\n"+"--------->"+message.getMessage());
                textAreaMessage.appendText("\n");
            }
            textAreaMessage.appendText(messageDTO.getNumePrenume()+" "+"said at " + messageDTO.getDataString()
                            +"\n" + "--------->" +messageDTO.getMessage());
        }

    }

    public void handleReply(ActionEvent actionEvent) {

        MessageDTO messageDTO = tableViewMessages.getSelectionModel().getSelectedItem();
        if(messageDTO == null)
            MessageAlert.showWarningMessage(null,"No message selected!");
        else
        {
            List<Long> toList = new ArrayList<>();
            for (Long idd: messageDTO.getTo())
            {
                if(!idd.equals(id))
                    toList.add(idd);
            }
            toList.add(messageDTO.getIdFrom());
            showReplyToOne(toList,textAreaReply.getText(),messageDTO.getId());
            textAreaReply.setText("");
        }
    }

    public void handleReplyAll(ActionEvent actionEvent) {

        MessageDTO messageDTO = tableViewMessages.getSelectionModel().getSelectedItem();
        if(messageDTO == null)
            MessageAlert.showWarningMessage(null,"No message selected!");
        else
        {
            if(textAreaReply.getText().isEmpty())
                MessageAlert.showWarningMessage(null,"No reply entered!");
            else
            {
                try {
                mesajeService.replyAll(id,textAreaReply.getText(),messageDTO.getId());
                clearFields();
                textFieldFrom.setText("You");
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Success","Reply sent!");
            } catch (EntityNotFoundException e) {
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
            }
        }
    }

    private void clearFields()
    {

        textFieldTo.setText("");
        textFieldFrom.setText("");
        textAreaMessage.setText("");
        textAreaReply.setText("");
    }

    public void showReplyToOne(List<Long> to, String message, Long messageId)
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/replyToOneView.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Reply to");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ReplyToOneController replyToOneController = loader.getController();
            replyToOneController.setService(utilizatorService,mesajeService,dialogStage,id,to,message,messageId);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleNewMessage(ActionEvent actionEvent) {
        clearFields();
        textFieldFrom.setText("You");
        listTo.clear();
    }

    @Override
    public void update(MessagesEvent messagesEvent) {
        initModel();
    }
}
