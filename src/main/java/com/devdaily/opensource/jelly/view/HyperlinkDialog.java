package com.devdaily.opensource.jelly.view;

import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.*;

import com.devdaily.opensource.jelly.controller.HyperlinkDialogController;
import com.devdaily.utils.swing.DDDialog;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Alvin Alexander
 */
public class HyperlinkDialog extends DDDialog 
{
  HyperlinkDialogController hyperlinkDialogController;
  
  public HyperlinkDialog(Frame owner, HyperlinkDialogController hyperlinkDialogController) {
    super(owner);
    this.hyperlinkDialogController = hyperlinkDialogController;
    initComponents();
  }

  private HyperlinkDialog(Frame owner) {
    super(owner);
    initComponents();
  }

  private HyperlinkDialog(Dialog owner) {
    // modified by al
    super();
    initComponents();
  }

  public JTextField getUrlTextField() {
    return urlTextField;
  }

  public JTextField getTextTextField() {
    return textTextField;
  }

  public JTextField getAltTextTextField() {
    return altTextTextField;
  }

  public JButton getOkButton() {
    return okButton;
  }

  public JButton getCancelButton() {
    return cancelButton;
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    dialogPane = new JPanel();
    contentPanel = new JPanel();
    headerLabel = new JLabel();
    urlLabel = new JLabel();
    urlTextField = new JTextField();
    textLabel = new JLabel();
    textTextField = new JTextField();
    altTextLabel = new JLabel();
    altTextTextField = new JTextField();
    buttonBar = new JPanel();
    okButton = new JButton();
    cancelButton = new JButton();
    CellConstraints cc = new CellConstraints();

    //======== this ========
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    {
      dialogPane.setBorder(Borders.DIALOG_BORDER);
      dialogPane.setLayout(new BorderLayout());

      //======== contentPanel ========
      {
        contentPanel.setLayout(new FormLayout(
          new ColumnSpec[] {
            new ColumnSpec(ColumnSpec.RIGHT, Sizes.DEFAULT, FormSpec.NO_GROW),
            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            FormFactory.DEFAULT_COLSPEC
          },
          new RowSpec[] {
            FormFactory.DEFAULT_ROWSPEC,
            FormFactory.LINE_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC,
            FormFactory.LINE_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC,
            FormFactory.LINE_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC,
            FormFactory.LINE_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC
          }));

        //---- headerLabel ----
//        headerLabel.setText("Hyperlink Properties");
//        contentPanel.add(headerLabel, cc.xy(3, 1));

        //---- urlLabel ----
        urlLabel.setText("URL");
        urlLabel.setLabelFor(urlTextField);
        contentPanel.add(urlLabel, cc.xy(1, 1));

        //---- urlTextField ----
        urlTextField.setColumns(30);
        contentPanel.add(urlTextField, cc.xy(3, 1));

        //---- textLabel ----
        textLabel.setText("Text");
        textLabel.setLabelFor(textTextField);
        contentPanel.add(textLabel, cc.xy(1, 3));
        contentPanel.add(textTextField, cc.xy(3, 3));

        //---- altTextLabel ----
        altTextLabel.setText("Title Text");
        altTextLabel.setLabelFor(altTextTextField);
        contentPanel.add(altTextLabel, cc.xy(1, 5));
        contentPanel.add(altTextTextField, cc.xy(3, 5));
      }
      dialogPane.add(contentPanel, BorderLayout.CENTER);

      //======== buttonBar ========
      {
        buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
        buttonBar.setLayout(new FormLayout(
          new ColumnSpec[] {
            FormFactory.GLUE_COLSPEC,
            FormFactory.BUTTON_COLSPEC,
            FormFactory.RELATED_GAP_COLSPEC,
            FormFactory.BUTTON_COLSPEC
          },
          RowSpec.decodeSpecs("pref")));

        //---- okButton ----
        okButton.setText("OK");
        buttonBar.add(okButton, cc.xy(2, 1));

        //---- cancelButton ----
        cancelButton.setText("Cancel");
        buttonBar.add(cancelButton, cc.xy(4, 1));
      }
      dialogPane.add(buttonBar, BorderLayout.SOUTH);
    }
    contentPane.add(dialogPane, BorderLayout.CENTER);
    pack();
    setLocationRelativeTo(getOwner());
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JPanel dialogPane;
  private JPanel contentPanel;
  private JLabel headerLabel;
  private JLabel urlLabel;
  private JTextField urlTextField;
  private JLabel textLabel;
  private JTextField textTextField;
  private JLabel altTextLabel;
  private JTextField altTextTextField;
  private JPanel buttonBar;
  private JButton okButton;
  private JButton cancelButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables

  @Override
  public void escapeCommandPerformed(KeyEvent e)
  {
    hyperlinkDialogController.doCancelButtonAction();
  }

  @Override
  public void okCommandPerformed(KeyEvent e)
  {
    hyperlinkDialogController.doOkButtonAction();
  }
}
