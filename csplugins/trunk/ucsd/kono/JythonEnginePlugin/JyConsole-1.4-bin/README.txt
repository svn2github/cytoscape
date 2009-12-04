
                          JyConsole 
             Advanced Java based Jython graphic console
                 (c) Artenum, Paris, France, 2005
                 http://www.artenum.com/jyconsole
                 
                    Version 1.4 (August 2007)

Authors: 
    - Sebastien Jourdain (Artenum), contact: sebastien.Jourdain@artenum.com
    - Artenum SARL, Paris, France. 

1) Introduction / Purpose

   JyConsole is an advanced Java console for Jython, able to manipulate
   Python and Java objects.

   JyConsole is fully written in Java and based on the Jython library and
   modules. It answers to several requirements of modern consoles, such as
   command history, completion, easy script loading and basic GUI preference
   management.

   More especially, JyConsole provides an advanced object oriented completion
   on Java and Python objects. JyConsole allows the direct manipulation of any
   objects used in the python and class paths, independently of their initial
   language (Java/Python).
 
   JyConsole can be extended as any application, but its first goal is to
   offer a simple and powerful Java/Python console fully portable. JyConsole
   can be directly used as standalone application or as a embedded
   graphical component into a Java or Jython application.

2) Build from source package

   To build the JyConsole from the source package please read the file 
   BUILD.txt.

3) Install and use

   To install the binary package of JyConsole please read the file 
   INSTALL.txt
   
4) Test it

  The package comes with a default set of script and a default configuration file.
  The default loaded script provide a "f" variable which is a JFrame java object.
  
  Features:
   - Command history (Key: Up and Down)
   - Style management on text (Popup menu on the console/jyconsole.propertie file)
   - Script loader (Popup menu on the console)
   - Java completion (Key: Ctrl + Space)
   - Python completion (Key: Ctrl + Space)
  
5) Intellectual properties and licensing

  5.1) JyConsole

    All rights, trademarks and intellectual propertie reserved by Artenum, 
    Paris, France, 2006.

    This software can not be used or copy or diffused without an explicit 
    license of Artenum SARL, Paris-France. The license file is available 
    in LICENSE.txt

    For further information, please contact contact@artenum.com or our 
    Web site: http://www.artenum.com


  5.2) ThirdPart and external components

    See the "THIRDPART.txt" for more details. 
    This file could be in the thirdpart directory if provided otherwise in 
    the root directory.
    
  5.3) Contribution
  
    Colin Crist (colincrist@hermesjms.com) 2006-06-23
      => Improved the thread management of JyConsole by introducing the 
         CommandRunner interface.

6) Contact

    E-mail: jourdain@artenum.com

    Mail: Artenum SARL
          101-103, Bd Mac Donald
          75019 PARIS, FRANCE

    Phone: +33(0)1 44 89 45 15
    Fax:   +33(0)1 44 89 45 17
