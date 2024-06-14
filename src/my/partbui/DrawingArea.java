/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package my.partbui;

import java.awt.*;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.Timer;

/**
 *
 * @author chesp
 */
public class DrawingArea extends javax.swing.JPanel {

    //Intiating the global variable for an ArrayList of type integer that stores the coordinates
    private static ArrayList<Integer> coordinates = new ArrayList<>();

    //Initiating the timer variables for both the submarine and the Haida
    Timer t1;
    Timer t2;

    //Intiating the global variables of the current coordinates of the submarine and Haida
    private static int subX = -100;
    private static int subY = -100;
    private static int haidaX = -1000;
    private static int haidaY = -1000;

    //Instance variables, which are different from global/class variables
    private boolean showPrevious = true;
    private int subCount = 0;
    private int haidaCount = 0;

    //Boolean variable to check whether to spiral inwards or not
    private boolean spiralInwards = true;
    //Boolean variable to check whether Haida has already been initiated or not
    private boolean firstHaida = true;
    //Boolean variables to check whether or not it's been interepted or submarine has escaped yet
    private boolean intercepted = false;
    private boolean subEscaped = false;
    //Integer variables to check the count of the submarine and Haida when it's been intercepted
    private int interceptedSubCount = 0;
    private int interceptedHaidaCount = 0;
    //Boolean to check when it's hunting
    private boolean hunting = false;
    //Integer variables store intercept coordinates
    private int interceptSubX = 0;
    private int interceptSubY = 0;

    int storeCenterX = 0; // X coordinate of the center
    int storeCenterY = 0; // Y coordinate of the center
    double radius = 1;  // Starting radius
    double angle = Math.PI / 2; // Starting angle (12 o'clock)

    double firstRad = 1; //First radius, when showPrevious is toggled
    double firstAngle = Math.PI / 2; //First angle, when showPrevious is toggled

    //Component variables sent from the main frame
    int subSpeed = -1; // Speed of the submarine
    int haidaSpeed = -1; // Speed of the Haida
    int searchRadius = -1; // Search radius
    double a = -10; // a of the exponential function
    double b = -1; // b of the exponential function
    //Boolean variable to check if the program has started or not
    private boolean programStarted = false;

    /**
     * Creates new form DrawingArea
     */
    public DrawingArea() {
        initComponents();
    }

