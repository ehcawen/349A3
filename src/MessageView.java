import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MessageView extends JPanel implements Observer {

    // status messages for game
    JLabel fuel = new JLabel("fuel");
    JLabel speed = new JLabel("speed");
    JLabel message = new JLabel("message");
    GameModel model;
    Ship ship;

    public MessageView(GameModel model) {

        this.model=model;
        this.ship=model.ship;
        // want the background to be black
        setBackground(Color.BLACK);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(fuel);
        add(speed);
        add(message);

        for (Component c: this.getComponents()) {
            c.setForeground(Color.WHITE);
            c.setPreferredSize(new Dimension(100, 20));
        }
        model.addObserver(this);
        model.ship.addObserver(this);
        model.setChangedAndNotify();
    }



    @Override
    public void update(Observable o, Object arg) {
        if(ship.getFuel()<=10){
            fuel.setForeground(Color.RED);
        }else {
            fuel.setForeground(Color.WHITE);
        }

        if(ship.getSpeed()<=ship.getSafeLandingSpeed()){
            speed.setForeground(Color.GREEN);
        }else {
            speed.setForeground(Color.WHITE);
        }


        //int speedRound = (int)(ship.getSpeed()*100);
        String twoDec = String.format ("%.2f", ship.getSpeed());


        fuel.setText("fuel: "+ship.getFuel());
        speed.setText("speed: "+twoDec);

        if(ship.landed){
            speed.setForeground(Color.WHITE);
            message.setText("LANDED!");
        }else if(ship.crashed){
            speed.setForeground(Color.WHITE);
            message.setText("CRASH");
        }else if(ship.isPaused()){
            message.setText("Paused");
        }else {
            message.setText("");
        }
        repaint();
    }
}