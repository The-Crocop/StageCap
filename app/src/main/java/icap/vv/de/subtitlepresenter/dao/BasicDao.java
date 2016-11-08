package icap.vv.de.subtitlepresenter.dao;

import java.util.List;

/**
 * Created by Marko Nalis on 28.10.2015.
 */
public interface BasicDao<T> {
    List<T> getAll();
    void save(T element);
    void delete(T element);
    void update(T element);
    T findById(String id);
    void clearAll();
}
