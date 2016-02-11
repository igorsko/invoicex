/*
 * popup_dialog.java
 *
 * Created on July 12, 2004, 4:52 PM
 */

package it.tnx.gui;

import java.awt.*;
import javax.swing.*;
import java.net.*;
import javax.swing.border.*;
import java.awt.event.*;

public class popup_dialog extends JDialog {
  private Color TITLE_BAR_COLOR = java.awt.Color.YELLOW;
  private Color CLOSE_BOX_COLOR = java.awt.Color.RED;
  
  private JLabel title = new JLabel("xxxxxxxxxxxxxx");
  {   title.setHorizontalAlignment(SwingConstants.CENTER);
      title.setOpaque( false );
      title.setFont( title.getFont().deriveFont(Font.BOLD) );
  }
  
  private JPanel header = new JPanel();
  {   header.setBackground( TITLE_BAR_COLOR );
      header.setLayout( new BorderLayout() );
      header.setBorder( BorderFactory.createEmptyBorder(2,2,2,2) );
      header.add( title                   , BorderLayout.CENTER );
      header.add( create_close_button()   , BorderLayout.EAST   );
  }
  
  private JPanel content_pane = new JPanel();
  {   content_pane.setLayout( new BorderLayout() );
  }
  
  public popup_dialog( Frame owner ){ super(owner); setModal(true); }
  public popup_dialog( Dialog owner){ super(owner); setModal(true); }
  
  /* Code common to all constructors. */
  {
    //init_dragable();
    
    setUndecorated( true );
    JPanel contents = new JPanel();
    contents.setBorder( BorderFactory.createLineBorder(Color.BLACK,1) );
    contents.setLayout(new BorderLayout());
    contents.add(header,       BorderLayout.NORTH);
    contents.add(content_pane, BorderLayout.CENTER);
    contents.setBackground( Color.WHITE );
    
    setContentPane( contents ); // , BorderLayout.CENTER );
    setLocation(100,100);
  }
  
  private JButton create_close_button() {
    URL image = getClass().getClassLoader().getResource(
    "images/8px.red.X.gif");
    
    JButton b = (image!=null) ? new JButton( new ImageIcon(image) )
    : new JButton( "  X  " )
    ;
    
    Border outer = BorderFactory.createLineBorder(CLOSE_BOX_COLOR,1);
    Border inner = BorderFactory.createEmptyBorder(2,2,2,2);
    
    b.setBorder( BorderFactory.createCompoundBorder(outer,inner) );
    
    b.setOpaque( false );
    b.addActionListener
    (   new ActionListener()
    {   public void actionPerformed(ActionEvent e)
        {   popup_dialog.this.setVisible(false);
            popup_dialog.this.dispose();
        }
    }
    );
    
    b.setFocusable( false );
    return b;
  }
  
  /** Set the dialog title to the indicated text. */
  public void setTitle( String text ){ title.setText( text ); }
  
  /** Add your widgets to the window returned by this method, in
   *  a manner similar to a JFrame. Do not modify the Popup_dialog
   *  itself. The returned container is a {@link JPanel JPanel}
   *  with a preinstalled {@link BorderLayout}.
   *  By default, it's colored dialog-box gray.
   *  @return the content pane.
   */
  public Container getContentPane(){ return content_pane; }
}