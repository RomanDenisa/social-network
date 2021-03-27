package socialnetwork.utils.events;
import socialnetwork.domain.Prietenie;

public class FriendsChangeEvent implements Event{

    private Prietenie data, oldData;

    public FriendsChangeEvent(Prietenie data) {
        this.data = data;
    }
    public FriendsChangeEvent(Prietenie data, Prietenie oldData) {
        this.data = data;
        this.oldData=oldData;
    }

    public Prietenie getData() {
        return data;
    }

    public Prietenie getOldData() {
        return oldData;
    }
}
