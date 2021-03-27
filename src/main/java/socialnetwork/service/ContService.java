package socialnetwork.service;
import socialnetwork.domain.Cont;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.ContDbRepository;
import socialnetwork.service.exceptions.DuplicatedIDException;
import socialnetwork.service.exceptions.EntityNotFoundException;

public class ContService {

    private ContDbRepository repoCont;
    private Repository<Long, Utilizator> repoUtilizator;

    public ContService(ContDbRepository repoCont,Repository<Long, Utilizator> repoUtilizator) {
        this.repoCont = repoCont;
        this.repoUtilizator = repoUtilizator;
    }

    public Cont findOne(String username, String password) throws EntityNotFoundException {
        Cont cont = repoCont.findOne(username, password);
        if(cont==null)
            throw new EntityNotFoundException("You don't have an account!");
        return cont;
    }

    public void addCont(Long id, String username, String password,String firstName,String lastName) throws DuplicatedIDException {
        Cont cont = repoCont.findOneUsername(username);
        if(cont!=null)
            throw new DuplicatedIDException("Account with this username already exists");

        Utilizator user = new Utilizator(firstName,lastName);
        user.setId(id);
        repoUtilizator.save(user);

        Cont cont2 = new Cont(username,password);
        cont2.setId(id);
        repoCont.save(cont2);

    }
}
