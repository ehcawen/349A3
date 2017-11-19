import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

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

        fuel.setText("fuel: "+ship.getFuel());
    }
}