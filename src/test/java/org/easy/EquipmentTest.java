package org.easy;

import junit.framework.TestCase;
import org.easy.dao.EquipmentDao;
import org.easy.entity.Equipment;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("classpath:application.properties")
public class EquipmentTest extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentTest.class);

    @Autowired
    EquipmentDao equipmentDao;

    @Ignore
    @Test
    public void testfindByPKTBLSeriesID() {

        Equipment equipment = equipmentDao.findByPkTBLSeriesID(3L);
        assertNotNull(equipment);
        LOG.info(equipment.toString());
    }
}
