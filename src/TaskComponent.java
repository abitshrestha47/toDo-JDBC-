import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskComponent extends JPanel {
    public int generatedId=0;
    private PreparedStatement insertStatement;
    private PreparedStatement completedStatement;
    private PreparedStatement updateStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement updateCompleted;
    public JCheckBox checkBox;
    public JTextPane textField;
    public DBManager dbManager;
    public JButton delete;
    public TodoFrame todo;
    private int width=350,height=30;
    TaskComponent(int id,TodoFrame todoframe) throws SQLException, ClassNotFoundException, RuntimeException {
        dbManager=new DBManager();
        this.todo=todoframe;
        deleteStatement=dbManager.conn.prepareStatement("DELETE FROM todos WHERE id=(?)");
        this.generatedId=id;
        setSize(100,200);
        checkBox=new JCheckBox();
        textField=new JTextPane();
        textField.setPreferredSize(new Dimension(width,height));
        delete=new JButton("Delete");
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(generatedId!=0){
                    try{
                        deleteStatement.setInt(1,generatedId);
                        deleteStatement.executeUpdate();
                        notifyDeletion();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        add(checkBox);
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    updateTextStrike();
            }
        });
        textField.setContentType("text/html");
        add(textField);
        add(delete);
        insertStatement=dbManager.conn.prepareStatement("INSERT INTO todos(task) VALUES (?)",PreparedStatement.RETURN_GENERATED_KEYS);
        updateStatement=dbManager.conn.prepareStatement("UPDATE todos SET task=? WHERE id=?");
        completedStatement = dbManager.conn.prepareStatement("SELECT * FROM todos WHERE id=?");
        try {
            completedStatement.setInt(1, generatedId);
            ResultSet resultSet = completedStatement.executeQuery();
            if (resultSet.next()) {
                boolean completed = resultSet.getBoolean("completed");
                if(completed){
                    checkBox.setSelected(true);
                }
//                System.out.println(completed);
            } else {
//                System.out.println("No result found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private void updateTextStrike() {
        if(checkBox.isSelected()){
            String getString=textField.getText();
            textField.setText("<html><s>" + getString + "</s></html>");
            try {
                updateCompleted=dbManager.conn.prepareStatement("UPDATE todos SET completed=? WHERE id=?");
                updateCompleted.setInt(1,1);
                updateCompleted.setInt(2,generatedId);
                updateCompleted.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            String text = textField.getText().replace("<s>", "");
            text = text.replace("</s>", "");
            textField.setText(text);        }
    }

    public void notifyDeletion() {
        todo.taskDeleted(this);
    }

    public JTextPane getTextField(){
        return textField;
    }

    public void setUpTextFieldListener() {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (generatedId == 0) {
                    try {
                        insertStatement.setString(1, textField.getText());
                        insertStatement.executeUpdate();

                        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            generatedId = generatedKeys.getInt(1);
//                            System.out.println(generatedId);
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else{
                    try {
                        updateStatement.setString(1,textField.getText());
                        updateStatement.setInt(2,generatedId);
                        updateStatement.executeUpdate();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
//                System.out.println(textField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    updateStatement.setString(1,textField.getText());
                    updateStatement.setInt(2,generatedId);
                    updateStatement.executeUpdate();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
//                System.out.println(textField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    updateStatement.setString(1,textField.getText());
                    updateStatement.setInt(2,generatedId);
                    updateStatement.executeUpdate();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
//                System.out.println(textField.getText());
            }
        });
    }
}
