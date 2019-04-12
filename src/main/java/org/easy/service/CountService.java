/**
 * @projectName EasyPACS
 * @package org.easy.service
 * @className org.easy.service.CountService
 * @copyright Copyright 2019 Daleyzou, Inc. All rights reserved.
 */
package org.easy.service;

/**
 * CountService
 * @description 计算射血分数
 * @author zoudaifa
 * @date 2019年03月24日 18:49
 * @version 1.0.0
 */
public interface CountService {
    /**
     * CountService
     * @description 将要进行图像分割的图片移动到指定的目录
     * @param pkTBLPatientID 患者的主键
     * @return
     * @author zoudaifa
     * @date 2019/4/10 8:40
     * @version 1.0.0
     */
    void moveJpgToDir(Long pkTBLPatientID);

    /**
     * CountService
     * @description 调用Python程序，分割图片，识别出图片中的心脏部位
     * @param
     * @return
     * @author zoudaifa
     * @date 2019/4/12 11:40
     * @version 1.0.0
     */
    void splitPicture();

//    // 将计算后得到的左心室面积存入instance的对应字段，patient表有一个字段用于标识是否已经计算射血分数
//    void saveData();
//
//    // 计算射血分数
//    void countEF();
}
