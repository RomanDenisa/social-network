package socialnetwork.repository.database;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CerereDbRepository implements Repository<Tuple<Long,Long>,CererePrietenie> {

    private String url;
    private String username;
    private String password;
    private Validator<CererePrietenie> validator;

    public CerereDbRepository(String url, String username, String password, Validator<CererePrietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public CererePrietenie findOne(Tuple<Long, Long> id) {
       CererePrietenie cererePrietenie = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from cereriprietenie WHERE userid1 = ? AND userid2 = ?"))
             {
                 statement.setLong(1,id.getLeft());
                 statement.setLong(2,id.getRight());
                 ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long user1id = resultSet.getLong("userid1");
                Long user2id = resultSet.getLong("userid2");
                LocalDateTime dataPrietenie = resultSet.getTimestamp("data").toLocalDateTime();
                String status = resultSet.getString("status");
                cererePrietenie=new CererePrietenie();
                cererePrietenie.setDataCerere(dataPrietenie);
                cererePrietenie.setStatus(StatusEnum.valueOf(status));
                cererePrietenie.setId(id);
            }
            return cererePrietenie;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cererePrietenie;
    }

    @Override
    public Iterable<CererePrietenie> findAll() {
        Set<CererePrietenie> cereri = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from cereriprietenie");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("userid1");
                Long id2 = resultSet.getLong("userid2");
                String status = resultSet.getString("status");
                LocalDateTime dataCerere = resultSet.getTimestamp("data").toLocalDateTime();

                CererePrietenie cererePrietenie = new CererePrietenie();
                cererePrietenie.setId(new Tuple<>(id1,id2));
                cererePrietenie.setStatus(StatusEnum.valueOf(status));
                cererePrietenie.setDataCerere(dataCerere);
                cereri.add(cererePrietenie);
            }
            return cereri;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cereri;
    }

    @Override
    public CererePrietenie save(CererePrietenie entity) {
        CererePrietenie cererePrietenie = entity;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO cereriprietenie(userid1,userid2,data) VALUES(?,?,?)" );
             PreparedStatement findStatement = connection.prepareStatement("SELECT * FROM cereriprietenie WHERE userid1 =? AND userid2 = ?"))
        {
            validator.validate(entity);
            findStatement.setLong(1,entity.getId().getLeft());
            findStatement.setLong(2,entity.getId().getRight());
            ResultSet res=findStatement.executeQuery();
            if(res.next())
            {
                return cererePrietenie;
            }
            statement.setLong(1,entity.getId().getLeft());
            statement.setLong(2,entity.getId().getRight());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDataCerere()));
            int nrAffectedRows = statement.executeUpdate();
            if(nrAffectedRows>0)
                cererePrietenie=null;
            return cererePrietenie;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQLError");
        }
        return cererePrietenie;
    }

    @Override
    public CererePrietenie delete(Tuple<Long, Long> id) {
        CererePrietenie cererePrietenie = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement findStatement = connection.prepareStatement("SELECT * FROM cereriprietenie WHERE userid1 =? AND userid2 =?");
             PreparedStatement statement = connection.prepareStatement("DELETE FROM cereriprietenie WHERE userid1=? AND userid2=?"))
             {
                 findStatement.setLong(1,id.getLeft());
                 findStatement.setLong(2,id.getRight());
                 ResultSet resultSet = findStatement.executeQuery();
                 if(resultSet.next()) {
                Long userid1 = resultSet.getLong("userid1");
                Long userid2 = resultSet.getLong("userid2");
                String status = resultSet.getString("status");
                LocalDateTime dataCerere = resultSet.getTimestamp("data").toLocalDateTime();
                cererePrietenie=new CererePrietenie();
                cererePrietenie.setId(new Tuple<>(userid1,userid2));
                cererePrietenie.setStatus(StatusEnum.valueOf(status));
                cererePrietenie.setDataCerere(dataCerere);
            }
            statement.setLong(1,id.getLeft());
                 statement.setLong(2,id.getRight());
            int nrRowsAffected = statement.executeUpdate();
            if(nrRowsAffected==0)
                cererePrietenie=null;
            return cererePrietenie;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cererePrietenie;
    }

    @Override
    public CererePrietenie update(CererePrietenie entity) {
        CererePrietenie cererePrietenie = entity;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE cereriprietenie SET status = ? WHERE userid1= ? AND userid2 = ? " );
             PreparedStatement findStatement = connection.prepareStatement("SELECT * FROM cereriprietenie WHERE userid1 =? AND userid2 = ?"))
        {
            validator.validate(entity);
            findStatement.setLong(1,entity.getId().getLeft());
            findStatement.setLong(2,entity.getId().getRight());
            ResultSet res=findStatement.executeQuery();
            if(!res.next())
            {
                return cererePrietenie;
            }
            statement.setString(1,entity.getStatus().toString());
            statement.setLong(2,entity.getId().getLeft());
            statement.setLong(3,entity.getId().getRight());
            int nrAffectedRows = statement.executeUpdate();
            if(nrAffectedRows>0)
                cererePrietenie=null;
            return cererePrietenie;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQLError");
        }
        return cererePrietenie;
    }
}
