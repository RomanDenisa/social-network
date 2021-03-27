package socialnetwork.service;
import socialnetwork.domain.*;
import socialnetwork.repository.Repository;
import socialnetwork.service.exceptions.DuplicatedIDException;
import socialnetwork.service.exceptions.EntityNotFoundException;
import socialnetwork.utils.events.FriendReqChangeEvent;
import socialnetwork.utils.events.FriendRequestsChangeEvent;
import socialnetwork.utils.events.FriendsChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CerereService implements Observable<FriendReqChangeEvent> {

    private List<Observer<FriendReqChangeEvent>> observers=new ArrayList<>();
    private Repository<Tuple<Long,Long>, CererePrietenie> repoCerere;
    private Repository<Long, Utilizator> repoUtilizator;
    private Repository<Tuple<Long,Long>,Prietenie> repoPrietenii;

    public CerereService(Repository<Tuple<Long, Long>, CererePrietenie> repoCerere, Repository<Long, Utilizator> repoUtilizator
    ,Repository<Tuple<Long,Long>,Prietenie> repoPrietenii) {
        this.repoCerere = repoCerere;
        this.repoUtilizator = repoUtilizator;
        this.repoPrietenii = repoPrietenii;
    }

    public void save(Long id1, Long id2) throws EntityNotFoundException, DuplicatedIDException {
        Utilizator user1 = repoUtilizator.findOne(id1);
        if(user1==null)
            throw new EntityNotFoundException("user1 with specified ID doesn't exist");
        Utilizator user2 = repoUtilizator.findOne(id2);
        if(user2==null)
            throw new EntityNotFoundException("user2 with specified ID doesn't exist");
        Prietenie pr1 = repoPrietenii.findOne(new Tuple<>(id1,id2));
        if(pr1 !=null)
            throw new DuplicatedIDException("this friendship already exists!");
        CererePrietenie cererePrietenie1 = repoCerere.findOne(new Tuple<>(id1,id2));
        if(cererePrietenie1 != null && cererePrietenie1.getStatus()==StatusEnum.REJECTED
        || cererePrietenie1 != null && cererePrietenie1.getStatus()==StatusEnum.APPROVED) //not sure
        {
            cererePrietenie1.setStatus(StatusEnum.PENDING);
            repoCerere.update(cererePrietenie1);
            notifyObservers(new FriendReqChangeEvent());
        }
        else if(cererePrietenie1 != null && cererePrietenie1.getStatus()==StatusEnum.PENDING)//!=?
            throw new DuplicatedIDException("friend request between these 2 already exists");
        else {
        CererePrietenie cererePrietenie = new CererePrietenie();
        cererePrietenie.setId(new Tuple<>(id1,id2));
        cererePrietenie.setDataCerere(LocalDateTime.now());
        repoCerere.save(cererePrietenie);
        notifyObservers(new FriendReqChangeEvent());
        }
    }

    public void approve(Long id1, Long id2) throws EntityNotFoundException {
        Utilizator user1 = repoUtilizator.findOne(id1);
        if(user1==null)
            throw new EntityNotFoundException("user1 with specified ID doesn't exist");
        Utilizator user2 = repoUtilizator.findOne(id2);
        if(user2==null)
            throw new EntityNotFoundException("user2 with specified ID doesn't exist");
        CererePrietenie cererePrietenie = repoCerere.findOne(new Tuple<>(id2,id1));
        if(cererePrietenie == null)
            throw new EntityNotFoundException("friend request between these 2 doesn't exist");
        if(!cererePrietenie.getId().getLeft().equals(id2))
            throw new EntityNotFoundException("friend request from user with id1 doesn't exist");
        if(cererePrietenie.getStatus()!=StatusEnum.PENDING)
        {
            throw new EntityNotFoundException("friend request already rejected/accepted");
        }
        if(cererePrietenie.getStatus()==StatusEnum.PENDING)
        {
            cererePrietenie.setStatus(StatusEnum.APPROVED);
            repoCerere.update(cererePrietenie);
            Prietenie pr = new Prietenie();
            pr.setDate(LocalDateTime.now());
            pr.setId(new Tuple<>(id1,id2));
            repoPrietenii.save(pr);
            notifyObservers(new FriendReqChangeEvent());
        }
    }

    public void reject(Long id1, Long id2) throws EntityNotFoundException {
        Utilizator user1 = repoUtilizator.findOne(id1);
        if(user1==null)
            throw new EntityNotFoundException("user1 with specified ID doesn't exist");
        Utilizator user2 = repoUtilizator.findOne(id2);
        if(user2==null)
            throw new EntityNotFoundException("user2 with specified ID doesn't exist");
        CererePrietenie cererePrietenie = repoCerere.findOne(new Tuple<>(id2,id1));
        if(cererePrietenie == null)
            throw new EntityNotFoundException("friend request between these 2 doesn't exist");
        if(!cererePrietenie.getId().getLeft().equals(id2))
            throw new EntityNotFoundException("friend request from user with id1 doesn't exist");
        if(cererePrietenie.getStatus()!=StatusEnum.PENDING)
        {
            throw new EntityNotFoundException("friend request already rejected/accepted");
        }
        if(cererePrietenie.getStatus()==StatusEnum.PENDING)
        {
            cererePrietenie.setStatus(StatusEnum.REJECTED);
            repoCerere.update(cererePrietenie);
            notifyObservers(new FriendReqChangeEvent());
        }
    }

    public Iterable<CererePrietenie> getAll()
    {
        return repoCerere.findAll();
    }

    public List<CerereDTO> getCereriSentForUser(Long id) throws EntityNotFoundException {
        Iterable<CererePrietenie> cereri = repoCerere.findAll();
        Utilizator user = repoUtilizator.findOne(id);
        if(user == null)
            throw new EntityNotFoundException("the user with the specified id doesn't exist");
        return StreamSupport.stream(cereri.spliterator(),false)
                .filter(x->x.getId().getLeft().equals(user.getId()))
                .map(x->
                {
                    Utilizator utilizator = repoUtilizator.findOne(x.getId().getRight());
                    return new CerereDTO(x.getId().getRight(),utilizator.getFirstName(), utilizator.getLastName(), x.getStatus(),x.getDataCerere());
                })
                .collect(Collectors.toList());
    }

    public List<CerereDTO> getCereriReceivedForUser(Long id) throws EntityNotFoundException {
        Iterable<CererePrietenie> cereri = repoCerere.findAll();
        Utilizator user = repoUtilizator.findOne(id);
        if(user == null)
            throw new EntityNotFoundException("the user with the specified id doesn't exist");
        return StreamSupport.stream(cereri.spliterator(),false)
                .filter(x->x.getId().getRight().equals(user.getId()))
                .map(x->
                {
                    Utilizator utilizator = repoUtilizator.findOne(x.getId().getLeft());
                    return new CerereDTO(x.getId().getLeft(),utilizator.getFirstName(), utilizator.getLastName(), x.getStatus(),x.getDataCerere());
                })
                .collect(Collectors.toList());
    }

    public void deleteRequest(Long id1, Long id2) throws EntityNotFoundException {
        Utilizator user1 = repoUtilizator.findOne(id1);
        if(user1==null)
            throw new EntityNotFoundException("user1 with specified ID doesn't exist");
        Utilizator user2 = repoUtilizator.findOne(id2);
        if(user2==null)
            throw new EntityNotFoundException("user2 with specified ID doesn't exist");
        CererePrietenie cererePrietenie = repoCerere.findOne(new Tuple<>(id1,id2));
        if(cererePrietenie == null)
            throw new EntityNotFoundException("friend request between these 2 doesn't exist");
        if(cererePrietenie.getStatus()!=StatusEnum.PENDING)
            throw new EntityNotFoundException("You can't delete a request that has already been approved/rejected!");
        repoCerere.delete(new Tuple<>(id1,id2));
        notifyObservers(new FriendReqChangeEvent());
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
