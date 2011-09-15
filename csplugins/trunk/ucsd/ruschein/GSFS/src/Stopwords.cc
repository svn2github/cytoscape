/** \file    stopwords.cc
 *  \brief   Implementation of function IsStopword.
 *  \author  Dr. Johannes Ruscheinski
 */

/*
 *  Copyright 2002-2008 Project iVia.
 *  Copyright 2002-2008 The Regents of The University of California.
 *
 *  This file is part of the libiViaCore package.
 *
 *  The libiViaCore package is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  libiViaCore is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with libiViaCore; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#include <Stopwords.h>
#include <GnuHash.h>


namespace {


GNU_HASH_SET<std::string> stopwords;
GNU_HASH_SET<std::string> glue_stopwords;


void InitializeStopwords()
{
	stopwords.insert("a");
	stopwords.insert("about");
	stopwords.insert("above");
	stopwords.insert("across");
	stopwords.insert("after");
	stopwords.insert("again");
	stopwords.insert("against");
	stopwords.insert("all");
	stopwords.insert("almost");
	stopwords.insert("alone");
	stopwords.insert("along");
	stopwords.insert("already");
	stopwords.insert("also");
	stopwords.insert("although");
	stopwords.insert("always");
	stopwords.insert("among");
	stopwords.insert("an");
	stopwords.insert("and");
	stopwords.insert("another");
	stopwords.insert("any");
	stopwords.insert("anybody");
	stopwords.insert("anyone");
	stopwords.insert("anything");
	stopwords.insert("anywhere");
	stopwords.insert("are");
	stopwords.insert("around");
	stopwords.insert("as");
	stopwords.insert("ask");
	stopwords.insert("asked");
	stopwords.insert("asking");
	stopwords.insert("asks");
	stopwords.insert("at");
	stopwords.insert("away");
	stopwords.insert("b");
	stopwords.insert("backed");
	stopwords.insert("backing");
	stopwords.insert("be");
	stopwords.insert("became");
	stopwords.insert("because");
	stopwords.insert("become");
	stopwords.insert("becomes");
	stopwords.insert("been");
	stopwords.insert("before");
	stopwords.insert("began");
	stopwords.insert("behind");
	stopwords.insert("being");
	stopwords.insert("beings");
	stopwords.insert("best");
	stopwords.insert("better");
	stopwords.insert("between");
	stopwords.insert("big");
	stopwords.insert("both");
	stopwords.insert("but");
	stopwords.insert("by");
	stopwords.insert("c");
	stopwords.insert("came");
	stopwords.insert("can");
	stopwords.insert("cannot");
	stopwords.insert("case");
	stopwords.insert("cases");
	stopwords.insert("certain");
	stopwords.insert("certainly");
	stopwords.insert("clear");
	stopwords.insert("clearly");
	stopwords.insert("come");
	stopwords.insert("could");
	stopwords.insert("d");
	stopwords.insert("did");
	stopwords.insert("differ");
	stopwords.insert("different");
	stopwords.insert("differently");
	stopwords.insert("do");
	stopwords.insert("does");
	stopwords.insert("done");
	stopwords.insert("down");
	stopwords.insert("downed");
	stopwords.insert("downing");
	stopwords.insert("downs");
	stopwords.insert("during");
	stopwords.insert("e");
	stopwords.insert("each");
	stopwords.insert("early");
	stopwords.insert("either");
	stopwords.insert("end");
	stopwords.insert("ended");
	stopwords.insert("ending");
	stopwords.insert("ends");
	stopwords.insert("enough");
	stopwords.insert("even");
	stopwords.insert("evenly");
	stopwords.insert("ever");
	stopwords.insert("every");
	stopwords.insert("everybody");
	stopwords.insert("everyone");
	stopwords.insert("everything");
	stopwords.insert("everywhere");
	stopwords.insert("f");
	stopwords.insert("face");
	stopwords.insert("faces");
	stopwords.insert("fact");
	stopwords.insert("facts");
	stopwords.insert("far");
	stopwords.insert("felt");
	stopwords.insert("few");
	stopwords.insert("find");
	stopwords.insert("finds");
	stopwords.insert("first");
	stopwords.insert("for");
	stopwords.insert("four");
	stopwords.insert("from");
	stopwords.insert("full");
	stopwords.insert("fully");
	stopwords.insert("further");
	stopwords.insert("furthered");
	stopwords.insert("furthering");
	stopwords.insert("furthers");
	stopwords.insert("g");
	stopwords.insert("gave");
	stopwords.insert("general");
	stopwords.insert("generally");
	stopwords.insert("get");
	stopwords.insert("gets");
	stopwords.insert("give");
	stopwords.insert("given");
	stopwords.insert("gives");
	stopwords.insert("go");
	stopwords.insert("going");
	stopwords.insert("good");
	stopwords.insert("goods");
	stopwords.insert("got");
	stopwords.insert("great");
	stopwords.insert("greater");
	stopwords.insert("greatest");
	stopwords.insert("group");
	stopwords.insert("grouped");
	stopwords.insert("grouping");
	stopwords.insert("groups");
	stopwords.insert("h");
	stopwords.insert("had");
	stopwords.insert("has");
	stopwords.insert("have");
	stopwords.insert("having");
	stopwords.insert("he");
	stopwords.insert("her");
	stopwords.insert("here");
	stopwords.insert("herself");
	stopwords.insert("high");
	stopwords.insert("higher");
	stopwords.insert("highest");
	stopwords.insert("him");
	stopwords.insert("himself");
	stopwords.insert("his");
	stopwords.insert("how");
	stopwords.insert("however");
	stopwords.insert("i");
	stopwords.insert("if");
	stopwords.insert("important");
	stopwords.insert("in");
	stopwords.insert("interest");
	stopwords.insert("interested");
	stopwords.insert("interesting");
	stopwords.insert("interests");
	stopwords.insert("into");
	stopwords.insert("is");
	stopwords.insert("it");
	stopwords.insert("its");
	stopwords.insert("itself");
	stopwords.insert("j");
	stopwords.insert("just");
	stopwords.insert("k");
	stopwords.insert("keep");
	stopwords.insert("keeps");
	stopwords.insert("kind");
	stopwords.insert("knew");
	stopwords.insert("know");
	stopwords.insert("known");
	stopwords.insert("knows");
	stopwords.insert("l");
	stopwords.insert("large");
	stopwords.insert("largely");
	stopwords.insert("last");
	stopwords.insert("later");
	stopwords.insert("latest");
	stopwords.insert("least");
	stopwords.insert("less");
	stopwords.insert("let");
	stopwords.insert("lets");
	stopwords.insert("like");
	stopwords.insert("likely");
	stopwords.insert("long");
	stopwords.insert("longer");
	stopwords.insert("longest");
	stopwords.insert("m");
	stopwords.insert("made");
	stopwords.insert("make");
	stopwords.insert("making");
	stopwords.insert("man");
	stopwords.insert("many");
	stopwords.insert("may");
	stopwords.insert("me");
	stopwords.insert("member");
	stopwords.insert("members");
	stopwords.insert("men");
	stopwords.insert("might");
	stopwords.insert("more");
	stopwords.insert("most");
	stopwords.insert("mostly");
	stopwords.insert("mr");
	stopwords.insert("mrs");
	stopwords.insert("much");
	stopwords.insert("must");
	stopwords.insert("my");
	stopwords.insert("myself");
	stopwords.insert("n");
	stopwords.insert("necessary");
	stopwords.insert("need");
	stopwords.insert("needed");
	stopwords.insert("needing");
	stopwords.insert("needs");
	stopwords.insert("never");
//	stopwords.insert("new"); causes problems w/ words like "new mexico"
	stopwords.insert("newer");
	stopwords.insert("newest");
	stopwords.insert("next");
	stopwords.insert("no");
	stopwords.insert("nobody");
	stopwords.insert("non");
	stopwords.insert("noone");
	stopwords.insert("not");
	stopwords.insert("nothing");
	stopwords.insert("now");
	stopwords.insert("nowhere");
	stopwords.insert("number");
	stopwords.insert("numbers");
	stopwords.insert("o");
	stopwords.insert("of");
	stopwords.insert("off");
	stopwords.insert("often");
	stopwords.insert("old");
	stopwords.insert("older");
	stopwords.insert("oldest");
	stopwords.insert("on");
	stopwords.insert("once");
	stopwords.insert("one");
	stopwords.insert("only");
	stopwords.insert("open");
	stopwords.insert("opened");
	stopwords.insert("opening");
	stopwords.insert("opens");
	stopwords.insert("or");
	stopwords.insert("order");
	stopwords.insert("ordered");
	stopwords.insert("ordering");
	stopwords.insert("orders");
	stopwords.insert("other");
	stopwords.insert("others");
	stopwords.insert("our");
	stopwords.insert("out");
	stopwords.insert("over");
	stopwords.insert("p");
	stopwords.insert("part");
	stopwords.insert("parted");
	stopwords.insert("parting");
	stopwords.insert("parts");
	stopwords.insert("per");
	stopwords.insert("perhaps");
	stopwords.insert("place");
	stopwords.insert("places");
	stopwords.insert("point");
	stopwords.insert("pointed");
	stopwords.insert("pointing");
	stopwords.insert("points");
	stopwords.insert("possible");
	stopwords.insert("present");
	stopwords.insert("presented");
	stopwords.insert("presenting");
	stopwords.insert("presents");
	stopwords.insert("problem");
	stopwords.insert("problems");
	stopwords.insert("put");
	stopwords.insert("puts");
	stopwords.insert("q");
	stopwords.insert("quite");
	stopwords.insert("r");
	stopwords.insert("rather");
	stopwords.insert("really");
	stopwords.insert("right");
	stopwords.insert("room");
	stopwords.insert("rooms");
	stopwords.insert("s");
	stopwords.insert("said");
	stopwords.insert("same");
	stopwords.insert("saw");
	stopwords.insert("say");
	stopwords.insert("says");
	stopwords.insert("second");
	stopwords.insert("seconds");
	stopwords.insert("see");
	stopwords.insert("seem");
	stopwords.insert("seemed");
	stopwords.insert("seeming");
	stopwords.insert("seems");
	stopwords.insert("sees");
	stopwords.insert("several");
	stopwords.insert("shall");
	stopwords.insert("she");
	stopwords.insert("should");
	stopwords.insert("show");
	stopwords.insert("showed");
	stopwords.insert("showing");
	stopwords.insert("shows");
	stopwords.insert("side");
	stopwords.insert("sides");
	stopwords.insert("since");
	stopwords.insert("small");
	stopwords.insert("smaller");
	stopwords.insert("smallest");
	stopwords.insert("so");
	stopwords.insert("some");
	stopwords.insert("somebody");
	stopwords.insert("someone");
	stopwords.insert("something");
	stopwords.insert("somewhere");
//	stopwords.insert("state");
//	stopwords.insert("states");
	stopwords.insert("still");
	stopwords.insert("such");
	stopwords.insert("sure");
	stopwords.insert("t");
	stopwords.insert("take");
	stopwords.insert("taken");
	stopwords.insert("than");
	stopwords.insert("that");
	stopwords.insert("the");
	stopwords.insert("their");
	stopwords.insert("them");
	stopwords.insert("then");
	stopwords.insert("there");
	stopwords.insert("therefore");
	stopwords.insert("these");
	stopwords.insert("they");
	stopwords.insert("thing");
	stopwords.insert("things");
	stopwords.insert("think");
	stopwords.insert("thinks");
	stopwords.insert("this");
	stopwords.insert("those");
	stopwords.insert("though");
	stopwords.insert("thought");
	stopwords.insert("thoughts");
	stopwords.insert("three");
	stopwords.insert("through");
	stopwords.insert("thus");
	stopwords.insert("to");
	stopwords.insert("today");
	stopwords.insert("together");
	stopwords.insert("too");
	stopwords.insert("took");
	stopwords.insert("toward");
	stopwords.insert("turn");
	stopwords.insert("turned");
	stopwords.insert("turning");
	stopwords.insert("turns");
	stopwords.insert("two");
	stopwords.insert("u");
	stopwords.insert("under");
	stopwords.insert("until");
	stopwords.insert("up");
	stopwords.insert("upon");
	stopwords.insert("us");
	stopwords.insert("use");
	stopwords.insert("used");
	stopwords.insert("uses");
	stopwords.insert("v");
	stopwords.insert("very");
	stopwords.insert("w");
	stopwords.insert("want");
	stopwords.insert("wanted");
	stopwords.insert("wanting");
	stopwords.insert("wants");
	stopwords.insert("was");
	stopwords.insert("way");
	stopwords.insert("ways");
	stopwords.insert("we");
	stopwords.insert("well");
	stopwords.insert("wells");
	stopwords.insert("went");
	stopwords.insert("were");
	stopwords.insert("what");
	stopwords.insert("when");
	stopwords.insert("where");
	stopwords.insert("whether");
	stopwords.insert("which");
	stopwords.insert("while");
	stopwords.insert("who");
	stopwords.insert("whole");
	stopwords.insert("whose");
	stopwords.insert("why");
	stopwords.insert("will");
	stopwords.insert("with");
	stopwords.insert("within");
	stopwords.insert("without");
	stopwords.insert("work");
	stopwords.insert("worked");
	stopwords.insert("working");
	stopwords.insert("works");
	stopwords.insert("would");
	stopwords.insert("x");
	stopwords.insert("y");
	stopwords.insert("year");
	stopwords.insert("years");
	stopwords.insert("yet");
	stopwords.insert("you");
	stopwords.insert("young");
	stopwords.insert("younger");
	stopwords.insert("youngest");
	stopwords.insert("your");
	stopwords.insert("yours");
	stopwords.insert("z");
}


void InitializeGlueStopwords()
{
	glue_stopwords.insert("and");
	glue_stopwords.insert("about");
	glue_stopwords.insert("above");
	glue_stopwords.insert("across");
	glue_stopwords.insert("after");
	glue_stopwords.insert("against");
	glue_stopwords.insert("along");
	glue_stopwords.insert("among");
	glue_stopwords.insert("around");
	glue_stopwords.insert("as");
	glue_stopwords.insert("at");
	glue_stopwords.insert("away");
	glue_stopwords.insert("back");
	glue_stopwords.insert("before");
	glue_stopwords.insert("behind");
	glue_stopwords.insert("between");
	glue_stopwords.insert("by");
	glue_stopwords.insert("down");
	glue_stopwords.insert("from");
	glue_stopwords.insert("in");
	glue_stopwords.insert("into");
	glue_stopwords.insert("of");
	glue_stopwords.insert("off");
	glue_stopwords.insert("on");
	glue_stopwords.insert("out");
	glue_stopwords.insert("over");
	glue_stopwords.insert("through");
	glue_stopwords.insert("to");
	glue_stopwords.insert("toward");
	glue_stopwords.insert("under");
	glue_stopwords.insert("up");
	glue_stopwords.insert("upon");
	glue_stopwords.insert("with");
	glue_stopwords.insert("within");
	glue_stopwords.insert("without");
}


} // unnamed namespace


bool IsStopword(const std::string &word)
{
	// Make sure the stopwords hash is initialised:
	static bool stopwords_are_initalised(false);
	if (not stopwords_are_initalised) {
		InitializeStopwords();
		stopwords_are_initalised = true;
	}

	// Can we find the word in the hash?
	return stopwords.find(word) != stopwords.end();
}


bool IsGlueStopword(const std::string &word)
{
	// Make sure the stopwords hash is initialised:
	static bool glue_stopwords_are_initalised(false);
	if (not glue_stopwords_are_initalised) {
		InitializeGlueStopwords();
		glue_stopwords_are_initalised = true;
	}

	// Can we find the word in the hash?
	return glue_stopwords.find(word) != glue_stopwords.end();
}
