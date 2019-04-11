package org.easy.controller;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.dcm4che3.tool.dcm2jpg.Dcm2Jpg;
import org.easy.component.ActiveDicoms;
import org.easy.dao.InstanceDao;
import org.easy.dao.PatientDao;
import org.easy.dao.SeriesDao;
import org.easy.dao.StudyDao;
import org.easy.entity.Instance;
import org.easy.entity.Patient;
import org.easy.entity.Series;
import org.easy.entity.Study;
import org.easy.rest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Controller
public class HomeController {

    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

    private static final String JPG_EXT = ".jpg";

    private static final int THUMBNAIL_WIDTH = 300;

    private static final int THUMBNAIL_HEIGHT = 300;

    private static final int MAX_IMAGE_WIDTH = 1000;

    private static final int MAX_IMAGE_HEIGHT = 800;

    @Autowired
    PatientDao patientDao;

    @Autowired
    StudyDao studyDao;

    @Autowired
    SeriesDao seriesDao;

    @Autowired
    InstanceDao instanceDao;

    @Value("${pacs.storage.image}")
    private String pacsImageStoragePath;

    @Value("${pacs.storage.dcm}")
    private String pacsDcmStoragePath;

    @Autowired
    private ActiveDicoms activeDicoms;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        LOG.debug("index()");
        return "index";
    }

    @RequestMapping(value = "/server", method = RequestMethod.GET)
    public String server(@RequestParam(defaultValue = "1", value = "page", required = false) Integer page,
            @RequestParam(defaultValue = "10", value = "size", required = false) Integer size,
            @RequestParam(defaultValue = "0", value = "pkTBLPatientID", required = false) Long pkTBLPatientID,
            @RequestParam(defaultValue = "0", value = "pkTBLStudyID", required = false) Long pkTBLStudyID,
            @RequestParam(defaultValue = "0", value = "pkTBLSeriesID", required = false) Long pkTBLSeriesID, Model model) {

        LOG.debug("server()");

        int firstResult = (page == null) ? 0 : (page - 1) * size;
        List<Patient> patients = patientDao.findAll(firstResult, size);
        model.addAttribute("patients", patients);
        float nrOfPages = (float) patientDao.count() / size;
        int maxPages = (int) (((nrOfPages > (int) nrOfPages) || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages);

        int begin = Math.max(1, page - 5);
        int end = Math.min(begin + 10, maxPages); // how many pages to display in the pagination bar

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", page);
        model.addAttribute("maxPages", maxPages);

        //get related study, series and instance objects
        List<Study> studies = (pkTBLPatientID != 0) ?
                studyDao.findByPkTBLPatientID(pkTBLPatientID) :
                studyDao.findByPkTBLPatientID(patients.get(0).getPkTBLPatientID());
        List<Series> serieses = (pkTBLStudyID != 0) ?
                seriesDao.findByPkTBLStudyID(pkTBLStudyID) :
                seriesDao.findByPkTBLStudyID(studies.get(0).getPkTBLStudyID());
        List<Instance> instances = (pkTBLSeriesID != 0) ?
                instanceDao.findByPkTBLSeriesID(pkTBLSeriesID) :
                instanceDao.findByPkTBLSeriesID(serieses.get(0).getPkTBLSeriesID());

        //add to our model
        model.addAttribute("studies", studies);
        model.addAttribute("serieses", serieses);
        model.addAttribute("instances", instances);

        LOG.info("page no:{} page size:{} nrOfPages:{} maxPages:{}", page, size, nrOfPages, maxPages);

        return "server";

    }

    @RequestMapping(value = "/details/{pkTBLInstanceID}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable Long pkTBLInstanceID, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        java.io.File tempImage = null;
        Instance instance = instanceDao.findById(pkTBLInstanceID);

        if (instance != null) {
            File dicomFile = new File(pacsDcmStoragePath + "/" + instance.getMediaStorageSopInstanceUID() + ".dcm");

            /********************************************************* TEMP IMAGE FILE CREATION *****************************************************************/
            Dcm2Jpg dcm2Jpg = null;
            try {
                // 每一次都需要创建一个实例，因为 Dcm2Jpg 线程不安全
                dcm2Jpg = new Dcm2Jpg();
                // default JPEG writer class, compressionType, and quality
                dcm2Jpg.initImageWriter("JPEG", "jpeg", null, null, null);
                //remove the .dcm and  assign a JPG extension
                String newfilename = FilenameUtils.removeExtension(dicomFile.getName()) + JPG_EXT;
                //create the temporary image file instance
                tempImage = new java.io.File(pacsImageStoragePath, newfilename);

                // 如果文件不存在，就进行文件转换
                if (!tempImage.exists()) {
                    //save the new jpeg into the .img temp folder
                    dcm2Jpg.convert(dicomFile, tempImage);
                }

                if (!tempImage.exists())
                    // 文件不存在
                    throw new Exception();

            } catch (Exception e) {
                LOG.error("failed convert {} to jpeg... Exception: {}", dicomFile, e.getMessage());
            }
            /********************************************************** END OF TEMP FILE CREATION ***************************************************************/

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            if (tempImage != null) {
                byte[] bytes = IOUtils.toByteArray(new FileInputStream(tempImage));
                return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
            }

        }

        return null;
    }

    /**
     * HomeController
     * @description 实时的转换得到图片，便于用于网页展示
     * @param pkTBLInstanceID dicom 文件的文件名（去除了后缀）
     * @return dicom-》jpg后的图片的路径
     * @author zoudaifa
     * @date 2019/3/23 18:16
     * @version 1.0.0
     */
    @RequestMapping(value = "/showimage/{pkTBLInstanceID}", method = RequestMethod.GET)
    public @ResponseBody
    String showImage(@PathVariable Long pkTBLInstanceID) throws IOException {

        String img = "";
        File file = null;
        int width = 0;
        int height = 0;
        Instance instance = null;
        Dimension newImageSize = null;

        try {
            instance = instanceDao.findById(pkTBLInstanceID);

            if (instance != null) {

                File dicomFile = new File(pacsDcmStoragePath + "/" + instance.getMediaStorageSopInstanceUID() + ".dcm");
                String newfilename = FilenameUtils.removeExtension(dicomFile.getName()) + JPG_EXT;
                //create the temporary image file instance
                file = new java.io.File(pacsImageStoragePath, newfilename);
                BufferedImage bimg = ImageIO.read(file);
                width = bimg.getWidth();
                height = bimg.getHeight();
                LOG.debug("Original width:" + width + " Original height:" + height);
                System.setProperty("java.awt.headless", "true");

                /*getScreenSize doesn't work properly, hold this until get it fixed and keep 1000x800 constant size*/
                //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                //keep constant size for now
                Dimension screenSize = new Dimension(1000, 800);
                //                if (width >= MAX_IMAGE_WIDTH || height >= MAX_IMAGE_HEIGHT) {
                //                    Dimension imgSize = new Dimension(width, height);
                //                    Dimension boundary = new Dimension(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);
                //                    newImageSize = Utils.getScaledDimension(imgSize, boundary);
                //                    width = newImageSize.width;
                //                    height = newImageSize.height;
                //                }

                // 设置显示的图片的宽度和高度
                width = 512;
                height = 512;

                LOG.debug("Screen width:" + screenSize.width + "  Screen Height:" + screenSize.height);
                LOG.debug("Image width:" + width + " Image height:" + height);

                if (file.exists()) {
                    img = "<img  src=\'/details/" + pkTBLInstanceID + "\' style=\'width: " + width + "px; height: " + height + "px;\' /> ";
                }
            }

        } catch (Exception ex) {
            LOG.error("将文件转换为jpg图片过程中出错: {}", ex.getMessage());
        }
        return img;
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public String showDetails(@RequestParam(defaultValue = "0", value = "pkTBLPatientID", required = false) Long pkTBLPatientID,
            Model model) {

        if (pkTBLPatientID != 0) {
            //add to our model
            model.addAttribute("patient", patientDao.findById(pkTBLPatientID));
        }

        return "details";
    }

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String welcome() {
        LOG.debug("welcome()");
        return "welcome";
    }

	/*
	@RequestMapping(value = "/series", method = RequestMethod.GET)
	public @ResponseBody List<Series> getEntities(@RequestParam(defaultValue="1", value="page", required=false) Integer page,
			@RequestParam(defaultValue="10", value="size", required=false) Integer size, 
			@RequestParam(defaultValue="0", value="pkTBLPatientID", required=false) Long pkTBLPatientID,
			@RequestParam(defaultValue="0", value="pkTBLStudyID", required=false) Long pkTBLStudyID,
			@RequestParam(defaultValue="0", value="pkTBLSeriesID", required=false) Long pkTBLSeriesID,
			Model model, HttpServletRequest request) {
		
		int firstResult = (page==null)?0:(page-1) * size;		
		List<Patient> patients = patientDao.findAll(firstResult, size);
	   
	    //get related study, series and instance objects
	    List<Study> studies = (pkTBLPatientID != 0)?studyDao.findByPkTBLPatientID(pkTBLPatientID): studyDao.findByPkTBLPatientID(patients.get(0).getPkTBLPatientID());
	   	List<Series> serieses = (pkTBLStudyID != 0)?seriesDao.findByPkTBLStudyID(pkTBLStudyID): seriesDao.findByPkTBLStudyID(studies.get(0).getPkTBLStudyID());
	    	
		
		//return new AjaxResultList(patients, studies, serieses, instances, begin, end, page, maxPages);
	    return serieses;
		
	}*/

    @RequestMapping(value = "/hello/{name:.+}", method = RequestMethod.GET)
    public ModelAndView welcome(@PathVariable("name") String name) {

        LOG.debug("welcome() - name {}", name);

        ModelAndView model = new ModelAndView();
        model.setViewName("welcome");
        model.addObject("name", name);

        return model;
    }

    @RequestMapping(value = "/live", method = RequestMethod.GET)
    public @ResponseBody
    AjaxResult live() {
        return new AjaxResult(true, activeDicoms.toString());
    }

    @RequestMapping(value = "/study", method = RequestMethod.GET)
    public @ResponseBody
    AjaxStudy study(@RequestParam(defaultValue = "0", value = "pkTBLStudyID", required = false) Long pkTBLStudyID) {
        Study study = studyDao.findById(pkTBLStudyID);
        return new AjaxStudy(true, study);
    }

    @RequestMapping(value = "/series", method = RequestMethod.GET)
    public @ResponseBody
    AjaxSeries series(@RequestParam(defaultValue = "0", value = "pkTBLSeriesID", required = false) Long pkTBLSeriesID) {
        Series series = seriesDao.findById(pkTBLSeriesID);
        return new AjaxSeries(true, series);
    }

    @RequestMapping(value = "/instance", method = RequestMethod.GET)
    public @ResponseBody
    AjaxInstance instance(@RequestParam(defaultValue = "0", value = "pkTBLInstanceID", required = false) Long pkTBLInstanceID) {
        Instance instance = instanceDao.findById(pkTBLInstanceID);
        return new AjaxInstance(true, instance);
    }

    @RequestMapping(value = "/patient", method = RequestMethod.GET)
    public @ResponseBody
    AjaxPatient patient(@RequestParam(defaultValue = "0", value = "pkTBLPatientID", required = false) Long pkTBLPatientID) {
        Patient patient = patientDao.findById(pkTBLPatientID);
        return new AjaxPatient(true, patient);
    }
}
