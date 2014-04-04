import TUIO.*;
import java.util.*;
import themidibus.*;
import processing.video.*;

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
color c;
PShape s;

void setup() {
  size(580,480);
  noStroke();
  fill(0);
  
  loop();
  frameRate(30);
  
  hint(ENABLE_NATIVE_FONTS);
  font = createFont("Arial", 18);
  
  tuioClient = new TuioProcessing(this);
  
  //MidiBus.list(); //looking for [x] "Bus 1", if this ever breaks, replace last number
  myBus = new MidiBus (this, -1, 0);
  
  //image = loadImage("debug.png");
  s = loadShape("eyedropper.svg");
  myMovie = new Movie(this, "test.mp4");
  myMovie.loop();
}


void draw()
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
   for (int i=0; i < tuioCursorList.size(); i++) {
      TuioCursor tcur = (TuioCursor)tuioCursorList.elementAt(i);
      Vector pointList = tcur.getPath();
      
      if (pointList.size() > 0) {
        shape(s, tcur.getScreenX(width)-15, tcur.getScreenY(height)-60, 64, 64);
      }
   }
}

void movieEvent(Movie m) {
  m.read();
  //if(lastTcur != null)
    //updateTuioCursor(lastTcur);
}

void setPV (TuioCursor tcur) {
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

void addTuioCursor(TuioCursor tcur) {
  lastTcur = tcur;
  setPV(tcur);
  myBus.sendNoteOn(channel, pitch, velocity);
}

void updateTuioCursor(TuioCursor tcur) {
  lastTcur = tcur;
  myBus.sendNoteOff(channel, pitch, velocity);
  setPV(tcur);
  myBus.sendNoteOn(channel, pitch, velocity);
}

void removeTuioCursor(TuioCursor tcur) {
  lastTcur = tcur;
  myBus.sendNoteOff(channel, pitch, velocity);
}
