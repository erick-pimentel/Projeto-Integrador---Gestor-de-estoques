package gestorestoques;

import java.awt.Component;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;

public class relatorios extends javax.swing.JFrame {

    private String perfil;
    private int idUsuario;

    public relatorios(String perfil, int idUsuario) {
        initComponents();
        this.perfil = perfil;
        this.idUsuario = idUsuario;

        // Configurar JTable
        DefaultTableModel model = (DefaultTableModel) table_relatorios.getModel();
        model.setColumnIdentifiers(new Object[]{"Semana", "Período", "Total Produtos", "Produtos Vendidos", "Faturamento", "Detalhes"});
        table_relatorios.getColumn("Detalhes").setCellRenderer(new ButtonRenderer());
        table_relatorios.getColumn("Detalhes").setCellEditor(new ButtonEditor(new JCheckBox()));

        carregarRelatoriosPassados();
    }

    private void carregarRelatoriosPassados() {
        String url = "jdbc:mysql://localhost:3306/gestor_estoque";
        String user = "root";
        String pass = "1234";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String sql = "SELECT semana, ano, data_inicial, data_final, quantidade_produtos, produtos_vendidos, faturamento, descricao_produtos " +
                         "FROM relatorios WHERE id_usuario = ? ORDER BY ano DESC, semana DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, idUsuario);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table_relatorios.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                int semana = rs.getInt("semana");
                int ano = rs.getInt("ano");
                int totalProdutos = rs.getInt("quantidade_produtos");
                int produtosVendidos = rs.getInt("produtos_vendidos");
                double faturamento = rs.getDouble("faturamento");
                Date dataInicial = rs.getDate("data_inicial");
                Date dataFinal = rs.getDate("data_final");
                String descricao = rs.getString("descricao_produtos");

                String periodo = dataInicial + " a " + dataFinal;

                model.addRow(new Object[]{semana + "/" + ano, periodo, totalProdutos, produtosVendidos, faturamento, descricao});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar relatórios passados: " + e.getMessage());
        }
    }
    
    private void gerarRelatorioAtual() {
        String url = "jdbc:mysql://localhost:3306/gestor_estoque";
        String user = "root";
        String pass = "1234";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            // Definir período: última semana completa
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
            Date dataInicial = new Date(cal.getTimeInMillis());
            cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
            Date dataFinal = new Date(cal.getTimeInMillis());

            // Consultar movimentações do período
            String sqlMov = "SELECT tipo_movimentacao, SUM(quantidade_movimentacao) AS total_produtos " +
                            "FROM movimentacoes WHERE data_movimentacao BETWEEN ? AND ? GROUP BY tipo_movimentacao";
            PreparedStatement pst = conn.prepareStatement(sqlMov);
            pst.setDate(1, dataInicial);
            pst.setDate(2, dataFinal);
            ResultSet rs = pst.executeQuery();

            int totalProdutos = 0;
            int produtosVendidos = 0;
            double faturamento = 0.0;
            StringBuilder descricao = new StringBuilder();

            while (rs.next()) {
                String tipo = rs.getString("tipo_movimentacao");
                int qtd = rs.getInt("total_produtos");
                totalProdutos += qtd;

                if (tipo.equals("Saída")) {
                    produtosVendidos += qtd;
                    // aqui você pode calcular faturamento se tiver preço unitário
                    descricao.append("Saídas: ").append(qtd).append(" | ");
                } else if (tipo.equals("Entrada")) {
                    descricao.append("Entradas: ").append(qtd).append(" | ");
                } else {
                    descricao.append("Ajustes: ").append(qtd).append(" | ");
                }
            }

            // Inserir relatório na tabela relatorios
            java.util.Calendar calSemana = java.util.Calendar.getInstance();
            calSemana.setTime(dataInicial);
            int semana = calSemana.get(java.util.Calendar.WEEK_OF_YEAR);
            int ano = calSemana.get(java.util.Calendar.YEAR);

            String sqlInsert = "INSERT INTO relatorios (id_usuario, semana, ano, data_inicial, data_final, quantidade_produtos, produtos_vendidos, faturamento, descricao_produtos) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstInsert = conn.prepareStatement(sqlInsert);
            pstInsert.setInt(1, idUsuario);
            pstInsert.setInt(2, semana);
            pstInsert.setInt(3, ano);
            pstInsert.setDate(4, dataInicial);
            pstInsert.setDate(5, dataFinal);
            pstInsert.setInt(6, totalProdutos);
            pstInsert.setInt(7, produtosVendidos);
            pstInsert.setDouble(8, faturamento);
            pstInsert.setString(9, descricao.toString());
            pstInsert.executeUpdate();

            JOptionPane.showMessageDialog(this, "Relatório gerado com sucesso!");
            carregarRelatoriosPassados();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage());
        }
    }

    // Renderer do botão
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Ver Detalhes" : "Ver Detalhes");
            return this;
        }
    }

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;
        private String detalhes;

        public ButtonEditor(JCheckBox checkBox) {
            button = new JButton("Ver Detalhes");
            button.addActionListener(e -> {
                JOptionPane.showMessageDialog(button, detalhes, "Relatório Detalhado", JOptionPane.INFORMATION_MESSAGE);
            });
        }

        @Override
        public Object getCellEditorValue() {
            return detalhes;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            detalhes = (String) value;
            return button;
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

        jScrollPane2 = new javax.swing.JScrollPane();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_relatorios = new javax.swing.JTable();
        combo_relatoriosFiltro = new javax.swing.JComboBox<>();
        btn_relatoriosVoltar = new javax.swing.JButton();
        btn_relatorioGerar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("RELATÓRIOS");

        table_relatorios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Semana", "Período", "Faturamento"}
        ));
        jScrollPane1.setViewportView(table_relatorios);

        combo_relatoriosFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btn_relatoriosVoltar.setBackground(new java.awt.Color(0, 255, 0));
        btn_relatoriosVoltar.setText("Voltar");
        btn_relatoriosVoltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_relatoriosVoltarActionPerformed(evt);
            }
        });

        btn_relatorioGerar.setBackground(new java.awt.Color(0, 255, 255));
        btn_relatorioGerar.setText("GERAR RELATÓRIO");
        btn_relatorioGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_relatorioGerarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(combo_relatoriosFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_relatorioGerar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_relatoriosVoltar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(35, 35, 35))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_relatorioGerar, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_relatoriosVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(combo_relatoriosFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_relatoriosVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_relatoriosVoltarActionPerformed
        menuView menu = new menuView(this.perfil, this.idUsuario);
        menu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btn_relatoriosVoltarActionPerformed

    private void btn_relatorioGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_relatorioGerarActionPerformed
        String url = "jdbc:mysql://localhost:3306/gestor_estoque";
        String usuarioDB = "root";
        String senhaDB = "1234";

        try (Connection conn = DriverManager.getConnection(url, usuarioDB, senhaDB)) {
            DefaultTableModel model = (DefaultTableModel) table_relatorios.getModel();
            model.setRowCount(0);

            String sql = "SELECT YEAR(data_movimentacao) AS ano, "
                       + "WEEK(data_movimentacao) AS semana, "
                       + "SUM(CASE WHEN tipo_movimentacao='Entrada' THEN quantidade_movimentacao ELSE 0 END) AS total_entrada, "
                       + "SUM(CASE WHEN tipo_movimentacao='Saída' THEN quantidade_movimentacao ELSE 0 END) AS total_saida, "
                       + "SUM(CASE WHEN tipo_movimentacao='Ajuste' THEN quantidade_movimentacao ELSE 0 END) AS total_ajuste "
                       + "FROM movimentacoes "
                       + "GROUP BY ano, semana "
                       + "ORDER BY ano DESC, semana DESC";

            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int semana = rs.getInt("semana");
                int ano = rs.getInt("ano");
                int entrada = rs.getInt("total_entrada");
                int saida = rs.getInt("total_saida");
                int ajuste = rs.getInt("total_ajuste");

                String semanaAno = "Semana " + semana + "/" + ano;

                model.addRow(new Object[]{semanaAno, entrada, saida, ajuste, "Ver Detalhes"});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatórios: " + e.getMessage());
        }
    }//GEN-LAST:event_btn_relatorioGerarActionPerformed


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
            java.util.logging.Logger.getLogger(relatorios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(relatorios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(relatorios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(relatorios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_relatorioGerar;
    private javax.swing.JButton btn_relatoriosVoltar;
    private javax.swing.JComboBox<String> combo_relatoriosFiltro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable table_relatorios;
    // End of variables declaration//GEN-END:variables
}
