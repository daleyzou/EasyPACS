package org.easy.dao.impl;

import org.easy.dao.AbstractJpaDao;
import org.easy.dao.PatientDao;
import org.easy.entity.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class PatientDaoImpl extends AbstractJpaDao<Patient> implements PatientDao {

//    @PersistenceContext(unitName = "dbdicom")
    @Autowired
    private EntityManager entityManager;

    public PatientDaoImpl() {
        super();
        setClazz(Patient.class);
    }
	
	/*@Transactional
	@Override
	public void save(Patient patient) {
		entityManager.persist(patient);		
		entityManager.flush();
	}

	@Override
	public List<Patient> findAll() {
		
		try{			
			TypedQuery<Patient> query = entityManager.createQuery("select p FROM Patient p", Patient.class);			 
			return query.getResultList();
			
		}catch(Exception e){
			return null;		
		}
	}

	@Override
	public Patient findByID(long pkTBLPatientID) {
		try{			
			return entityManager.find(Patient.class, pkTBLPatientID);
			
		}catch(Exception e){
			return null;
		}
	}*/

    @Override
    public Patient findByPatientID(String patientID) {

        try {
            return entityManager.createQuery("select p from Patient p where p.patientID LIKE :patientID", Patient.class)
                    .setParameter("patientID", patientID).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
