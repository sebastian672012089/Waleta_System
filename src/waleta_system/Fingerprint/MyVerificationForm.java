package waleta_system.Fingerprint;

import com.digitalpersona.onetouch.*;
import com.digitalpersona.onetouch.verification.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.EnumMap;
import waleta_system.Class.Utility;
import waleta_system.HRD.JPanel_Data_Karyawan;

public class MyVerificationForm extends CaptureForm {

    String id_pegawai;
    String jenisFinger;
    JPanel_Data_Karyawan jPanel_DataKaryawan;
    
    private DPFPVerification verificator = DPFPGlobal.getVerificationFactory().createVerification();
    private EnumMap<DPFPFingerIndex, DPFPTemplate> templates = new EnumMap<DPFPFingerIndex, DPFPTemplate>(DPFPFingerIndex.class);

    public MyVerificationForm(Frame owner, JPanel_Data_Karyawan jPanel_DataKaryawan, String id_pegawai, String jenisFinger) {
        super(owner);
        this.id_pegawai = id_pegawai;
        this.jenisFinger = jenisFinger;
        this.jPanel_DataKaryawan = jPanel_DataKaryawan;
    }

    @Override
    protected void init() {
        super.init();
        this.setTitle("Fingerprint Enrollment");
        updateStatus(0);
    }

    @Override
    protected void process(DPFPSample sample) {
        super.process(sample);

        // Process the sample and create a feature set for the enrollment purpose.
        DPFPFeatureSet features = extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        // Check quality of the sample and start verification if it's good
        if (features != null) {
            // Compare the feature set with our template
            try {
                
                Connection con = Utility.db.getConnection();

                ResultSet rs;
                PreparedStatement st;

                st = con.prepareStatement("SELECT `" + jenisFinger + "` as finger FROM tb_karyawan_fingerprint WHERE id_pegawai = '" + id_pegawai + "'");
                rs = st.executeQuery();
                if (rs.next()) {
                    byte[] byte_finger = rs.getBytes("finger");
                    if (!rs.wasNull()) {
                        DPFPTemplate finger = DPFPGlobal.getTemplateFactory().createTemplate();
                        finger.deserialize(byte_finger);

                        DPFPVerificationResult result = verificator.verify(features, finger);
                        updateStatus(result.getFalseAcceptRate());
                        if (result.isVerified()) {
                            makeReport("The fingerprint was VERIFIED.");
                        } else {
                            makeReport("The fingerprint was NOT VERIFIED.");
                        }
                        jPanel_DataKaryawan.hasilFinger(result.isVerified());
                    }
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void updateStatus(int FAR) {
        // Show "False accept rate" value
        setStatus(String.format("False Accept Rate (FAR) = %1$s", FAR));
    }

}
