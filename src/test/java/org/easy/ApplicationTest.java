package org.easy;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.easy.handler.IncomingFileHandler;
import org.easy.server.DicomServer;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("classpath:application.properties")
public class ApplicationTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);
	
	public static void main(String[] args) {
        
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
        
        LOG.info("Welcome to EasyPACS!");
    }
    
    
    /************************** Handler for incoming files works with asynchronous event bus initiated by the DicomServer ****************************/    
    @Bean // only one incoming file handler. Even we have multiple DicomServer instances, they all forward files to the same handler...
    public IncomingFileHandler incomingFileHandler(){
        return new IncomingFileHandler();
    }

    @Bean //Guava asynch event bus that initiates 100 fixed thread pool
    public EventBus asyncEventBus(){       
    	EventBus eventBus =  new AsyncEventBus(java.util.concurrent.Executors.newFixedThreadPool(100));
        return eventBus;
    }

    @Bean //dicom server takes storage output directory, ae title and ports. Server listens same number of ports with same ae title 
    public Map<String, DicomServer> dicomServers(@Value("${pacs.storage.dcm}") String storageDir, @Value("${pacs.aetitle}") String aeTitle, @Value("#{'${pacs.ports}'.split(',')}") List<Integer> ports){
        Map<String, DicomServer> dicomServers = new HashMap<>();
        for (int port:ports) {
            dicomServers.put("DICOM_SERVER_AT_" + port, DicomServer.init(null, port, aeTitle, storageDir, asyncEventBus()));
        }
        return dicomServers;
    }
    /************************** End of Handler for incoming files works with asynchronous event bus initiated by the DicomServer ****************************/
    
    
    
    
    
    
    
    /************************************************** Database JPA and Hibernate Settings ********************************************************/
    @Bean //Creating and registering in spring context an entityManager
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(primaryDataSource());
        em.setPersistenceUnitName("dbdicom");
        em.setPackagesToScan(new String[]{"org.easy.entity"}); // package where are the @Entity classes are located, usually your domain package
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter(); // JPA implementation 
        em.setJpaVendorAdapter(vendorAdapter);    

        return em;
    }
    
    @Bean
    @Primary //configure the primary database
    @ConfigurationProperties(prefix="datasource.primary")
    public DataSource primaryDataSource() {
        return null;
    }
    
    @Bean //Configuring the transactionManager
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
    
    /************************************************* End of Database JPA and Hibernate Settings ********************************************************/
      

}
