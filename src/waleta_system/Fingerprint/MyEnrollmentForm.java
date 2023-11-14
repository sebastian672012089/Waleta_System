package waleta_system.Fingerprint;

import com.digitalpersona.onetouch.*;
import com.digitalpersona.onetouch.processing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import waleta_system.Class.Utility;

public class MyEnrollmentForm extends CaptureForm {

    String id_pegawai;
    String jenisFinger;
    
    private DPFPEnrollment enroller = DPFPGlobal.getEnrollmentFactory().createEnrollment();

    public MyEnrollmentForm(Frame owner, String id_pegawai, String jenisFinger) {
        super(owner);
        this.id_pegawai = id_pegawai;
        this.jenisFinger = jenisFinger;
    }

    @Override
    protected void init() {
        super.init();
        this.setTitle("Fingerprint Enrollment");
        updateStatus();
    }

    @Override
    protected void process(DPFPSample sample) {
        super.process(sample);
        // Process the sample and create a feature set for the enrollment purpose.
        DPFPFeatureSet features = extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        // Check quality of the sample and add to enroller if it's good
        if (features != null) {
            try {
                makeReport("The fingerprint feature set was created.");
                enroller.addFeatures(features);		// Add feature set to template.
            } catch (DPFPImageQualityException ex) {
            } finally {
                updateStatus();

                // Check if template has been created.
                switch (enroller.getTemplateStatus()) {
                    case TEMPLATE_STATUS_READY:	// report success and stop capturing
                        stop();
                        try {
                            
                            Connection con = Utility.db.getConnection();
                            PreparedStatement st;
                            st = con.prepareStatement("INSERT INTO `tb_karyawan_fingerprint`(`id_pegawai`, `" + jenisFinger + "`) VALUES (?,?) ON DUPLICATE KEY UPDATE `id_pegawai`=?, `" + jenisFinger + "`=?");
                            st.setString(1, id_pegawai);
                            st.setString(3, id_pegawai);
                            if (enroller.getTemplate() != null) {
                                st.setBytes(2, enroller.getTemplate().serialize());
                                st.setBytes(4, enroller.getTemplate().serialize());
                            } else {
                                st.setNull(2, java.sql.Types.BLOB);
                                st.setNull(4, java.sql.Types.BLOB);
                            }
                            st.executeUpdate();
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }

                        setPrompt("Click Close, and then click Fingerprint Verification.");
                        this.dispose();
                        break;

                    case TEMPLATE_STATUS_FAILED:	// report failure and restart capturing
                        enroller.clear();
                        stop();
                        updateStatus();
                        try {
                            
                            Connection con = Utility.db.getConnection();
                            PreparedStatement st;
                            st = con.prepareStatement("INSERT INTO `tb_karyawan_fingerprint`(`id_pegawai`, `" + jenisFinger + "`) VALUES (?,?) ON DUPLICATE KEY UPDATE `id_pegawai`=?, `" + jenisFinger + "`=?");
                            st.setString(1, id_pegawai);
                            st.setNull(2, java.sql.Types.BLOB);
                            st.setString(3, id_pegawai);
                            st.setNull(4, java.sql.Types.BLOB);
                            st.executeUpdate();
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                        JOptionPane.showMessageDialog(MyEnrollmentForm.this, "The fingerprint template is not valid. Repeat fingerprint enrollment.", "Fingerprint Enrollment", JOptionPane.ERROR_MESSAGE);
                        start();
                        break;
                }
            }
        }
    }

    private void updateStatus() {
        // Show number of samples needed.
        setStatus(String.format("Fingerprint samples needed: %1$s", enroller.getFeaturesNeeded()));
    }

}
