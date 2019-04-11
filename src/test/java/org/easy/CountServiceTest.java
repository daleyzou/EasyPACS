/**
 * @projectName EasyPACS
 * @package org.easy
 * @className org.easy.CountServiceTest
 * @copyright Copyright 2019 Thuisoft, Inc. All rights reserved.
 */
package org.easy;

import org.easy.service.CountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * CountServiceTest
 * @description TODO
 * @author zoudaifa
 * @date 2019年04月11日 9:28
 * @version 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("classpath:application.properties")
public class CountServiceTest {

    @Autowired
    CountService countService;

    @Test
    public void testConvertAndMove(){
        countService.moveJpgToDir(new Long(1));
    }
}
