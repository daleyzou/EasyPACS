package org.easy.dao.impl;

import org.easy.dao.AbstractJpaDao;
import org.easy.dao.InstanceDao;
import org.easy.entity.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class InstanceDaoImpl extends AbstractJpaDao<Instance> implements InstanceDao {

    //    @PersistenceContext(unitName = "dbdicom")
    @Autowired
    private EntityManager entityManager;

    public InstanceDaoImpl() {
        super();
        setClazz(Instance.class);
    }

    @Override
    public List<Instance> findByPkTBLSeriesID(Long pkTBLSeriesID) {

        try {
            return entityManager.createQuery("select i from Instance i  where i.series.pkTBLSeriesID = :pkTBLSeriesID", Instance.class)
                    .setParameter("pkTBLSeriesID", pkTBLSeriesID).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Instance findBySopInstanceUID(String sopInstanceUID) {

        try {
            return entityManager.createQuery("select i from Instance i  where i.sopInstanceUID = :sopInstanceUID", Instance.class)
                    .setParameter("sopInstanceUID", sopInstanceUID).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Instance> findAllByPkTBLPatientID(Long pkTBLPatientID) {

        try {

            return entityManager.createQuery("select i from Instance i LEFT OUTER JOIN i.series s " + "LEFT OUTER JOIN i.series.study st "
                    + "LEFT OUTER JOIN i.series.study.patient p " + "where p.pkTBLPatientID = :pkTBLPatientID", Instance.class)
                    .setParameter("pkTBLPatientID", pkTBLPatientID).getResultList();

        } catch (Exception e) {
            return null;
        }
    }

}
