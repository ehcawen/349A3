import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.undo.*;
import javax.vecmath.*;
import java.util.Random;

public class GameModel extends Observable {

    public GameModel(int fps, int width, int height, int peaks) {

        ship = new Ship(60, width/2, 50);
        shipWeight =10;

        worldBounds = new Rectangle2D.Double(0, 0, width, height);

        //landPad initialization
        padWidth=40;
        padHeight=10;
        padX=330;
        padY=100;
        landPad = new Rectangle2D.Double(padX,padY,padWidth,padHeight);
        centerX = padWidth/2 +330;
        centerY = padHeight/2+100;

        //Terrain initialiazation
        backX = new int[4];
        backY = new int[4];
        backX[0]=0;
        backX[1]=width;
        backX[2]=width;
        backX[3]=0;
        backY[0]=0;
        backY[1]=0;
        backY[2]=height;
        backY[3]=height;
        Random rand = new Random();
        xpoints= new int[22];
        ypoints= new int[22];
        xpoints[0]=0;
        xpoints[21]=width;
        ypoints[0]=height;
        ypoints[21]=height;

        int interval = width/19;

        for(int i=1;i<21;i++){
            xpoints[i]=(i-1)*interval;
            if(i==20){
                xpoints[20]=width;
            }
            ypoints[i]=rand.nextInt(height/2)+height/2;
            System.out.println("x"+i+"= "+xpoints[i]);
        }

        terrain = new Polygon(xpoints,ypoints,22);

        // anonymous class to monitor ship updates
        ship.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                setChangedAndNotify();
            }
        });
    }

    // World
    // - - - - - - - - - - -
    public final Rectangle2D getWorldBounds() {
        return worldBounds;
    }

    Rectangle2D.Double worldBounds;


    // Ship
    // - - - - - - - - - - -
    //blue
    public Ship ship;
    int shipWeight;

    // Landing Pad
    public Rectangle2D.Double landPad;
    int padX,padY;
    int centerX,centerY;
    int padWidth;
    int padHeight;

    void moveLandPad(int x, int y){
        int deltaX = x-centerX;
        int deltaY = y-centerY;
        landPad.x=landPad.x+deltaX;
        landPad.y=landPad.y+deltaY;
        centerX=x;
        centerY=y;
        setChangedAndNotify();
    }

    // mouseclick hit test
    // ship hit test

    //background
    int [] backX;
    int [] backY;
    public int[] getBackX(){
        return backX;
    }
    public int[] getBackY(){
        return backY;
    }

    // Terrain
    private int peaks = 20;
    public Polygon terrain;
    int[] xpoints, ypoints;

    public Polygon getTerrain(){
        return terrain;
    }
    // draw 20 circles 15 pix rad
    //circle mouseclick hit test (distance <= 15)
    //terrain hit test


    // Observerable
    // - - - - - - - - - - -

    // helper function to do both
    void setChangedAndNotify() {
        setChanged();
        notifyObservers();
    }

}



