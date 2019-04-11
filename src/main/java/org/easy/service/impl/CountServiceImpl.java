/**
 * @projectName EasyPACS
 * @package org.easy.service.impl
 * @className org.easy.service.impl.CountServiceImpl
 * @copyright Copyright 2019 daleyzou, Inc. All rights reserved.
 */
package org.easy.service.impl;

import org.apache.commons.io.FilenameUtils;
import org.dcm4che3.tool.dcm2jpg.Dcm2Jpg;
import org.easy.dao.InstanceDao;
import org.easy.entity.Instance;
import org.easy.service.CountService;
import org.easy.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * CountServiceImpl
 * @description TODO
 * @author zoudaifa
 * @date 2019年04月10日 8:57
 * @version 1.0.0
 */
@Service
public class CountServiceImpl implements CountService {

    private static final Logger LOG = LoggerFactory.getLogger(CountServiceImpl.class);

    private static final String JPG_EXT = ".jpg";

    @Autowired
    InstanceDao instanceDao;

    @Value("${pacs.storage.temp.image}")
    String pacsOriginalPath;

    @Value("${pacs.storage.temp.result}")
    String pacsResultPath;

    @Value("${pacs.storage.dcm}")
    private String pacsDcmStoragePath;

    @Override
    public void moveJpgToDir(Long pkTBLPatientID) {
        try {

            // 查询该患者的所有心脏dcm文件
            List<Instance> instances = instanceDao.findAllByPkTBLPatientID(pkTBLPatientID);
            if (CollectionUtils.isEmpty(instances)) {
                // TODO 没有这个患者的dcm文件数据
            }
            for (Instance instance : instances) {
                File dicomFile = new File(pacsDcmStoragePath + "/" + instance.getMediaStorageSopInstanceUID() + ".dcm");
                Dcm2Jpg dcm2Jpg = new Dcm2Jpg();
                dcm2Jpg.initImageWriter("JPEG", "jpeg", null, null, null);
                String newfilename = FilenameUtils.removeExtension(dicomFile.getName()) + JPG_EXT;
                String outputPath = pacsOriginalPath + pkTBLPatientID + "/";
                // 目录不存在就创建
                boolean orExistsDir = FileUtils.createOrExistsDir(outputPath);
                if (!orExistsDir){
                    LOG.error("创建目录失败");
                }
                java.io.File tempImage = new java.io.File(outputPath, newfilename);
                // 如果文件不存在，就进行文件转换
                if (!tempImage.exists()) {
                    dcm2Jpg.convert(dicomFile, tempImage);
                }

                if (!tempImage.exists())
                    // 文件不存在
                    throw new Exception();
            }
        }catch (Exception e){
            String message = "将患者id为" + pkTBLPatientID.toString() + "进行文件处理失败！";
            LOG.error(message, e);
        }
    }
}
