import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import TUIO.*; 
import java.util.*; 
import themidibus.*; 
import processing.video.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class TUIO_with_midibus extends PApplet {






TuioProcessing tuioClient;
TuioCursor lastTcur;
MidiBus myBus;

float table_size = 760;
float scale_factor = 1;
PFont font;

int channel = 0;
int pitch = 40;
int velocity = 127;

int red, green, blue;

//PImage image;
Movie myMovie;
int c;
PShape s;

public void setup() {
  size(580,480);
  noStroke();
  fill(0);
  
  loop();
  frameRate(30);
  
  font = createFont("Arial", 18);
  
  tuioClient = new TuioProcessing(this);
  
  //MidiBus.list(); //looking for [x] "Bus 1", if this ever breaks, replace last number
  myBus = new MidiBus (this, -1, 0);
  
  //image = loadImage("debug.png");
  s = loadShape("circle.svg");
  myMovie = new Movie(this, "test2.mp4");
  myMovie.loop();
}


public void draw()
{
  background(255);
  
  //image(image, 100, 0);
  image(myMovie, 100,0);

  noStroke();
  fill(c);
  rect(25, 25, 50, 50);
  
  fill(0);
  textFont(font,18*scale_factor);
  text("R: " + red + "\nG: " + green + "\nB: " + blue, 15, 100);
 
   Vector tuioCursorList = tuioClient.getTuioCursors();
   if(tuioCursorList.size() == 0 && lastTcur != null) {
     shape(s, lastTcur.getScreenX(width), lastTcur.getScreenY(height), 16, 16);
   } else {
     for (int i=0; i < tuioCursorList.size(); i++) {
        TuioCursor tcur = (TuioCursor)tuioCursorList.elementAt(i);
        Vector pointList = tcur.getPath();
        
        if (pointList.size() > 0) {
          shape(s, tcur.getScreenX(width), tcur.getScreenY(height), 16, 16);
        }
     }
   }
}

public void movieEvent(Movie m) {
  m.read();
  if(lastTcur != null)
    updateTuioCursor(lastTcur);
}

public void setPV (TuioCursor tcur) {
  c = get(tcur.getScreenX(width),tcur.getScreenY(height));

  //int alpha = (c >> 24) & 0xFF;
  red   = (c >> 16) & 0xFF;
  green = (c >> 8)  & 0xFF;
  blue  =  c        & 0xFF;
  //print(" R: " + red + " G: " + green + " B: " + blue);
  
  velocity = red;
  pitch = blue/3;
  //velocity = 100-(int)(tcur.getY()*100);
  //pitch = (int)(tcur.getX()*100);
  //println("V: " + velocity + " P: " + pitch);
}

public void addTuioCursor(TuioCursor tcur) {
  if(lastTcur == null)
    myBus.sendNoteOff(channel, pitch, velocity);
  lastTcur = tcur;
  setPV(tcur);
  myBus.sendNoteOn(channel, pitch, velocity);
}

public void updateTuioCursor(TuioCursor tcur) {
  lastTcur = tcur;
  myBus.sendNoteOff(channel, pitch, velocity);
  setPV(tcur);
  myBus.sendNoteOn(channel, pitch, velocity);
}

public void removeTuioCursor(TuioCursor tcur) {
  lastTcur = tcur;
  //myBus.sendNoteOff(channel, pitch, velocity);
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "TUIO_with_midibus" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
