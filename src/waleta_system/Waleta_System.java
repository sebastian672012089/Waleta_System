package waleta_system;

public class Waleta_System {
    
    public static void main(String[] args) {
        // TODO code application logic here
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Login in = new Login();
                in.pack();        
                in.setLocationRelativeTo(null);
                in.setVisible(true);
//                new MainForm().setVisible(true);
            }
        });
    }
    
}
