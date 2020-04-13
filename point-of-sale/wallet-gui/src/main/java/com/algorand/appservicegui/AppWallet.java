/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algorand.appservicegui;

import com.algorand.app.service.core.TransactionEnvelope;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.app.service.core.UserAccountRegistration;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.ws.rs.core.GenericType;

class UserAccount {

    public String alias;
    public String address;
    public String passPhrase;
    public Account account;

    public static UserAccount CreateUserAccount(String alias, String passPhrase) throws GeneralSecurityException {
        UserAccount userAccount = new UserAccount();
        userAccount.alias = alias;
        userAccount.passPhrase = passPhrase;
        userAccount.account = userAccount.getAccountForPassPhrase();
        userAccount.address = userAccount.account.getAddress().encodeAsString();
        return userAccount;
    }

    private Account getAccountForPassPhrase() throws GeneralSecurityException {
        Account newAccount = new Account(passPhrase);
        return newAccount;
    }

    public String toString() {
        return alias;
    }
}

/**
 *
 * @author ericgieseke
 */
public class AppWallet extends javax.swing.JFrame {

    private Logger log = LoggerFactory.getLogger("WalletRESTClientController");
    private String selectedTransactionIndex = null;
    private static final String APP_SERVICE_HOST = "app-service-toronto-hackathon-1811157952.us-east-2.elb.amazonaws.com";
    private static final int APP_SERVICE_PORT = 80;       

    private static String serviceHost = "http://" + APP_SERVICE_HOST + ":" + APP_SERVICE_PORT;

    private List<UserAccount> userAccountList = new ArrayList<>();

    private final String SRC_ACCOUNT = "only atom opera jealous obscure fade drama bicycle near cable company other hazard math argue anxiety corn approve crumble trust hunt cattle parent ability raw";

    final String account1_mnemonic = "portion never forward pill lunch organ biology"  
                           + " weird catch curve isolate plug innocent skin grunt" 
                           + " bounce clown mercy hole eagle soul chunk type absorb trim";
    final String account2_mnemonic = "place blouse sad pigeon wing warrior wild script"
                            + " problem team blouse camp soldier breeze twist mother"
                            + " vanish public glass code arrow execute convince ability"
                            + " there";
    final String account3_mnemonic = "image travel claw climb bottom spot path roast "
                            + "century also task cherry address curious save item "
                            + "clean theme amateur loyal apart hybrid steak about blanket";
    
    final DefaultComboBoxModel userAccountModel = new DefaultComboBoxModel();

    private void initAccounts() {
        try {
            userAccountList.add(UserAccount.CreateUserAccount("Question Sponser", SRC_ACCOUNT));
            userAccountList.add(UserAccount.CreateUserAccount("Bob", account1_mnemonic));
            userAccountList.add(UserAccount.CreateUserAccount("Alice", account2_mnemonic));
            userAccountList.add(UserAccount.CreateUserAccount("Judy", account3_mnemonic));
            
            selectedUserAccount = getUserAccountList().get(0);

            userAccountModel.addAll(userAccountList);
            userAccountModel.setSelectedItem(userAccountModel.getElementAt(0));
            
            publishUserAccountRegistration();


        } catch (GeneralSecurityException gse) {
            log.error("error initialization accounts " + gse);
        }
    }

    private UserAccount selectedUserAccount = null;

    List<UserAccount> getUserAccountList() {
        return userAccountList;
    }

