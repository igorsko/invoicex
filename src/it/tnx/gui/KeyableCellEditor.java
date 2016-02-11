/*
 * Test.java
 *
 * Created on January 31, 2005, 12:48 PM
 */

package it.tnx.gui;

/*
Wotonomy: OpenStep design patterns for pure Java applications.
Copyright (C) 2000 Blacksmith, Inc.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, see http://www.gnu.org
*/

//package net.wotonomy.ui.swing.components;

import java.awt.Component;
import java.awt.Color;
import java.awt.event.*;
import java.io.Serializable;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;

import java.util.*; //collections
import java.util.EventObject;

/**
* A table cell editor customized for keyboard navigation, much like
* working with a spreadsheet.  The default cell editor unfortunately
* does none of these things:
* <ul>
*    <li> Selects text on start of editing.
*    <li> Up and down keys move edit cell up and down.
*    <li> Right and left keys move cell when selection caret is at end of text.
*    <li> Escape cancels editing.
*    <li> Enter commits edit.
*    <li> Edits are properly committed on lost focus.
*    <li> Tab and shift-tab work as expected.
*    <li> Cell selection moves with the edit cell.
* </ul>
*
* @author michael@mpowers.net
* @author $Author: cecca $
* @version $Revision: 1.1 $
* $Date: 2005/10/05 15:15:40 $
*/
public class KeyableCellEditor implements TableCellEditor, FocusListener,
                                        KeyListener, Serializable
{
    List listeners;
    JTextField textField;
    Object lastValue;
    Format currentFormat;

    JTable table;

/**
* Default constructor - a standard JTextField will be used for editing.
*/
    public KeyableCellEditor()
    {
        this( (JTextField) null );
    }

/**
* Constructor specifying a type of JTextField to be used for editing.
* The JTextField will have its border replaced with a black line border.
* @param aTextField A JTextField or subclass for editing values.
*/
    public KeyableCellEditor( JTextField aTextField )
    {
        listeners = new Vector();
        lastValue = null;

        // default to stock JTextField
        textField = aTextField;
        if ( textField == null )
        {
            textField = new JTextField();
        }

        textField.setBorder(new LineBorder(Color.black));

        // handle arrow keys while caret is showing
        textField.addKeyListener( this );

        // handle lost focus
        textField.addFocusListener( this );
    }

    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column)
    {
        this.table = table;
        table.removeKeyListener( this ); // if any
        table.addKeyListener( this );
        return getEditorComponent( value );
    }

    protected Component getEditorComponent( Object value )
    {
        if ( value != null )
        {
            textField.setText( value.toString() );
        }
        else
        {
            textField.setText( "" );
        }

        if ( value instanceof Number )
        {
            textField.setHorizontalAlignment(JTextField.RIGHT);
        }
        else
        {
            textField.setHorizontalAlignment(JTextField.LEFT);
        }

        // remember original value
        lastValue = value;

        // select all text and get focus
        textField.selectAll();
        textField.requestFocus();

        return textField;
    }

    public Object getCellEditorValue()
    {
        return lastValue;
    }

    public boolean isCellEditable(EventObject anEvent)
    {
        // key events should replace the selection
        // NOTE: For whatever reason, key events trigger result in a null parameter
        if ( anEvent == null )
        {
            textField.setText("");
            textField.requestFocus();
            return true;
        }

        return true;
    }

    public boolean shouldSelectCell(EventObject anEvent)
    { // System.out.println( "KeyableCellEditor.shouldSelectCell: " + anEvent );

        // key events should replace the selection
        // NOTE: For whatever reason, key events are not generated
        if ( anEvent instanceof KeyEvent )
        {
            textField.setText("");
            textField.requestFocus();
            return true;
        }

        // otherwise, select all text and continue
        textField.selectAll();
        textField.requestFocus();

        return true;
    }

    public boolean stopCellEditing()
    {
        lastValue = textField.getText();
        fireEditingStopped();
        table.removeKeyListener( this ); // if any
        return true;
    }

    public void cancelCellEditing()
    {
        fireEditingCanceled();
        table.removeKeyListener( this ); // if any
    }

    public void addCellEditorListener(CellEditorListener l)
    {
        listeners.add( l );
    }

    public void removeCellEditorListener(CellEditorListener l)
    {
        listeners.remove( l );
    }

    protected void fireEditingCanceled()
    {
        ChangeEvent event = new ChangeEvent( this );
        Iterator it = new ArrayList( listeners ).iterator(); // copy to prevent modification exception
        while ( it.hasNext() )
        {
            ((CellEditorListener)it.next()).editingCanceled( event );
        }
    }

    protected void fireEditingStopped()
    {
        ChangeEvent event = new ChangeEvent( this );
        Iterator it = new ArrayList( listeners ).iterator(); // copy to prevent modification exception
        while ( it.hasNext() )
        {
            ((CellEditorListener)it.next()).editingStopped( event );
        }
    }

    protected void onEnterKey()
    {
        stopCellEditing();
    }

    protected void onEscapeKey()
    {
        cancelCellEditing();
    }

    protected void moveEditCell( int dRow, int dCol )
    {
        if ( table == null ) return;
        int row = table.getSelectedRow() + dRow;
        int col = table.getSelectedColumn() + dCol;

        row = Math.max( 0, row );
        row = Math.min( row, table.getRowCount() - 1 );
        col = Math.max( 0, col );
        col = Math.min( col, table.getColumnCount() - 1 );

        stopCellEditing();
        table.setRowSelectionInterval( row, row );
        table.setColumnSelectionInterval( col, col );
        table.editCellAt( row, col );
        textField.selectAll();
        textField.requestFocus();
    }

    // interface KeyListener

    public void keyTyped(KeyEvent e)
    { // System.out.println( "KeyableCellEditor.keyTyped: " + KeyEvent.getKeyText( e.getKeyCode() ) );
    }

    public void keyPressed(KeyEvent e)
    { // System.out.println( "KeyableCellEditor.keyPressed: " + KeyEvent.getKeyText( e.getKeyCode() ) );

        // catch LEFT and RIGHT here before JTextField consumes them

        int keyCode = e.getKeyCode();
        if ( keyCode == KeyEvent.VK_LEFT )
        {
            if ( textField.getSelectionStart() == 0 )
            {
                moveEditCell( 0, -1 );
                e.consume();
                return;
            }
        }
        if ( keyCode == KeyEvent.VK_RIGHT )
        {
            if ( textField.getSelectionEnd() == textField.getText().length() )
            {
                moveEditCell( 0, 1 );
                e.consume();
                return;
            }
        }
        if ( keyCode == KeyEvent.VK_UP )
        {
            moveEditCell( -1, 0 );
            e.consume();
            return;
        }
        if ( keyCode == KeyEvent.VK_DOWN )
        {
            moveEditCell( 1, 0 );
            e.consume();
            return;
        }
    }

    public void keyReleased(KeyEvent e)
    { // System.out.println( "KeyableCellEditor.keyReleased: " + KeyEvent.getKeyText( e.getKeyCode() ) );

        // catch ENTER here to allow JTextField to process it as well

        int keyCode = e.getKeyCode();
        if ( keyCode == KeyEvent.VK_ENTER )
        {
            onEnterKey();
            return;
        }
        if ( keyCode == KeyEvent.VK_ESCAPE )
        {
            onEscapeKey();
            return;
        }

        // tabs are apparently only received on key release
        if ( keyCode == KeyEvent.VK_TAB )
        {
            if ( e.isShiftDown() )
            {
                moveEditCell( 0, -1 );
            }
            else
            {
                moveEditCell( 0, 1 );
            }
            e.consume();
            return;
        }

    }

    // interface FocusListener

    public void focusGained(FocusEvent e)
    { // System.out.println( "focusGained: " );        
    }

    public void focusLost(FocusEvent e)
    { // System.out.println( "focusLost: " );        
        stopCellEditing();
    }

}