    @Override
    protected void paintComponent(Graphics g) {
        //Paint component
        super.paintComponent(g);
        //Checking if it's intercepted
        intercepted = intercept();
        //Check if it's hunting, and if it is, if the submarine escaped
        if (hunting) {
            if (subX < 0 || subX > 450 || subY < 0 || subY > 450) {
                subEscaped = true;
            }
        }
        //Checking for whether or not to stop the hunting variable
        if (intercepted && hunting) {
            interceptSubX = subX;
            interceptSubY = subY;
            hunting = false;
        } else if (subEscaped) {
            interceptSubX = subX;
            interceptSubY = subY;
            hunting = false;
            int sz = coordinates.size()-1;
            g.setColor(Color.blue);
            g.fillOval(coordinates.get(sz-3), coordinates.get(sz-2),10,20);
            g.fillOval(interceptSubX, interceptSubY, 10, 20);
        }
        //Main drawing loop, if it's not intercepted and the submarine hasn't already escaped
        if (!intercepted && !subEscaped) {
            //If show previous check is toggled, it runs this
            if (showPrevious) {
                for (int i = 0; i < haidaCount; i++) {
                    calcHaida(haidaX, haidaY);
                    g.setColor(Color.red);
                    g.fillRect(haidaX, haidaY, 15, 25);
                }
                firstRad = 1;
                firstAngle = Math.PI / 2;
                if (coordinates.size() > 0 && (subCount - 1) * 2 <= coordinates.size()) {
                    for (int i = 0; i < subCount - 1; i++) {
                        int xPos = coordinates.get(2 * i);
                        int yPos = coordinates.get(2 * i + 1);
                        g.setColor(Color.blue);
                        g.fillOval(xPos, yPos, 10, 20);
                    }
                }
                //If show previous isn't toggled, it runs this
            } else {
                g.setColor(Color.red);
                g.fillRect(haidaX, haidaY, 15, 25);
                g.setColor(Color.blue);
                g.fillOval(subX, subY, 10, 20);
            }
            //If if it's been intercepted and show previous has been toggled, it draws this
        } else if (intercepted && showPrevious) {
            interceptedHaidaCount = haidaCount;
            for (int i = 0; i < interceptedHaidaCount; i++) {
                calcHaida(haidaX, haidaY);
                g.setColor(Color.red);
                g.fillRect(haidaX, haidaY, 15, 25);
            }
            firstRad = 1;
            firstAngle = Math.PI / 2;
            interceptedSubCount = subCount;
            if (coordinates.size() > 0 && (subCount - 1) * 2 <= coordinates.size()) {
                for (int i = 0; i < interceptedSubCount - 1; i++) {
                    int xPos = coordinates.get(2 * i);
                    int yPos = coordinates.get(2 * i + 1);
                    g.setColor(Color.blue);
                    g.fillOval(xPos, yPos, 10, 20);
                }
            }
            //If intercepted and show previous isn't on, it draws this
        } else if (intercepted && !showPrevious) {
            g.setColor(Color.red);
            g.fillRect(haidaX, haidaY, 15, 25);
            g.setColor(Color.blue);
            g.fillOval(interceptSubX, interceptSubY, 10, 20);
            //If submarine has escaped and show previous is on, it draws this
        } else if (subEscaped && showPrevious) {
            interceptedHaidaCount = haidaCount;
            for (int i = 0; i < interceptedHaidaCount; i++) {
                calcHaida(haidaX, haidaY);
                g.setColor(Color.red);
                g.fillRect(haidaX, haidaY, 15, 25);
            }
            firstRad = 1;
            firstAngle = Math.PI / 2;
            interceptedSubCount = subCount;
            if (coordinates.size() > 0 && (subCount - 1) * 2 <= coordinates.size()) {
                for (int i = 0; i < interceptedSubCount - 1; i++) {
                    int xPos = coordinates.get(2 * i);
                    int yPos = coordinates.get(2 * i + 1);
                    g.setColor(Color.blue);
                    g.fillOval(xPos, yPos, 10, 20);
                }
            }
            //If submarine has escaped and show previous isn't on, it draws this
        } else if (subEscaped && !showPrevious) {
            g.setColor(Color.red);
            g.fillRect(haidaX, haidaY, 15, 25);
            g.setColor(Color.blue);
            g.fillOval(interceptSubX, interceptSubY, 10, 20);
        }
        //If not intercepted and hunting, it draws current distance
        if (!intercepted && hunting) {
            g.setColor(Color.black);
            double dist = calculateDistance(subX, subY, haidaX, haidaY);
            g.drawString("Current Distance: " + (int) dist, 10, 440);
            //If intercepted, it shows the coordinates that the submarine has been intercepted
        } else if (intercepted) {
            g.setColor(Color.black);
            g.drawString("Sub intercepted at: " + interceptSubX + ", " + interceptSubY, 10, 440);
            //If submarine escaped, then then it shows that the submarine escaped
        } else if (subEscaped) {
            g.setColor(Color.black);
            g.drawString("Submarine escaped!", 10, 440);
        }
    }

    private class TimerListener implements ActionListener {

        @Override
        //Haida timer
        public void actionPerformed(ActionEvent ae) {
            if (!showPrevious && hunting) {
                calcHaida(haidaX, haidaY);
            } else if (!firstHaida && !intercepted && hunting) {
                haidaCount++;
            }
            repaint();
        }
    }

    private class TimerListener2 implements ActionListener {

        @Override
        //Submarine timer
        public void actionPerformed(ActionEvent ae) {
            if (coordinates.size() > 0 && hunting) {
                getSubCoords();
            }
            if (subCount * 2 - 1 < coordinates.size() && !intercepted && hunting) {
                subCount++;
            }
            repaint();
        }
    }
    
