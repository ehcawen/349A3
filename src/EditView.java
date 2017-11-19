import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;
import java.awt.geom.Point2D;

// the editable view of the terrain and landing pad
public class EditView extends JPanel implements Observer {
    GameModel model;
    boolean landPadSelected;
    int peakSelected;//-1 => nothing selected; 1-20 => peak index

    int lastX;
    int lastY;
    int lastCenterX;
    int lastCenterY;

    boolean dragging = false;
    public EditView(GameModel model) {
        this.model = model;
        lastX=0;
        lastY=0;
        lastCenterX=model.getCenterX();
        lastCenterY=model.getCenterY();
        // want the background to be light grey
        setBackground(Color.BLACK);

        //  landing pad can be repositioned with a double-click
        this.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2){
                    double currentPosX= e.getX();
                    double currentPosY = e.getY();
                    if(currentPosX+model.landPad.width/2<=model.worldBounds.width
                            && currentPosX-model.landPad.width/2>=0
                            && currentPosY+model.landPad.height/2<=model.worldBounds.height
                            && currentPosY-model.landPad.height/2>=0){
                    //move landing pad center to e.x e.y
                        System.out.println("Double click");
                        model.padClick=true;
                        model.dropLandingPad(e.getX(),e.getY());
                        lastCenterX=e.getX();
                        lastCenterY=e.getY();
                        model.setLastCenterX(lastCenterX);
                        model.setLastCenterY(lastCenterY);
                    }
                }

            }
        });

        this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
                lastX=e.getX();
                lastY=e.getY();
                Point2D pos = new Point2D.Double(e.getX(),e.getY());
                //hittest for landing pad
                if(model.landPad.contains(e.getX(),e.getY())){
                    landPadSelected=true;
                }else{
                    for(int i =1;i<21;i++){
                        Point2D peak = new Point2D.Double(model.terrain.xpoints[i],model.terrain.ypoints[i]);
                        if(pos.distance(peak)<=15){
                            peakSelected=i;
                            break;
                        }
                    }
                }
            }
        });


        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                //drag selected
                int offsetX = e.getX()-lastX;
                int offsetY = e.getY()-lastY;
                if(landPadSelected){//drag landing pad
                    model.dragLandingPad(model.getCenterX()+offsetX,model.getCenterY()+offsetY);
                }else if(peakSelected!=-1){

                    int y= model.terrain.ypoints[peakSelected];

                        model.dragPeak(y+offsetY,peakSelected);


                }
                lastX=e.getX();
                lastY=e.getY();
            }
        });

        this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                int offsetX = e.getX()-lastX;
                int offsetY = e.getY()-lastY;
                if(landPadSelected){
                    if(e.getX()+model.landPad.width/2<=model.worldBounds.width
                            && e.getX()-model.landPad.width/2>=0
                            && e.getY()+model.landPad.height/2<=model.worldBounds.height
                            && e.getY()-model.landPad.height/2>=0){

                        model.dropLandingPad(model.getCenterX(),model.getCenterY());
                        lastCenterX=model.getCenterX()+offsetX;
                        lastCenterY=model.getCenterY()+offsetY;
                        model.setLastCenterX(lastCenterX);
                        model.setLastCenterY(lastCenterY);

                    }else{
                        model.dragLandingPad(lastCenterX,lastCenterY);
                    }

                }else if(peakSelected!=-1){
                    int y= model.terrain.ypoints[peakSelected];
                    if(e.getY()>=0 && e.getY()<=model.worldBounds.height){
                        model.dropPeak(y,peakSelected);
                    }else{
                        model.dragPeak(model.ypoints[peakSelected],peakSelected);
                    }


                }
                //nothing selected now
                landPadSelected=false;
                peakSelected=-1;
                lastX=e.getX();
                lastY=e.getY();
            }
        });

        model.addObserver(this);
        model.ship.addObserver(this);
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
