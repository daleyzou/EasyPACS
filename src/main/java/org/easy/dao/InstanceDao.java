package org.easy.dao;

import org.easy.entity.Instance;

import java.util.List;

public interface InstanceDao {

    void save(Instance instance);

    Instance update(Instance instance);

    List<Instance> findAll(int firstResult, int maxResults);

    Long count();

    Instance findById(long pkTBLInstanceID);

    List<Instance> findByPkTBLSeriesID(Long pkTBLSeriesID);

    Instance findBySopInstanceUID(String sopInstanceUID);

    List<Instance> findAllByPkTBLPatientID(Long pkTBLPatientID);
}
