package SawdVisualizer;

import java.awt.event.*;
import java.util.*;

public class Main
{
	private static List<SessionWindow> session_windows = new ArrayList<SessionWindow>();
	
	public static void main(String[] args)
	{
		ActionListener title_listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				newSession();
			}
		};

		TitleWindow title_window = new TitleWindow(title_listener);
		title_window.setVisible(true);
	}

	public static SessionWindow newSession()
	{
		SessionWindow session_window = new SessionWindow();
		session_window.setVisible(true);
		session_windows.add(session_window);
		return session_window;
	}

	public static void endAllSessions()
	{
		for (SessionWindow session_window : session_windows)
		{
			session_window.closeSession();
		}
	}
}
