package socialnetwork.domain;
import java.util.*;

public class Retea {

    private static Retea retea = null;

    private Retea(){}

    public static Retea getInstanceRetea()
    {
        if(retea==null)
            retea = new Retea();
        return retea;
    }

    private void DFS(Long v, Map<Long,List<Long>> graf, Set<Long> visited,List<Long> rez)
    {
        visited.add(v);
        rez.add(v);
        graf.get(v).forEach((x)->{if(!visited.contains(x))
        DFS(x,graf,visited,rez);
        });

    }

    public Map<Integer,List<Long>> getNrAndLongestComponent(Map<Long,List<Long>> graf)
    {
        Set<Long> visited = new HashSet<>();
        int nr=0;
        Map<Integer,List<Long>> rezultat = new HashMap<>();
        List<Long> compMaxima = new ArrayList<>();

        for(Map.Entry<Long,List<Long>> pereche : graf.entrySet())
        {
            if(!visited.contains(pereche.getKey()))
            {List<Long> rez = new ArrayList<>();
            nr++;
            DFS(pereche.getKey(),graf,visited,rez);
            if(rez.size()>compMaxima.size())
                compMaxima=rez;}
        }
        rezultat.put(nr,compMaxima);
        return rezultat;
    }

    public int getNrComponenteConexe(Map<Long,List<Long>> graf)
    {
        return this.getNrAndLongestComponent(graf).keySet().stream().findFirst().get();
    }

    public List<Long> getLargestComponent(Map<Long,List<Long>> graf)
    {
        return this.getNrAndLongestComponent(graf).values().stream().findFirst().get();
    }


}
