/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gestorestoques;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;


/**
 *
 * @author Ericz
 */
public class gestaoDeUser extends javax.swing.JFrame {

    /**
     * Creates new form gestaoDeUser
     */
    
    String url = "jdbc:mysql://localhost:3306/gestor_estoque"; // seu banco
    String user = "root"; // seu usuário
    String password = "1234"; // sua senha
    Connection conn;
    
    private String perfil;
    private int idUsuario;
    
    public gestaoDeUser(String perfil, int idUsuario) {
        initComponents();
        
        this.perfil=perfil;
        this.idUsuario=idUsuario;
        
        conectar();
        carregarUsuarios();
        adicionarBotoesNaTabela();
    }
    
    private void conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Conectado com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro na conexão: " + e.getMessage());
        }
    }
    
    private void carregarUsuarios() {
        DefaultTableModel model = (DefaultTableModel) tableGestao.getModel();
        model.setRowCount(0); // limpa tabela antes de carregar

        String sql = "SELECT nome_usuario, perfil_usuario FROM usuarios";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nome = rs.getString("nome_usuario");
                String nivel = rs.getString("perfil_usuario");

                // adiciona linha com botão "Detalhes"
                model.addRow(new Object[]{nome, nivel, "Ver Detalhes"});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + e.getMessage());
        }
    }
     
    private void adicionarBotoesNaTabela() {
        // coluna do botão (assumindo que seja a terceira)
        tableGestao.getColumn("DETALHES").setCellRenderer(new ButtonRenderer());
        tableGestao.getColumn("DETALHES").setCellEditor(new ButtonEditor(new JCheckBox()));
    }
    
    private void mostrarDetalhesUsuario(int row) {
        String nome = tableGestao.getValueAt(row, 0).toString();
        try {
            String sql = "SELECT * FROM usuarios WHERE nome_usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email_usuario");
                String perfil = rs.getString("perfil_usuario");
                String cadastro = rs.getString("data_cadastro");
                String ultimoAcesso = rs.getString("ultimo_acesso");
                boolean ativo = rs.getBoolean("usuario_ativo");

                JOptionPane.showMessageDialog(this,
                    "Nome: " + nome +
                    "\nEmail: " + email +
                    "\nPerfil: " + perfil +
                    "\nCadastro: " + cadastro +
                    "\nÚltimo Acesso: " + ultimoAcesso +
                    "\nAtivo: " + (ativo ? "Sim" : "Não"),
                    "Detalhes do Usuário", JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar detalhes: " + e.getMessage());
        }
    }

    // ---------------- Classes do botão ----------------
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Detalhes" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean clicked;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "Detalhes" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                mostrarDetalhesUsuario(row);
            }
            clicked = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableGestao = new javax.swing.JTable();
        btn_userAdd = new javax.swing.JButton();
        btn_userEdit = new javax.swing.JButton();
        btn_menuVoltar = new javax.swing.JButton();
        comboFiltro = new javax.swing.JComboBox<>();
        btn_deleteUser = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("GESTÃO DE USUÁRIOS");

        tableGestao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"NOME", "ACESSO", "DETALHES"}
        ));
        jScrollPane2.setViewportView(tableGestao);

        btn_userAdd.setBackground(new java.awt.Color(0, 255, 30));
        btn_userAdd.setText("ADICIONAR USUÁRIO");
        btn_userAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_userAddActionPerformed(evt);
            }
        });

        btn_userEdit.setBackground(new java.awt.Color(0, 255, 255));
        btn_userEdit.setText("EDITAR USUÁRIO");
        btn_userEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_userEditActionPerformed(evt);
            }
        });

        btn_menuVoltar.setBackground(new java.awt.Color(0, 255, 0));
        btn_menuVoltar.setText("VOLTAR");
        btn_menuVoltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_menuVoltarActionPerformed(evt);
            }
        });

        comboFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Administrador", "Gestor", "Operador de estoque" }));
        comboFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboFiltroActionPerformed(evt);
            }
        });

        btn_deleteUser.setBackground(new java.awt.Color(255, 0, 0));
        btn_deleteUser.setText("EXCLUIR USUÁRIO");
        btn_deleteUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_deleteUserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(comboFiltro, 0, 103, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_menuVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_userAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_userEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_deleteUser, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(110, 110, 110))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btn_menuVoltar)
                    .addComponent(comboFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_userEdit, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                    .addComponent(btn_userAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_deleteUser, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_userEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_userEditActionPerformed
        int row = tableGestao.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para editar.");
            return;
        }

        String nome = tableGestao.getValueAt(row, 0).toString();

        try {
            String sql = "SELECT * FROM usuarios WHERE nome_usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email_usuario");
                String perfil = rs.getString("perfil_usuario");
                boolean ativo = rs.getBoolean("usuario_ativo");

                // Editando dados com JOptionPane simples
                String novoEmail = JOptionPane.showInputDialog(this, "Email:", email);
                String novoPerfil = JOptionPane.showInputDialog(this, "Perfil (Admin/Gerente/Funcionário):", perfil);
                int ativoInt = JOptionPane.showConfirmDialog(this, "Ativo?", "Status", JOptionPane.YES_NO_OPTION);

                // Atualiza no banco
                String updateSQL = "UPDATE usuarios SET email_usuario=?, perfil_usuario=?, usuario_ativo=? WHERE nome_usuario=?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                updateStmt.setString(1, novoEmail);
                updateStmt.setString(2, novoPerfil);
                updateStmt.setBoolean(3, ativoInt == JOptionPane.YES_OPTION);
                updateStmt.setString(4, nome);
                updateStmt.executeUpdate();

            
                carregarUsuarios();
                adicionarBotoesNaTabela();

                JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao editar usuário: " + e.getMessage());
        }
    }//GEN-LAST:event_btn_userEditActionPerformed

    private void btn_userAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_userAddActionPerformed
        try {
            String nome = JOptionPane.showInputDialog(this, "Nome do usuário:");
            if (nome == null || nome.trim().isEmpty()) return;

            String email = JOptionPane.showInputDialog(this, "Email do usuário:");
            if (email == null || email.trim().isEmpty()) return;

            String senha = JOptionPane.showInputDialog(this, "Senha do usuário:");
            if (senha == null || senha.trim().isEmpty()) return;

            String perfil = JOptionPane.showInputDialog(this, "Perfil (Administrador/Gestor/Operador de estoque):");
            if (perfil == null || perfil.trim().isEmpty()) return;

            boolean ativo = JOptionPane.showConfirmDialog(this, "Ativo?", "Status", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

            // Inserção no banco
            String sql = "INSERT INTO usuarios (nome_usuario, email_usuario, senha_usuario, perfil_usuario, usuario_ativo) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha); // futuramente pode ser hash
            stmt.setString(4, perfil);
            stmt.setBoolean(5, ativo);
            stmt.executeUpdate();

            // Recarrega tabela
            carregarUsuarios();
            adicionarBotoesNaTabela();

            JOptionPane.showMessageDialog(this, "Usuário adicionado com sucesso!");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar usuário: " + e.getMessage());
        }
    }//GEN-LAST:event_btn_userAddActionPerformed

    private void btn_menuVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_menuVoltarActionPerformed
        menuView menu = new menuView(this.perfil, this.idUsuario);
        menu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btn_menuVoltarActionPerformed

    private void comboFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboFiltroActionPerformed
        String perfilSelecionado = comboFiltro.getSelectedItem().toString();
        DefaultTableModel model = (DefaultTableModel) tableGestao.getModel();
        model.setRowCount(0); // limpa a tabela

        String sql;
        if (perfilSelecionado.equals("Todos")) {
            sql = "SELECT nome_usuario, perfil_usuario FROM usuarios";
        } else {
            sql = "SELECT nome_usuario, perfil_usuario FROM usuarios WHERE perfil_usuario = ?";
        }

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (!perfilSelecionado.equals("Todos")) {
                stmt.setString(1, perfilSelecionado);
            }
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String nome = rs.getString("nome_usuario");
                String perfil = rs.getString("perfil_usuario");
                model.addRow(new Object[]{nome, perfil, "Ver Detalhes"});
            }

            adicionarBotoesNaTabela(); // mantém os botões funcionando

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao filtrar: " + e.getMessage());
        }
    }//GEN-LAST:event_comboFiltroActionPerformed

    private void btn_deleteUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_deleteUserActionPerformed
        int row = tableGestao.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para deletar.");
            return;
        }

        String nome = tableGestao.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja deletar o usuário: " + nome + "?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return; // usuário cancelou
        }

        try {
            String sql = "DELETE FROM usuarios WHERE nome_usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            int excluidos = stmt.executeUpdate();

            if (excluidos > 0) {
                JOptionPane.showMessageDialog(this, "Usuário deletado com sucesso!");
                carregarUsuarios();
                adicionarBotoesNaTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro: usuário não encontrado.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar usuário: " + e.getMessage());
        }
    }//GEN-LAST:event_btn_deleteUserActionPerformed

    /**
     * @param args the command line arguments
     */
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(gestaoDeUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(gestaoDeUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(gestaoDeUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(gestaoDeUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_deleteUser;
    private javax.swing.JButton btn_menuVoltar;
    private javax.swing.JButton btn_userAdd;
    private javax.swing.JButton btn_userEdit;
    private javax.swing.JComboBox<String> comboFiltro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tableGestao;
    // End of variables declaration//GEN-END:variables
}
