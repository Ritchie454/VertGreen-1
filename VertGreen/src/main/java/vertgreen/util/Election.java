package vertgreen.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Election<E extends Object> {

    private final HashMap<E, ArrayList<Object>> candidates;

    public Election(ArrayList<E> cands) {
        candidates = new HashMap<>();

        for (E candidate : cands) {
            candidates.put(candidate, new ArrayList<>());
        }
    }

    public E setVote(Object voter, E newCandidate) {
        E originalCandidate = null;
        for (E cand : candidates.keySet()) {
            ArrayList<Object> v = candidates.get(cand);
            if (v.contains(voter)) {
                originalCandidate = cand;
                v.remove(voter);
            }
        }

        candidates.get(newCandidate).add(voter);

        return originalCandidate;
    }

    public ArrayList<E> getSortedByVotes() {
        ArrayList<E> newList = new ArrayList<>(candidates.keySet());

        Collections.sort(newList, (E o1, E o2) -> {
            int s1 = candidates.get(o1).size();
            int s2 = candidates.get(o2).size();
            return Math.max(1, Math.min(-1, s1 - s2));
        });

        return newList;
    }

    public E pickWinner() {
        ArrayList<E> top = new ArrayList<>();
        int maxVoteCount = 0;

        for (E cand : candidates.keySet()) {
            int votes = candidates.get(cand).size();
            if (votes > maxVoteCount) {
                maxVoteCount = votes;
            }
        }

        for (E cand : candidates.keySet()) {
            int votes = candidates.get(cand).size();
            if (votes == maxVoteCount) {
                top.add(cand);
            }
        }

        return top.isEmpty() ? null : top.get(ThreadLocalRandom.current().nextInt(top.size()));
    }
    
    public int getTotalVotes(){
        int i = 0;
        
        for (ArrayList<Object> votes : candidates.values()) {
            i = i + votes.size();
        }
        
        return i;
    }

}
