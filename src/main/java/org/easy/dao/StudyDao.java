package org.easy.dao;

import org.easy.entity.Study;

import java.util.List;

public interface StudyDao {

    void save(Study study);

    Study update(Study study);

    List<Study> findAll(int firstResult, int maxResults);

    Long count();

    Study findById(long pkTBLStudyID);

    List<Study> findByPkTBLPatientID(Long pkTBLPatientID);

    Study findByStudyInstanceUID(String studyInstanceUID);
}
