import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



class StudentGradeCalculator1 extends JFrame  {
private JTextField Maths1,English1,Physics1,Chemistry1,ComputerScience1;
private JTextArea resultArea;
private JButton calculateButton;
    public StudentGradeCalculator1() {
    setTitle("Calculate Student Grade");
    setSize(500, 500);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    resultArea = new JTextArea(40,50);
    resultArea.setBounds(10,300,100,200);
    resultArea.setSize(100,200);
    resultArea.setEditable(false);
    calculateButton = new JButton("Calculate Scorecard");

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JPanel Maths = new JPanel();
    Maths.add(new JLabel("Math's marks:"));
    Maths.setBounds(10,10,100,50);
    Maths1=new JTextField(10);
    Maths.add(Maths1);
    panel.add(Maths);

    JPanel English = new JPanel();
    English.add(new JLabel("English's marks:"));
    English.setBounds(60,10,100,50);
    English1=new JTextField(10);
    English.add(English1);
    panel.add(English);

    JPanel Physics= new JPanel();
    Physics.add(new JLabel("Physics's marks:"));
    Physics.setBounds(110,10,100,50);
    Physics1=new JTextField(10);
    Physics.add(Physics1);
    panel.add(Physics);

    JPanel Chemistry= new JPanel();
    Chemistry.add(new JLabel("Chemistry's marks:"));
    Chemistry.setBounds(160,10,100,50);
    Chemistry1=new JTextField(10);
    Chemistry.add(Chemistry1);
    panel.add(Chemistry);

    JPanel ComputerScience= new JPanel();
    ComputerScience.add(new JLabel("ComputerScience's marks:"));
    ComputerScience.setBounds(210,10,100,50);
    ComputerScience1=new JTextField(10);
    ComputerScience.add(ComputerScience1);
    panel.add(ComputerScience);

    panel.add(Box.createVerticalStrut(100)); // Add space between input and button

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(calculateButton);

    panel.add(buttonPanel);
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scorecardCalculate();
            }
        });
    add(panel, BorderLayout.NORTH);
   // add(new JScrollPane(resultArea), BorderLayout.CENTER);
        panel.add(resultArea);
    setVisible(true);
        }
        //Main function to calculate grade scorecard
    public void scorecardCalculate(){
      try {
          int maths = Integer.parseInt((Maths1.getText()));
          int english = Integer.parseInt(English1.getText());
          int computerScience = Integer.parseInt(Maths1.getText());
          int physics = Integer.parseInt(Physics1.getText());
          int chemistry = Integer.parseInt(Chemistry1.getText());
          int totalmarks = maths + english + computerScience + physics + chemistry;

          double average = totalmarks / 5;
          if (average >= 85) {
              resultArea.setText(" AveragePercentage : " + average + "%\n Total marks : " + totalmarks + "\n OverallGrade : A+");
          } else if (average >= 75) {
              resultArea.setText(" AveragePercentage : " + average + "%\n Total marks : " + totalmarks + "\n OverallGrade : A");
          } else if (average >= 60) {
              resultArea.setText(" AveragePercentage : " + average + "%\n Total marks : " + totalmarks + "\n OverallGrade : B");
          } else if (average >= 45) {
              resultArea.setText(" AveragePercentage : " + average + "%\n Total marks : " + totalmarks + "\n OverallGrade : C");
          } else if (average >= 33) {
              resultArea.setText(" AveragePercentage : " + average + "%\n Total marks : " + totalmarks + "\n OverallGrade : D");
          } else {
              resultArea.setText(" AveragePercentage : " + average + "%\n Total marks : " + totalmarks + "\n OverallGrade : E");
          }
      }
      catch (NumberFormatException e){
          resultArea.setText(" ! Your marks cannot be empty or illegal character.\n   Please enter valid marks");
      }
    }
    public static void main(String[] args) {
        new StudentGradeCalculator1();
    }
}


