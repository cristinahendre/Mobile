package repository;

import java.util.List;

public interface IRepository<ID, E > {

    E findOne(ID id);
    List<E> findAll();



}

