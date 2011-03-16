#!/usr/bin/env python

import Usefuls.TmpFile

tmp_obj = Usefuls.TmpFile.TmpFile_II("""
<team>
  <player name='Mick Fowler' age='27' height='1.96m'>
    <points>17.1</points>
    <rebounds>6.4</rebounds>
  </player>
  <player name='Ivan Ivanovic' age='29' height='2.04m'>
    <points>15.5</points>
    <rebounds>7.8</rebounds>
  </player>
</team>
""")

from xml.sax import make_parser
from xml.sax.handler import ContentHandler


class BasketBallHandler(ContentHandler):

    def __init__ (self, searchTerm):
        self.searchTerm= searchTerm;
        self.isPointsElement, self.isReboundsElement = 0, 0;

        def startElement(self, name, attrs):

            if name == 'player':
                self.playerName = attrs.get('name',"")
                self.playerAge = attrs.get('age',"")
                self.playerHeight = attrs.get('height',"")
            elif name == 'points':
                self.isPointsElement= 1;
                self.playerPoints = "";
            elif name == 'rebounds':
                self.isReboundsElement = 1;
                self.playerRebounds = "";
                return

        def characters (self, ch):
            if self.isPointsElement== 1:
                self.playerPoints += ch
            if self.isReboundsElement == 1:
                self.playerRebounds += ch

        def endElement(self, name):
            if name == 'points':
                self.isPointsElement= 0
            if name == 'rebounds':
                self.inPlayersContent = 0
            if name == 'player' and self.searchTerm== self.playerName :
                print '<h2>Statistics for player:' , self.playerName, '</h2><br>(age:', self.playerAge , 'height' , self.playerHeight , ")<br>"
                print 'Match average:', self.playerPoints , 'points,' , self.playerRebounds, 'rebounds'

parser = make_parser()
curHandler = BasketBallHandler('Mick Fowler')
parser.setContentHandler(curHandler)
parser.parse(open(tmp_obj.filename()))

