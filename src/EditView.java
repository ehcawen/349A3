import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

// the editable view of the terrain and landing pad
public class EditView extends JPanel implements Observer {
    GameModel model;
    boolean landPadSelected;
    int peakSelected;//-1 => nothing selected; 1-20 => peak index

    public EditView(GameModel model) {
        this.model = model;

        // want the background to be light grey
        setBackground(Color.BLACK);

        //  landing pad can be repositioned with a double-click
        this.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2){
                    //move landing pad center to e.x e.y
                    model.moveLandPad(e.getX(),e.getY());
                }

            }
        });

        this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
                //hittest
            }
        });

        this.addMouseListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                //drag selected
            }
        });

        this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                //nothing selected
            }
        });

        model.addObserver(this);
        repaint();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                RenderingHints.VALUE_ANTIALIAS_ON);

        //draw grey background

        g2.setColor(Color.LIGHT_GRAY);
        g2.fillPolygon(model.getBackX(),model.getBackY(),4);

        //draw terrain
        g2.setColor(Color.DARK_GRAY);
        g2.fillPolygon(model.getTerrain());

        //draw peak circle
        g2.setColor(Color.GRAY);
        Polygon terrain = model.getTerrain();
        int r = 15;
        for(int i =1;i<21;i++){
            int x = terrain.xpoints[i]-r;
            int y = terrain.ypoints[i]-r;
            g2.drawOval(x,y,r*2,r*2);
        }

        //draw landing pad
        g2.setColor(Color.RED);
        g2.fillRect((int)model.landPad.x,(int)model.landPad.y,(int)model.landPad.width,(int)model.landPad.height);


        repaint();
    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

}
