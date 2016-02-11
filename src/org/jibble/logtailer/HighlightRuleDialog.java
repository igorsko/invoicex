/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of JLogTailer.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: HighlightRuleDialog.java,v 1.2 2004/02/01 13:21:17 pjm2 Exp $

*/

package org.jibble.logtailer;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * JLogTailer - A log tailer utility written in Java.
 * Copyright Paul James Mutton, 2002.
 * 
 * @author Paul James Mutton, http://www.jibble.org/
 * @version 2.0
 */
public class HighlightRuleDialog extends JDialog {
    
    public HighlightRuleDialog(Dialog owner, HighlightRule rule) {
        super(owner);
        
        _ruleField = new JTextField(rule.getName());
        _textField = new JTextField(rule.getRegexp());
        _bold.setSelected(rule.getBold());
        _underlined.setSelected(rule.getUnderlined());
        _filtered.setSelected(rule.getFiltered());
        _beep.setSelected(rule.getBeep());
        _colorField = new JTextField("#" + Integer.toHexString(rule.getColor().getRGB()).substring(2));
        
        Container pane = this.getContentPane();
        JPanel panel = new JPanel(new GridLayout(12, 1));
        
        pane.add(panel, BorderLayout.CENTER);
        
        panel.add(_ruleField);
        panel.add(new JLabel("If the line matches this regular expression:"));
        panel.add(_textField);
        panel.add(new JLabel("then:"));
        panel.add(_bold);
        panel.add(_underlined);
        panel.add(_filtered);
        panel.add(_beep);
        panel.add(new JLabel("and color the text with:"));
        panel.add(_colorField);
        panel.add(_okay);
        panel.add(_cancel);
        
        _okay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    _rule = new HighlightRule(_ruleField.getText(), _textField.getText(), _underlined.isSelected(), _bold.isSelected(), _filtered.isSelected(), _beep.isSelected(), Color.decode(_colorField.getText()));
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(HighlightRuleDialog.this, "Your regular expression syntax is not valid, please try again.", "Regexp failed", JOptionPane.ERROR_MESSAGE); 
                    return;
                }
                dispose();
            }
        });
        
        _cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                dispose();
            }
        });
        
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.pack();
        this.setTitle("Rule specification");
        this.setModal(true);
        this.setResizable(false);
        this.setVisible(true);
    }
    
    public HighlightRule getRule() {
        return _rule;
    }
    
    private JTextField _textField;
    private JTextField _colorField;
    private JTextField _ruleField;
    
    private JCheckBox _bold = new JCheckBox("Highlight in bold");
    private JCheckBox _underlined = new JCheckBox("Underline the text");
    private JCheckBox _filtered = new JCheckBox("Do not display the line");
    private JCheckBox _beep = new JCheckBox("Make a beep sound");
    
    private JButton _okay = new JButton("Okay");
    private JButton _cancel = new JButton("Cancel");
    
    private HighlightRule _rule = null;
    
}