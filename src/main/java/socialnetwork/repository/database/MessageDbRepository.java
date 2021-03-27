package socialnetwork.repository.database;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageDbRepository implements Repository<Long, Message> {

    private String url;
    private String username;
    private String password;
    private Validator<Message> validator;

    public MessageDbRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Message findOne(Long id) {
        Message mesaj = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from mesaje WHERE mesajid = ?");
             PreparedStatement statementFindUser = connection.prepareStatement("SELECT * from utilizatori WHERE userid = ?");
             PreparedStatement statementLoadTo = connection.prepareStatement("SELECT toid from mesajtouser WHERE mesajid = ?"))
             {
                 statement.setLong(1,id);
                 ResultSet resultSet = statement.executeQuery();
                 if(resultSet.next()) {

                     Long user_id = resultSet.getLong("fromid");
                     String msg = resultSet.getString("mesaj");
                     LocalDateTime dataMesaj = resultSet.getTimestamp("datamesaj").toLocalDateTime();
                     Long reply_id = resultSet.getLong("replyid");
                     //Long mesaj_id = resultSet.getLong("mesajid");
                     //if(reply_id.equals(0))
                     statementFindUser.setLong(1,user_id);
                     ResultSet resultSet1 = statementFindUser.executeQuery();
                     Utilizator utilizator = null;
                     if(resultSet1.next())
                     {
                         Long userId = resultSet1.getLong("userid");
                         String firstName = resultSet1.getString("firstname");
                         String lastName = resultSet1.getString("lastname");
                         utilizator = new Utilizator(firstName, lastName);
                         utilizator.setId(userId);
                     }

                     statementLoadTo.setLong(1,id);
                     ResultSet resultSet2 = statementLoadTo.executeQuery();
                     List<Utilizator> toList = new ArrayList<>();
                     while(resultSet2.next())
                     {
                         Utilizator user = null;
                         Long userId = resultSet2.getLong("toid");
                         statementFindUser.setLong(1,userId);
                         ResultSet resultSet3 = statementFindUser.executeQuery();
                         resultSet3.next();
                         String firstName = resultSet3.getString("firstname");
                         String lastName = resultSet3.getString("lastname");
                         user = new Utilizator(firstName, lastName);
                         user.setId(userId);
                         toList.add(user);
                     }

                     mesaj = new Message(utilizator,toList,msg);
                     mesaj.setId(id);
                     mesaj.setData(dataMesaj);
                     if(reply_id == 0)
                        mesaj.setReplyTo(null);
                     else
                         mesaj.setReplyTo(reply_id);
                 }
            return mesaj;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mesaj;
    }

    //SELECT * from mesaje WHERE mesajid <> 0"
    @Override
    public Iterable<Message> findAll() {
        Set<Message> mesaje = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from mesaje");
             PreparedStatement statementFindUser = connection.prepareStatement("SELECT * from utilizatori WHERE userid = ?");
             PreparedStatement statementLoadTo = connection.prepareStatement("SELECT toid from mesajtouser WHERE mesajid = ?");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long mesaj_id = resultSet.getLong("mesajid");
                Long user_id = resultSet.getLong("fromid");
                String msg = resultSet.getString("mesaj");
                LocalDateTime dataMesaj = resultSet.getTimestamp("datamesaj").toLocalDateTime();
                Long reply_id = resultSet.getLong("replyid");

                statementFindUser.setLong(1,user_id);
                ResultSet resultSet1 = statementFindUser.executeQuery();
                Utilizator utilizator = null;
                if(resultSet1.next())
                {
                    Long userId = resultSet1.getLong("userid");
                    String firstName = resultSet1.getString("firstname");
                    String lastName = resultSet1.getString("lastname");
                    utilizator = new Utilizator(firstName, lastName);
                    utilizator.setId(userId);
                }

                statementLoadTo.setLong(1,mesaj_id);
                ResultSet resultSet2 = statementLoadTo.executeQuery();
                List<Utilizator> toList = new ArrayList<>();
                while(resultSet2.next())
                {
                    Utilizator user = null;
                    Long userId = resultSet2.getLong("toid");
                    statementFindUser.setLong(1,userId);
                    ResultSet resultSet3 = statementFindUser.executeQuery();
                    resultSet3.next();
                    String firstName = resultSet3.getString("firstname");
                    String lastName = resultSet3.getString("lastname");
                    user = new Utilizator(firstName, lastName);
                    user.setId(userId);
                    toList.add(user);
                }
                Message mesaj;
                mesaj = new Message(utilizator,toList,msg);
                mesaj.setId(mesaj_id);
                mesaj.setData(dataMesaj);
                if(reply_id == 0)
                    mesaj.setReplyTo(null);
                else
                    mesaj.setReplyTo(reply_id);
                mesaje.add(mesaj);
            }
            return mesaje;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mesaje;
    }

    @Override
    public Message save(Message entity) {
        Message mesaj = entity;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO mesaje(fromid,mesaj,dataMesaj,replyid) VALUES(?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
             PreparedStatement statement1 = connection.prepareStatement("INSERT INTO mesajToUser(mesajid,toid) VALUES(?,?)"))
        {
            validator.validate(entity);
            statement.setLong(1,entity.getFrom().getId());
            statement.setString(2,entity.getMessage());
            statement.setTimestamp(3,Timestamp.valueOf(entity.getData()));
            if(entity.getReplyTo()!=null)
                statement.setLong(4,entity.getReplyTo());
            else
                statement.setNull(4,Types.NULL);
            int numberOfInsertedRows = statement.executeUpdate();
            if(numberOfInsertedRows>0) {
                ResultSet rs = statement.getGeneratedKeys();
                Long id_mesaj;
                rs.next();
                id_mesaj = rs.getLong(1);
                mesaj = null;
                for(Utilizator us:entity.getTo())
                {
                    statement1.setLong(1,id_mesaj);
                    statement1.setLong(2,us.getId());
                    statement1.executeUpdate();
                }
            }
            return mesaj;
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQLError");
        }
        return mesaj;
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }

}
