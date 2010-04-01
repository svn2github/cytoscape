package org.cytoscape.data.webservice.cytscanner.test;

import org.jdesktop.swingx.JXLoginPane;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Twitter4jTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testClient() throws TwitterException {
		
//		Twitter twitter = new TwitterFactory().getInstance();
//		ResponseList<Status> timeline = twitter.getHomeTimeline();
//		
//		System.out.println("hits:" + timeline.size());
//		for (Status status : timeline) {
//			System.out.println(status.getUser().getName() + ":" + status.getText());
//		}

	}
}
