import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

// the editable view of the terrain and landing pad
public class EditView extends JPanel implements Observer {

    public EditView(GameModel model) {

        // want the background to be black
        setBackground(Color.BLACK);

    }

    @Override
    public void update(Observable o, Object arg) {

    }

}
