package socialnetwork.service;
import socialnetwork.domain.Message;
import socialnetwork.domain.MessageDTO;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.MesajValidator;
import socialnetwork.repository.Repository;
import socialnetwork.service.exceptions.EntityNotFoundException;
import socialnetwork.utils.events.FriendReqChangeEvent;
import socialnetwork.utils.events.MessagesEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MesajeService implements Observable<MessagesEvent> {

    private List<Observer<MessagesEvent>> observers=new ArrayList<>();
    private Repository<Long, Message> repoMesaje;
    private Repository<Long, Utilizator> repoUtilizator;

    public MesajeService(Repository<Long, Message> repoMesaje,Repository<Long,Utilizator> repoUtilizator) {
        this.repoMesaje = repoMesaje;
        this.repoUtilizator = repoUtilizator;
    }

    public void addMessage(Long idFrom, List<Long> idTo, String message) throws EntityNotFoundException{
        Utilizator user = repoUtilizator.findOne(idFrom);
        if(user == null)
            throw new EntityNotFoundException("User who wants to send the message not found");
        List<Utilizator> toUsers = verifyToList(idTo);
        Message message1 = new Message(user,toUsers,message);
        message1.setData(LocalDateTime.now());
        Message message2 = repoMesaje.save(message1);
        notifyObservers(new MessagesEvent());
    }

    public void replyAll(Long idFrom, String message, Long messageID) throws EntityNotFoundException{
        Utilizator userFrom = repoUtilizator.findOne(idFrom);
        if(userFrom == null)
            throw new EntityNotFoundException("User who wants to send the message not found");
        Message msg = repoMesaje.findOne(messageID);
        if(msg == null)
            throw new EntityNotFoundException("Message with this id doesn't exist");
        if(!msg.getTo().contains(userFrom))
            throw new EntityNotFoundException("User can't reply to a conversation they're not in");
        List<Utilizator> computedTo = new ArrayList<>();
        computedTo.add(msg.getFrom());
        msg.getTo().forEach(x->{
            if(!x.getId().equals(userFrom.getId()))
                computedTo.add(x);
        });
        Message message1 = new Message(userFrom,computedTo,message,messageID);
        message1.setData(LocalDateTime.now());
        repoMesaje.save(message1);
        notifyObservers(new MessagesEvent());
    }

    public void replyToOne(Long idFrom,Long idTo, String message, Long messageID) throws EntityNotFoundException{

        Utilizator userFrom = repoUtilizator.findOne(idFrom);
        if(userFrom == null)
            throw new EntityNotFoundException("Sender user not found");
        Utilizator userTo = repoUtilizator.findOne(idTo);
        if(userTo == null)
            throw new EntityNotFoundException("Receiver user not found!");
        Message msg = repoMesaje.findOne(messageID);
        if(msg == null)
            throw new EntityNotFoundException("Message with this id doesn't exist");
        List<Utilizator> toList = msg.getTo();
        if(!toList.contains(userFrom))
            throw new EntityNotFoundException("User can't reply to a conversation they're not in");
        if(!toList.contains(userTo) && !msg.getFrom().equals(userTo))
            throw new EntityNotFoundException("Receiver user is not in this conversation");

        List<Utilizator> toList2 = new ArrayList<>();
        toList2.add(userTo);
        Message message1 = new Message(userFrom,toList2,message,messageID);
        message1.setData(LocalDateTime.now());
        repoMesaje.save(message1);
        notifyObservers(new MessagesEvent());
    }

    private List<Utilizator> verifyToList(List<Long> idTo) throws EntityNotFoundException {

        List<Utilizator> users = new ArrayList<>();
        for(Long id : idTo)
        {
            Utilizator user = repoUtilizator.findOne(id);
            if(user==null)
                throw new EntityNotFoundException("User with ID " + id + " not found");
            users.add(user);
        }
        return users;
    }


    public List<Message> findConversation(Long user1id, Long user2id) throws EntityNotFoundException {
        Utilizator user = repoUtilizator.findOne(user1id);
        if(user == null)
            throw new EntityNotFoundException("User1 doesn't exist");
        user = repoUtilizator.findOne(user2id);
        if(user == null)
            throw new EntityNotFoundException("User2 doesn't exist");

        Iterable<Message> msg = repoMesaje.findAll();
        List<Message> lista = new ArrayList<>();
        msg.forEach(x->{
            if(x.getFrom().getId().equals(user1id) && x.getTo().contains(repoUtilizator.findOne(user2id))
                || x.getFrom().getId().equals(user2id) && x.getTo().contains(repoUtilizator.findOne(user1id)))
                lista.add(x);
        });
        lista.sort(Comparator.comparing(Message::getData));
        return lista;
    }

    public List<Message> findReceivedMessages(Long id)
    {
        Utilizator user = repoUtilizator.findOne(id);
        return StreamSupport.stream(repoMesaje.findAll().spliterator(),false)
                .filter(x->x.getTo().contains(user))
                .collect(Collectors.toList());
    }

    public List<MessageDTO> findReceivedMessages2(Long id)
    {
        Utilizator user = repoUtilizator.findOne(id);
        return StreamSupport.stream(repoMesaje.findAll().spliterator(),false)
                .filter(x->x.getTo().contains(user))
                .map(x->{
                    List<Long> toId = x.getTo().stream().map(y->y.getId())
                    .collect(Collectors.toList());
                    MessageDTO messageDTO =  new MessageDTO(x.getFrom().getId(), x.getFrom().getFirstName(),
                        x.getFrom().getLastName(),toId,x.getMessage(),x.getData(),x.getReplyTo());
                    messageDTO.setId(x.getId());
                    return messageDTO;
                })
                .collect(Collectors.toList());
    }

    public Message findOne(Long id)
    {
        return repoMesaje.findOne(id);
    }

    public void findone(Long id)
    {
        Message m = repoMesaje.findOne(id);
        System.out.println(m);
        repoMesaje.save(m);
    }

    @Override
    public void addObserver(Observer<MessagesEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessagesEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessagesEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
