package frame;

import helpers.JasperDataSourceBuilder;
import helpers.koneksi;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class RokokElektrikViewFrame extends JFrame{
    private JPanel panel1;
    private JPanel mainPanel;
    private JPanel cariPanel;
    private JScrollPane viewScrollPanel;
    private JPanel buttonPanel;
    private JTextField cariTextField;
    private JButton cariButton;
    private JTable viewTable;
    private JButton tambahButton;
    private JButton ubahButton;
    private JButton hapusButton;
    private JButton batalButton;
    private JButton cetakButton;
    private JButton tutupButton;

    public RokokElektrikViewFrame(){
        tutupButton.addActionListener(e -> {
            dispose();
        });
        batalButton.addActionListener(e -> {
            isiTable();
        });
        addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e){
                isiTable();
            }
        });
        tambahButton.addActionListener(e -> {
            RokokElektrikInputFrame inputFrame = new RokokElektrikInputFrame();
            inputFrame.setVisible(true);
        });
        cariButton.addActionListener(e -> {
            if (cariTextField.getText().equals("")){
                JOptionPane.showMessageDialog(null,"Isi kata kunci pencarian","Validasi kata kunci kosong",JOptionPane.WARNING_MESSAGE);
                cariTextField.requestFocus();
                return;
            }
            Connection c = koneksi.getConnection();
            String keyword = "%" + cariTextField.getText() + "%";
            String searchSQL = "select * from rokoelektrik WHERE nama like ?";
            try {
                PreparedStatement ps = c.prepareStatement(searchSQL);
                ps.setString(1,keyword);
                ResultSet rs = ps.executeQuery();
                DefaultTableModel dtm = (DefaultTableModel) viewTable.getModel();
                dtm.setRowCount(0);
                Object[] row = new Object[2];
                while (rs.next()){
                    row[0] = rs.getInt("id");
                    row[1] = rs.getString("nama");
                    dtm.addRow(row);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        ubahButton.addActionListener(e -> {
            int barisTerpilih = viewTable.getSelectedRow();
            if(barisTerpilih < 0) {
                JOptionPane.showMessageDialog(null,"Pilih data dulu");
                return;
            }
            TableModel tm = viewTable.getModel();
            int id = Integer.parseInt(tm.getValueAt(barisTerpilih,0).toString());
            RokokElektrikInputFrame inputFrame = new RokokElektrikInputFrame();
            inputFrame.setId(id);
            inputFrame.isiKomponen();
            inputFrame.setVisible(true);
        });



        hapusButton.addActionListener(e -> {
            int barisTerpilih = viewTable.getSelectedRow();
            if (barisTerpilih < 0){
                JOptionPane.showMessageDialog(null,"Pilih data dulu");
                return;
            }
            int pilihan = JOptionPane.showConfirmDialog(null,"Yakin mau dihapus ?","Konfirmasi Hapus",JOptionPane.YES_NO_OPTION);
            if (pilihan == 0){
                TableModel tm = viewTable.getModel();
                int id = Integer.parseInt(tm.getValueAt(barisTerpilih,0).toString());
                Connection c = koneksi.getConnection();
                String deleteSQL = "DELETE FROM rokoelektrik WHERE id = ?";
                try {
                    PreparedStatement ps = c.prepareStatement(deleteSQL);
                    ps.setInt(1,id);
                    ps.executeUpdate();
                } catch (SQLException ex){
                    throw new RuntimeException(ex);
                }
            }
        });

        cetakButton.addActionListener(e -> {
            Connection c = koneksi.getConnection();
            String selectSQL = "SELECT * FROM rokoelektrik";
            Object[][] row;
            try {
                Statement s = c.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = s.executeQuery(selectSQL);
                rs.last();
                int jumlah = rs.getRow();
                row = new Object[jumlah][2];
                int i = 0;
                rs.beforeFirst();
                while (rs.next()){
                    row[i][0] = rs.getInt("id");
                    row[i][1] = rs.getString("nama");
                    i++;
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            try {
                JasperReport jasperReport = JasperCompileManager.compileReport("/MATERI KULIAH/SEMESTER 4/PBO1/ProjectJava/src/main/resources/RokokElektrik_report.jrxml");
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,null, new JasperDataSourceBuilder(row));
                JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setVisible(true);
            } catch (JRException ex) {
                throw new RuntimeException(ex);
            }
        });

        isiTable();
        init();
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Data Rokok Elektrik");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
    }

    public void isiTable(){
        Connection c = koneksi.getConnection();
        String selectSQL = "select * from rokoelektrik";
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(selectSQL);
            String header[] = {"id","Nama Roko Elektrik"};
            DefaultTableModel dtm = new DefaultTableModel(header,0);
            viewTable.setModel(dtm);
            Object[] row = new Object[2];
            while (rs.next()){
                row[0] = rs.getInt("id");
                row[1] = rs.getString("nama");
                dtm.addRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
