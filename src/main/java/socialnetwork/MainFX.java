package socialnetwork;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.Controller;
import socialnetwork.controller.LoginController;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.service.*;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        final String username= ApplicationContext.getPROPERTIES().getProperty("databse.socialnetwork.username");
        final String pasword= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.pasword");
        Repository<Long, Utilizator> userFileRepository3 =
                new UtilizatorDbRepository(url,username, pasword,  new UtilizatorValidator());
        Repository<Tuple<Long,Long>, Prietenie> prieteniiFileRepo3 = new PrietenieDbRepository(url,username,pasword,new PrietenieValidator());
        Repository<Tuple<Long,Long>, CererePrietenie> repoCerere = new CerereDbRepository(url,username,pasword,new CerereValidator());
        ContDbRepository contDbRepository = new ContDbRepository(url,username,pasword,new ContValidator());
        Repository<Long, Message> mesajeDbRepo = new MessageDbRepository(url,username,pasword,new MesajValidator());

        UtilizatorService utilizatorService = new UtilizatorService(userFileRepository3,prieteniiFileRepo3);
        PrietenieService prietenieService = new PrietenieService(prieteniiFileRepo3,userFileRepository3);
        CerereService cerereService = new CerereService(repoCerere,userFileRepository3,prieteniiFileRepo3);
        ContService contService = new ContService(contDbRepository,userFileRepository3);
        MesajeService mesajeService = new MesajeService(mesajeDbRepo,userFileRepository3);

        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/loginView.fxml"));
        AnchorPane root=loader.load();
        LoginController ctrl=loader.getController();
        ctrl.setService(prietenieService,utilizatorService,cerereService,contService,mesajeService,primaryStage);
        //ctrl.setService(new CerereService(repoCerere,userFileRepository3,prieteniiFileRepo3),new PrietenieService(prieteniiFileRepo3,userFileRepository3));

        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.setTitle("Welcome to our Social Network!");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