    /**
     * Creates new form CreateTransaction
     */
    public AppWallet() {
        initComponents();

        transactionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here
                int selectedRow = transactionTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedTransactionIndex = transactionTable.getValueAt(selectedRow, 0).toString();
                    System.out.println(selectedTransactionIndex);
                    updateSelectedTransaction();
                }
            }
        });

        transactionTable.setColumnSelectionAllowed(false);
        initAccounts();

        this.addressTextField.setText(selectedUserAccount.address);

        fetchTransactions();
    }

    /**
     * Update selected transaction
     */
    private void updateSelectedTransaction() {
        
        amountTextField.setText("");
        payerAddressTextField.setText("");
        receiverAddressTextField.setText("");
        noteFieldTextField.setText("");
        feeTextField.setText("");
        agreementTextArea.setText("");
        transactionIDTextField.setText("");
        transactionTypeTextField.setText("");
        approveButton.setEnabled(false);
        declineButton.setEnabled(false);
        approveAllTransactionsButton.setEnabled(transactionEnvelopeList.size() > 0);
        for (TransactionEnvelope te : transactionEnvelopeList) {
            if (te.getTransactionPrototype().getApplicationTransactionId() == selectedTransactionIndex) {
                selectedTransactionEnvelope = te;
                amountTextField.setText(String.valueOf(te.getTransactionPrototype().getAmount()));
                payerAddressTextField.setText(te.getTransactionPrototype().getPayer());
                receiverAddressTextField.setText(te.getTransactionPrototype().getReceiver());
                noteFieldTextField.setText(new String(te.getAgreement().getSignature()));
                feeTextField.setText(String.valueOf(te.getTransactionPrototype().getFee()));
                String agreementText = fetchAgreement(new String (te.getAgreement().getSignature()));
                agreementTextArea.setText(agreementText);
                transactionIDTextField.setText(te.getTransactionPrototype().getApplicationTransactionId());
                transactionTypeTextField.setText(te.getTransactionPrototype().getTransactionType().toString());
                approveButton.setEnabled(true);
                declineButton.setEnabled(true);
                break;
            }
        }

    }

    private TransactionEnvelope selectedTransactionEnvelope;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        approveButton1 = new javax.swing.JButton();
        amountTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        payerAddressTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        receiverAddressTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        feeTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        declineButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        agreementTextArea = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        noteFieldTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        transactionIDTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        transactionTable = new javax.swing.JTable();
        approveButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();
        addressTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        transactionTypeTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        accountBalanceTextField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        accountAliasComboBox = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        approveAllTransactionsButton = new javax.swing.JButton();

        approveButton1.setText("Approve");
        approveButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                approveButton1ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        amountTextField.setEditable(false);
        amountTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        amountTextField.setText(" ");
        amountTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                amountTextFieldActionPerformed(evt);
            }
        });

        jLabel1.setText("Amount (Algos)");

        payerAddressTextField.setEditable(false);
        payerAddressTextField.setText(" ");
        payerAddressTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payerAddressTextFieldActionPerformed(evt);
            }
        });

        jLabel2.setText("Payer");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel3.setText("Algorand Application Wallet");

        receiverAddressTextField.setEditable(false);
        receiverAddressTextField.setText(" ");
        receiverAddressTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receiverAddressTextFieldActionPerformed(evt);
            }
        });

        jLabel4.setText("Receiver");

        feeTextField.setEditable(false);
        feeTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        feeTextField.setText(" ");
        feeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                feeTextFieldActionPerformed(evt);
            }
        });

        jLabel5.setText("Fee (uAlgos)");

        declineButton.setText("Decline");
        declineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                declineButtonActionPerformed(evt);
            }
        });

        agreementTextArea.setEditable(false);
        agreementTextArea.setColumns(20);
        agreementTextArea.setRows(5);
        jScrollPane1.setViewportView(agreementTextArea);

        jLabel6.setText("Receipt");

        jLabel7.setText("Note Field: ");

        noteFieldTextField.setEditable(false);
        noteFieldTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteFieldTextFieldActionPerformed(evt);
            }
        });

        jLabel8.setText("ID");

        transactionIDTextField.setEditable(false);
        transactionIDTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        transactionIDTextField.setText(" ");
        transactionIDTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transactionIDTextFieldActionPerformed(evt);
            }
        });

        transactionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Trx Id", "Amount", "Fee", "Receiver", "State", "Type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        transactionTable.setColumnSelectionAllowed(true);
        jScrollPane2.setViewportView(transactionTable);
        transactionTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        approveButton.setText("Approve");
        approveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                approveButtonActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel9.setText("Transactions");

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        addressTextField.setText("address");
        addressTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addressTextFieldActionPerformed(evt);
            }
        });

        jLabel10.setText("Type");

        transactionTypeTextField.setEditable(false);
        transactionTypeTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        transactionTypeTextField.setText(" ");
        transactionTypeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transactionTypeTextFieldActionPerformed(evt);
            }
        });

        jLabel11.setText("Account Balance ");

        accountBalanceTextField.setEditable(false);
        accountBalanceTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        accountBalanceTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accountBalanceTextFieldActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel12.setText("Transaction Details");

        jLabel13.setText("Account");

        accountAliasComboBox.setModel(userAccountModel);
        accountAliasComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accountAliasComboBoxActionPerformed(evt);
            }
        });

        jLabel14.setText("Account Alias");

        approveAllTransactionsButton.setText("Approve All");
        approveAllTransactionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                approveAllTransactionsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(amountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(feeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(95, 95, 95)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(transactionIDTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                            .addComponent(transactionTypeTextField))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(approveAllTransactionsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(addressTextField))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(accountBalanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(noteFieldTextField)
                                .addGap(18, 18, 18)
                                .addComponent(declineButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(approveButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(36, 36, 36)
                                .addComponent(jScrollPane1))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(payerAddressTextField)
                                    .addComponent(receiverAddressTextField)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(70, 70, 70)
                                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(accountAliasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(68, 68, 68)
                                .addComponent(refreshButton)))
                        .addGap(14, 14, 14))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(refreshButton)
                    .addComponent(accountAliasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(addressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(accountBalanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(approveAllTransactionsButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(amountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel8)
                            .addComponent(transactionIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(feeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(transactionTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(payerAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(receiverAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(approveButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(noteFieldTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(declineButton)))
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void amountTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_amountTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_amountTextFieldActionPerformed

    private void receiverAddressTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receiverAddressTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_receiverAddressTextFieldActionPerformed

    private void feeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_feeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_feeTextFieldActionPerformed

    private void declineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_declineButtonActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_declineButtonActionPerformed

    private void payerAddressTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payerAddressTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_payerAddressTextFieldActionPerformed

    private void noteFieldTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noteFieldTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_noteFieldTextFieldActionPerformed

    private void transactionIDTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transactionIDTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_transactionIDTextFieldActionPerformed

    private void approveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approveButtonActionPerformed
        // TODO add your handling code here:
        submitSignedTransactionEnvelope(selectedTransactionEnvelope);
    }//GEN-LAST:event_approveButtonActionPerformed

    private void approveButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approveButton1ActionPerformed
        // TODO add your handling code here:
        submitSignedTransactionEnvelope(selectedTransactionEnvelope);
    }//GEN-LAST:event_approveButton1ActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        // TODO add your handling code here:
        fetchTransactions();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void addressTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addressTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addressTextFieldActionPerformed

    private void transactionTypeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transactionTypeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_transactionTypeTextFieldActionPerformed

    private void accountBalanceTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accountBalanceTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_accountBalanceTextFieldActionPerformed

    private void accountAliasComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accountAliasComboBoxActionPerformed
        // TODO add your handling code here:
        JComboBox comboBox = (JComboBox) evt.getSource();

        Object selected = comboBox.getSelectedItem();
        selectedUserAccount = (UserAccount) selected;
        addressTextField.setText(selectedUserAccount.address);
        fetchTransactions();
    }//GEN-LAST:event_accountAliasComboBoxActionPerformed

    private void approveAllTransactionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approveAllTransactionsButtonActionPerformed
        // TODO add your handling code here:
        for (TransactionEnvelope transactionEnvelope : this.transactionEnvelopeList) {
            submitSignedTransactionEnvelope(transactionEnvelope);
            fetchTransactions();
        }
    }//GEN-LAST:event_approveAllTransactionsButtonActionPerformed
    
    private List<TransactionEnvelope> transactionEnvelopeList = null;
    private Client client = ClientBuilder.newClient();

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
            java.util.logging.Logger.getLogger(AppWallet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AppWallet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AppWallet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AppWallet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AppWallet().setVisible(true);
            }
        });

    }
    
    private String fetchAgreement(String ipfsURL) {
        
        String ipfsHash = ipfsURL.substring(6);
        WebTarget webTarget = client.target(serviceHost + "/transaction-envelope/get-contents/" + ipfsHash);
        
        log.info("web target: " + webTarget);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        String content = invocationBuilder.get(new GenericType<String>() {
        });

        log.info("Received transaction count " + content);
        return content;
    }

    private void fetchTransactions() {

        String accountAddress = selectedUserAccount.address;

        WebTarget webTarget = client.target(serviceHost + "/transaction-envelope/account/" + accountAddress);
        
        log.info("web target: " + webTarget);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        List<TransactionEnvelope> transactionEnvelopes = invocationBuilder.get(new GenericType<List<TransactionEnvelope>>() {
        });

        log.info("Received transaction count " + transactionEnvelopes.size());

        DefaultTableModel model = (DefaultTableModel) transactionTable.getModel();
        model.setRowCount(0);

        for (TransactionEnvelope te : transactionEnvelopes) {

            log.info("Adding transaction" + te);
            String rowData[] = {String.valueOf(te.getTransactionPrototype().getApplicationTransactionId()),
                String.valueOf(te.getTransactionPrototype().getAmount()),
                String.valueOf(te.getTransactionPrototype().getFee()),
                String.valueOf(te.getTransactionPrototype().getReceiver()),
                String.valueOf(te.getState()),
                String.valueOf(te.getTransactionPrototype().getTransactionType().toString())
            };

            model.addRow(rowData);
        }
        transactionTable.setModel(model);
        transactionEnvelopeList = transactionEnvelopes;

        updateSelectedTransaction();

        com.algorand.algosdk.algod.client.model.Account accountInfo = getAccount(accountAddress);
        if (selectedUserAccount != null) {
            this.accountBalanceTextField.setText(accountInfo.getAmount().toString());
        }
    }


    private com.algorand.algosdk.algod.client.model.Account getAccount(String accountAddress) {
        log.info("getAccountStatus: " + accountAddress);
        WebTarget webTarget = client.target(serviceHost + "/algod/account/" + accountAddress);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        com.algorand.algosdk.algod.client.model.Account account = invocationBuilder.get(new GenericType<com.algorand.algosdk.algod.client.model.Account>() {
        });
        log.info("account: " + account);
        return account;
    }

    private void submitSignedTransactionEnvelope(TransactionEnvelope transactionEnvelope) {

        log.info("submitting signed transaction: " + transactionEnvelope);
        try {
            transactionEnvelope = signTransaction(transactionEnvelope);
            WebTarget webTarget = client.target(serviceHost + "/transaction-envelope/submit-signed-transaction");

            Invocation invocation = webTarget.request(MediaType.APPLICATION_JSON).buildPost(Entity.entity(transactionEnvelope, MediaType.APPLICATION_JSON));

            Response response = invocation.invoke();
            log.info("submit signed transaction response: " + response);
            if (response != null && response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                String message = "Transaction signed";
                JOptionPane.showMessageDialog(rootPane, message, "Sign Transaction", JOptionPane.PLAIN_MESSAGE);
            } else {
                String message = "Error signing transaction";
                JOptionPane.showMessageDialog(rootPane, message, "Sign Transaction", JOptionPane.ERROR_MESSAGE);
            }
        } catch (GeneralSecurityException ex) {
            java.util.logging.Logger.getLogger(AppWallet.class.getName()).log(Level.SEVERE, null, ex);
            String message = "Error signing transaction: " + ex.getLocalizedMessage();
            JOptionPane.showMessageDialog(rootPane, message, "Sign Transaction", JOptionPane.ERROR_MESSAGE);
        }

    }
    
    private void updateUserAccountRegistration(String alias, String address) {
        
        UserAccountRegistration userAccountRegistration = new UserAccountRegistration();
        userAccountRegistration.setAlias(alias);
        userAccountRegistration.setAddress(address);

        log.info("sending userAccountRegistration: " + userAccountRegistration);

        WebTarget webTarget = client.target(serviceHost + "/account-application-registration/user-account");

        Invocation invocation = webTarget.request(MediaType.APPLICATION_JSON).buildPost(Entity.entity(userAccountRegistration, MediaType.APPLICATION_JSON));

        Response response = invocation.invoke();
        log.info("submit userAccountRegistration response: " + response);

    }

    public TransactionEnvelope signTransaction(TransactionEnvelope transactionEnvelope) throws GeneralSecurityException {

        Account account = selectedUserAccount.account;

        Transaction alogorandTransaction = transactionEnvelope.getAlgorandTransaction();
        // Sign the Transaction
        SignedTransaction signedTransaction = account.signTransaction(alogorandTransaction);

        log.info("signed transaction id: " + signedTransaction.transactionID);

        transactionEnvelope.setSignedTransaction(signedTransaction);
        transactionEnvelope.setState("signed");
        return transactionEnvelope;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> accountAliasComboBox;
    private javax.swing.JTextField accountBalanceTextField;
    private javax.swing.JTextField addressTextField;
    private javax.swing.JTextArea agreementTextArea;
    private javax.swing.JTextField amountTextField;
    private javax.swing.JButton approveAllTransactionsButton;
    private javax.swing.JButton approveButton;
    private javax.swing.JButton approveButton1;
    private javax.swing.JButton declineButton;
    private javax.swing.JTextField feeTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField noteFieldTextField;
    private javax.swing.JTextField payerAddressTextField;
    private javax.swing.JTextField receiverAddressTextField;
    private javax.swing.JButton refreshButton;
    private javax.swing.JTextField transactionIDTextField;
    private javax.swing.JTable transactionTable;
    private javax.swing.JTextField transactionTypeTextField;
    // End of variables declaration//GEN-END:variables

    private void publishUserAccountRegistration() {
        userAccountList.forEach((userAccount) -> {
            updateUserAccountRegistration(userAccount.alias, userAccount.address);
        });
    }
}
