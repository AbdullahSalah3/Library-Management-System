
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class WaitingList {
    private Map<Integer, Queue<String>> waitingLists = new HashMap<>();

    public void addToWaitingList(int bookID, String memberID) {
        waitingLists.putIfAbsent(bookID, new LinkedList<>());
        waitingLists.get(bookID).add(memberID);
    }

    public String removeFromWaitingList(int bookID) {
        if (!waitingLists.containsKey(bookID)) return null;
        Queue<String> q = waitingLists.get(bookID);
        if (q.isEmpty()) return null;
        String next = q.poll();
        if (q.isEmpty()) waitingLists.remove(bookID);
        return next;
    }

    public boolean isEmpty(int bookID) {
        return !waitingLists.containsKey(bookID) || waitingLists.get(bookID).isEmpty();
    }
}
