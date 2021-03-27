package socialnetwork.repository.database;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class PrietenieDbRepository implements Repository<Tuple<Long,Long>, Prietenie> {

    private String url;
    private String username;
    private String password;
    private Validator<Prietenie> validator;

    public PrietenieDbRepository(String url, String username, String password, Validator<Prietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Prietenie findOne(Tuple<Long, Long> id) {
        Prietenie prietenie = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from prietenii WHERE user1id = " + id.getLeft() +
                     "AND user2ID = " + id.getRight() + "OR user1id = "+ id.getRight() + "AND user2id = "+id.getLeft() );
             ResultSet resultSet = statement.executeQuery()) {

            if(resultSet.next()) {
                Long user1id = resultSet.getLong("user1id");
                Long user2id = resultSet.getLong("user2id");
                LocalDateTime dataPrietenie = resultSet.getTimestamp("dataprietenie").toLocalDateTime();
                prietenie=new Prietenie();
                prietenie.setId(new Tuple(user1id,user2id));
                prietenie.setDate(dataPrietenie);
            }
            return prietenie;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prietenie;
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> prietenii = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from prietenii");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("user1id");
                Long id2 = resultSet.getLong("user2id");
                LocalDateTime dataPrietenie = resultSet.getTimestamp("dataprietenie").toLocalDateTime();

                Prietenie prietenie=new Prietenie();
                prietenie.setId(new Tuple(id1,id2));
                prietenie.setDate(dataPrietenie);

                prietenii.add(prietenie);
            }
            return prietenii;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prietenii;
    }

    @Override
    public Prietenie save(Prietenie entity) {
        Prietenie prietenie = entity;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO prietenii(user1id,user2id,dataprietenie) VALUES(?,?,?)" );
             PreparedStatement findStatement = connection.prepareStatement("SELECT * FROM prietenii WHERE user1id = " + entity.getId().getRight()+ "AND user2id = " +entity.getId().getLeft() +
                     "OR user1id = " + entity.getId().getLeft()+ "AND user2id = " +entity.getId().getRight()))
             {
            validator.validate(entity);
            ResultSet res=findStatement.executeQuery();
            if(res.next())
            {
                return prietenie;
            }
            statement.setLong(1,entity.getId().getLeft());
            statement.setLong(2,entity.getId().getRight());
            statement.setTimestamp(3,Timestamp.valueOf(entity.getDate()));
            int nrAffectedRows = statement.executeUpdate();
            if(nrAffectedRows>0)
                prietenie=null;
            return prietenie;
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("SQLError");
        }
        return prietenie;
    }

    @Override
    public Prietenie delete(Tuple<Long, Long> id) {
        Prietenie prietenie = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement findStatement = connection.prepareStatement("SELECT * FROM prietenii WHERE user1id =" + id.getLeft() + "AND user2id = "+ id.getRight() + "OR user1id =" + id.getRight() + "AND user2id = "+ id.getLeft() );
             PreparedStatement statement = connection.prepareStatement("DELETE FROM prietenii WHERE user1id =" + id.getLeft() + "AND user2id = "+ id.getRight() +
                     "OR user1id = "+ id.getRight() + "AND user2id="+id.getLeft());
             ResultSet resultSet = findStatement.executeQuery()) {

            if(resultSet.next()) {
                Long user1id = resultSet.getLong("user1id");
                Long user2id = resultSet.getLong("user2id");
                LocalDateTime dataPrietenie = resultSet.getTimestamp("dataprietenie").toLocalDateTime();
                prietenie=new Prietenie();
                prietenie.setId(new Tuple(user1id,user2id));
                prietenie.setDate(dataPrietenie);
            }
            int nrRowsAffected = statement.executeUpdate();
            if(nrRowsAffected==0)
                prietenie=null;
            return prietenie;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prietenie;
    }

    @Override
    public Prietenie update(Prietenie entity) {
        return null;
    }
}
