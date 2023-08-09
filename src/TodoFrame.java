import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class TodoFrame extends JFrame {
    boolean check;
    private PreparedStatement selectStatement;
    private JLabel todolist;
    private DBManager dbManager;
    private JPanel taskPanel;
    private JPanel taskComponentPanel;
    private JTextField textField;
    private TaskComponent taskComponent;
    private JPanel box;
    private JButton add;
    private TodoFrame todo;
    TodoFrame(){
        todo=this;
        taskComponentPanel = new JPanel();
        try{
            dbManager=new DBManager();
        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        try {
            selectStatement=dbManager.conn.prepareStatement("SELECT * FROM todos");
            ResultSet resultSet= selectStatement.executeQuery();
            while (resultSet.next()){
                 int id=resultSet.getInt("id");
                 String task = resultSet.getString("task");
                taskComponent = new TaskComponent(id,todo);
                taskComponent.getTextField().setText(task);
                taskComponentPanel.add(taskComponent);
                taskComponent.setUpTextFieldListener();
            }
            resultSet.close();
            selectStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        setLayout(new BorderLayout());
        JPanel panel=new JPanel();
        todolist=new JLabel("TO DO LIST");
        Font font=todolist.getFont();
        Font largerFont=font.deriveFont(font.getSize()+20f);
        todolist.setFont(largerFont);
        panel.add(todolist);
        add(panel,BorderLayout.NORTH);


        box=new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.drawRect(20,0,getWidth()-40,getHeight()-40);
            }
        };
        box.setLayout(new BorderLayout());
        add(box, BorderLayout.CENTER);

        taskPanel=new JPanel();

        taskComponentPanel.setLayout(new BoxLayout(taskComponentPanel, BoxLayout.Y_AXIS));
        taskPanel.add(taskComponentPanel);
        JScrollPane scrollPane = new JScrollPane(taskPanel);
        add(scrollPane, BorderLayout.CENTER);

        add=new JButton("Add");
        add(add,BorderLayout.SOUTH);
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    taskComponent = new TaskComponent(0,todo);
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                taskComponentPanel.add(taskComponent);
                taskComponentPanel.revalidate();
                taskComponentPanel.repaint();
                taskComponent.setUpTextFieldListener();
            }
        });

        setSize(500,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public void taskDeleted(TaskComponent taskComponent) {
        taskComponentPanel.remove(taskComponent);
        taskComponentPanel.revalidate();
        taskComponentPanel.repaint();
    }
}
