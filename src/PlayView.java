import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;
import java.awt.geom.*;
import javax.vecmath.*;

// the actual game view
public class PlayView extends JPanel implements Observer {

    GameModel model;
    Ship ship;


    public PlayView(GameModel model) {
        this.model=model;
        this.ship=model.ship;
        // needs to be focusable for keylistener
        setFocusable(true);

        // want the background to be black
        setBackground(Color.BLACK);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                width=e.getComponent().getSize().width;
                height=e.getComponent().getSize().height;
                transformX=(int)(-ship.getPosition().getX()+width/6);
                transformY=(int)(-ship.getPosition().getY()+height/6);
            }
        });

        this.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {

                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    if(ship.isPaused()){
                        ship.setPaused(false);
                    }else {
                        ship.setPaused(true);
                    }
                }

                if(!ship.isPaused()){
                    if(e.getKeyCode()==KeyEvent.VK_W){
                        ship.thrustUp();
                    }

                    if(e.getKeyCode()==KeyEvent.VK_A){
                        ship.thrustLeft();
                    }

                    if(e.getKeyCode()==KeyEvent.VK_S){
                        ship.thrustDown();
                    }

                    if(e.getKeyCode()==KeyEvent.VK_D){
                        ship.thrustRight();
                    }
                }

            }
        });

        model.addObserver(this);
        model.ship.addObserver(this);

    }

    int height=309;
    int width=700;
    //ship initially 350,50
    //mid initial 350,154.5
    //transformX 0, 50-154.5
    //transformCenter = -startpoint+ windowSize/scale/2
    int transformX=-350+700/6;
    int transformY=-50+300/6;

    void transformCenter(){
        transformX=(int)(-ship.getPosition().getX()+width/6);
        transformY=(int)(-ship.getPosition().getY()+height/6);
    }

    public void paintComponent(Graphics g){
        transformCenter();
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                RenderingHints.VALUE_ANTIALIAS_ON);


        AffineTransform M = g2.getTransform();

        // multiply in this shape's transform
        // (uniform scale)
        g2.scale(3, 3);

        g2.translate(transformX,transformY);

        //draw grey background
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillPolygon(model.getBackX(),model.getBackY(),4);

        //draw terrain
        g2.setColor(Color.DARK_GRAY);
        g2.fillPolygon(model.getTerrain());

        //draw landing pad
        g2.setColor(Color.RED);
        g2.fillRect((int)model.landPad.x,(int)model.landPad.y,(int)model.landPad.width,(int)model.landPad.height);

        // draw ship
        g2.setColor(Color.blue);
        g2.fillRect((int)ship.position.getX()-5,(int)ship.position.getY()-5,model.shipWidth,model.shipWidth);

        // save the current g2 transform matrix


        // reset the transform to what it was before we drew the shape
        g2.setTransform(M);

        repaint();
    }


    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }
}
