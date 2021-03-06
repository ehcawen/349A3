import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

// the edit toolbar
public class ToolBarView extends JPanel implements Observer {
    GameModel model;
    JButton undo = new JButton("Undo");
    JButton redo = new JButton("Redo");

    public ToolBarView(GameModel model) {
        this.model=model;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // prevent buttons from stealing focus
        undo.setFocusable(false);
        redo.setFocusable(false);

        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.undo();
            }
        });

        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.redo();
            }
        });

        add(undo);
        add(redo);
        model.addObserver(this);
        model.ship.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(!model.canUndo()){
            undo.setEnabled(false);
        }else {
            undo.setEnabled(true);
        }

        if(!model.canRedo()){
            redo.setEnabled(false);
        }else {
            redo.setEnabled(true);
        }
        repaint();
    }
}
