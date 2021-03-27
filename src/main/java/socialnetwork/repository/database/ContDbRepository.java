package socialnetwork.repository.database;

import socialnetwork.domain.Cont;
import socialnetwork.domain.validators.Validator;

import java.sql.*;


public class ContDbRepository {

    private String url;
    private String username;
    private String password;
    private Validator<Cont> validator;

    public ContDbRepository(String url, String username, String password, Validator<Cont> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    public Cont findOne(String user, String pass) {
        Cont cont = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from conturi WHERE username =? AND password =?"))
        {
            statement.setString(1,user);
            statement.setString(2,pass);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long userid = resultSet.getLong("userid");
                String usernameRes = resultSet.getString("username");
                String passwordRes = resultSet.getString("password");
                cont =new Cont(passwordRes,usernameRes);
                cont.setId(userid);
            }
            return cont;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cont;
    }

    public Cont save(Cont entity) {
        Cont cont = entity;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO conturi(userid,username,password) VALUES(?,?,?)");)
        {
            validator.validate(entity);

            statement.setLong(1,entity.getId());
            statement.setString(2,entity.getUsername());
            statement.setString(3,entity.getPassword());

            int numberOfInsertedRows = statement.executeUpdate();
            if(numberOfInsertedRows>0)
                cont=null;

            return cont;
        }
        catch (SQLException e) {
            e.printStackTrace();
            //System.out.println("SQLError");
        }
        return cont;
    }

    public Cont findOneUsername(String user) {
        Cont cont = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from conturi WHERE username=?"))
        {
            statement.setString(1,user);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long userid = resultSet.getLong("userid");
                String usernameRes = resultSet.getString("username");
                String passwordRes = resultSet.getString("password");
                cont =new Cont(passwordRes,usernameRes);
                cont.setId(userid);
            }
            return cont;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cont;
    }
}
