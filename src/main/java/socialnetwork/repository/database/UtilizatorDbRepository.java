package socialnetwork.repository.database;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UtilizatorDbRepository implements Repository<Long, Utilizator> {
    private String url;
    private String username;
    private String password;
    private Validator<Utilizator> validator;

    public UtilizatorDbRepository(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }
    @Override
    public Utilizator findOne(Long id) {
        Utilizator utilizator = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from utilizatori WHERE userid = ?");
             PreparedStatement statement2 = connection.prepareStatement("SELECT * from prietenii WHERE user1id = ? OR user2id = ?"))
              {


            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long user_id = resultSet.getLong("userid");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(user_id);
            }
            return utilizator;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilizator;
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from utilizatori");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("userid");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");

                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(id);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Utilizator save(Utilizator entity) {
        Utilizator user = entity;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO utilizatori(userID,firstName,lastName) VALUES(?,?,?)");)
             {
             validator.validate(entity);
             statement.setLong(1,entity.getId());
             statement.setString(2,entity.getFirstName());
             statement.setString(3,entity.getLastName());

             int numberOfInsertedRows = statement.executeUpdate();
             if(numberOfInsertedRows>0)
                user=null;

            return user;
        }
        catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("SQLError");
        }
        return user;
    }

    @Override
    public Utilizator delete(Long id) {
        Utilizator utilizator = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement findStatement = connection.prepareStatement("SELECT * FROM utilizatori WHERE userid = " + id);
             PreparedStatement deleteStatament = connection.prepareStatement("DELETE FROM utilizatori WHERE userid = " + id);
             ResultSet resultSet = findStatement.executeQuery();)
        {
            if (resultSet.next()) {
                Long user_id = resultSet.getLong("userid");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(user_id);
            }
            deleteStatament.executeUpdate();
            //deleteFriendships.execute();
            return utilizator;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQLException");
        }
        return utilizator;
    }

    @Override
    public Utilizator update(Utilizator entity) {
        return null;
    }
}
