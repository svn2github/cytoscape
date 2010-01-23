/*
 Some javascript to read the RSS feed our the cytoscape-announce mailing list.
*/

google.load("feeds", "1");

function formatDate(dateLiteral)
{
	var date = new Date(dateLiteral);
	var month = date.getMonth() + 1;
	var day = date.getDate();
	var year = date.getFullYear();
	return month + "/" + day + "/" + year;
}
              
function initialize() 
{
	var feed = new google.feeds.Feed("http://groups.google.com/group/cytoscape-announce/feed/rss_v2_0_msgs.xml");
	feed.load(function(result) 
	{
		if (!result.error) 
		{
			try
			{
				var container = document.getElementById("feed");
				var numEntries = Math.min(3,result.feed.entries.length);

				for (var i = 0; i < numEntries; i++)
				{
					var entry = result.feed.entries[i];
	
					var item = document.createElement("li");
	
					item.innerHTML = "<a href='" + entry.link + "'>" 
					                 + entry.title + "</a><br/>"
					                 + formatDate(entry.publishedDate);

					var content = document.createElement("div");
					var p = entry.content.replace(/<br.?>/g,"");
					content.innerHTML = p;
					item.appendChild(content);

					container.appendChild(item);

				}
			}
			catch (e)
			{
				alert("Error setting up announcements: " + e);
			}
		}
	});
}

google.setOnLoadCallback(initialize);
