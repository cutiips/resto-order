package ch.hearc.ig.orderresto.persistence.mappers;

import ch.hearc.ig.orderresto.persistence.IdentityMap;

import java.util.Optional;


public abstract class BaseMapper<T> {
    protected final IdentityMap<T> identityMap = new IdentityMap<>();


    protected Optional<T> findInCache(Long id) {
        return Optional.ofNullable(identityMap.get(id));
    }

    protected void addToCache(Long id, T entity) {
        identityMap.put(id, entity);
    }

    protected void updateInCache(Long id, T entity) {
        identityMap.put(id, entity);
    }

    protected void removeFromCache(Long id) {
        identityMap.remove(id);
    }
}