    //Method that calculates distance between two points
    private static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
    }
    
    //Method that returns true or false if the submarine and Haida have been intercepted or not
    private boolean intercept() {
        if (calculateDistance(haidaX, haidaY, subX, subY) < searchRadius) {
            return true;
        } else {
            return false;
        }
    }
    
    //This is the recursive function that generates an ArrayList of coordinates
    private ArrayList<Integer> generateCoords(ArrayList<Integer> coords, double a, double b) {
        //Base case of the recursive function
        if (coords.get(coords.size() - 1) < 0 || coords.get(coords.size() - 1) > 450 || coords.get(coords.size() - 2) < 0 || coords.get(coords.size() - 2) > 450) {
            return coords;
        }
        int len = coords.size();

        if (len < 2) {
            return coords;
        }

        int subX1st = coords.get(0);
        int subY1st = coords.get(1);

        int subXlast = coords.get(len - 2);
        int subYlast = coords.get(len - 1);

        int delta = 10;
        int ovalsize = 20;
        double scaledown = 10.0;
        // Calculate next coordinates
        // Wwo conditions
        // Either x + delta
        // Or y is greater than the oval height
        int newX, newY;
        newX = subXlast + delta;
        newY = (int) (subY1st - (a * Math.pow(b, (newX - subX1st) / scaledown)) / scaledown);

        //If y jumps too fast, use Y to calcuate X
        if (Math.abs(newY - subYlast) > ovalsize) {
            if (a > 0) {
                newY = subYlast - ovalsize;
            } else {
                newY = subYlast + ovalsize;
            }

            newX = (int) (Math.log((subY1st - newY) * scaledown / a) / Math.log(b) * scaledown + subX1st);
        }
        
        //Returning the recursive function
        coords.add(newX);
        coords.add(newY);
        return generateCoords(coords, a, b);
    }
    
    //Method that gets the next sub coordinates
    private static boolean getSubCoords() {
        int size = coordinates.size();
        int index = -1;
        for (int i = 0; i < size; i += 2) {
            if (coordinates.get(i) == subX) {
                index = i;
                if (i == size - 2) {
                    return false;
                }
            }
        }
        //Setting the next sub coordinates to subX and subY
        subX = coordinates.get(index + 2);
        subY = coordinates.get(index + 3);
        return true;
    }
    
    //Method that calculates the Haidas next point
    private void calcHaida(int xCoord, int yCoord) {
        if (spiralInwards) {
            //If the Haida spirals clockwise
            if (!showPrevious) {
                if (haidaX < 0 || haidaY < 0) {
                    haidaX = xCoord;
                    haidaY = yCoord;
                    storeCenterX = xCoord;
                    storeCenterY = yCoord;
                } else {
                    if (radius == 1) {
                        angle += Math.PI / 4;
                    } else {
                        angle += Math.PI / 18;
                    }
                    if (angle < 0) {
                        angle += 2 * Math.PI;
                    }

                    if (angle >= (Math.PI) / 2 + 6.2) {
                        angle = Math.PI / 2;
                        radius += 30;
                    }
                }
                haidaX = (int) (storeCenterX + radius * Math.cos(angle));
                haidaY = (int) (storeCenterY + radius * Math.sin(angle));
            } else {
                if (firstHaida) {
                    if (haidaX < 0 || haidaY < 0) {
                        haidaX = xCoord;
                        haidaY = yCoord;
                        storeCenterX = xCoord;
                        storeCenterY = yCoord;
                        firstHaida = false;
                        haidaCount++;
                    }
                } else {
                    if (firstRad == 1) {
                        firstAngle += Math.PI / 4;
                    } else {
                        firstAngle += Math.PI / 18;
                    }
                    if (firstAngle < 0) {
                        firstAngle += 2 * Math.PI;
                    }

                    if (firstAngle >= (Math.PI) / 2 + 6.2) {
                        firstAngle = Math.PI / 2;
                        firstRad += 30;
                    }
                }
                haidaX = (int) (storeCenterX + firstRad * Math.cos(firstAngle));
                haidaY = (int) (storeCenterY + firstRad * Math.sin(firstAngle));
            }
        } else {
            //If the Haida spirals counterclockwise
            if (!showPrevious) {
                if (haidaX < 0 || haidaY < 0) {
                    haidaX = xCoord;
                    haidaY = yCoord;
                    storeCenterX = xCoord;
                    storeCenterY = yCoord;
                } else {
                    if (radius == 1) {
                        angle += Math.PI / 4;
                    } else {
                        angle += Math.PI / 18;
                    }
                    if (angle < 0) {
                        angle += 2 * Math.PI;
                    }

                    if (angle >= (Math.PI) / 2 + 6.2) {
                        angle = Math.PI / 2;
                        radius += 30;
                    }
                }
                haidaX = (int) (storeCenterX + radius * Math.cos(Math.PI * 2 - angle));
                haidaY = (int) (storeCenterY + radius * Math.sin(Math.PI * 2 - angle));
            } else {
                if (firstHaida) {
                    if (haidaX < 0 || haidaY < 0) {
                        haidaX = xCoord;
                        haidaY = yCoord;
                        storeCenterX = xCoord;
                        storeCenterY = yCoord;
                        firstHaida = false;
                        haidaCount++;
                    }
                } else {
                    if (firstRad == 1) {
                        firstAngle += Math.PI / 4;
                    } else {
                        firstAngle += Math.PI / 18;
                    }
                    if (firstAngle < 0) {
                        firstAngle += 2 * Math.PI;
                    }

                    if (firstAngle >= (Math.PI) / 2 + 6.2) {
                        firstAngle = Math.PI / 2;
                        firstRad += 30;
                    }
                }
                haidaX = (int) (storeCenterX + firstRad * Math.cos(Math.PI * 2 - firstAngle));
                haidaY = (int) (storeCenterY + firstRad * Math.sin(Math.PI * 2 - firstAngle));
            }
        }
    }

    //Method that gives the current distance inbetween the sub and the Haida
    public static int giveCurrentDistance() {
        double dist = calculateDistance(subX, subY, haidaX, haidaY);
        return (int) dist;
    }

    //Getter method for the showPrevious boolean for the main jFrame
    public void getShowPrevious(boolean showPrevious) {
        this.showPrevious = showPrevious;
    }
    
    //Method that gets the spiralInwards boolean from the main jFrame
    public void getSpiralInwards(boolean spiralInwards) {
        this.spiralInwards = spiralInwards;
    }

    //Method that gets the info from the main frame, and interepts it into usable information
    public void getInfo(int subSpeed, int haidaSpeed, int searchRadius, double a, double b) {
        //Checking to see which speed was entered and interepting it into usable data for submarine
        switch (subSpeed) {
            case 7:
                this.subSpeed = 150;
                break;
            case 8:
                this.subSpeed = 100;
                break;
            case 6:
                this.subSpeed = 200;
                break;
            default:
                this.subSpeed = 150;
                break;
        }
        //Checking to see which speed was entered and interepting it into usable data for Haida
        switch (haidaSpeed) {
            case 30:
                this.haidaSpeed = 90;
                break;
            case 31:
                this.haidaSpeed = 80;
                break;
            case 32:
                this.haidaSpeed = 70;
                break;
            case 33:
                this.haidaSpeed = 60;
                break;
            case 34:
                this.haidaSpeed = 50;
                break;
            case 35:
                this.haidaSpeed = 40;
                break;
            case 36:
                this.haidaSpeed = 30;
                break;
            case 37:
                this.haidaSpeed = 20;
                break;
            case 38:
                this.haidaSpeed = 10;
            default:
                this.haidaSpeed = 50;
                break;
        }
        this.searchRadius = searchRadius * 100;
        this.a = a;
        this.b = b;
    }

    //Method that returns programStarted to the main jFrame
    public boolean giveProgramStarted() {
        return programStarted;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createTitledBorder("HMCS Haida"));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 440, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 427, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        //Mouse click method, checks if the mouse has been clicked twice to start the program
        PointerInfo aM = MouseInfo.getPointerInfo();
        Point bM = aM.getLocation();
        int x = (int) bM.getX();
        int y = (int) bM.getY();
        if (a > -5.1) {
            if (!programStarted) {
                //First click check
                if (subX < 0 || subY < 0) {
                    subX = x - 10;
                    subY = y - 30;
                } else {
                    //Runs when the second click happens
                    coordinates.add(subX);
                    coordinates.add(subY);
                    coordinates = generateCoords(coordinates, a, b);
                    //Printing out the ArrayList as specified in the instructions
                    for (int i = 0; i < coordinates.size() / 2; i++) {
                        System.out.println("(" + coordinates.get(2 * i) + ", " + coordinates.get(2 * i + 1) + ")");
                    }
                    calcHaida(x - 10, y - 30);
                    //Starting the hunting and programStarted boolean variables
                    hunting = true;
                    programStarted = true;
                    
                    //Starting the timers with the given speed
                    t1 = new Timer(haidaSpeed, new DrawingArea.TimerListener());
                    t1.start();

                    t2 = new Timer(subSpeed, new DrawingArea.TimerListener2());
                    t2.start();
                }
            }
        }
    }//GEN-LAST:event_formMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
