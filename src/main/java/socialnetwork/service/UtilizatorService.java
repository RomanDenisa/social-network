package socialnetwork.service;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.service.exceptions.DuplicatedIDException;
import socialnetwork.service.exceptions.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilizatorService  {
    private Repository<Long, Utilizator> repo;
    private Repository<Tuple<Long,Long>, Prietenie> repoPrietenie;
    //private Validator<Utilizator> validator;

    /**
     * constructor
     * @param repo - utilizatorRepository ce contine toti utilizatorii
     * @param repoPrietenie - prietenieRepository ce contine toate relatiile de prietenie
     */
    public UtilizatorService(Repository<Long, Utilizator> repo,Repository<Tuple<Long,Long>, Prietenie> repoPrietenie) {
        this.repo = repo;
        this.repoPrietenie = repoPrietenie;
        //loadFrList();
    }

    /**
     * adauga un utilizator cu informatiile date
     * @param id-id-ul utilizatorului
     * @param firstName-numele de familie al utilizatorului
     * @param lastName- prenumele utilizatorului
     * @throws DuplicatedIDException daca exista deja acest utilizator
     */
    public void addUtilizator(Long id,String firstName, String lastName) throws DuplicatedIDException {
        Utilizator user = new Utilizator(firstName,lastName);
        user.setId(id);
        Utilizator userSaved = repo.save(user);
        if(userSaved!=null)
            throw new DuplicatedIDException("User with specified ID already exists");
    }

    /**
     * returneaza toti utilizatorii
     * @return o lista ce contine toti utilizatorii
     */
    public Iterable<Utilizator> getAll(){
        return repo.findAll();
    }

    /**
     * gaseste utilizatorul cu id-ul dat
     * @param id- id-ul utilizatorului
     * @return user - utilizatorul cautat
     * @throws EntityNotFoundException daca nu exista acest utilizator
     */
    public Utilizator findOne(Long id) throws EntityNotFoundException {
        Utilizator user = repo.findOne(id);
        if(user==null)
            throw new EntityNotFoundException("user with specified ID doesn't exist");
        return user;
    }

    /**
     * sterge un utilizator si prieteniile sale
     * @param id- id-ul utilizatorului care trebuie sters
     * @return user - utilizatorul sters
     * @throws EntityNotFoundException daca nu exista utilizatorul ce trebuie sters
     */
    public Utilizator deleteUtilizator(Long id) throws EntityNotFoundException {
        deleteFrForUser(id);
        Utilizator user = repo.delete(id);
        if(user == null)
            throw new EntityNotFoundException("user with specified id doesn't exist");
        //deleteFrForUser(id);
        return user;
    }

    public void updateUtilizator(Long id,String firstName, String lastName) throws EntityNotFoundException {
        Utilizator updatedUser = new Utilizator(firstName,lastName);
        updatedUser.setId(id);
        Utilizator oldUser = repo.update(updatedUser);
        if(oldUser!=null)
            throw new EntityNotFoundException("user with specified id doesn't exist");
    }

    /**
     * sterge toate relatiile de prietenie pentru un utilizator cu id-ul dat
     * @param id - id-ul utilizatorului
     */
    private void deleteFrForUser(Long id)
    {
        Iterable<Prietenie> lista = repoPrietenie.findAll();
        List<Prietenie> l = new ArrayList<>();
        lista.forEach(x->l.add(x));
        l.stream().forEach(x->{if(x.getId().getLeft().equals(id) || x.getId().getRight().equals(id))
            repoPrietenie.delete(x.getId());});
        //repoPrietenie.findAll().forEach();

    }
    //eventual fac aici loading ul la prietenii
    public List<UtilizatorDTO> getPrieteniiForUser(Long id) throws EntityNotFoundException {
        Utilizator user = repo.findOne(id);
        if(user == null)
            throw new EntityNotFoundException("the user with the specified id doesn't exist");
        user.setFriends(loadFrList(user));
        List<UtilizatorDTO> lista = user.getFriends().stream()
                .map(x-> {
                    LocalDateTime data = repoPrietenie.findOne(new Tuple<>(id,x.getId())).getDate();
                    return new UtilizatorDTO(x.getId(),x.getFirstName(),x.getLastName(),data);
                })
                .collect(Collectors.toList());
        return lista;
    }

    public List<UtilizatorDTO> getPrieteniiFiltered(Long id, int luna) throws EntityNotFoundException {
        Utilizator user = repo.findOne(id);
        if(user == null)
            throw new EntityNotFoundException("the user with the specified id doesn't exist");
        Predicate<UtilizatorDTO> byMonth = x->x.getDataPrietenie().getMonthValue() == luna;
        user.setFriends(loadFrList(user));
        List<UtilizatorDTO> lista = user.getFriends().stream()
                .map(x-> {
                    LocalDateTime data = repoPrietenie.findOne(new Tuple(id,x.getId())).getDate();
                    return new UtilizatorDTO(x.getId(),x.getFirstName(),x.getLastName(),data);
                })
                .filter(byMonth)
                .collect(Collectors.toList());
        return lista;
    }

    /**
     * incarca listele de prieteni pentru fiecare utilizator
     */
    private void loadFrList()
    {
        repoPrietenie.findAll().forEach(x->
        {
            Utilizator user1 = repo.findOne(x.getId().getLeft());
            Utilizator user2 = repo.findOne(x.getId().getRight());
            user1.addFriend(user2);
            user2.addFriend(user1);
            //System.out.println(user1.getFriends());
            //System.out.println(user2.getFriends());
        });
        System.out.println(repo.findOne(3L).getFriends());
    }

    private List<Utilizator> loadFrList(Utilizator user)
    {
        List<Utilizator> friends = new ArrayList<>();
        repoPrietenie.findAll().forEach(x->{
            if(x.getId().getRight().equals(user.getId()))
                friends.add(repo.findOne(x.getId().getLeft()));
            if (x.getId().getLeft().equals(user.getId()))
                friends.add(repo.findOne(x.getId().getRight()));
        });
        return friends;
    }

    public Long getMaxID()
    {
        Long max;
        Utilizator user = Collections.max(StreamSupport.stream(repo.findAll().spliterator(),false)
                .collect(Collectors.toList()), Comparator.comparing(x->x.getId()));
        System.out.println(user.getId());
        return user.getId();
    }

}
