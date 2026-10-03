package vn.edu.vhu.ltdd.a4events;

import java.util.ArrayList;
import java.util.List;

/** Most recent five successful calculations, newest first. */
public final class HistoryStore {
    private final ArrayList<String> entries = new ArrayList<>();
    public void add(String entry) {
        entries.add(0, entry);
        if (entries.size() > 5) entries.remove(entries.size() - 1);
    }
    public ArrayList<String> snapshot() { return new ArrayList<>(entries); }
    public void restore(List<String> saved) {
        entries.clear();
        if (saved != null) entries.addAll(saved.subList(0, Math.min(5, saved.size())));
    }
}
