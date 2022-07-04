package frame;

import helpers.ComboBoxItem;
import helpers.koneksi;

import javax.swing.*;
import java.sql.*;

public class JenisInputFrame extends JFrame{
    private JPanel mainPanel;
    private JTextField idTextField;
    private JTextField namaTextField;
    private JPanel buttonPanel;
    private JButton simpanButton;
    private JButton batalButton;
    private JComboBox jenisComboBox;
    private JRadioButton freebaseRadioButton;
    private JRadioButton saltnicRadioButton;
    private JTextField wattTextField;
    private JTextField ohmTextField;
    private JLabel luasLabel;
    private ButtonGroup klasifikasiButtonGoup;

    private int id;

    public void setId(int id){
        this.id = id;
    }

    public JenisInputFrame(){
        batalButton.addActionListener(e -> {
            dispose();
        });
        simpanButton.addActionListener(e ->{
            String nama = namaTextField.getText();
            if (nama.equals("")){
                JOptionPane.showMessageDialog(null,"Isi Nama RokoK Elektrik","Validasi kata kunci kosong",JOptionPane.WARNING_MESSAGE);
                namaTextField.requestFocus();
                return;
            }

            ComboBoxItem item = (ComboBoxItem) jenisComboBox.getSelectedItem();
            int rokoelektrik_id = item.getValue();
            if (rokoelektrik_id == 0){
                JOptionPane.showMessageDialog(null,"Pilih Rokok Elektrik","Validasi ComboBox",JOptionPane.WARNING_MESSAGE);
                jenisComboBox.requestFocus();
                return;
            }
            String klasifikasi = "";
            if (freebaseRadioButton.isSelected()){
                klasifikasi = "FREEBASE";
            } else if (saltnicRadioButton.isSelected()){
                klasifikasi = "SALTNIC";
            } else {
                JOptionPane.showMessageDialog(null,"Pilih klasifikasi","Validasi Data Kosong",JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (wattTextField.getText().equals("")){
                wattTextField.setText("0");
            }
            int watt = Integer.parseInt(wattTextField.getText());
            if (watt == 0){
                JOptionPane.showMessageDialog(null,"Isi watt","Validasi Data Kosong",JOptionPane.WARNING_MESSAGE);
                wattTextField.requestFocus();
                return;
            }

            if (ohmTextField.getText().equals("")){
                ohmTextField.setText("0");
            }

            double ohm = Float.parseFloat(ohmTextField.getText());
            if (ohm == 0){
                JOptionPane.showMessageDialog(null,"Isi ohm","Validasi Data Kosong",JOptionPane.WARNING_MESSAGE);
                ohmTextField.requestFocus();
                return;
            }

//            String email = emailTextField.getText();
//            if (!email.contains("@") || !email.contains(".")){
//                JOptionPane.showMessageDialog(null,"Isi dengan email valid","Validasi Email",JOptionPane.WARNING_MESSAGE);
//                emailTextField.requestFocus();
//                return;
//            }

            Connection c = koneksi.getConnection();
            PreparedStatement ps;
            try {
                if (id == 0) {
                    String cekSQL = "SELECT * FROM jenisrokokelektrik WHERE nama= ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1,nama);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null,"Data sama sudah ada");
                    } else {
                        String insertSQL = "INSERT INTO jenisrokokelektrik (id,nama,rokoelektrik_id,liquid,watt,ohm) VALUES (NULL, ?, ?, ?, ?, ?)";
                        ps = c.prepareStatement(insertSQL);
                        ps.setString(1,nama);
                        ps.setInt(2,rokoelektrik_id);
                        ps.setString(3,klasifikasi);
                        ps.setInt(4,watt);
                        ps.setDouble(5,ohm);
                        ps.executeUpdate();
                        dispose();
                    }

                } else {
                    String cekSQL = "SELECT * FROM jenisrokokelektrik WHERE nama= ? AND id != ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1,nama);
                    ps.setInt(2,id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null,"Data sama sudah ada");
                    } else {
                        String updateSQL = "UPDATE jenisrokokelektrik SET nama= ?, rokoelektrik_id= ?, liquid = ?, watt = ?, ohm = ? WHERE id= ?";
                        ps = c.prepareStatement(updateSQL);
                        ps.setString(1,nama);
                        ps.setInt(2,rokoelektrik_id);
                        ps.setString(3,klasifikasi);
                        ps.setInt(4,watt);
                        ps.setDouble(5,ohm);
                        ps.setInt(6,id);
                        ps.executeUpdate();
                        dispose();
                    }

                }

            } catch (SQLException ex) {
                throw  new RuntimeException(ex);
            }
        });
        kustomisasiKomponen();
        init();
    }
    public void isiKomponen(){
        Connection c = koneksi.getConnection();
        String findSQL = "SELECT * FROM jenisrokokelektrik WHERE id= ?";
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement(findSQL);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                idTextField.setText(String.valueOf(rs.getInt("id")));
                namaTextField.setText(rs.getString("nama"));
                wattTextField.setText(String.valueOf(rs.getInt("watt")));
                ohmTextField.setText(String.valueOf(rs.getDouble("ohm")));
                int rokoelektrik_Id = rs.getInt("rokoelektrik_Id");
                for (int i = 0; i < jenisComboBox.getItemCount(); i++){
                    jenisComboBox.setSelectedIndex(i);
                    ComboBoxItem item = (ComboBoxItem) jenisComboBox.getSelectedItem();
                    if (rokoelektrik_Id == item.getValue()){
                        break;
                    }
                }
                String klasifikasi = rs.getString("liquid");
                if (klasifikasi != null){
                    if (klasifikasi.equals("FREEBASE")){
                        freebaseRadioButton.setSelected(true);
                    } else if (klasifikasi.equals("SALTNIC")){
                        saltnicRadioButton.setSelected(true);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Input Jenis Rokok Electrik");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void kustomisasiKomponen(){
        Connection c = koneksi.getConnection();
        String selectSQL = "SELECT * FROM rokoelektrik ORDER BY nama";
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(selectSQL);
            jenisComboBox.addItem(new ComboBoxItem(0,"Pilih Jenis"));
            while (rs.next()){
                jenisComboBox.addItem(new ComboBoxItem(rs.getInt("id"),rs.getString("nama")));
            }
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
        klasifikasiButtonGoup = new ButtonGroup();
        klasifikasiButtonGoup.add(freebaseRadioButton);
        klasifikasiButtonGoup.add(saltnicRadioButton);

//        luasLabel.setText("Luas (Km\u00B2)");
//        silinderTextField.setHorizontalAlignment(SwingConstants.RIGHT);
//        silinderTextField.setText("0");
//        silinderTextField.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent ke) {
//                silinderTextField.setEditable(
//                        (ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE || ke.getKeyCode() == KeyEvent.VK_LEFT || ke.getKeyCode() == KeyEvent.VK_RIGHT);
//            }
//        });

//        luasTextField.setHorizontalAlignment(SwingConstants.RIGHT);
//        luasTextField.setText("0");
//        luasTextField.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent ke) {
//                luasTextField.setEditable(
//                        (ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE || ke.getKeyCode() == KeyEvent.VK_LEFT || ke.getKeyCode() == KeyEvent.VK_RIGHT || ke.getKeyCode() == KeyEvent.VK_PERIOD);
//            }
//        });
    }
    }

