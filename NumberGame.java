import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ThreadLocalRandom;

public class NumberGame implements ActionListener {
    JFrame Frame1;
    JLabel label;
    JTextArea TextArea;
    JButton button;
    NumberGame(){
        //set Frame
        Frame1=new JFrame();
        Frame1.setBounds(50,50,400,400);
        Frame1.setSize(400,400);
        Frame1.setVisible(true);
        ImageIcon icon=new ImageIcon("C://Users//suraj chorassiya//Music//OneDrive//Desktop//number.png/");
        System.out.println(icon.getIconHeight()+" "+icon.getIconWidth());

        button=new JButton("Play Games");
        button.setBounds(300,300,300,100);


        Frame1.add(new JLabel(icon));
        Frame1.pack();
        Frame1.add(button);
        button.addActionListener(this);
    }
   // public void playGames()

    public static void main(String[] args) {
        NumberGame game=new NumberGame();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
       new SecondFrame();
    }
}
class SecondFrame extends JFrame  {
    int scorecard=0;
    private JTextField GuessNumber;
    private JTextArea resultArea;
    private JButton playGamesAgain;
    int count=0;
    public SecondFrame() {

        setTitle("Number Games");
        setSize(600, 600);

        resultArea = new JTextArea(40,50);
        resultArea.setBounds(360,360,100,200);
        resultArea.setSize(100,200);
        resultArea.setEditable(false);

        GuessNumber=new JTextField(10);
        GuessNumber.setSize(100,100);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Enter your guess number between 1 to 100 :   "));
        panel.add(GuessNumber);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(10));
        panel.add(GuessNumber);
        panel.add(Box.createVerticalStrut(100));// Add space between input and button

        playGamesAgain= new JButton("Play game");
        playGamesAgain.setBounds(180,100,40,80);
        panel.add(Box.createHorizontalStrut(300));

        panel.add(playGamesAgain);
        panel.add(Box.createHorizontalStrut(300));
        panel.add(Box.createVerticalStrut(100));
        SecondFrame.setDefaultLookAndFeelDecorated(true);

        playGamesAgain.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                count++;
                //System.out.println(count);
                if(count>=6){
                    JOptionPane.showMessageDialog(panel,"You have exhausted your limit !\n You have scored :-"+ scorecard+" \nPress OK to play again");
                   count=0;
                   scorecard=0;
                }
                playGames();
            }
        });
        //panel.add(GuessNumber);
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
        setVisible(true);
    }
    public void playGames() {
        try {
            int randomNumber = ThreadLocalRandom.current().nextInt(1, 100);

            int guessNumber = Integer.parseInt(GuessNumber.getText());
            if (guessNumber == randomNumber) {
                scorecard++;
                resultArea.setText("Congratulation ! You have got the number." + "\nScorecard : " + scorecard);
            } else if (guessNumber < randomNumber) {
                resultArea.setText("The number  is too less" + "\n The random number was " + randomNumber + "\nScorecard : " + scorecard);
            } else {
                resultArea.setText("The number is too high" + "\n The random number was " + randomNumber + "\nScorecard : " + scorecard);
            }
        } catch (NumberFormatException e) {
            resultArea.setText("Please enter valid input");
        }
    }

}
