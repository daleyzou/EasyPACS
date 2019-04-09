package org.easy;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.easy.component.ActiveDicoms;
import org.easy.handler.IncomingFileHandler;
import org.easy.server.DicomServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;

@SpringBootApplication
@Configuration
@EnableJpaRepositories(basePackages = { "org.easy.dao" }) // The package where dao classes reside
@PropertySource("classpath:application.properties")
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

//        SpringApplication app = new SpringApplication(Application.class);
//        app.setShowBanner(false);
//        app.setWebEnvironment(true);
//        app.setWebApplicationType(WebApplicationType.SERVLET);
//        app.run(args);
        SpringApplication.run(Application.class, args);

        LOG.info("Welcome to Hospital PACS Server!");
    }

    /************************** Handler for incoming files works with asynchronous event bus initiated by the DicomServer ****************************/
    @Bean // only one incoming file handler. Even we have multiple DicomServer instances, they all forward files to the same handler...
    public IncomingFileHandler incomingFileHandler() {
        return new IncomingFileHandler();
    }

    @Bean //Guava asynch event bus that initiates 100 fixed thread pool
    public EventBus asyncEventBus() {
        EventBus eventBus = new AsyncEventBus(java.util.concurrent.Executors.newFixedThreadPool(100));
        return eventBus;
    }

    @Bean //dicom server takes storage output directory, ae title and ports. Server listens same number of ports with same ae title 
    public Map<String, DicomServer> dicomServers(@Value("${pacs.storage.dcm}") String storageDir, @Value("${pacs.aetitle}") String aeTitle,
            @Value("#{'${pacs.ports}'.split(',')}") List<Integer> ports) {
        Map<String, DicomServer> dicomServers = new HashMap<>();
        for (int port : ports) {
            dicomServers.put("DICOM_SERVER_AT_" + port, DicomServer.init(null, port, aeTitle, storageDir, asyncEventBus()));
        }
        return dicomServers;
    }

    /************************** End of Handler for incoming files works with asynchronous event bus initiated by the DicomServer ****************************/

    @Bean
    @Qualifier(value = "activeDicoms")
    public ActiveDicoms activeDicoms() {
        return new ActiveDicoms();
    }

    /************************************************** Database JPA and Hibernate Settings ********************************************************/
    //Creating and registering in spring context an entityManager
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
////        em.setDataSource(primaryDataSource());
//        em.setPersistenceUnitName("dbdicom");
//        // 设置扫描 @Entity 的实体类
//        em.setPackagesToScan("org.easy.entity");
//        // JPA implementation
//        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
//        return em;
//    }

//    @Bean
//    @Primary //configure the primary database
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource primaryDataSource() {
//        return DataSourceBuilder.create().build();
//    }
////
//    @Bean //Configuring the transactionManager
//    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(emf);
//        return transactionManager;
//    }

//    @Bean
//    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
//        return new PersistenceExceptionTranslationPostProcessor();
//    }
//
//    /************************************************* End of Database JPA and Hibernate Settings ********************************************************/

}
