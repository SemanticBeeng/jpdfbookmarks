/*
 * GeneralOptionsPanel.java
 *
 * Copyright (c) 2010 Flaviano Petrocchi <flavianopetrocchi at gmail.com>.
 * All rights reserved.
 *
 * This file is part of JPdfBookmarks.
 *
 * JPdfBookmarks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPdfBookmarks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JPdfBookmarks.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.flavianopetrocchi.jpdfbookmarks;

import java.nio.charset.Charset;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SeparatorsPanel extends javax.swing.JPanel {

    Prefs userPrefs;
    String previewText = "Chapter 1/1,Black,notBold,notItalic,open,FitWidth,80\n" +
            "\tParagraph 1/5,Red,bold,italic,closed,FitWidth,120\n" +
            "\t\tParagraph 1.1/9,Black,notBold,notItalic,closed,FitWidth,500\n" +
            "\tParagraph 2/13,Black,bold,italic,closed,FitWidth,250";
    int defCharsetIndex = 0;
    String defCharset = Charset.defaultCharset().displayName();
    String defCharsetDecorated = defCharset + " (" + Res.getString("SYSTEM_DEFAULT") + ")";

    /** Creates new form GeneralOptionsPanel */
    public SeparatorsPanel(Prefs userPrefs) {
        this.userPrefs = userPrefs;
        initComponents();
        String ind = userPrefs.getIndentationString();
        String pag = userPrefs.getPageSeparator();
        String sep = userPrefs.getAttributesSeparator();
        txtPageSeparator.setText(formatText(pag));
        txtAttributesSeparator.setText(formatText(sep));
        txtIndentationString.setText(formatText(ind));

        setPreview();

        TextChangedListener textListener = new TextChangedListener();
        txtPageSeparator.getDocument().addDocumentListener(textListener);
        txtAttributesSeparator.getDocument().addDocumentListener(textListener);
        txtIndentationString.getDocument().addDocumentListener(textListener);
    }

    public String getPageSeparator() {
        return unformatText(txtPageSeparator.getText());
    }
    
    

    public String getIndentationString() {
        return unformatText(txtIndentationString.getText());
    }

    public String getAttributesSeparator() {
        return unformatText(txtAttributesSeparator.getText());
    }

    private class TextChangedListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            setPreview();
        }

        public void removeUpdate(DocumentEvent e) {
            setPreview();
        }

        public void changedUpdate(DocumentEvent e) {
            setPreview();
        }
    }

    private String formatText(String text) {
        String formatted = text.replace("\t", "[tab]");
        formatted = formatted.replace(" ", "[space]");
        return formatted;
    }

    private String unformatText(String text) {
        String formatted = text.replace("[tab]", "\t");
        formatted = formatted.replace("[space]", " ");
        return formatted;
    }

    private void setPreview() {
        String pag = unformatText(txtPageSeparator.getText());
        String ind = unformatText(txtIndentationString.getText());
        String sep = unformatText(txtAttributesSeparator.getText());

        String text = previewText.replace("\t", ind).replace("/", pag).replace(",", sep);

        txtPreview.setText(text);
    }

    private boolean checkNotEmpty(JTextField... textFields) {
        for (JTextField field : textFields) {
            if (field.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        Res.getString("ERROR_EMPTY_FIELD"),
                        JPdfBookmarks.APP_NAME, JOptionPane.WARNING_MESSAGE);
                field.requestFocusInWindow();
                return false;
            }
        }
        return true;
    }

    private boolean checkDifferentValues() {
        String pag = unformatText(txtPageSeparator.getText());
        String ind = unformatText(txtIndentationString.getText());
        String sep = unformatText(txtAttributesSeparator.getText());

        JTextField duplicated = null;

        if (pag.equals(ind)) {
            duplicated = txtIndentationString;
        } else if (pag.equals(sep)) {
            duplicated = txtAttributesSeparator;
        } else if (ind.equals(sep)) {
            duplicated = txtPageSeparator;
        }

        if (duplicated != null) {
            JOptionPane.showMessageDialog(this,
                    Res.getString("ERROR_DUPLICATED_VALUES"),
                    JPdfBookmarks.APP_NAME, JOptionPane.WARNING_MESSAGE);
            duplicated.requestFocusInWindow();
            return false;
        }
        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        valueSeparatorsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtPageSeparator = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtAttributesSeparator = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtIndentationString = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtPreview = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("it/flavianopetrocchi/jpdfbookmarks/locales/localizedText"); // NOI18N
        valueSeparatorsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("OPTIONS_SEPARATORS"))); // NOI18N

        jLabel1.setText(bundle.getString("LABEL_PAGE_SEPARATOR")); // NOI18N

        txtPageSeparator.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtPageSeparatorInputMethodTextChanged(evt);
            }
        });

        jLabel2.setText(bundle.getString("LABEL_ATTRIBUTES_SEPARATOR")); // NOI18N

        jLabel3.setText(bundle.getString("LABEL_INDENTATION_STRING")); // NOI18N

        txtPreview.setColumns(20);
        txtPreview.setEditable(false);
        txtPreview.setRows(5);
        txtPreview.setFocusable(false);
        jScrollPane1.setViewportView(txtPreview);

        jLabel4.setText(bundle.getString("LABEL_PREVIEW")); // NOI18N

        jLabel5.setText(bundle.getString("OPTIONS_DESCRIPTION_01")); // NOI18N

        jLabel6.setText(bundle.getString("OPTIONS_DESCRIPTION_02")); // NOI18N

        javax.swing.GroupLayout valueSeparatorsPanelLayout = new javax.swing.GroupLayout(valueSeparatorsPanel);
        valueSeparatorsPanel.setLayout(valueSeparatorsPanelLayout);
        valueSeparatorsPanelLayout.setHorizontalGroup(
            valueSeparatorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(valueSeparatorsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(valueSeparatorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addGroup(valueSeparatorsPanelLayout.createSequentialGroup()
                        .addGroup(valueSeparatorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(valueSeparatorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtIndentationString, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                            .addComponent(txtAttributesSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                            .addComponent(txtPageSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)))
                    .addComponent(jLabel6))
                .addContainerGap())
        );
        valueSeparatorsPanelLayout.setVerticalGroup(
            valueSeparatorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, valueSeparatorsPanelLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(valueSeparatorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtPageSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(valueSeparatorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtAttributesSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(valueSeparatorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIndentationString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(valueSeparatorsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(valueSeparatorsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

        private void txtPageSeparatorInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtPageSeparatorInputMethodTextChanged
            System.out.println("textchanged");
}//GEN-LAST:event_txtPageSeparatorInputMethodTextChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtAttributesSeparator;
    private javax.swing.JTextField txtIndentationString;
    private javax.swing.JTextField txtPageSeparator;
    private javax.swing.JTextArea txtPreview;
    private javax.swing.JPanel valueSeparatorsPanel;
    // End of variables declaration//GEN-END:variables
}
