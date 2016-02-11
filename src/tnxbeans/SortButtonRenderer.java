package tnxbeans;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * @version 1.0 02/25/99
 */
public class SortButtonRenderer extends JLabel implements TableCellRenderer {

    public static final int NONE = 0;
    public static final int DOWN = 1;
    public static final int UP = 2;
    int pushedColumn;
    Hashtable state;
    JLabel downButton, upButton;

    public SortButtonRenderer() {
        pushedColumn = -1;
        state = new Hashtable();

//        setMargin(new Insets(0,0,0,0));
        setHorizontalTextPosition(LEFT);
        setIcon(new BlankIcon());
//setFont(new Font("Dialog",Font.PLAIN,12));
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
//        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

// perplexed
// ArrowIcon(SwingConstants.SOUTH, true)
// BevelArrowIcon (int direction, boolean isRaisedView, boolean isPressedView)

        downButton = new JLabel();
//downButton.setFont(new Font("Dialog",Font.BOLD,12));
//downButton.setMargin(new Insets(0,0,0,0));
        downButton.setBackground(new Color(200, 250, 200));
        downButton.setOpaque(true);
//        downButton.setHorizontalTextPosition(LEFT);
        downButton.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));
        downButton.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
//        downButton.setBorder(javax.swing.BorderFactory.createLineBorder(Color.gray, 1));
//downButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));

        upButton = new JLabel();
//upButton.setFont(new Font("Dialog",Font.BOLD,12));
        upButton.setBackground(new Color(250, 200, 200));
        upButton.setOpaque(true);
//upButton.setMargin(new Insets(0,0,0,0));
//        upButton.setHorizontalTextPosition(LEFT);
        upButton.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));
        upButton.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
//        upButton.setBorder(javax.swing.BorderFactory.createLineBorder(Color.gray, 1));
//upButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));

    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel button = this;
        Object obj = state.get(new Integer(column));
        if (obj != null) {
            if (((Integer) obj).intValue() == DOWN) {
                button = downButton;
            } else {
                button = upButton;
            }
        }
        button.setText((value == null) ? "" : value.toString());
        boolean isPressed = (column == pushedColumn);
//button.getModel().setPressed(isPressed);
//button.getModel().setArmed(isPressed);
        return button;
    }

    public void setPressedColumn(int col) {
        pushedColumn = col;
    }

    public void setSelectedColumn(int col) {
        if (col < 0) {
            return;
        }
        Integer value = null;
        Object obj = state.get(new Integer(col));
        if (obj == null) {
            value = new Integer(DOWN);
        } else {
            if (((Integer) obj).intValue() == DOWN) {
                value = new Integer(UP);
            } else {
                value = new Integer(DOWN);
            }
        }
        state.clear();
        state.put(new Integer(col), value);
    }

    public int getState(int col) {
        int retValue;
        Object obj = state.get(new Integer(col));
        if (obj == null) {
            retValue = NONE;
        } else {
            if (((Integer) obj).intValue() == DOWN) {
                retValue = DOWN;
            } else {
                retValue = UP;
            }
        }
        return retValue;
    }
}

