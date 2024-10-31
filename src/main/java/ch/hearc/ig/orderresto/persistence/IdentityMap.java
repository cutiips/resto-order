package ch.hearc.ig.orderresto.persistence;

import java.util.HashMap;
import java.util.Map;

public class IdentityMap<T> {

    // Utilisation d'une Map pour stocker les entités par ID
    private final Map<Long, T> cache = new HashMap<>();

    // Méthode pour récupérer une entité par son ID
    public T get(Long id) {
        return cache.get(id);
    }

    // Méthode pour ajouter une nouvelle entité dans le cache
    public void put(Long id, T entity) {
        cache.put(id, entity);
    }

    // Méthode pour supprimer une entité du cache
    public void remove(Long id) {
        cache.remove(id);
    }

    // Méthode pour vérifier si une entité est présente dans le cache
    public boolean contains(Long id) {
        return cache.containsKey(id);
    }
}
