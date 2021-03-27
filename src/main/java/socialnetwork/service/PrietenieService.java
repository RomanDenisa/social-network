package socialnetwork.service;
import socialnetwork.domain.*;
import socialnetwork.repository.Repository;
import socialnetwork.service.exceptions.DuplicatedIDException;
import socialnetwork.service.exceptions.EntityNotFoundException;
import socialnetwork.utils.events.FriendReqChangeEvent;
import socialnetwork.utils.events.FriendsChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PrietenieService implements Observable<FriendReqChangeEvent> {

    //private List<Observer<FriendsChangeEvent>> observers=new ArrayList<>();
    private List<Observer<FriendReqChangeEvent>> observers=new ArrayList<>();

    private Repository<Tuple<Long,Long>, Prietenie> repoPrietenie;
    private Repository<Long, Utilizator> repoUtilizator;

    public PrietenieService(Repository<Tuple<Long, Long>, Prietenie> repoPrietenie, Repository<Long, Utilizator> repoUtilizator) {
        this.repoPrietenie = repoPrietenie;
        this.repoUtilizator = repoUtilizator;
    }

    /**
     * creeaza o prietenie intre 2 utilizatori
     * @param id1-Long id ul primului user
     * @param id2-Long id ul celui de al doilea user
     * @throws EntityNotFoundException daca utilizatorii nu exista
     * @throws DuplicatedIDException daca exista deja aceasta prietenie
     */
    public void addPrietenie(Long id1, Long id2) throws EntityNotFoundException, DuplicatedIDException {

        Utilizator user1 = repoUtilizator.findOne(id1);
        if(user1==null)
            throw new EntityNotFoundException("user1 with specified ID doesn't exist");
        Utilizator user2 = repoUtilizator.findOne(id2);
        if(user2==null)
            throw new EntityNotFoundException("user2 with specified ID doesn't exist");
        Prietenie prietenie = new Prietenie();
        prietenie.setId(new Tuple(id1,id2));
        prietenie.setDate(LocalDateTime.now());
        Prietenie pr = repoPrietenie.save(prietenie);
        if(pr!=null)
            throw new DuplicatedIDException("this friendship already exists");
    }

    /**
     *
     * @return toate prieteniile
     */
    public Iterable<Prietenie> getAll(){
        return repoPrietenie.findAll();
    }

    /**
     *
     * @param id-ul prieteniei cautate
     * @return prietenia cautata
     * @throws EntityNotFoundException daca prietenia nu a fost gasita
     */
    public Prietenie findOne(Tuple<Long,Long> id) throws EntityNotFoundException {
        Prietenie pr = repoPrietenie.findOne(id);
        if(pr==null)
            throw new EntityNotFoundException("this friendship doesn't exist");
        return pr;
    }

    /**
     * sterge o prietenie din 2 utilizatori
     * @param id1-long
     * @param id2-long
     * @return
     * @throws EntityNotFoundException daca utilizatorii nu exista
     */
    public Prietenie deletePrietenie(Long id1, Long id2) throws EntityNotFoundException {

        Prietenie pr;
        pr = repoPrietenie.delete(new Tuple<>(id1,id2));
        if(pr==null)
        {
            //pr = repoPrietenie.delete(new Tuple(id2,id1));
            //if(pr==null)
                throw new EntityNotFoundException("friendship not found");
        }
        notifyObservers(new FriendReqChangeEvent());
        return pr;
    }

    public void updatePrietenie(Tuple<Long,Long> id, LocalDateTime time) throws EntityNotFoundException {
        Prietenie updatedFr = new Prietenie();
        updatedFr.setId(id);
        updatedFr.setDate(time);
        Prietenie oldFr = repoPrietenie.update(updatedFr);
        if(oldFr!=null)
            throw new EntityNotFoundException("friendship not found");
    }

    @Override
    public void addObserver(Observer<FriendReqChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendReqChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendReqChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
