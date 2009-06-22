/**************************************************************************************
Copyright (C) Apeksha Godiyal, 2008
Copyright (C) Gerardo Huck, 2009


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

See license.h for more information.
**************************************************************************************/
// GPLv3 License
#include "license.h"




globalScope *_DISPLAY_SCOPE;


// Reshape screen
void reshape(int w,int h)
{
  glViewport(0,0,w,h);
}



// Show results in screen 
void _display()
{	
  glLoadIdentity();
  glClearColor(1.0f, 1.0f, 1.0f, 1.0f);	
  int l = 0;
  
  glClear(GL_COLOR_BUFFER_BIT);
  glLoadIdentity();
  glBegin(GL_LINES);
  glColor3f(0.2,0.2,0.2);
  for(int i = 0; i < _DISPLAY_SCOPE->gArray[l]->numVertices; i++)
    for(int j = _DISPLAY_SCOPE->gArray[l]->AdjMatIndex[i]; j < _DISPLAY_SCOPE->gArray[l]->AdjMatIndex[i+1]; j++){
      int k = _DISPLAY_SCOPE->gArray[l]->AdjMatVals[j];
      glVertex3f(_DISPLAY_SCOPE->gArray[l]->NodePos[i].x, _DISPLAY_SCOPE->gArray[l]->NodePos[i].y,00);
      glVertex3f(_DISPLAY_SCOPE->gArray[l]->NodePos[k].x, _DISPLAY_SCOPE->gArray[l]->NodePos[k].y,00);
    }
  glEnd();
  glColor3f(1,0,0);
  glPointSize(1.1);
  glBegin(GL_POINTS);
  for(int i = 0; i < _DISPLAY_SCOPE->gArray[l]->numVertices; i++)
    glVertex3f(_DISPLAY_SCOPE->gArray[l]->NodePos[i].x, _DISPLAY_SCOPE->gArray[l]->NodePos[i].y,00);
  glEnd();
  
  glFlush();  /* OpenGL is pipelined, and sometimes waits for a full buffer to execute */
  glutSwapBuffers();
}


int showGraph(globalScope* scope, int argc, char** argv)
{
  _DISPLAY_SCOPE = scope;

  // setup GLUT
  glutInit(&argc, argv);
  glutInitDisplayMode(GLUT_RGB); 
  glutInitWindowSize(SCREEN_W,SCREEN_H);
  glutInitWindowPosition(100,100);
  // open a window 
  glutCreateWindow(argv[0]);	   
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
  gluOrtho2D(0,SCREEN_W,0,SCREEN_H);
  glMatrixMode(GL_MODELVIEW);
  glutReshapeFunc(reshape);
  // Tell GLUT how to fill window
  glutDisplayFunc(_display);		
  // let glut manage i/o processing
  glutMainLoop();			
  
  return 0;
}
