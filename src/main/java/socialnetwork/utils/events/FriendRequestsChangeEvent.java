package socialnetwork.utils.events;
import socialnetwork.domain.CererePrietenie;

public class FriendRequestsChangeEvent implements Event{

    private CererePrietenie data, oldData;

    public FriendRequestsChangeEvent(CererePrietenie data) {
        this.data = data;
    }
    public FriendRequestsChangeEvent(CererePrietenie data, CererePrietenie oldData) {
        this.data = data;
        this.oldData=oldData;
    }

    public CererePrietenie getData() {
        return data;
    }

    public CererePrietenie getOldData() {
        return oldData;
    }
}
