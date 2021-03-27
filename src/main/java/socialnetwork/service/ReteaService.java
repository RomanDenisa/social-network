package socialnetwork.service;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Retea;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReteaService {

    private Repository<Tuple<Long,Long>, Prietenie> repoPrietenie;
    private Repository<Long, Utilizator> repoUtilizator;

    public ReteaService(Repository<Tuple<Long, Long>, Prietenie> repoPrietenie, Repository<Long, Utilizator> repoUtilizator) {
        this.repoPrietenie = repoPrietenie;
        this.repoUtilizator = repoUtilizator;
    }

    /**
     *
     * @return creeaza graful pentru reteaua de prietenie
     */
    private Map<Long, List<Long>> getListOfFriends()
    {
        Iterable<Utilizator> userList = repoUtilizator.findAll();
        Iterable<Prietenie> frList = repoPrietenie.findAll();
        Map<Long, List<Long>> graf = new HashMap<>();
        userList.forEach(x->graf.put(x.getId(),new ArrayList<>()));
        frList.forEach(x->{
            graf.get(x.getId().getLeft()).add(x.getId().getRight());
            graf.get(x.getId().getRight()).add(x.getId().getLeft());
        });
        return graf;
    }

    /**
     *
     * @return numarul de componente conexe
     */
    public int getNrComponenteConexe()
    {
        Retea retea = Retea.getInstanceRetea();
        return retea.getNrComponenteConexe(getListOfFriends());
    }

    /**
     *
     * @return cea mai mare componenta conexa
     */
    public List<Long> getLargestComponent()
    {
        Retea retea = Retea.getInstanceRetea();
        return retea.getLargestComponent(getListOfFriends());
    }
    
}
