package org.easy.dao;

import org.easy.entity.Equipment;

import java.util.List;

public interface EquipmentDao {

    void save(Equipment equipment);

    Equipment update(Equipment equipment);

    List<Equipment> findAll(int firstResult, int maxResults);

    Long count();

    Equipment findById(long pkTBLEquipmentID);

    Equipment findByPkTBLSeriesID(Long pkTBLSeriesID);
}
