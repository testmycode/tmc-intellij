package fi.helsinki.cs.tmc.intellij.ui.exercisedownloadlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;


/**
 * A list of labeled checkboxes. Based on: http://www.devx.com/tips/Tip/5342 See also:
 * http://stackoverflow.com/questions/19766/how-do-i-make-a-list-with-checkboxes-in-java-swing
 * Borrowed from tmc-netbeans plugin
 */
public class CustomCheckBoxList extends JList implements Iterable<JCheckBox> {

    private static final Logger logger = LoggerFactory.getLogger(CustomCheckBoxList.class);
    private List<ItemListener> itemListeners;

    public CustomCheckBoxList() {
        logger.info("Creating custom checkbox list");
        this.itemListeners = new ArrayList<ItemListener>();
        this.setCellRenderer(new CellRenderer());
        this.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent event) {
                        int index = locationToIndex(event.getPoint());
                        if (index != -1) {
                            JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
                            if (CustomCheckBoxList.this.isEnabled() && checkbox.isEnabled()) {
                                checkbox.setSelected(!checkbox.isSelected());
                            }
                            repaint();
                        }
                    }
                });
    }

    private ItemListener itemEventForwarder =
            new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent event) {
                    fireItemEvent(event);
                }
            };

    private PropertyChangeListener checkBoxPropChangeListener =
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    repaint();
                }
            };

    public void addItemListener(ItemListener listener) {
        itemListeners.add(listener);
    }

    protected void fireItemEvent(ItemEvent event) {
        for (ItemListener listener : itemListeners) {
            listener.itemStateChanged(event);
        }
    }

    public int getElementCount() {
        return getModel().getSize();
    }

    public JCheckBox getElement(int element) {

        return (JCheckBox) getModel().getElementAt(element);
    }

    @Override
    public Iterator<JCheckBox> iterator() {
        return new Iterator<JCheckBox>() {
            private int iteratori = 0;

            @Override
            public boolean hasNext() {
                return iteratori < getElementCount();
            }

            @Override
            public JCheckBox next() {
                JCheckBox cb = getElement(iteratori);
                iteratori++;
                return cb;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void addCheckbox(JCheckBox newCheckBox) {
        newCheckBox.addItemListener(itemEventForwarder);
        newCheckBox.addPropertyChangeListener(checkBoxPropChangeListener);

        ListModel model = getModel();
        JCheckBox[] newData = new JCheckBox[model.getSize() + 1];
        for (int i = 0; i < model.getSize(); ++i) {
            newData[i] = (JCheckBox) model.getElementAt(i);
        }
        newData[newData.length - 1] = newCheckBox;
        setListData(newData);
    }

    public boolean isSelected(int selecti) {
        return ((JCheckBox) getModel().getElementAt(selecti)).isSelected();
    }

    public void setSelected(int setselecti, boolean selected) {

        ((JCheckBox) getModel().getElementAt(setselecti)).setSelected(selected);
    }

    public boolean isAnySelected() {
        for (int i = 0; i < getModel().getSize(); ++i) {
            if (isSelected(i)) {
                return true;
            }
        }
        return false;
    }

    protected class CellRenderer implements ListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JCheckBox checkbox = (JCheckBox) value;
            checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
            checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
            checkbox.setFont(getFont());
            checkbox.setFocusPainted(false);
            checkbox.setBorderPainted(false);
            checkbox.setBorder(
                    isSelected
                            ? UIManager.getBorder("List.focusCellHighlightBorder")
                            : new EmptyBorder(1, 1, 1, 1));
            return checkbox;
        }
    }
}
