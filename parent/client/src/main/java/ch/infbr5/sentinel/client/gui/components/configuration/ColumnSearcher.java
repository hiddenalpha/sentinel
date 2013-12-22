package ch.infbr5.sentinel.client.gui.components.configuration;

import java.util.regex.PatternSyntaxException;

import javax.swing.JTextField;
import javax.swing.RowFilter;

public class ColumnSearcher<T> {
    private final int column;
    private final JTextField textField;

    public ColumnSearcher(int column, JTextField textField) {
        this.column = column;
        this.textField = textField;
    }

    public JTextField getTextField() {
        return textField;
    }

    public boolean isEmpty() {
        String txt = textField.getText();
        
        return txt == null || txt.trim().length() == 0;
    }

    /**
     * @return Filter based on the associated text field's value, or null if the text does not compile to a valid
     * Pattern, or the text field is empty / contains whitespace.
     */
    public RowFilter<AbstractAdminTableModel<T>, Object> createFilter() {
        RowFilter<AbstractAdminTableModel<T>, Object> ftr = null;

        if (!isEmpty()) {
            try {
                ftr = RowFilter.regexFilter(textField.getText(), column);
            } catch(PatternSyntaxException ex) {
                // Do nothing - do not filter
            }
        }

        return ftr;
    }
}

