package org.easy.server;

import com.google.common.eventbus.EventBus;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.net.*;
import org.dcm4che3.net.pdu.AAssociateAC;
import org.dcm4che3.net.pdu.AAssociateRQ;
import org.dcm4che3.net.pdu.PresentationContext;
import org.dcm4che3.net.pdu.UserIdentityAC;
import org.dcm4che3.net.service.BasicCEchoSCP;
import org.dcm4che3.net.service.BasicCStoreSCP;
import org.dcm4che3.net.service.DicomServiceRegistry;
import org.dcm4che3.util.SafeClose;
import org.easy.event.NewFileEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DicomServer {
    private static final Logger LOG = LoggerFactory.getLogger(DicomServer.class);

    private static final String DCM_EXT = ".dcm";

    private final Device device = new Device("storescp");

    private final ApplicationEntity ae = new ApplicationEntity("*");

    private final Connection conn = new Connection();

    public EventBus eventBus;

    private File storageDir;

    private int status;

    /**
     * DicomServer
     * @description 不做操作，这里全部使用默认实现
     * @author zoudaifa
     * @date 2019/3/24 16:36
     * @version 1.0.0
     */
    private AssociationHandler associationHandler = new AssociationHandler() {

        @Override
        protected AAssociateAC makeAAssociateAC(Association as, AAssociateRQ rq, UserIdentityAC arg2) throws IOException {

            State st = as.getState();

            if (as != null) {
                LOG.info("makeAAssociateAC: {}  Associate State: {}  Associate State Name: {}", as.toString(), st, st.name());
                try {
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                }
            }

            if (rq != null)
                LOG.info("Max OpsInvoked: {}  Max OpsPerformed: {}  Max PDU Length: {}  Number of Pres. Contexts: {}",
                        rq.getMaxOpsInvoked(), rq.getMaxOpsPerformed(), rq.getMaxPDULength(), rq.getNumberOfPresentationContexts());

            if (arg2 != null)
                LOG.info("UserIdentityAC Length:{}", arg2.length());

            return super.makeAAssociateAC(as, rq, arg2);
        }

        @Override
        protected AAssociateAC negotiate(Association as, AAssociateRQ rq) throws IOException {

            if (as != null)
                LOG.info("AAssociateAC negotiate:{}", as.toString());

            return super.negotiate(as, rq);
        }

        @Override
        protected void onClose(Association as) {

            State st = as.getState();

            if (as != null && st == State.Sta13) {
                LOG.info("Assocation Released and Closed: {} Name: {}", as.getState(), as.toString());
            } else {
                LOG.info("Association Closed");
            }
            super.onClose(as);
        }
    };

    public DicomServer() {
        device.setDimseRQHandler(createServiceRegistry());
        device.addConnection(conn);
        device.addApplicationEntity(ae);
        device.setAssociationHandler(associationHandler);
        ae.setAssociationAcceptor(true);
        ae.addConnection(conn);
    }

    private static void deleteFile(Association as, File file) {
        if (file.delete())
            LOG.info("{}: M-DELETE {}", as, file);
        else
            LOG.warn("{}: M-DELETE {} failed!", as, file);
    }

    public static void configureConn(Connection conn) {
        conn.setReceivePDULength(Connection.DEF_MAX_PDU_LENGTH);
        conn.setSendPDULength(Connection.DEF_MAX_PDU_LENGTH);

        conn.setMaxOpsInvoked(0);
        conn.setMaxOpsPerformed(0);
    }

    /**
     * DicomServer
     * @description 初始化文件服务
     * @param aeHost
     * @param aePort
     * @param aeTitle
     * @param storageDirectory
     * @param eventBus
     * @return
     * @author zoudaifa
     * @date 2019/3/24 16:44
     * @version 1.0.0
     */
    public static DicomServer init(String aeHost, int aePort, String aeTitle, String storageDirectory, EventBus eventBus) {
        LOG.info("Bind to: " + aeTitle + "@" + aeHost + ":" + aePort + "; storage: " + storageDirectory);

        DicomServer ds = null;
        try {
            ds = new DicomServer();

            ds.eventBus = eventBus;
            if (aeHost != null) {
                ds.conn.setHostname(aeHost);
            }
            ds.conn.setPort(aePort);
            ds.ae.setAETitle(aeTitle);

            //default conn parameters
            configureConn(ds.conn);

            //accept-unknown
            ds.ae.addTransferCapability(new TransferCapability(null, "*", TransferCapability.Role.SCP, "*"));

            ds.setStorageDirectory(new File(storageDirectory));

            // 多线程的方式接受文件
            ExecutorService executorService = Executors.newCachedThreadPool();
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            ds.device.setScheduledExecutor(scheduledExecutorService);
            ds.device.setExecutor(executorService);
            ds.device.bindConnections();
        } catch (Exception e) {
            LOG.error("dicomserver: {}", e.getMessage());
            e.printStackTrace();
        }
        return ds;
    }

    private void storeTo(Association as, Attributes fmi, PDVInputStream data, File file) throws IOException {
        LOG.info("{}: M-WRITE {}", as, file);
        file.getParentFile().mkdirs();
        DicomOutputStream out = new DicomOutputStream(file);
        try {
            out.writeFileMetaInformation(fmi);
            data.copyTo(out);
        } finally {
            SafeClose.close(out);
        }
    }

    private DicomServiceRegistry createServiceRegistry() {
        DicomServiceRegistry serviceRegistry = new DicomServiceRegistry();
        serviceRegistry.addDicomService(new BasicCEchoSCP());
        serviceRegistry.addDicomService(new CStoreSCPImpl());
        return serviceRegistry;
    }

    public void setStorageDirectory(File storageDir) {
        if (storageDir != null)
            storageDir.mkdirs();
        this.storageDir = storageDir;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * DicomServer
     * @description https://github.com/dcm4che/dcm4chee-arc-light/blob/5f49fa72d08f5400e0952299ddeaa6511d138748/dcm4chee-arc-store-scp/src/main/java/org/dcm4chee/arc/store/scp/CStoreSCP.java
     * @param
     * @return
     * @author zoudaifa
     * @date 2019/3/24 16:31
     * @version 1.0.0
     */
    private final class CStoreSCPImpl extends BasicCStoreSCP {

        CStoreSCPImpl() {
            super("*");
        }

        @Override
        protected void store(Association as, PresentationContext pc, Attributes rq, PDVInputStream data, Attributes rsp)
                throws IOException {
            rsp.setInt(Tag.Status, VR.US, status);
            if (storageDir == null)
                return;

            String ipAddress = as.getSocket().getInetAddress().getHostAddress(); //ip address
            String associationName = as.toString();
            String cuid = rq.getString(Tag.AffectedSOPClassUID);
            String iuid = rq.getString(Tag.AffectedSOPInstanceUID);
            String tsuid = pc.getTransferSyntax();

            //File file = new File(storageDir, ipAddress + "_" + iuid + DCM_EXT);
            File file = new File(storageDir, iuid + DCM_EXT);
            try {
                LOG.info("as: {}", as);
                storeTo(as, as.createFileMetaInformation(iuid, cuid, tsuid), data, file);
                if (!file.exists()) {
                    LOG.error(
                            "File {} does not exists! Connection Details--> ipAddress: {}  associationName: {}  sopclassuid: {}  sopinstanceuid: {} transfersyntax: {}",
                            file.getAbsolutePath(), ipAddress, associationName, cuid, iuid, tsuid);
                    return;
                }

                // 发布接收到新文件的事件
                eventBus.post(new NewFileEvent(file));
            } catch (EOFException e) {
                // 它虽然是异常，但其实是正常运行结束的标志。
                // EOF表示读到了文件尾（ String str = dis.readUTF(); ，
                // 客户端已经断开，后面已经没有内容可以读了），发送结束自然连接也就断开了。
                LOG.error("Dicom Store EOFException: " + e.getMessage());
            } catch (Exception e) {
                //broken file, just remove...
                deleteFile(as, file);
                LOG.error("Dicom Store Exception: " + e.getMessage());
            }
        }
    }
}
