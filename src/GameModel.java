import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.undo.*;
import javax.vecmath.*;
import java.util.Random;

public class GameModel extends Observable {
    // Undo manager
    private UndoManager undoManager;

    public GameModel(int fps, int width, int height, int peaks) {

        undoManager = new UndoManager();

        ship = new Ship(60, width/2, 50);
        shipWidth =10;

        worldBounds = new Rectangle2D.Double(0, 0, width, height);

        //landPad initialization
        padWidth=40;
        padHeight=10;
        padX=330;
        padY=100;
        landPad = new Rectangle2D.Double(padX,padY,padWidth,padHeight);
        centerX = padWidth/2 +330;
        centerY = padHeight/2+100;
        exactCenterX=centerX;
        exactCenterY=centerY;
        lastCenterX=centerX;
        lastCenterY=centerY;

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
        exactYpoints=new int[21];
        xpoints[0]=0;
        xpoints[21]=width;
        ypoints[0]=height;
        ypoints[21]=height;
        exactYpoints[0]=-1;

        int interval = width/19;

        for(int i=1;i<21;i++){
            xpoints[i]=(i-1)*interval;
            if(i==20){
                xpoints[20]=width;
            }
            ypoints[i]=rand.nextInt(height/2)+height/2;
            exactYpoints[i]=ypoints[i];
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
    int shipWidth;
    int shipPosX=350;
    int shipPosY=50;



    // Landing Pad
    public Rectangle2D.Double landPad;
    public  boolean padClick=false;
    int padX,padY;
    int centerX,centerY;
    int padWidth;
    int padHeight;

    int lastCenterX;
    int lastCenterY;
    //center x y for redo undo
    public int exactCenterX;
    public int exactCenterY;

    int getCenterX(){
        return centerX;
    }
    int getCenterY(){
        return centerY;
    }

    void setLastCenterX(int x){
        lastCenterX = x;
    }
    void setLastCenterY(int y){
        lastCenterY = y;
    }

    //normal move landing pad
    void dragLandingPad(int x, int y){
        int deltaX = x-centerX;
        int deltaY = y-centerY;
        landPad.x=landPad.x+deltaX;
        landPad.y=landPad.y+deltaY;
        centerX=x;
        centerY=y;
        setChangedAndNotify();
    }

    void dropLandingPad(int x,int y){
        System.out.println("Model: drop pad to " + x + " " + y);
        // create undoable edit
        UndoableEdit undoableEdit = new AbstractUndoableEdit() {

            // capture variables for closure
            final int oldX = exactCenterX;
            final int newX = x;
            final int oldY = exactCenterY;
            final int newY = y;


            // Method that is called when we must redo the undone action
            public void redo() throws CannotRedoException {
                super.redo();
                centerX = newX;
                centerY = newY;
                exactCenterX=newX;
                exactCenterY=newY;
                landPad.x=centerX-padWidth/2;
                landPad.y=centerY-padHeight/2;
                System.out.println("Model: redo value to " + centerX + " " + centerY);
                setChangedAndNotify();
            }

            public void undo() throws CannotUndoException {
                super.undo();
                centerX = oldX;
                centerY = oldY;
                exactCenterX=oldX;
                exactCenterY=oldY;
                landPad.x=centerX-padWidth/2;
                landPad.y=centerY-padHeight/2;
                System.out.println("Model: undo value to " + centerX + " " + centerY);
                setChangedAndNotify();
            }
        };

        // Add this undoable edit to the undo manager
        undoManager.addEdit(undoableEdit);

        if(padClick){
            landPad.x=x-landPad.width/2;
            landPad.y=y-landPad.height/2;
            centerX=x;
            centerY=y;
            exactCenterX=x;
            exactCenterY=y;
            padClick=false;
        }else{
            exactCenterX=centerX;
            exactCenterY=centerY;
        }

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
    int currentPeak = 0;
    public Polygon terrain;
    int[] xpoints, ypoints;
    int[] exactYpoints;

    public Polygon getTerrain(){
        return terrain;
    }

    public void dragPeak(int y, int peakSelected){
        terrain.ypoints[peakSelected]=y;
        setChangedAndNotify();
    }

    public void dropPeak(int y, int peakSelected){
        UndoableEdit undoableEdit = new AbstractUndoableEdit() {

            // capture variables for closure

            final int oldValue = exactYpoints[peakSelected];
            final int newValue = y;


            // Method that is called when we must redo the undone action
            public void redo() throws CannotRedoException {
                super.redo();
                exactYpoints[peakSelected] = newValue;
                terrain.ypoints[peakSelected] = newValue;

                System.out.println("Model: redo peak to " + terrain.ypoints[peakSelected]);
                setChanged();
                notifyObservers();
            }

            public void undo() throws CannotUndoException {
                super.undo();
                exactYpoints[peakSelected] = oldValue;
                terrain.ypoints[peakSelected] = oldValue;

                System.out.println("Model: undo peak to " + terrain.ypoints[peakSelected]);
                setChanged();
                notifyObservers();
            }
        };

        // Add this undoable edit to the undo manager
        undoManager.addEdit(undoableEdit);
        exactYpoints[peakSelected]=y;
        terrain.ypoints[peakSelected]=y;
        currentPeak=peakSelected;
        setChangedAndNotify();
    }
    // draw 20 circles 15 pix rad
    //circle mouseclick hit test (distance <= 15)
    //terrain hit test

    // undo and redo methods
    // - - - - - - - - - - - - - -

    public void undo() {
        if (canUndo())
            undoManager.undo();
    }

    public void redo() {
        if (canRedo())
            undoManager.redo();
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canRedo();
    }

    // Observerable
    // - - - - - - - - - - -

    // helper function to do both
    void setChangedAndNotify() {
        setChanged();
        notifyObservers();
    }

}



